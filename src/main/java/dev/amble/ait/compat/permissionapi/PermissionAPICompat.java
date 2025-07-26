package dev.amble.ait.compat.permissionapi;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;

import net.minecraft.server.command.ServerCommandSource;

import dev.amble.ait.compat.DependencyChecker;

public class PermissionAPICompat implements ModInitializer {

    private static PermissionCheck CHECKER = (ctx, permission, level) -> ctx.hasPermissionLevel(level);

    @FunctionalInterface
    public interface PermissionCheck {
        boolean hasPermission(ServerCommandSource ctx, String permission, int level);
    }

    @Override
    public void onInitialize() {
        if (DependencyChecker.hasPermissionApi()) {
            CHECKER = (ctx, permission, level) -> Permissions.check(ctx, permission, level);
        }
    }

    // Public static method to use the permission check lambda
    public static boolean hasPermission(ServerCommandSource ctx, String permission, int level) {
        return CHECKER.hasPermission(ctx, permission, level);
    }
}
