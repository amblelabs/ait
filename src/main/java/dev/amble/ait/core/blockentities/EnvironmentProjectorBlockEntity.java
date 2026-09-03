package dev.amble.ait.core.blockentities;

import static dev.amble.ait.core.blocks.EnvironmentProjectorBlock.*;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blocks.EnvironmentProjectorBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.properties.Value;

public class EnvironmentProjectorBlockEntity extends InteriorLinkableBlockEntity {

    private static final RegistryKey<World> DEFAULT = World.END;
    private RegistryKey<World> current = DEFAULT;
    private float currentYaw = 0f;
    private float currentPitch = 0f;

    public EnvironmentProjectorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ENVIRONMENT_PROJECTOR_BLOCK_ENTITY_TYPE, pos, state);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos) {
        boolean powered = world.isReceivingRedstonePower(pos);

        if (powered != state.get(POWERED)) {
            if (state.get(ENABLED) != powered && this.isLinked()) {
                state = state.with(ENABLED, powered);

                EnvironmentProjectorBlock.toggle(this.tardis().get(), null, world, pos, state, powered);
            }

            state = state.with(POWERED, powered);
        }

        world.setBlockState(pos, state.with(SILENT, world.getBlockState(pos.down()).isIn(BlockTags.WOOL)),
                Block.NOTIFY_LISTENERS);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!this.isLinked())
            return ActionResult.FAIL;

        Tardis tardis = this.tardis().get();

        if (player.isSneaking()) {
            state = state.cycle(ENABLED);
            AITMod.sendProjectorToggle(pos, state.get(ENABLED));
        }

        return ActionResult.SUCCESS;
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.current = RegistryKey.of(RegistryKeys.WORLD, new Identifier(nbt.getString("dimension")));

        if (nbt.contains("yaw")) {
            this.currentYaw = nbt.getFloat("yaw");
        } else if (nbt.contains("direction")) {
            try {
                Direction dir = Direction.valueOf(nbt.getString("direction"));
                this.applyLegacyDirection(dir);
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (nbt.contains("pitch")) {
            this.currentPitch = nbt.getFloat("pitch");
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putString("dimension", this.current.getValue().toString());
        nbt.putFloat("yaw", this.currentYaw);
        nbt.putFloat("pitch", this.currentPitch);
    }

    private void applyLegacyDirection(Direction dir) {
        switch (dir) {
            case UP -> { this.currentYaw = 0f; this.currentPitch = 90f; }
            case DOWN -> { this.currentYaw = 0f; this.currentPitch = -90f; }
            default -> { this.currentYaw = dir.asRotation(); this.currentPitch = 0f; }
        }
    }

    public void switchSkybox(Tardis tardis, BlockState state, PlayerEntity player) {
        ServerWorld next = findNext(this.current);

        if (next == null) {
            player.sendMessage(Text.translatableWithFallback("message.ait.projector.no_worlds",
                    "No worlds are currently available for the Environment Projector."));
            return;
        }

        player.sendMessage(Text.translatable("message.ait.projector.skybox", next.getRegistryKey().getValue().toString()));
        AITMod.LOGGER.debug("Last: {}, next: {}", this.current, next);

        this.current = next.getRegistryKey();

        if (state.get(EnvironmentProjectorBlock.ENABLED))
            this.apply(tardis, state);
    }

    public void toggle(Tardis tardis, BlockState state, boolean active) {
        if (active) {
            this.apply(tardis, state);
        } else {
            this.disable(tardis);
        }
    }

    public void apply(Tardis tardis, BlockState state) {
        tardis.stats().skybox().set(this.current);
        tardis.stats().skyboxYaw().set(this.currentYaw);
        tardis.stats().skyboxPitch().set(this.currentPitch);
    }

    public void disable(Tardis tardis) {
        Value<RegistryKey<World>> value = tardis.stats().skybox();

        if (same(this.current, value.get()))
            value.set(DEFAULT);
    }

    private static @Nullable ServerWorld findNext(RegistryKey<World> last) {
        ServerWorld first = null;
        boolean returnNext = false;

        for (ServerWorld world : WorldUtil.getProjectorWorlds()) {
            if (TardisServerWorld.isTardisDimension(world))
                continue;

            if (first == null)
                first = world;

            if (returnNext)
                return world;

            if (same(world.getRegistryKey(), last))
                returnNext = true;
        }

        return first;
    }

    private static boolean same(RegistryKey<World> a, RegistryKey<World> b) {
        return a == b || a.getValue().equals(b.getValue());
    }

    public void setAnglesFromClient(float yaw, float pitch, ServerPlayerEntity player) {
        if (!(this.world instanceof ServerWorld serverWorld))
            return;

        this.currentYaw = MathHelper.wrapDegrees(yaw);
        this.currentPitch = MathHelper.clamp(pitch, -90f, 90f);
        this.markDirty();

        BlockState state = serverWorld.getBlockState(this.pos);

        Direction nearest = Direction.fromRotation(this.currentYaw);
        if (state.get(EnvironmentProjectorBlock.FACING) != nearest) {
            state = state.with(EnvironmentProjectorBlock.FACING, nearest);
            serverWorld.setBlockState(this.pos, state, Block.NOTIFY_ALL);
        }

        if (state.get(EnvironmentProjectorBlock.ENABLED)) {
            Tardis tardis = this.tardis().get();
            if (tardis != null) {
                tardis.stats().skyboxYaw().set(this.currentYaw);
                tardis.stats().skyboxPitch().set(this.currentPitch);
            }
        }
    }

    public void setCurrentFromClient(RegistryKey<World> key, ServerPlayerEntity player) {
        this.current = key;
        this.markDirty();

        if (this.world instanceof ServerWorld serverWorld) {
            BlockState state = serverWorld.getBlockState(this.pos);
            if (state.get(EnvironmentProjectorBlock.ENABLED)) {
                Tardis tardis = this.tardis().get();
                if (tardis != null) {
                    this.apply(tardis, state);
                }
            }

            serverWorld.updateListeners(this.pos, state, state, Block.NOTIFY_ALL);
        }
    }

}
