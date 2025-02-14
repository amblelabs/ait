package dev.amble.ait.core.tardis.handler.permissions;

import java.util.Map;

public interface PermissionLike {
    String name();

    Permission parent();

    Map<String, Permission> children();

    String getId();
}
