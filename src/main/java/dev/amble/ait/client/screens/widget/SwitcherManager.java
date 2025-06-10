package dev.amble.ait.client.screens.widget;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import dev.amble.ait.api.Nameable;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.sounds.ClientSoundManager;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.sounds.flight.FlightSound;
import dev.amble.ait.core.sounds.flight.FlightSoundRegistry;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;
import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.core.tardis.handler.ServerHumHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.vortex.reference.VortexReference;
import dev.amble.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import dev.amble.ait.data.hum.Hum;
import dev.amble.ait.registry.impl.HumRegistry;

public class SwitcherManager<T extends Nameable, U> implements Nameable {

    private final Function<T, T> next;
    private final Function<T, T> previous;
    private final BiConsumer<T, U> sync;
    private T current;
    protected final String id;

    public SwitcherManager(Function<T, T> next, Function<T, T> previous, BiConsumer<T, U> sync, T current, String id) {
        this.next = next;
        this.previous = previous;
        this.sync = sync;
        this.current = current;
        this.id = id.toLowerCase();
    }

    public void next() {
        this.current = this.next.apply(this.current);
    }

    public void previous() {
        this.current = this.previous.apply(this.current);
    }

    public void sync(U arg) {
        this.sync.accept(this.current, arg);
    }

    public T get() {
        return this.current;
    }

    @Override
    public String name() {
        return id;
    }

    public static class HumSwitcher extends SwitcherManager<Hum, ClientTardis> {

        public HumSwitcher(Hum current) {
            super(HumSwitcher::next, HumSwitcher::previous, HumSwitcher::sync, current, "hum");
        }

        public HumSwitcher(Tardis tardis) {
            this(tardis.<ServerHumHandler>handler(TardisComponent.Id.HUM).get());
        }

        private static Hum next(Hum current) {
            List<Hum> list = HumRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            idx = (idx + 1) % list.size();
            return list.get(idx);
        }

        private static Hum previous(Hum current) {
            List<Hum> list = HumRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            idx = (idx - 1 + list.size()) % list.size();
            return list.get(idx);
        }

        private static void sync(Hum current, ClientTardis tardis) {
            ClientSoundManager.getHum().setServersHum(tardis, current);
        }
    }

    public static class VortexSwitcher extends SwitcherManager<VortexReference, ClientTardis> {

        public VortexSwitcher(VortexReference current) {
            super(VortexSwitcher::next, VortexSwitcher::previous, VortexSwitcher::sync, current, "vortex");
        }

        public VortexSwitcher(Tardis tardis) {
            this(tardis.stats().getVortexEffects());
        }

        private static VortexReference next(VortexReference current) {
            List<VortexReference> list = VortexReferenceRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            idx = (idx + 1) % list.size();
            return list.get(idx);
        }

        private static VortexReference previous(VortexReference current) {
            List<VortexReference> list = VortexReferenceRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            idx = (idx - 1 + list.size()) % list.size();
            return list.get(idx);
        }

        private static void sync(VortexReference current, ClientTardis tardis) {
            tardis.stats().setVortexEffects(current);
        }
    }

    public static class AnimationSwitcher extends SwitcherManager<TardisAnimation, ClientTardis> {
        public final TravelHandlerBase.State target;

        protected AnimationSwitcher(TardisAnimation current, TravelHandlerBase.State target) {
            super((var) -> next(var, target), (var) -> previous(var, target), AnimationSwitcher::sync, current, target.name());

            this.target = target;
        }

        public AnimationSwitcher(Tardis tardis, TravelHandlerBase.State target) {
            this(TardisAnimationRegistry.getInstance().getOrFallback(tardis.travel().getAnimationIdFor(target)), target);
        }

        private static TardisAnimation next(TardisAnimation current, TravelHandlerBase.State target) {
            TardisAnimation found = current;

            while (found == null || found.getExpectedState() != target || found == current) {
                found = nextOfAnyState(found);
            }

            return found;
        }
        private static TardisAnimation nextOfAnyState(TardisAnimation current) {
            List<TardisAnimation> list = TardisAnimationRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            idx = (idx + 1) % list.size();
            return list.get(idx);
        }

        private static TardisAnimation previous(TardisAnimation current, TravelHandlerBase.State target) {
            TardisAnimation found = current;

            while (found == null || found.getExpectedState() != target || found == current) {
                found = previousOfAnyState(found);
            }

            return found;
        }
        private static TardisAnimation previousOfAnyState(TardisAnimation current) {
            List<TardisAnimation> list = TardisAnimationRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            idx = (idx - 1 + list.size()) % list.size();
            return list.get(idx);
        }

        private static void sync(TardisAnimation current, ClientTardis tardis) {
            tardis.travel().setAnimationFor(current.getExpectedState(), current.id());
        }
    }

    public static class FlightSoundSwitcher extends SwitcherManager<FlightSound, ClientTardis> {
        protected FlightSoundSwitcher(FlightSound current) {
            super(FlightSoundSwitcher::next, FlightSoundSwitcher::previous, FlightSoundSwitcher::sync, current, "flight");
        }
        public FlightSoundSwitcher(Tardis tardis) {
            this(tardis.stats().getFlightEffects());
        }

        private static FlightSound next(FlightSound current) {
            List<FlightSound> list = FlightSoundRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            idx = (idx + 1) % list.size();
            return list.get(idx);
        }

        private static FlightSound previous(FlightSound current) {
            List<FlightSound> list = FlightSoundRegistry.getInstance().toList();

            int idx = list.indexOf(current);
            idx = (idx - 1 + list.size()) % list.size();
            return list.get(idx);
        }

        private static void sync(FlightSound current, ClientTardis tardis) {
            tardis.stats().setFlightEffects(current);
        }
    }

    public static class ModeManager extends SwitcherManager<SwitcherManager<?, ClientTardis>, ClientTardis> {

        public ModeManager(Tardis tardis) {
            super((var) -> next(var, tardis), (var) -> previous(var, tardis), ModeManager::sync, new HumSwitcher(tardis), "mode");
        }

        private static SwitcherManager<?, ClientTardis> next(SwitcherManager<?, ClientTardis> current, Tardis tardis) {
            return switch (current.id) {
                case "hum" -> new VortexSwitcher(tardis);
                case "vortex" -> new FlightSoundSwitcher(tardis);
                case "flight" -> new AnimationSwitcher(tardis, TravelHandlerBase.State.DEMAT);
                case "demat" -> new AnimationSwitcher(tardis, TravelHandlerBase.State.MAT);
                default -> new HumSwitcher(tardis);
            };
        }
        private static SwitcherManager<?, ClientTardis> previous(SwitcherManager<?, ClientTardis> current, Tardis tardis) {
            return switch (current.id) {
                case "hum" -> new AnimationSwitcher(tardis, TravelHandlerBase.State.MAT);
                case "vortex" -> new HumSwitcher(tardis);
                case "flight" -> new VortexSwitcher(tardis);
                case "demat" -> new FlightSoundSwitcher(tardis);
                default -> new AnimationSwitcher(tardis, TravelHandlerBase.State.DEMAT);
            };
        }

        private static void sync(SwitcherManager<?, ClientTardis> current, ClientTardis object) {
            current.sync(object);
        }
    }
}
