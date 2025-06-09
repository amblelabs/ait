package dev.amble.ait.core.blockentities;

import dev.amble.ait.core.AITBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.function.Function;

import static dev.amble.ait.core.blocks.RadioBlock.*;
import static java.lang.Double.NaN;

public class AITRadioBlockEntity extends BlockEntity {

    public PlayerEntity player;
    public float tickRotT, tickRotV;

    private int timeInSeconds;

    public AITRadioBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.AIT_RADIO_BLOCK_ENTITY_TYPE, pos, state);
        setTickRot("tuner", 0);
        setTickRot("volume", 0);
        toggleRadio(true);
    }

    public static void tick(World world1, BlockPos pos, BlockState state, AITRadioBlockEntity be) {
        be.timeInSeconds++;
        if (be.getTickRot("volume") > 0 && be.hasSecondPassed()) {
            be.timeInSeconds = 0;
            be.spawnNoteParticle(world1, pos);
        }
    }

    public boolean hasSecondPassed() {
        return this.timeInSeconds >= 20;
    }

    private void spawnNoteParticle(World world, BlockPos pos) {
        if (world instanceof ServerWorld serverWorld) {
            Vec3d vec3d = Vec3d.ofBottomCenter(pos).add(0.0, 1.2f, 0.0);
            serverWorld.spawnParticles(ParticleTypes.NOTE, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0, NaN, 0.0, 0.0,
                    1.0F);
        }
    }

    public void useOn(BlockHitResult hit, BlockState state, PlayerEntity player, World world, boolean isSneaking) {
        this.player = player;
        double mouseX = (hit.getPos().x * 16) - (hit.getBlockPos().getX() * 16);
        double mouseY = (hit.getPos().y * 16) - (hit.getBlockPos().getY() * 16);
        double mouseZ = (hit.getPos().z * 16) - (hit.getBlockPos().getZ() * 16);

        double[] xTuner = {0, 0, PZ_AXIS_TUNER.getMin(Direction.Axis.X), NZ_AXIS_TUNER.getMin(Direction.Axis.X),
                PX_AXIS_TUNER.getMin(Direction.Axis.X), NX_AXIS_TUNER.getMin(Direction.Axis.X)};
        double[] yTuner = {0, 0, PZ_AXIS_TUNER.getMin(Direction.Axis.Y), NZ_AXIS_TUNER.getMin(Direction.Axis.Y),
                PX_AXIS_TUNER.getMin(Direction.Axis.Y), NX_AXIS_TUNER.getMin(Direction.Axis.Y)};
        double[] zTuner = {0, 0, PZ_AXIS_TUNER.getMin(Direction.Axis.Z), NZ_AXIS_TUNER.getMin(Direction.Axis.Z),
                PX_AXIS_TUNER.getMin(Direction.Axis.Z), NX_AXIS_TUNER.getMin(Direction.Axis.Z)};

        double[] xVolume = {0, 0, PZ_AXIS_VOLUME.getMin(Direction.Axis.X), NZ_AXIS_VOLUME.getMin(Direction.Axis.X),
                PX_AXIS_VOLUME.getMin(Direction.Axis.X), NX_AXIS_VOLUME.getMin(Direction.Axis.X)};
        double[] yVolume = {0, 0, PZ_AXIS_VOLUME.getMin(Direction.Axis.Y), NZ_AXIS_VOLUME.getMin(Direction.Axis.Y),
                PX_AXIS_VOLUME.getMin(Direction.Axis.Y), NX_AXIS_VOLUME.getMin(Direction.Axis.Y)};
        double[] zVolume = {0, 0, PZ_AXIS_VOLUME.getMin(Direction.Axis.Z), NZ_AXIS_VOLUME.getMin(Direction.Axis.Z),
                PX_AXIS_VOLUME.getMin(Direction.Axis.Z), NX_AXIS_VOLUME.getMin(Direction.Axis.Z)};

        double width = 2;
        double height = 2;
        double length = 2;
        float multiVal = 36; // 22.5;

        boolean tmx = mouseX >= (xTuner[state.get(FACING).ordinal()] * 16)
                && mouseY >= (yTuner[state.get(FACING).ordinal()] * 16)
                && mouseZ >= (zTuner[state.get(FACING).ordinal()] * 16)
                && mouseX <= ((xTuner[state.get(FACING).ordinal()] * 16) + width)
                && mouseY <= ((yTuner[state.get(FACING).ordinal()] * 16) + height)
                && mouseZ <= ((zTuner[state.get(FACING).ordinal()] * 16) + length);

        boolean vmx = mouseX >= (xVolume[state.get(FACING).ordinal()] * 16)
                && mouseY >= (yVolume[state.get(FACING).ordinal()] * 16)
                && mouseZ >= (zVolume[state.get(FACING).ordinal()] * 16)
                && mouseX <= ((xVolume[state.get(FACING).ordinal()] * 16) + width)
                && mouseY <= ((yVolume[state.get(FACING).ordinal()] * 16) + height)
                && mouseZ <= ((zVolume[state.get(FACING).ordinal()] * 16) + length);

        if (tmx && this.isRadioOn() && !isSneaking) {
            if (this.tickRotT < (360F - 22.5F) * ((float) Math.PI / 180f))
                this.tickRotT = this.tickRotT + 22.5F * ((float) Math.PI / 180f);
            else if (this.tickRotT >= (360F - 22.5F) * ((float) Math.PI / 180f))
                this.tickRotT = 0;
            //System.out.println(this.tickRotT + " ?=? " + 360F * ((float) Math.PI / 180f));
            if (player != null)
                player.sendMessage(Text.literal("Changing Frequency..."), true);
            world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 0.1F,
                    this.tickRotT * this.tickRotT);
        }
        if (vmx && this.isRadioOn() && !isSneaking) {
            if (this.tickRotV <= (360F - multiVal) * ((float) Math.PI / 180f))
                this.tickRotV = this.tickRotV + multiVal * ((float) Math.PI / 180f);
            else if (this.tickRotV <= 360F * ((float) Math.PI / 180f))
                this.tickRotV = 0;

            world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 0.1F,
                    this.tickRotV * this.tickRotV);
        }

        if (!isSneaking && tmx && this.isRadioOn())
            this.setTickRot("tuner", this.tickRotT);

        if (!isSneaking && vmx && this.isRadioOn())
            this.setTickRot("volume", this.tickRotV);

        // @TODO Use SoundInstance and Minecraft.getInstance().getSoundManager() to play
        // music. It
        // allows for multiple
        // values like looping and stopping the audio. It's perfect for our use case. -
        // Loqor
        // @Creativious
        if (((int) (Math.nextUp(this.getTickRot("volume") * (180 / Math.PI) * 11) / 360) + 1) == 10) {
            // world.playSound(null, pos, AITSounds.SECRET_MUSIC, SoundCategory.MASTER, 1F, 1F);
        }

        if (this.isRadioOn()) {
            if (isSneaking && !vmx && !tmx) {
                this.toggleRadio(!this.isRadioOn());
                if (player != null)
                    player.sendMessage(Text.literal("Radio Off"), true);
                world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.MASTER, 0.2F, 1F);
            }
        } else {
            this.toggleRadio(!this.isRadioOn());
            if (player != null)
                player.sendMessage(Text.literal("Radio On"), true);
            world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.MASTER, 0.2F, 2F);
        }
    }

    public float getTickRot(String name) {
        return switch (name) {
            case "tuner" -> tickRotT;
            case "volume" -> tickRotV;
            default -> throw new IllegalArgumentException("Unexpected value: " + name);
        };
    }

    public boolean isRadioOn() {
        return true;
    }

    public void setTickRot(String name, float value) {
        if (name.equals("tuner"))
            this.tickRotT = value;
        else if (name.equals("volume"))
            this.tickRotV = value;
    }

    public void toggleRadio(boolean bool) {
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    record Handle(Function<Direction, VoxelShape> shape) {

        public boolean check(BlockHitResult hit, BlockState state) {
            double mouseX = (hit.getPos().x * 16) - (hit.getBlockPos().getX() * 16);
            double mouseY = (hit.getPos().y * 16) - (hit.getBlockPos().getY() * 16);
            double mouseZ = (hit.getPos().z * 16) - (hit.getBlockPos().getZ() * 16);

            VoxelShape s = shape.apply(state.get(FACING));

            double minX = s.getMin(Direction.Axis.X);
            double maxX = s.getMax(Direction.Axis.X);
            double minY = s.getMin(Direction.Axis.Y);
            double maxY = s.getMax(Direction.Axis.Y);
            double minZ = s.getMin(Direction.Axis.Z);
            double maxZ = s.getMax(Direction.Axis.Z);

            // DOWN: >= 0 <= 0 + 2
            // UP: >= 16 <= 18
            //
            boolean checkX = mouseX >= (minX * 16) && mouseX <= (maxX * 16);

            boolean checkY = mouseY >= (minY * 16) && mouseY <= (maxY * 16);

            boolean checkZ = mouseZ >= (minZ * 16) && mouseZ <= (maxZ * 16);

            return checkX && checkY && checkZ;
        }
    }
}
