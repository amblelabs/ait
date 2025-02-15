package dev.amble.ait.core.tardis.handler.permissions;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

import com.google.gson.*;

public record Permission(String name, Permission parent, Map<String, Permission> children) implements PermissionLike {

    public interface USE {
        PermissionWrapper CONSOLE = new PermissionWrapper("console"); // Permission to use console
        PermissionWrapper TRAVEL = new PermissionWrapper("travel"); // Permission to travel inside the tardis
        PermissionWrapper LINK = new PermissionWrapper("link"); // Permission to attune sonic or key to a tardis
    }

    public interface MODIFY {
        PermissionWrapper PLACE = new PermissionWrapper("place"); // Permission to place blocks
        PermissionWrapper BREAK = new PermissionWrapper("break"); // Permission to break blocks
        PermissionWrapper INTERACT = new PermissionWrapper("break"); // Permission to interact with blocks
        PermissionWrapper CONTAINER = new PermissionWrapper("container"); // Permission to open containers

        PermissionWrapper DESKTOP = new PermissionWrapper("desktop");
        PermissionWrapper EXTERIOR = new PermissionWrapper("exterior");
    }

    public interface SPECIAL {
        PermissionWrapper CLOAK = new PermissionWrapper("cloak"); // Permission to see through cloak
        PermissionWrapper SNAP = new PermissionWrapper("snap");
    }

    public static final Permission LOOKUP = withChildren("tardis", null,
            base -> withChildren("use", base, USE.CONSOLE, USE.TRAVEL, USE.LINK),
            base -> withChildren("modify", base, MODIFY.PLACE, MODIFY.BREAK, MODIFY.INTERACT, MODIFY.CONTAINER,
                    MODIFY.DESKTOP, MODIFY.EXTERIOR),
            base -> withChildren("special", base, SPECIAL.CLOAK, SPECIAL.SNAP));

    public static void collect(Collection<String> result, Permission root) {
        for (Permission permission : root.children.values()) {
            if (permission.children == null || permission.children.isEmpty()) {
                result.add(permission.getId());
                continue;
            }

            collect(result, permission);
        }
    }

    public static Collection<String> collect() {
        Set<String> result = new HashSet<>();
        Permission.collect(result, LOOKUP);

        return result;
    }

    @SafeVarargs
    public static Permission withChildren(String name, Permission parent,
            Function<Permission, Permission>... children) {
        Permission base = new Permission(name, parent, new HashMap<>());

        for (Function<Permission, Permission> func : children) {
            Permission child = func.apply(base);
            base.children.put(child.name(), child);
        }

        return base;
    }

    public static Permission withChildren(String name, Permission parent, PermissionWrapper... children) {
        Permission base = new Permission(name, parent, new HashMap<>());

        for (PermissionWrapper wrapper : children) {
            wrapper.construct(base, null);
            base.children.put(wrapper.getName(), wrapper.get());
        }

        return base;
    }

    /**
     * @return Returns the fully qualified permission id, unlike the permission
     *         property.
     */
    public String getId() {
        String base = "";

        if (this.parent != null)
            base = parent.getId() + ".";

        return base + this.name;
    }

    public static Permission from(String id) {
        String[] parts = id.split("\\.");
        Permission node = LOOKUP;

        for (int i = 1; i < parts.length; i++) {
            node = node.children.get(parts[i]);

            if (node == null)
                return null;
        }

        return node;
    }

    @Override
    @SuppressWarnings("EqualsBetweenInconvertibleTypes") // shut up IntelliJ
    public boolean equals(Object obj) {
        if (obj instanceof PermissionWrapper wrapper)
            return wrapper.equals(this); // delegates to PermissionWrapper's equals method

        if (obj instanceof Permission other)
            return this == other; // finally checks if the permission is the same!

        return false;
    }

    @Override
    public String toString() {
        return this.getId();
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    public static Object serializer() {
        return new Serializer();
    }

    private static class Serializer implements JsonDeserializer<PermissionLike>, JsonSerializer<PermissionLike> {

        @Override
        public PermissionLike deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return Permission.from(json.getAsString());
        }

        @Override
        public JsonElement serialize(PermissionLike permission, Type type,
                JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(permission.getId());
        }
    }
}
