package dev.amble.ait.tardis.v2.event;

import dev.amble.ait.api.tardis.v2.event.TEvent;
import dev.amble.ait.api.tardis.v2.event.TEvents;
import dev.amble.ait.tardis.v2.Tardis;
import net.minecraft.server.MinecraftServer;

import java.lang.ref.WeakReference;

/**
 * General server events.
 */
public interface ServerEvents extends TEvents {

    Holder<ServerEvents> HOLDER = new Holder<>(ServerEvents.class);

    default void event$startServerTick(MinecraftServer server) { }

    default void event$endServerTick(MinecraftServer server) { }

    default void event$tardisTick(Tardis tardis, MinecraftServer server) { }

    interface ServerEvents$Event extends TEvent.Notify<ServerEvents> {

        @Override
        default Holder<ServerEvents> handler() {
            return HOLDER;
        }
    }

    record StartServerTick(WeakReference<MinecraftServer> server) implements ServerEvents$Event {

        // micro-optimization :D
        private static StartServerTick instance;

        private StartServerTick(MinecraftServer server) {
            this(new WeakReference<>(server));
        }

        public static StartServerTick get(MinecraftServer server) {
            if (instance != null && instance.server.refersTo(server))
                return instance;

            return instance = new StartServerTick(server);
        }

        @Override
        public void handle(ServerEvents handler) {
            handler.event$startServerTick(server.get());
        }
    }

    record EndServerTick(WeakReference<MinecraftServer> server) implements ServerEvents$Event {

        // micro-optimization :D
        private static EndServerTick instance;

        private EndServerTick(MinecraftServer server) {
            this(new WeakReference<>(server));
        }

        public static EndServerTick get(MinecraftServer server) {
            if (instance != null && instance.server.refersTo(server))
                return instance;

            return instance = new EndServerTick(server);
        }

        @Override
        public void handle(ServerEvents handler) {
            handler.event$endServerTick(server.get());
        }
    }

    record TardisTick(Tardis tardis, MinecraftServer server) implements ServerEvents$Event {

        @Override
        public void handle(ServerEvents handler) {
            handler.event$tardisTick(tardis, server);
        }
    }
}
