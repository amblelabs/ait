package dev.amble.ait.core.devteam;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import dev.amble.ait.AITMod;

import java.net.InetSocketAddress;
import java.awt.Desktop;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class LocalCallbackServer {
    private static String receivedToken = null;
    private static HttpServer server;

    public record ServerData(String url) { }

    public static ServerData SERVER_DATA;

    public static void fetchServerData() {
        BetaTeam.downloadAsString("https://amblelabs.github.io/data.json").exceptionally(throwable -> {
            AITMod.LOGGER.error("Failed to access the server data, falling back", throwable);
            return new Gson().toJson(new ServerData("https://amble-verifier.drtheo.workers.dev"));
        }).thenAccept(s -> {
            SERVER_DATA = new Gson().fromJson(s, ServerData.class);
            AITMod.LOGGER.info("Successfully fetched server data: {}", SERVER_DATA);
        });
    }

    public static void startAndWaitForToken() {
        try {
            startAndWaitForToken0(SERVER_DATA);
        } catch (Exception e) {
            AITMod.LOGGER.error("Failed to wait for token", e);
        }
    }

    private static void startAndWaitForToken0(ServerData data) throws Exception {
        int port = 54321;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/callback", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("token=")) {
                receivedToken = URLDecoder.decode(query.substring(6), StandardCharsets.UTF_8);
                String response = "<html><body><h1>Verification successful! You can close this window.</h1></body></html>";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
                new Thread(() -> {
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                    server.stop(0);
                }).start();
            } else {
                exchange.sendResponseHeaders(400, 0);
                exchange.getResponseBody().close();
            }
        });
        server.start();

        String authUrl = data.url + "/auth?port=" + port;
        Desktop.getDesktop().browse(new URI(authUrl));

        while (receivedToken == null) {
            Thread.sleep(500);
        }

        TokenPrefs.saveToken(receivedToken);
        System.out.println("JWT received");
    }
}