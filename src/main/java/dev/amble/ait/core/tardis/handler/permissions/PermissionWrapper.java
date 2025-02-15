package dev.amble.ait.core.tardis.handler.permissions;

import java.util.Map;

public class PermissionWrapper implements PermissionLike {

    private final String name;
    private Permission permission;

    public PermissionWrapper(String name) {
        this.name = name;
    }

    public void construct(Permission parent, Map<String, Permission> children) {
        this.set(new Permission(this.name, parent, children));
    }

    public void set(Permission perm) {
        this.permission = perm;
    }

    public Permission get() {
        return this.permission;
    }

    public String getName() {
        return name;
    }

    @Override
    public String name() {
        return this.permission != null ? this.permission.name() : this.name;
    }

    @Override
    public Permission parent() {
        return this.permission.parent();
    }

    @Override
    public Map<String, Permission> children() {
        return this.permission.children();
    }

    @Override
    public String getId() {
        return this.permission.getId();
    }

    @Override
    @SuppressWarnings("EqualsBetweenInconvertibleTypes") // shut up IntelliJ
    public boolean equals(Object obj) {
        if (obj instanceof PermissionWrapper wrapper)
            return this.equals(wrapper.permission); // delegates to the second if statement

        if (obj instanceof Permission other)
            return this.permission != null && this.permission.equals(other); // delegates to the permission's equals
                                                                                // method

        return false;
    }

    @Override
    public String toString() {
        return this.getId();
    }
}
