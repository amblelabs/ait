package dev.amble.ait.core.devteam;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BetaTeam extends HashMap<UUID, String> {

    public static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static CompletableFuture<String> downloadAsString(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(1))
                .GET()
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    // Fail the future for non-successful HTTP status codes
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return response.body();
                    } else {
                        throw new RuntimeException("HTTP error code: " + response.statusCode());
                    }
                });
    }

    public static void init() {
        LocalCallbackServer.fetchServerData();
    }
}
