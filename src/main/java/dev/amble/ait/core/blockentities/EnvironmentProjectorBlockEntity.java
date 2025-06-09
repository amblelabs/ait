package dev.amble.ait.core.blockentities;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blocks.EnvironmentProjectorBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.properties.Value;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.WorldPresetTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;

import static dev.amble.ait.core.blocks.EnvironmentProjectorBlock.*;

public class EnvironmentProjectorBlockEntity extends InteriorLinkableBlockEntity {

    private static final RegistryKey<World> DEFAULT = World.END;
    private RegistryKey<World> current = DEFAULT;

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
            this.switchSkybox(tardis, state, player);
            return ActionResult.SUCCESS;
        }

        state = state.cycle(ENABLED);
        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);

        EnvironmentProjectorBlock.toggle(tardis, null, world, pos, state, state.get(ENABLED));
        return ActionResult.SUCCESS;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.current = RegistryKey.of(RegistryKeys.WORLD, new Identifier(nbt.getString("dimension")));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putString("dimension", this.current.getValue().toString());
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
}
