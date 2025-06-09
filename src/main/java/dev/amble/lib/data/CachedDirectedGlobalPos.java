package dev.amble.lib.data;


import dev.amble.ait.data.Exclude;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CachedDirectedGlobalPos extends DirectedGlobalPos {

    @Exclude
    private ServerWorld world;

    private CachedDirectedGlobalPos(RegistryKey<World> key, BlockPos pos, byte rotation) {
        super(key, pos, rotation);
    }

    private CachedDirectedGlobalPos(ServerWorld world, BlockPos pos, byte rotation) {
        this(world.getRegistryKey(), pos, rotation);
        this.world = world;
    }

    public static CachedDirectedGlobalPos create(ServerWorld world, BlockPos pos, byte rotation) {
        return new CachedDirectedGlobalPos(world, pos, rotation);
    }

    public static CachedDirectedGlobalPos create(RegistryKey<World> world, BlockPos pos, byte rotation) {
        return new CachedDirectedGlobalPos(world, pos, rotation);
    }

    private static CachedDirectedGlobalPos createSame(ServerWorld world, RegistryKey<World> dimension, BlockPos pos, byte rotation) {
        if (world == null)
            return new CachedDirectedGlobalPos(dimension, pos, rotation);

        return CachedDirectedGlobalPos.create(world, pos, rotation);
    }

    private static CachedDirectedGlobalPos createNew(ServerWorld lastWorld, RegistryKey<World> newWorldKey, BlockPos pos,
                                                     byte rotation) {
        if (lastWorld == null)
            return new CachedDirectedGlobalPos(newWorldKey, pos, rotation);

        ServerWorld newWorld = lastWorld;

        if (lastWorld.getRegistryKey() != newWorldKey)
            newWorld = lastWorld.getServer().getWorld(newWorldKey);

        return CachedDirectedGlobalPos.create(newWorld, pos, rotation);
    }

    public void init(MinecraftServer server) {
        if (this.world == null)
            this.world = server.getWorld(this.getDimension());
    }

    public ServerWorld getWorld() { // TODO - this is often null
        return world;
    }

    @Override
    public CachedDirectedGlobalPos offset(int x, int y, int z) {
        return pos(this.getPos().add(x, y, z));
    }

    @Override
    public CachedDirectedGlobalPos world(RegistryKey<World> dimension) {
        return CachedDirectedGlobalPos.createNew(this.world, dimension, this.getPos(), this.getRotation());
    }

    @Override
    public CachedDirectedGlobalPos pos(BlockPos pos) {
        return CachedDirectedGlobalPos.createSame(this.world, this.getDimension(), pos, this.getRotation());
    }

    @Override
    public CachedDirectedGlobalPos pos(int x, int y, int z) {
        return pos(new BlockPos(x, y, z));
    }

    @Override
    public CachedDirectedGlobalPos rotation(byte rotation) {
        return CachedDirectedGlobalPos.createSame(this.world, this.getDimension(), this.getPos(), rotation);
    }

    public CachedDirectedGlobalPos world(ServerWorld world) {
        return CachedDirectedGlobalPos.create(world, this.getPos(), this.getRotation());
    }

    public static CachedDirectedGlobalPos fromNbt(NbtCompound compound) {
        BlockPos pos = NbtHelper.toBlockPos(compound);
        RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD,
                new Identifier(compound.getString("dimension")));

        byte rotation = compound.getByte("rotation");
        return createNew(null, dimension, pos, rotation);
    }

    public static CachedDirectedGlobalPos read(PacketByteBuf buf) {
        RegistryKey<World> registryKey = buf.readRegistryKey(RegistryKeys.WORLD);
        BlockPos blockPos = buf.readBlockPos();
        byte rotation = buf.readByte();

        return CachedDirectedGlobalPos.createNew(null, registryKey, blockPos, rotation);
    }
}
