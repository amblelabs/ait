package dev.amble.ait.core.devteam;

import dev.amble.ait.AITMod;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.prefs.Preferences;

public class TokenPrefs {
    private static final Preferences prefs = Preferences.userNodeForPackage(AITMod.class);
    private static final String KEY = "ait-jwt";

    public static boolean verified = false;

    public static void saveToken(String jwt) {
        prefs.put(KEY, jwt);
        verified = false;
    }

    public static String loadToken() {
        verified = false;
        return prefs.get(KEY, null);
    }

    public static void clearToken() {
        prefs.remove(KEY);
        verified = false;
    }

    public static boolean isTokenValid() {
        if (verified) return true;

        String jwt = loadToken();
        if (jwt == null) return false;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LocalCallbackServer.SERVER_DATA.verifier() + "/verify"))
                    .header("Authorization", "Bearer " + jwt)
                    .GET()
                    .build();

            HttpResponse<String> response = BetaTeam.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return verified = response.statusCode() == 200;
        } catch (Exception e) {
            AITMod.LOGGER.error("Failed to check token validity", e);
            return false;
        }
    }
}