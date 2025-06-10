package dev.amble.ait.core.engine.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.amble.lib.util.ServerLifecycleHooks;
import org.joml.Vector3f;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelUtil;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.Exclude;

public class EngineSystem extends DurableSubSystem {
    @Exclude(strategy = Exclude.Strategy.FILE)
    private Status status;
    @Exclude(strategy = Exclude.Strategy.FILE)
    private Phaser phaser;

    public EngineSystem() {
        super(Id.ENGINE);
    }

    @Override
    public Item asItem() {
        return AITBlocks.ENGINE_BLOCK.asItem();
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        this.tardis().fuel().enablePower(true);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        this.tardis().fuel().disablePower();
    }

    @Override
    protected float cost() {
        return 1f;
    }

    @Override
    protected int changeFrequency() {
        return 1200; // drain 0.05 durability every minute
    }

    @Override
    protected boolean shouldDurabilityChange() {
        return tardis.fuel().hasPower();
    }

    @Override
    public void tick() {
        super.tick();

        this.tickForDurability();
        this.phaser().tick();
        this.tryUpdateStatus();
    }

    public Status status() {
        if (this.status == null) this.status = Status.OKAY;

        return this.status;
    }

    private void tryUpdateStatus() {
        if (ServerLifecycleHooks.get() == null) return;
        if (ServerLifecycleHooks.get().getTicks() % 40 != 0) return;

        this.status = Status.from(this);
        this.sync();
    }

    private void tickForDurability() {
        if (this.durability() <= 5) {
            this.tardis.alarm().enabled().set(true);
        }
    }

    @Override
    public List<ItemStack> toStacks() {
        List<ItemStack> stacks = new ArrayList<>();

        stacks.add(AITBlocks.ENGINE_BLOCK.asItem().getDefaultStack());

        return stacks;
    }

    public Phaser phaser() {
        if (this.phaser == null) this.phaser = Phaser.create(this);

        return this.phaser;
    }

    public static class Phaser {
        @Exclude(strategy = Exclude.Strategy.NETWORK)
        private final Function<Phaser, Boolean> allowed;
        @Exclude(strategy = Exclude.Strategy.NETWORK)
        private final Consumer<Phaser> miss;
        @Exclude(strategy = Exclude.Strategy.NETWORK)
        private final Consumer<Phaser> start;
        @Exclude(strategy = Exclude.Strategy.NETWORK)
        private final Consumer<Phaser> cancel;
        private int countdown;
        private int initial;

        public Phaser(Consumer<Phaser> onStart, Consumer<Phaser> onMiss, Consumer<Phaser> onCancel, Function<Phaser, Boolean> canPhase) {
            this.countdown = 0;
            this.miss = onMiss;
            this.allowed = canPhase;
            this.start = onStart;
            this.cancel = onCancel;
        }

        public void tick() {
            if (this.countdown > 0) {
                this.countdown--;
                if (this.countdown == 0) {
                    this.miss.accept(this);
                }
            } else {
                this.attempt();
            }
        }

        private void attempt() {
            if (this.allowed.apply(this)) {
                this.start();
            }
        }

        public void start() {
            this.initial = AITMod.RANDOM.nextInt(600, 1200);
            this.countdown = this.initial;
            this.start.accept(this);
        }

        public boolean isPhasing() {
            return this.countdown > 0;
        }

        public void cancel() {
            this.cancel.accept(this);
            this.countdown = 0;
        }

        public static Phaser create(EngineSystem system) {
            Tardis sTardis = system.tardis();
            TravelHandler travel = sTardis.travel();

            return new Phaser(
                    (phaser) -> {
                        ServerTardis tdis = sTardis.asServer();
                        TardisUtil.sendMessageToLinked(tdis, Text.translatable("tardis.message.engine.phasing").formatted(Formatting.RED));
                        tdis.alarm().enable(Text.translatable("tardis.message.engine.phasing").formatted(Formatting.RED));
                        tdis.getDesktop().playSoundAtEveryConsole(AITSounds.HOP_DEMAT);
                        tdis.getExterior().playSound(AITSounds.HOP_DEMAT);
                        sTardis.subsystems().demat().removeDurability(5);
                    },
                    (phaser) -> {
                        TravelUtil.randomPos(sTardis, 1, 300, cached -> {
                            travel.forceDestination(cached);
                            if (travel.isLanded()) {
                                sTardis.subsystems().demat().removeDurability(15);
                                sTardis.travel().speed(500);
                                sTardis.getDesktop().playSoundAtEveryConsole(AITSounds.UNSTABLE_FLIGHT_LOOP);
                                sTardis.getExterior().playSound(AITSounds.UNSTABLE_FLIGHT_LOOP);
                                sTardis.travel().forceDemat(TardisAnimationRegistry.getInstance().instantiate(AITMod.id("crumple")), TardisAnimationRegistry.getInstance().instantiate(AITMod.id("ghost_mat"))); // TODO - make phasing anims!! - duzo
                                sTardis.travel().autopilot(false);
                            }
                            TardisEvents.ENGINES_PHASE.invoker().onPhase(system);
                        });
                    },
                    (phaser) -> {
                        SoundEvent sound = (phaser.countdown < (phaser.initial - 300)) ? AITSounds.HOP_MAT : AITSounds.LAND_THUD;
                        sTardis.getDesktop().playSoundAtEveryConsole(sound);
                        sTardis.getExterior().playSound(sound);
                        sTardis.alarm().disable();
                    },
                    (phaser) -> travel.isLanded() &&
                            sTardis.subsystems().demat().durability() < 300 &&
                            !sTardis.subsystems().demat().isBroken() &&
                            !travel.handbrake() &&
                            !sTardis.isGrowth() &&
                            AITMod.RANDOM.nextInt(0, 1024) == 1
            );
        }
    }

    public static boolean hasEngine(Tardis t) {
        return t.subsystems().engine().isEnabled();
    }

    public enum Status {
        OKAY(132, 195, 240) {
            @Override
            public boolean isViable(EngineSystem system) {
                return true;
            }
        },
        OFF(0, 0, 0) {
            @Override
            public boolean isViable(EngineSystem system) {
                return !system.tardis.fuel().hasPower();
            }
        },
        CRITICAL(250, 33, 22) {
            @Override
            public boolean isViable(EngineSystem system) {
                if (system.phaser().isPhasing())
                    return true;

                for (SubSystem next : system.tardis().subsystems()) {
                    if (next instanceof DurableSubSystem durable && next.isEnabled() && durable.durability() <= 5)
                        return true;
                }

                return false;
            }
        },
        ERROR(250, 242, 22) {
            @Override
            public boolean isViable(EngineSystem system) {
                return system.tardis.alarm().enabled().get() || system.tardis.sequence().hasActiveSequence();
            }
        },
        LEAKAGE(114, 255, 33) {
            @Override
            public boolean isViable(EngineSystem system) {
                return false;
            }
        };

        public abstract boolean isViable(EngineSystem system);
        public final Vector3f colour;

        Status(int red, int green, int blue) {
            this.colour = new Vector3f(red / 255f, green / 255f, blue / 255f);
        }

        public static Status from(EngineSystem system) {
            for (Status status : values()) {
                if (status.isViable(system) && !status.equals(OKAY)) {
                    return status;
                }
            }
            return OKAY;
        }
    }
}
