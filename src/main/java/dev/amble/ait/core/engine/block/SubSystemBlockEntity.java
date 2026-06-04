package dev.amble.ait.core.engine.block;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import dev.amble.ait.core.engine.registry.SubSystemRegistry;
import dev.amble.ait.core.util.SoundData;

public class SubSystemBlockEntity extends FluidLinkBlockEntity {
    protected SubSystem.IdLike id;

    public SubSystemBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, SubSystem.IdLike id) {
        super(type, pos, state);
        this.id = id;
    }

    public SubSystem system() {
        if (!(this.isLinked()) || this.id() == null) return null;

        return this.tardis().get().subsystems().get(this.id());
    }
    protected SubSystem.IdLike id() {
        if (this.id == null) {
            this.id = ((SubSystemBlock) this.getCachedState().getBlock()).getSystemId();
        }

        return this.id;
    }

    @Override
    public void onGainFluid() {
        super.onGainFluid();

        if (this.system() == null) return;
        if (this.system() instanceof DurableSubSystem durable) {
            if (durable.isBroken()) return;
        }
        this.system().setEnabled(true);
    }

    @Override
    public void onLoseFluid() {
        super.onLoseFluid();

        if (this.system() == null) return;
        this.system().setEnabled(false);
    }

    @Override
    protected SoundData getGainPowerSound() {
        return new SoundData(AITSounds.SIEGE_DISABLE, SoundCategory.BLOCKS, 0.25f, 1.0f);
    }

    @Override
    protected SoundData getLosePowerSound() {
        return new SoundData(AITSounds.SIEGE_ENABLE, SoundCategory.BLOCKS, 0.25f, 1.0f);
    }

    public void tick(World world, BlockPos pos, BlockState state) {}

    public ActionResult useOn(BlockState state, World world, boolean sneaking, PlayerEntity player, ItemStack hand) {
        if (this.system() instanceof DurableSubSystem durable) {
            if (!durable.isRepairItem(hand)) return ActionResult.PASS;
            player.sendMessage(Text.literal(durable.durability() + "/" + DurableSubSystem.MAX_DURABILITY).setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)), true);
            world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANCIENT_DEBRIS_HIT, SoundCategory.BLOCKS, 0.5f, 0.8f);
            if  (durable.durability() < DurableSubSystem.MAX_DURABILITY) {
                int val = world.getRandom().nextBetween(10, DurableSubSystem.MAX_DURABILITY);
                durable.addDurability(world.getRandom().nextBetween(10, DurableSubSystem.MAX_DURABILITY));
                hand.setDamage(hand.getDamage() - 1);
                world.playSound(null, this.getPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 0.5f, 1.5f);
                for (int i=0;i<(val/2);i++) {
                    world.addImportantParticle(ParticleTypes.ENCHANT, pos.getX(), pos.getY(), pos.getZ(), 0, 0.1f, 0);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if (this.id != null) {
            nbt.putString("SystemId", this.id.name());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains("SystemId")) {
            this.id = SubSystemRegistry.getInstance().get(nbt.getString("SystemId"));
        }
    }
}
