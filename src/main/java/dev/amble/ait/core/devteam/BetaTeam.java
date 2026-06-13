package dev.amble.ait.core.devteam;

import com.google.gson.Gson;
import dev.amble.ait.AITMod;
import net.fabricmc.fabric.api.util.TriState;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BetaTeam extends HashMap<UUID, String> {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String API_TESTERS = "https://raw.githubusercontent.com/amblelabs/ait/refs/heads/main/scripts/testers.json";

    private static BetaTeam INSTANCE;
    private static boolean inProgress = false;

    private static CompletableFuture<String> downloadAsString(String url) {
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

    public static TriState isBetaTester(UUID id) {
        if (DevTeam.isDev(id)) return TriState.TRUE;

        if (INSTANCE != null) return INSTANCE.containsKey(id) ? TriState.TRUE : TriState.FALSE;

        if (inProgress)
            return TriState.DEFAULT;

        inProgress = true;
        downloadAsString(API_TESTERS).whenComplete((s, throwable) -> {
            inProgress = false;
        }).thenAccept(s -> {
            try {
                INSTANCE = new Gson().fromJson(s, BetaTeam.class);
            } catch (Exception e) {
                AITMod.LOGGER.error("Beta list failed to deserialize", e);
            }
        });

        return TriState.DEFAULT;
    }

    public static void init() {
        isBetaTester(DevTeam.THEO);
    }
}
