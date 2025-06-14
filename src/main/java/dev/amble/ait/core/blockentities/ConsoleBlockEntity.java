package dev.amble.ait.core.blockentities;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.entities.ConsoleControlEntity;
import dev.amble.ait.core.item.ChargedZeitonCrystalItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.control.ControlTypes;
import dev.amble.ait.core.tardis.control.sequences.SequenceHandler;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.registry.impl.console.ConsoleRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;

public class ConsoleBlockEntity extends InteriorLinkableBlockEntity implements BlockEntityTicker<ConsoleBlockEntity> {

    public final List<ConsoleControlEntity> controlEntities = new ArrayList<>();
    public final AnimationState ANIM_STATE = new AnimationState();

    private boolean needsControls = true;

    private ConsoleTypeSchema type;
    private ConsoleVariantSchema variant;

    public int age;

    public ConsoleBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.CONSOLE_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void onLinked() {
        if (this.getWorld() == null || !TardisServerWorld.isTardisDimension(this.getWorld())) return;
        if (this.tardis().isEmpty())
            return;

        Tardis tardis = this.tardis().get();

        if (tardis instanceof ClientTardis)
            return;

        tardis.getDesktop().getConsolePos().add(this.pos);
        tardis.asServer().markDirty(tardis.getDesktop());
        this.markNeedsControl();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putString("type", this.getTypeSchema().id().toString());
        nbt.putString("variant", this.getVariant().id().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.setType(ConsoleRegistry.REGISTRY.get(Identifier.tryParse(nbt.getString("type"))));

        this.setVariant(ConsoleVariantRegistry.getInstance().get(Identifier.tryParse(nbt.getString("variant"))));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        if (world.getRegistryKey().equals(World.OVERWORLD)) {
            return super.toInitialChunkDataNbt();
        }
        this.markNeedsControl();
        return super.toInitialChunkDataNbt();
    }

    public ConsoleTypeSchema getTypeSchema() {
        if (type == null)
            this.setType(ConsoleRegistry.HARTNELL);

        return type;
    }

    public void setType(ConsoleTypeSchema schema) {
        this.type = schema;
        this.markDirty();
    }

    public void setVariant(ConsoleVariantSchema schema) {
        this.variant = schema;
        this.markDirty();
    }

    public ConsoleVariantSchema getVariant() {
        if (variant == null)
            this.setVariant(this.getTypeSchema().getDefaultVariant());

        return variant;
    }

    public int getAge() {
        return age;
    }

    public void useOn(World world, boolean sneaking, PlayerEntity player) {
        if (world.isClient())
            return;

        if (this.tardis().isEmpty())
            return;

        ItemStack itemStack = player.getMainHandStack();
        if (itemStack.getItem() == AITBlocks.ZEITON_CLUSTER.asItem()) {
            this.tardis().get().addFuel(15);

            if (!player.isCreative())
                itemStack.decrement(1);

            return;
        }

        if (itemStack.getItem() instanceof ChargedZeitonCrystalItem) {
            NbtCompound nbt = itemStack.getOrCreateNbt();

            if (!nbt.contains(ChargedZeitonCrystalItem.FUEL_KEY))
                return;

            this.tardis().get().addFuel(nbt.getDouble(ChargedZeitonCrystalItem.FUEL_KEY));
            nbt.putDouble(ChargedZeitonCrystalItem.FUEL_KEY, 0);
        }
    }

    @Override
    public void markRemoved() {
        this.killControls();
        super.markRemoved();
    }

    public void onBroken() {
        this.killControls();
        if (this.tardis().isEmpty())
            return;

        Tardis tardis = this.tardis().get();
        TardisDesktop desktop = tardis.getDesktop();

        desktop.getConsolePos().remove(this.pos);
        tardis.asServer().markDirty(desktop);
    }

    public void killControls() {
        controlEntities.forEach(Entity::discard);
        controlEntities.clear();
    }

    public void spawnControls() {
        BlockPos current = this.getPos();

        if (!(this.world instanceof ServerWorld serverWorld))
            return;

        if (!TardisServerWorld.isTardisDimension((ServerWorld) this.getWorld()))
            return;

        this.killControls();
        ConsoleTypeSchema consoleType = this.getTypeSchema();
        ControlTypes[] controls = consoleType.getControlTypes();

        for (ControlTypes control : controls) {
            ConsoleControlEntity controlEntity = ConsoleControlEntity.create(this.world, this.tardis().get());

            Vector3f position = current.toCenterPos().toVector3f().add(control.getOffset().x(), control.getOffset().y(),
                    control.getOffset().z());
            controlEntity.setPosition(position.x(), position.y(), position.z());
            controlEntity.setYaw(0.0f);
            controlEntity.setPitch(0.0f);

            controlEntity.setControlData(consoleType, control, this.getPos());

            serverWorld.spawnEntity(controlEntity);
            this.controlEntities.add(controlEntity);
        }

        this.needsControls = false;
    }

    public void markNeedsControl() {
        this.needsControls = true;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, ConsoleBlockEntity blockEntity) {
        if (!TardisServerWorld.isTardisDimension(world)) {
            return;
        }

        if (this.needsControls)
            this.spawnControls();

        if (!(world instanceof ServerWorld serverWorld)) {
            this.age++;

            ANIM_STATE.startIfNotRunning(this.age);
            return;
        }

        if (!TardisServerWorld.isTardisDimension((ServerWorld) this.getWorld()))
            this.markRemoved();

        if (!this.isLinked())
            return;

        SequenceHandler handler = this.tardis().get().sequence();
        TravelHandlerBase.State travelState = this.tardis().get().travel().getState();

        if (travelState == TravelHandlerBase.State.FLIGHT || travelState == TravelHandlerBase.State.MAT) {
            if (handler.hasActiveSequence() && handler.getActiveSequence() != null) {
                List<Control> sequence = handler.getActiveSequence().getControls();

                // Iterate only through entities whose controls are in the sequenceSet
                this.controlEntities.forEach(entity -> {
                    // Since we're here, the entity's control is part of the sequence
                    int index = sequence.indexOf(entity.getControl());

                    Control control = entity.getControl();
                    entity.setPartOfSequence(index != -1);
                    entity.setWasSequenced(handler.doesControlIndexMatch(control));
                    entity.setSequenceIndex(index);
                    entity.setSequenceLength(sequence.size());
                });
            } else {
                this.controlEntities.forEach(entity -> entity.setPartOfSequence(false));
            }
        }

        ServerTardis tardis = (ServerTardis) this.tardis().get();
        boolean isRiftChunk = RiftChunkManager.isRiftChunk(tardis.travel().position());

        boolean moreThanAFew = this.controlEntities.stream()
                .filter(controlEntity -> controlEntity.getDurability() <
                        ConsoleControlEntity.DurabilityStates.FULL.durability)
                .count() > 5;

        if (tardis.travel().isCrashing() || moreThanAFew) {
            serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5f, pos.getY() + 1.25,
                    pos.getZ() + 0.5f, 5, 0, 0, 0, 0.025f);

            serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5f, pos.getY() + 1.85,
                    pos.getZ() + 0.5f, 2, 0.2f, 0.5f, 0.2f, 0.01f);

            serverWorld.spawnParticles(
                    new DustColorTransitionParticleEffect(new Vector3f(0.75f, 0.75f, 0.75f),
                            new Vector3f(0.1f, 0.1f, 0.1f), 1),
                    pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f, 1, 0, 0, 0, 0.01f);
        }

        if (tardis.crash().isToxic() || tardis.crash().isUnstable()) {
            serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5f, pos.getY() + 1.25,
                    pos.getZ() + 0.5f, 5, 0, 0, 0, 0.025f);

            serverWorld.spawnParticles(ParticleTypes.CLOUD, pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f, 1,
                    0, 0.05f, 0, 0.025f);
        }

        if (tardis.crash().isToxic()) {
            serverWorld.spawnParticles(
                    new DustColorTransitionParticleEffect(new Vector3f(0.75f, 0.85f, 0.75f),
                            new Vector3f(0.15f, 0.25f, 0.15f), 1),
                    pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f, 1,
                    AITMod.RANDOM.nextBoolean() ? 0.5f : -0.5f, 3f,
                    AITMod.RANDOM.nextBoolean() ? 0.5f : -0.5f, 0.025f);
        }

        if (tardis.isRefueling() && tardis.getFuel() < FuelHandler.TARDIS_MAX_FUEL) {
            serverWorld.spawnParticles((isRiftChunk) ? ParticleTypes.FIREWORK : ParticleTypes.END_ROD,
                    pos.getX() + 0.5f, pos.getY() + 1.25, pos.getZ() + 0.5f, 1, 0, 0, 0,
                    (isRiftChunk) ? 0.05f : 0.025f);
        }
    }

    public static ConsoleTypeSchema previousConsole(ConsoleTypeSchema current) {
        List<ConsoleTypeSchema> list = ConsoleRegistry.REGISTRY.stream().toList();
        int idx = list.indexOf(current);
        int size = list.size();

        return list.get((size + idx - 1) % size);
    }

    public static ConsoleTypeSchema nextConsole(ConsoleTypeSchema current) {
        List<ConsoleTypeSchema> list = ConsoleRegistry.REGISTRY.stream().toList();
        int idx = list.indexOf(current);
        int size = list.size();

        return list.get((idx + 1) % size);
    }

    public static ConsoleVariantSchema previousVariant(ConsoleVariantSchema current) {
        List<ConsoleVariantSchema> list = ConsoleVariantRegistry.withParent(current.parent());
        int idx = list.indexOf(current);
        int size = list.size();

        return list.get((size + idx - 1) % size);
    }

    public static ConsoleVariantSchema nextVariant(ConsoleVariantSchema current) {
        List<ConsoleVariantSchema> list = ConsoleVariantRegistry.withParent(current.parent());
        int idx = list.indexOf(current);
        int size = list.size();

        return list.get((idx + 1) % size);
    }
}
