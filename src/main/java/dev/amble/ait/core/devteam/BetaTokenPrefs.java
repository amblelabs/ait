package dev.amble.ait.core.devteam;

import java.util.prefs.Preferences;

import com.google.gson.Gson;

import dev.amble.ait.AITMod;

public class BetaTokenPrefs {
    private static final Preferences prefs = Preferences.userNodeForPackage(AITMod.class);
    private static final String KEY = "ait-jwt";

    private static Boolean verified = null;

    public static void verify() {
        verified = true;
    }

    public static void saveToken(String jwt) {
        prefs.put(KEY, jwt);
        verified = null;
    }

    public static String loadToken() {
        verified = null;
        return prefs.get(KEY, null);
    }

    public static void clearToken() {
        prefs.remove(KEY);
        verified = null;
    }

    public static boolean isTokenValid() {
        if (verified != null) return verified;

        String jwt = loadToken();
        if (jwt == null) return false;

        try {
            return verified = BetaVerification.downloadAsString(BetaVerification.SERVER_DATA.verifier() + "/verify",
                            builder -> builder.header("Authorization", "Bearer " + jwt))
                    .thenApply(s -> new Gson().fromJson(s, VerificationResponse.class))
                    .thenApply(VerificationResponse::valid).get();
        } catch (Exception e) {
            AITMod.LOGGER.error("Failed to check token validity", e);
            return false;
        }
    }

    record VerificationResponse(boolean valid, String error) { }
}