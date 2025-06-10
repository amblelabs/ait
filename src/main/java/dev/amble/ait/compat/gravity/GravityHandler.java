package dev.amble.ait.compat.gravity;

import gravity_changer.EntityTags;
import gravity_changer.api.GravityChangerAPI;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Direction;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisClientEvents;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.client.screens.interior.InteriorSettingsScreen;
import dev.amble.ait.client.screens.widget.DynamicPressableTextWidget;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.registry.impl.TardisComponentRegistry;

public class GravityHandler extends KeyedTardisComponent implements TardisTickable {

    private static final Identifier SYNC = AITMod.id("sync_gravity");
    private static final Property<Direction> DIRECTION = new Property<>(Property.DIRECTION, "direction",
            Direction.DOWN);

    private final Value<Direction> direction = DIRECTION.create(this);

    @Exclude
    private Direction tempDirection = Direction.DOWN;

    public GravityHandler() {
        super(ID);
    }

    @Override
    public void onLoaded() {
        direction.of(this, DIRECTION);
        this.tempDirection = Direction.DOWN;
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 20 == 0)
            this.onTick();
    }

    private void onTick() {
        for (Entity entity : this.tardis.asServer().world().getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), EntityTags::canChangeGravity)) {
            GravityChangerAPI.getGravityComponent(entity).setBaseGravityDirection(this.direction.get());
        }
    }

    private static void syncToServer(Tardis tardis, Direction direction) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(tardis.getUuid());
        buf.writeEnumConstant(direction);

        ClientPlayNetworking.send(SYNC, buf);
    }

    private static Direction nextDirection(Direction direction) {
        Direction[] values = Direction.values();
        int size = values.length;

        int nextIndex = direction.ordinal() + 1;

        if (nextIndex == size)
            nextIndex = 0;

        return values[nextIndex];
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(SYNC,
                ServerTardisManager.receiveTardis((tardis, server, player, handler, buf, responseSender) -> {
                    if (tardis == null)
                        return;

                    GravityHandler gravity = tardis.handler(ID);
                    Direction direction = buf.readEnumConstant(Direction.class);

                    gravity.direction.set(direction);
                }));

        TardisComponentRegistry.getInstance().register(ID);

        TardisEvents.LEAVE_TARDIS.register((tardis, entity) -> GravityChangerAPI.getGravityComponent(entity)
                .setBaseGravityDirection(Direction.DOWN));
    }

    public static void clientInit() {
        TardisClientEvents.SETTINGS_SETUP.register(GravityHandler::setup);
    }

    private static void setup(InteriorSettingsScreen screen) {
        screen.<DynamicPressableTextWidget>createAnyDynamicButton(
                button -> buttonText(screen, button), (x, y, width, height, text, onPress,
                        textRenderer) -> new DynamicPressableTextWidget(x, y, 80, height, text, onPress, textRenderer),
                button -> onButton(screen, (DynamicPressableTextWidget) button));
    }

    private static Text buttonText(InteriorSettingsScreen screen, DynamicPressableTextWidget button) {
        GravityHandler gravity = screen.tardis().handler(ID);
        boolean isChanged = !button.isLeftClick() || gravity.tempDirection != gravity.direction.get();

        Direction direction = isChanged ? gravity.tempDirection : gravity.direction.get();
        Formatting formatting = isChanged ? Formatting.YELLOW : Formatting.WHITE;

        return Text.translatable("screen.ait.gravity", capitalize(direction.getName())).formatted(formatting);
    }

    private static void onButton(InteriorSettingsScreen screen, DynamicPressableTextWidget button) {
        Tardis tardis = screen.tardis();
        GravityHandler gravity = tardis.handler(GravityHandler.ID);

        if (button.isLeftClick()) {
            gravity.direction.set(gravity.tempDirection, false);
            button.refresh();

            syncToServer(tardis, gravity.tempDirection);
            return;
        }

        gravity.tempDirection = nextDirection(gravity.tempDirection);
        button.refresh();
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    static final IdLike ID = new AbstractId<>("GRAVITY", GravityHandler::new, GravityHandler.class);
}
