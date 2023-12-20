package mdteam.ait.tardis.handler;

import mdteam.ait.client.renderers.exteriors.ExteriorEnum;
import mdteam.ait.core.AITDesktops;
import mdteam.ait.core.AITSounds;
import mdteam.ait.core.blockentities.ExteriorBlockEntity;
import mdteam.ait.core.item.InteriorSelectItem;
import mdteam.ait.tardis.Tardis;
import mdteam.ait.tardis.TardisTravel;
import mdteam.ait.tardis.handler.properties.PropertiesHandler;
import mdteam.ait.tardis.util.AbsoluteBlockPos;
import mdteam.ait.tardis.util.TardisUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static mdteam.ait.tardis.TardisTravel.State.LANDED;

public class DoorHandler extends TardisLink {
    private boolean locked, left, right;
    private DoorStateEnum doorState = DoorStateEnum.CLOSED;

    public DoorHandler(UUID tardis) {
        super(tardis);
    }

    // Remember to markDirty for these setters!!
    public void setLeftRot(boolean var) {
        this.left = var;
        if(this.left) this.setDoorState(DoorStateEnum.FIRST);

        tardis().markDirty();
    }

    public void setRightRot(boolean var) {
        this.right = var;
        if(this.right) this.setDoorState(DoorStateEnum.SECOND);

        tardis().markDirty();
    }

    public boolean isRightOpen() {
        return this.doorState == DoorStateEnum.SECOND || doorState == DoorStateEnum.BOTH|| this.right;
    }

    public boolean isLeftOpen() {
        return this.doorState == DoorStateEnum.FIRST || doorState == DoorStateEnum.BOTH || this.left;
    }

    public void setLocked(boolean var) {
        this.locked = var;
        if(this.locked) this.setDoorState(DoorStateEnum.LOCKED);

        tardis().markDirty();
    }

    public void setLockedAndDoors(boolean var) {
        this.setLocked(var);

        this.setLeftRot(false);
        this.setRightRot(false);
    }

    public boolean locked() {
        return this.doorState == DoorStateEnum.LOCKED || this.locked;
    }

    public boolean isDoubleDoor() {
        return tardis().getExterior().getType().isDoubleDoor();
    }

    public boolean isOpen() {
        if (isDoubleDoor()) {
            return this.isRightOpen() || this.isLeftOpen();
        }

        return this.isLeftOpen();
    }

    public boolean isClosed() {
        return !isOpen();
    }

    public boolean isBothOpen() {
        return this.isRightOpen() && this.isLeftOpen();
    }

    public boolean isBothClosed() {
        return !isBothOpen();
    }

    public void openDoors() {
        setLeftRot(true);

        if (isDoubleDoor()) {
            setRightRot(true);
            this.setDoorState(DoorStateEnum.BOTH);
        }
    }

    public void closeDoors() {
        setLeftRot(false);
        setRightRot(false);
        this.setDoorState(DoorStateEnum.CLOSED);
    }

    public void setDoorState(DoorStateEnum doorState) {
        this.doorState = doorState;
    }

    public DoorStateEnum getDoorState() {
        return doorState;
    }

    public static boolean useDoor(Tardis tardis, ServerWorld world, @Nullable BlockPos pos, @Nullable ServerPlayerEntity player) {
        if (isClient()) {
            return false;
        }

        if (tardis.getHandlers().getOvergrownHandler().isOvergrown()) {
            // Bro cant escape
            if (player == null) return false;

            // if holding an axe then break off the vegetation
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
            if (stack.getItem() instanceof AxeItem) {
                player.swingHand(Hand.MAIN_HAND);
                tardis.getHandlers().getOvergrownHandler().removeVegetation();
                stack.setDamage(stack.getDamage() - 1);

                if (pos != null)
                    world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 1f, 1f);
                world.playSound(null, tardis.getDoor().getDoorPos(), SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS);

                return false;
            }

            if (pos != null) // fixme will play sound twice on interior door
                world.playSound(null, pos, AITSounds.KNOCK, SoundCategory.BLOCKS, 3f, 1f);

            world.playSound(null, tardis.getDoor().getDoorPos(), AITSounds.KNOCK, SoundCategory.BLOCKS, 3f, 1f);

            return false;
        }

        if (tardis.getLockedTardis()) {
            //if (pos != null)
                //world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_STEP, SoundCategory.BLOCKS, 0.6F, 1F);
            if (player != null) {
                player.sendMessage(Text.literal("\uD83D\uDD12"), true);
                world.playSound(null, pos, AITSounds.KNOCK, SoundCategory.BLOCKS, 3f, 1f);
                world.playSound(null, tardis.getDoor().getDoorPos(), AITSounds.KNOCK, SoundCategory.BLOCKS, 3f, 1f);
            }
            return false;
        }

        if (tardis.getTravel().getState() != LANDED)
            return false;

        DoorHandler door = tardis.getDoor();

        if (door == null) return false; // how would that happen anyway

        // fixme this is loqors code so there might be a better way
        // PLEASE FIXME ALL THIS CODE IS SO JANK I CANT
        if (tardis.getExterior().getType().isDoubleDoor()) {
            if (door.isBothOpen()) {
                world.playSound(null, door.getExteriorPos(), tardis.getExterior().getType().getDoorCloseSound(), SoundCategory.BLOCKS, 0.6F, 1F);
                world.playSound(null, door.getDoorPos(), tardis.getExterior().getType().getDoorCloseSound(), SoundCategory.BLOCKS, 0.6F, 1F);
                door.setDoorState(DoorStateEnum.CLOSED);
            } else {
                world.playSound(null, door.getExteriorPos(), tardis.getExterior().getType().getDoorOpenSound(), SoundCategory.BLOCKS, 0.6F, 1F);
                world.playSound(null, door.getDoorPos(), tardis.getExterior().getType().getDoorOpenSound(), SoundCategory.BLOCKS, 0.6F, 1F);

                if (door.isOpen() && player.isSneaking()) {
                    door.setDoorState(DoorStateEnum.CLOSED);
                } else if (door.isBothClosed() && player.isSneaking()) {
                    door.setDoorState(DoorStateEnum.BOTH);
                } else {
                    door.setDoorState(door.getDoorState().next());
                }
            }
        } else {
            world.playSound(null, door.getExteriorPos(), tardis.getExterior().getType().getDoorOpenSound(), SoundCategory.BLOCKS, 0.6F, 1F);
            world.playSound(null, door.getDoorPos(), tardis.getExterior().getType().getDoorOpenSound(), SoundCategory.BLOCKS, 0.6F, 1F);
            door.setDoorState(door.getDoorState() == DoorStateEnum.FIRST ? DoorStateEnum.CLOSED : DoorStateEnum.FIRST);
        }

        tardis.markDirty();

        return true;
    }

    public static boolean toggleLock(Tardis tardis, ServerWorld world, @Nullable ServerPlayerEntity player) {
        return lockTardis(!tardis.getLockedTardis(), tardis, world, player, false);
    }

    public static boolean lockTardis(boolean locked, Tardis tardis, ServerWorld world, @Nullable ServerPlayerEntity player, boolean forced) {
        if (!forced) {
            if (tardis.getTravel().getState() != LANDED) return false;
        }
        tardis.setLockedTardis(locked);

        DoorHandler door = tardis.getDoor();

        if (door == null)
            return false; // could have a case where the door is null but the thing above works fine meaning this false is wrong fixme

        door.setDoorState(DoorStateEnum.CLOSED);

        if (!forced) {
            PropertiesHandler.setBool(tardis.getHandlers().getProperties(), PropertiesHandler.PREVIOUSLY_LOCKED, locked);
        }

        String lockedState = tardis.getLockedTardis() ? "\uD83D\uDD12" : "\uD83D\uDD13";
        if (player != null)
            player.sendMessage(Text.literal(lockedState), true);

        world.playSound(null, door.getExteriorPos(), SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.BLOCKS, 0.6F, 1F);
        world.playSound(null, door.getDoorPos(), SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.BLOCKS, 0.6F, 1F);

        tardis.markDirty();

        return true;
    }

    public enum DoorStateEnum {
        CLOSED {
            @Override
            public DoorStateEnum next() {
                return FIRST;
            }
        },
        FIRST {
            @Override
            public DoorStateEnum next() {
                return SECOND;
            }
        },
        SECOND {
            @Override
            public DoorStateEnum next() {
                return CLOSED;
            }
        },
        BOTH {
            @Override
            public DoorStateEnum next() {
                return CLOSED;
            }
        },
        LOCKED {
            @Override
            public DoorStateEnum next() {
                return CLOSED;
            }
        };

        public abstract DoorStateEnum next();
    }
}
