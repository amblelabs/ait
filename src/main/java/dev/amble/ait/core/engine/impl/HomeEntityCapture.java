package dev.amble.ait.core.engine.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import dev.amble.ait.api.HomeCaptureAware;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisHomeUtil;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Routes item and experience spawns through installed exact-home collectors.
 * Only active module instances are inspected, avoiding global TARDIS searches.
 */
public final class HomeEntityCapture {
    private static final int CELL_SIZE = 256;
    private static final WeakModuleRegistry<SculkCatalystCollector> SCULK = new WeakModuleRegistry<>();
    private static final WeakModuleRegistry<EnderChestCollector> ENDER = new WeakModuleRegistry<>();
    private static final ThreadLocal<Boolean> ROUTING_ITEM = ThreadLocal.withInitial(() -> false);

    private HomeEntityCapture() {
    }

    static void register(SculkCatalystCollector collector) {
        SCULK.register(collector);
    }

    static void unregister(SculkCatalystCollector collector) {
        SCULK.unregister(collector);
    }

    static void register(EnderChestCollector collector) {
        ENDER.register(collector);
    }

    static void unregister(EnderChestCollector collector) {
        ENDER.unregister(collector);
    }

    public static boolean tryCaptureExperience(ServerWorld world, Vec3d position, long amount) {
        if (world == null || position == null || amount <= 0)
            return false;

        return tryCandidates(SCULK, world, position, module -> module.captureExperience(amount));
    }

    static boolean tryCaptureExistingExperience(ExperienceOrbEntity orb) {
        if (orb == null || orb.isRemoved() || !(orb.getWorld() instanceof ServerWorld world))
            return false;

        if (!tryCaptureExperience(world, orb.getPos(), experienceAmount(orb)))
            return false;

        orb.discard();
        return true;
    }

    public static boolean tryCaptureExistingItem(ItemEntity item) {
        if (item == null || item.isRemoved() || item.getStack().isEmpty()
                || !(item.getWorld() instanceof ServerWorld world))
            return false;
        if (((HomeCaptureAware) item).ait$isHomeCaptureExcluded())
            return false;

        if (!tryCaptureItem(world, item))
            return false;

        item.discard();
        return true;
    }

    public static void excludeFromItemCapture(ItemEntity item) {
        if (item != null)
            ((HomeCaptureAware) item).ait$setHomeCaptureExcluded(true);
    }

    private static boolean tryCaptureItem(ServerWorld world, ItemEntity item) {
        if (ROUTING_ITEM.get())
            return false;

        return tryCandidates(ENDER, world, item.getPos(), module -> {
            ROUTING_ITEM.set(true);
            try {
                return module.captureItem(item);
            } finally {
                ROUTING_ITEM.set(false);
            }
        });
    }

    private static long experienceAmount(ExperienceOrbEntity orb) {
        long amount = orb.getExperienceAmount();
        if (orb instanceof dev.amble.ait.mixin.server.ExperienceOrbAccessor accessor)
            amount *= Math.max(1, accessor.ait$getPickingCount());
        return amount;
    }

    private static <T extends HomeBoundSubSystem> boolean tryCandidates(
            WeakModuleRegistry<T> registry, ServerWorld world, Vec3d position, Predicate<T> action) {
        List<T> modules = registry.candidates(world, position);
        if (modules.isEmpty())
            return false;

        List<Candidate<T>> candidates = new ArrayList<>(modules.size());
        for (T module : modules) {
            double distance = distanceSquared(module, world, position);
            if (Double.isFinite(distance))
                candidates.add(new Candidate<>(module, distance));
        }
        candidates.sort((first, second) -> {
            int distance = Double.compare(first.distanceSquared(), second.distanceSquared());
            return distance != 0 ? distance : compareIds(first.module(), second.module());
        });
        for (Candidate<T> candidate : candidates) {
            if (action.test(candidate.module()))
                return true;
        }
        return false;
    }

    private static int compareIds(HomeBoundSubSystem first, HomeBoundSubSystem second) {
        UUID firstId = first.tardis().getUuid();
        UUID secondId = second.tardis().getUuid();
        if (firstId == null)
            return secondId == null ? 0 : 1;
        return secondId == null ? -1 : firstId.compareTo(secondId);
    }

    private static double distanceSquared(HomeBoundSubSystem module, ServerWorld world, Vec3d position) {
        if (module == null || !module.isOperational())
            return Double.POSITIVE_INFINITY;

        ServerTardis tardis = module.serverTardis();
        if (tardis.isRemoved())
            return Double.POSITIVE_INFINITY;
        if (tardis.hasWorld() && tardis.world() == world)
            return 0;

        CachedDirectedGlobalPos home = tardis.stats().getHome();
        if (home == null || home.getWorld() != world)
            return Double.POSITIVE_INFINITY;

        double distance = position.squaredDistanceTo(Vec3d.ofCenter(home.getPos()));
        int radius = TardisHomeUtil.homeRadius();
        return distance <= (double) radius * radius ? distance : Double.POSITIVE_INFINITY;
    }

    private record Candidate<T extends HomeBoundSubSystem>(T module, double distanceSquared) {
    }

    /**
     * Spatially indexes collectors by their interior world and by a coarse cell
     * around their home. A spawn therefore only considers collectors which can
     * actually be in range instead of sorting every installed collector on the
     * server. Weak keys/references keep stopped integrated servers collectible.
     */
    private static final class WeakModuleRegistry<T extends HomeBoundSubSystem> {
        private final Map<T, Entry<T>> entries = new WeakHashMap<>();
        private final Map<ServerWorld, WorldIndex<T>> worlds = new WeakHashMap<>();

        private void register(T module) {
            if (module == null)
                return;

            if (!module.isInstalled()) {
                this.unregister(module);
                return;
            }

            Entry<T> entry = this.entries.computeIfAbsent(module, Entry::new);
            ServerTardis tardis = module.serverTardis();
            ServerWorld interior = tardis.hasWorld() ? tardis.world() : null;
            CachedDirectedGlobalPos home = tardis.stats().getHome();
            ServerWorld homeWorld = home == null ? null : home.getWorld();
            Cell homeCell = homeWorld == null ? null : Cell.of(home.getPos());
            if (entry.location != null && entry.location.matches(interior, homeWorld, homeCell))
                return;

            this.removeFromIndexes(entry);
            entry.location = new Location(interior, homeWorld, homeCell);
            this.addToIndexes(entry);
        }

        private void unregister(T module) {
            Entry<T> entry = this.entries.remove(module);
            if (entry != null)
                this.removeFromIndexes(entry);
        }

        private List<T> candidates(ServerWorld world, Vec3d position) {
            WorldIndex<T> index = this.worlds.get(world);
            if (index == null)
                return List.of();

            List<T> result = new ArrayList<>();
            collect(index.interior, result);

            Cell center = Cell.of(BlockPos.ofFloored(position));
            int radius = TardisHomeUtil.homeRadius();
            int range = Math.max(1, (radius + CELL_SIZE - 1) / CELL_SIZE);
            for (int x = center.x - range; x <= center.x + range; x++) {
                for (int y = center.y - range; y <= center.y + range; y++) {
                    for (int z = center.z - range; z <= center.z + range; z++) {
                        List<Entry<T>> cell = index.cells.get(new Cell(x, y, z));
                        if (cell != null)
                            collect(cell, result);
                    }
                }
            }
            return result;
        }

        private static <T extends HomeBoundSubSystem> void collect(List<Entry<T>> entries, List<T> result) {
            Iterator<Entry<T>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                T module = iterator.next().module.get();
                if (module == null) {
                    iterator.remove();
                    continue;
                }
                if (!module.isInstalled() || containsIdentity(result, module))
                    continue;
                result.add(module);
            }
        }

        private static <T> boolean containsIdentity(List<T> values, T candidate) {
            for (T value : values) {
                if (value == candidate)
                    return true;
            }
            return false;
        }

        private void addToIndexes(Entry<T> entry) {
            Location location = entry.location;
            if (location == null)
                return;

            ServerWorld interior = location.interiorWorld.get();
            if (interior != null)
                this.worlds.computeIfAbsent(interior, ignored -> new WorldIndex<>()).interior.add(entry);

            ServerWorld home = location.homeWorld.get();
            if (home != null && location.homeCell != null) {
                this.worlds.computeIfAbsent(home, ignored -> new WorldIndex<>()).cells
                        .computeIfAbsent(location.homeCell, ignored -> new ArrayList<>()).add(entry);
            }
        }

        private void removeFromIndexes(Entry<T> entry) {
            Location location = entry.location;
            if (location == null)
                return;

            ServerWorld interior = location.interiorWorld.get();
            if (interior != null) {
                WorldIndex<T> index = this.worlds.get(interior);
                if (index != null) {
                    index.interior.remove(entry);
                    this.removeEmptyWorld(interior, index);
                }
            }

            ServerWorld home = location.homeWorld.get();
            if (home != null && location.homeCell != null) {
                WorldIndex<T> index = this.worlds.get(home);
                if (index != null) {
                    List<Entry<T>> cell = index.cells.get(location.homeCell);
                    if (cell != null) {
                        cell.remove(entry);
                        if (cell.isEmpty())
                            index.cells.remove(location.homeCell);
                    }
                    this.removeEmptyWorld(home, index);
                }
            }
        }

        private void removeEmptyWorld(ServerWorld world, WorldIndex<T> index) {
            if (index.interior.isEmpty() && index.cells.isEmpty())
                this.worlds.remove(world);
        }
    }

    private static final class Entry<T extends HomeBoundSubSystem> {
        private final WeakReference<T> module;
        private Location location;

        private Entry(T module) {
            this.module = new WeakReference<>(module);
        }
    }

    private static final class WorldIndex<T extends HomeBoundSubSystem> {
        private final List<Entry<T>> interior = new ArrayList<>();
        private final Map<Cell, List<Entry<T>>> cells = new HashMap<>();
    }

    private static final class Location {
        private final WeakReference<ServerWorld> interiorWorld;
        private final WeakReference<ServerWorld> homeWorld;
        private final Cell homeCell;

        private Location(ServerWorld interiorWorld, ServerWorld homeWorld, Cell homeCell) {
            this.interiorWorld = new WeakReference<>(interiorWorld);
            this.homeWorld = new WeakReference<>(homeWorld);
            this.homeCell = homeCell;
        }

        private boolean matches(ServerWorld interior, ServerWorld home, Cell cell) {
            return this.interiorWorld.get() == interior
                    && this.homeWorld.get() == home
                    && java.util.Objects.equals(this.homeCell, cell);
        }
    }

    private record Cell(int x, int y, int z) {
        private static Cell of(BlockPos pos) {
            return new Cell(Math.floorDiv(pos.getX(), CELL_SIZE),
                    Math.floorDiv(pos.getY(), CELL_SIZE), Math.floorDiv(pos.getZ(), CELL_SIZE));
        }
    }
}
