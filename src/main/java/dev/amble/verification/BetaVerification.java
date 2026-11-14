package dev.amble.verification;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.prefs.Preferences;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BetaVerification {

    public static final Logger LOGGER = LoggerFactory.getLogger(BetaVerification.class);
    private static final String DATA_URL = "https://amblelabs.dev/data.json";

    private static final long TIMEOUT = 60_000L; // 60 seconds
    private static final int PORT = 54321;

    private static final String SUCCESS_HTML = "<html><body><h1>Verification successful! You can close this window.</h1></body></html>";

    private static String RECEIVED_TOKEN;

    private static HttpServer SERVER;
    private static HttpClient CLIENT;

    private final Backend<?> backend;
    private final BuildType type;

    private final Preferences prefs;
    private final String key;

    private ServerData data;
    private Boolean verified = null;

    public enum BuildType {
        RELEASE,
        BETA,
        LOCAL,
    }

    public interface ServerData {
        String verifier();
    }

    public BetaVerification(Class<?> clazz, String modId) {
        this.backend = this.backend();
        this.prefs = Preferences.userNodeForPackage(clazz);
        this.key = modId + "-jwt";

        // 1.x.xx-[BRANCH-]dev+mc.1.20.1
        String version = FabricLoader.getInstance()
                .getModContainer(modId)
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion)
                .map(Version::getFriendlyString)
                .orElse(null);

        if (version == null) {
            type = BuildType.LOCAL;
            return;
        }

        // get the part of the version string between the - and +
        this.type = this.getBuildTypeForBranch(version.substring(version.indexOf('-') + 1, version.indexOf('+')));

        if (this.isLocked()) this.fetchServerData().thenAcceptAsync(v -> isTokenValid());
    }

    protected BuildType getBuildTypeForBranch(String branch) {
        int idx = branch.lastIndexOf('-');
        String rawType = idx != -1 ? branch.substring(branch.lastIndexOf('-') + 1) : branch;

        return switch (rawType) {
            case "release" -> BuildType.RELEASE;
            case "dev" -> BuildType.BETA;
            case "local" -> BuildType.LOCAL;
            default -> throw new IllegalStateException("Unexpected value: " + branch);
        };
    }

    private void verify() {
        this.verified = true;
    }

    private void saveToken(String jwt) {
        prefs.put(key, jwt);
        this.verified = null;
    }

    private String loadToken() {
        verified = null;
        return prefs.get(key, null);
    }

    private void clearToken() {
        prefs.remove(key);
        verified = null;
    }

    private boolean isTokenValid() {
        if (verified != null) return verified;

        String jwt = this.loadToken();
        if (jwt == null) return false;

        try {
            return verified = this.query("verify",
                            builder -> builder.header("Authorization", "Bearer " + jwt))
                    .thenApply(s -> new Gson().fromJson(s, VerificationResponse.class))
                    .thenApply(VerificationResponse::valid).get();
        } catch (Exception e) {
            LOGGER.error("Failed to check token validity", e);
            return false;
        }
    }

    record VerificationResponse(boolean valid, String error) { }

    public BuildType getBuildType() {
        return type;
    }

    public boolean isLocked() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment() && this.type != BuildType.RELEASE && !this.isTokenValid();
    }

    public CompletableFuture<String> query(String path, UnaryOperator<HttpRequest.Builder> builder) {
        if (!path.isEmpty() && path.charAt(0) == '/')
            path = path.substring(1);

        return downloadAsString(this.data.verifier() + "/" + path, builder);
    }

    protected static CompletableFuture<String> downloadAsString(String url) {
        return downloadAsString(url, UnaryOperator.identity());
    }

    protected static CompletableFuture<String> downloadAsString(String url, UnaryOperator<HttpRequest.Builder> builder) {
        HttpRequest request = builder.apply(HttpRequest.newBuilder())
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(1))
                .GET()
                .build();

        if (CLIENT == null)
            CLIENT = HttpClient.newHttpClient();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    // Fail the future for non-successful HTTP status codes
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return response.body();
                    } else {
                        if (response.body() != null)
                            return response.body();

                        throw new RuntimeException("HTTP error code: " + response.statusCode());
                    }
                });
    }

    private CompletableFuture<Void> fetchServerData() {
        return downloadAsString(DATA_URL).thenApply(s -> {
            ServerData data = new Gson().fromJson(s, ServerData.class);
            LOGGER.info("Successfully fetched server data: {}", data);
            return data;
        }).exceptionally(throwable -> {
            LOGGER.error("Failed to access the server data, falling back", throwable);
            return this.backend.fallback().get();
        }).thenAccept(s -> data = s);
    }

    public String getAuthUrl() {
        return data.verifier() + "/auth?port=" + PORT;
    }

    protected Backend<?> backend() {
        return new Backend<>(DefaultServerData.class, () -> DefaultServerData.FALLBACK);
    }

    public record Backend<T extends ServerData>(Class<? extends T> clazz, Supplier<T> fallback) { }

    private record DefaultServerData(String verifier) implements ServerData {
        public static DefaultServerData FALLBACK = new DefaultServerData("https://amble-verifier.amblelabs.workers.dev");
    }

    public void startAndWaitForToken(BooleanConsumer consumer) {
        new Thread(() -> {
            try {
                startAndWaitForToken0();
            } catch (Exception e) {
                LOGGER.error("Failed to wait for token", e);
            }

            consumer.accept(this.isTokenValid());
        }).start();
    }

    private static HttpServer createServer(int port) throws IOException {
        return HttpServer.create(new InetSocketAddress(port), 0);
    }

    private void startAndWaitForToken0() throws Exception {
        if (SERVER == null) {
            for (int port = PORT; SERVER == null && port < PORT + 5; port++) {
                try {
                    SERVER = createServer(port);
                } catch (BindException e) {
                    LOGGER.error("Failed to bind to port {}", port, e);
                    break;
                }
            }

            Objects.requireNonNull(SERVER, "Failed to start the server");

            SERVER.createContext("/callback", exchange -> {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("token=")) {
                    RECEIVED_TOKEN = URLDecoder.decode(query.substring(6), StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, SUCCESS_HTML.length());
                    exchange.getResponseBody().write(SUCCESS_HTML.getBytes());
                    exchange.getResponseBody().close();
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ignored) { }
                        SERVER.stop(0);
                        SERVER = null;
                    }).start();
                } else {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.getResponseBody().close();
                }
            });
            SERVER.start();
        }

        Util.getOperatingSystem().open(new URI(this.getAuthUrl()));

        long waitingFor = 0L;
        while (RECEIVED_TOKEN == null) {
            Thread.sleep(500);
            waitingFor += 500L;

            if (waitingFor > TIMEOUT) {
                SERVER.stop(0);
                SERVER = null;
                return;
            }
        }

        this.saveToken(RECEIVED_TOKEN);
        this.verify();

        RECEIVED_TOKEN = null;
    }

    public static boolean isAttemptingAuth() {
        return SERVER != null;
    }
}
