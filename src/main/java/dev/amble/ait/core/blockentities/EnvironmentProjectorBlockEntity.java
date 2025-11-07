package dev.amble.ait.core.blockentities;

import static dev.amble.ait.core.blocks.EnvironmentProjectorBlock.*;

import java.util.Iterator;

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
    private Direction currentDirection = Direction.NORTH;

    public EnvironmentProjectorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ENVIRONMENT_PROJECTOR_BLOCK_ENTITY_TYPE, pos, state);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos) {
        boolean powered = world.isReceivingRedstonePower(pos);

        if (powered != state.get(POWERED)) {
            if (state.get(ENABLED) != powered) {
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
//            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
            AITMod.sendProjectorToggle(pos, state.get(ENABLED));
//            EnvironmentProjectorBlock.toggle(tardis, null, world, pos, state, state.get(ENABLED));
        }

        return ActionResult.SUCCESS;
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.current = RegistryKey.of(RegistryKeys.WORLD, new Identifier(nbt.getString("dimension")));

        if (nbt.contains("direction")) {
            try {
                this.currentDirection = Direction.valueOf(nbt.getString("direction"));
            } catch (IllegalArgumentException e) {
                this.currentDirection = Direction.NORTH;
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putString("dimension", this.current.getValue().toString());
        if (this.currentDirection != null) {
            nbt.putString("direction", this.currentDirection.name());
        }
    }

    public void switchSkybox(Tardis tardis, BlockState state, PlayerEntity player) {
        ServerWorld next = findNext(this.current);

        while (TardisServerWorld.isTardisDimension(next)) {
            next = findNext(next.getRegistryKey());
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
        tardis.stats().skyboxDirection().set(state.get(EnvironmentProjectorBlock.FACING));
    }

    public void disable(Tardis tardis) {
        Value<RegistryKey<World>> value = tardis.stats().skybox();

        if (same(this.current, value.get()))
            value.set(DEFAULT);
    }

    private static ServerWorld findNext(RegistryKey<World> last) {
        Iterator<ServerWorld> iter = WorldUtil.getProjectorWorlds().iterator();

        ServerWorld first = iter.next();
        ServerWorld found = first;

        while (iter.hasNext()) {
            if (same(found.getRegistryKey(), last)) {
                if (!iter.hasNext())
                    break;

                return iter.next();
            }

            found = iter.next();
        }

        return first;
    }

    private static boolean same(RegistryKey<World> a, RegistryKey<World> b) {
        return a == b || a.getValue().equals(b.getValue());
    }

    public void switchDirectionRotation(Direction direction) {
        if (this.world instanceof ServerWorld serverWorld) {
            BlockState state = serverWorld.getBlockState(this.pos);
            BlockState modifiedState = state.with(EnvironmentProjectorBlock.FACING, direction);
            serverWorld.setBlockState(this.pos, modifiedState, Block.NOTIFY_ALL);
            this.currentDirection = direction;
            this.markDirty();

            if (modifiedState.get(EnvironmentProjectorBlock.ENABLED)) {
                Tardis tardis = this.tardis().get();
                if (tardis != null) {
                    tardis.stats().skyboxDirection().set(modifiedState.get(EnvironmentProjectorBlock.FACING));
                }
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

    public void setDirectionFromClient(Direction direction, ServerPlayerEntity player) {
        this.currentDirection = direction;
        this.markDirty();

        if (this.world instanceof ServerWorld serverWorld) {
            BlockState state = serverWorld.getBlockState(this.pos);
            BlockState modifiedState = state.with(EnvironmentProjectorBlock.FACING, direction);
            serverWorld.setBlockState(this.pos, modifiedState, Block.NOTIFY_ALL);

            if (modifiedState.get(EnvironmentProjectorBlock.ENABLED)) {
                Tardis tardis = this.tardis().get();
                if (tardis != null) {
                    switchDirectionRotation(this.currentDirection);
                }
            }

            serverWorld.updateListeners(this.pos, modifiedState, modifiedState, Block.NOTIFY_ALL);
        }
    }

}
