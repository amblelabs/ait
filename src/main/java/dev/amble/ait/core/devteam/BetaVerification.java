package dev.amble.ait.core.devteam;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import net.minecraft.util.Util;

import dev.amble.ait.AITMod;

public class BetaVerification {

    private static final long TIMEOUT = 30_000L; // 30 seconds
    private static final int PORT = 54321;

    public static ServerData SERVER_DATA;
    private static String RECEIVED_TOKEN;

    private static HttpServer SERVER;
    private static HttpClient CLIENT;

    public record ServerData(String verifier) { }

    public static void init() {
        if (!AITMod.isBetaLocked()) return;

        BetaVerification.fetchServerData().thenAcceptAsync(unused
                -> BetaTokenPrefs.isTokenValid());
    }

    public static boolean isServerRunning() {
        return SERVER != null;
    }

    public static CompletableFuture<String> downloadAsString(String url) {
        return downloadAsString(url, UnaryOperator.identity());
    }

    public static CompletableFuture<String> downloadAsString(String url, UnaryOperator<HttpRequest.Builder> builder) {
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

    public static CompletableFuture<Void> fetchServerData() {
        return downloadAsString("https://amblelabs.dev/data.json").exceptionally(throwable -> {
            AITMod.LOGGER.error("Failed to access the server data, falling back", throwable);
            return new Gson().toJson(new ServerData("https://amble-verifier.drtheo.workers.dev"));
        }).thenAccept(s -> {
            SERVER_DATA = new Gson().fromJson(s, ServerData.class);
            AITMod.LOGGER.info("Successfully fetched server data: {}", SERVER_DATA);
        });
    }

    public static void startAndWaitForToken(Consumer<Boolean> consumer) {
        new Thread(() -> {
            try {
                startAndWaitForToken0();
            } catch (Exception e) {
                AITMod.LOGGER.error("Failed to wait for token", e);
            }

            consumer.accept(BetaTokenPrefs.isTokenValid());
        }).run();
    }

    private static void startAndWaitForToken0() throws Exception {
        if (SERVER == null) {
            SERVER = HttpServer.create(new InetSocketAddress(PORT), 0);
            SERVER.createContext("/callback", exchange -> {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("token=")) {
                    RECEIVED_TOKEN = URLDecoder.decode(query.substring(6), StandardCharsets.UTF_8);
                    String response = "<html><body><h1>Verification successful! You can close this window.</h1></body></html>";
                    exchange.sendResponseHeaders(200, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                    exchange.getResponseBody().close();
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ignored) {
                        }
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

        Util.getOperatingSystem().open(new URI(getAuthUrl()));

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

        BetaTokenPrefs.saveToken(RECEIVED_TOKEN);
        BetaTokenPrefs.verify();

        RECEIVED_TOKEN = null;
    }

    public static String getAuthUrl() {
        return SERVER_DATA.verifier + "/auth?port=" + PORT;
    }
}
