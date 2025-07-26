package dev.amble.ait.core.tardis.handler;

import java.util.ArrayList;
import java.util.List;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import dev.amble.lib.data.DirectedGlobalPos;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITDamageTypes;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.SafePosSearch;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.integer.IntProperty;
import dev.amble.ait.data.properties.integer.IntValue;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.registry.impl.CategoryRegistry;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

public class InteriorChangingHandler extends KeyedTardisComponent implements TardisTickable {
    public static final Identifier CHANGE_DESKTOP = AITMod.id("change_desktop");
    private static final Property<Identifier> QUEUED_INTERIOR_PROPERTY = new Property<>(Property.IDENTIFIER, "queued_interior", new Identifier(""));
    private static final BoolProperty QUEUED = new BoolProperty("queued");
    private static final BoolProperty REGENERATING = new BoolProperty("regenerating");
    private static final int MIN_FUEL_COST = 5000;

    public static final int MAX_PLASMIC_MATERIAL_AMOUNT = 8;
    private static final Text HINT_TEXT = Text.translatable("tardis.message.growth.hint").formatted(Formatting.DARK_GRAY, Formatting.ITALIC);

    private final Value<Identifier> queuedInterior = QUEUED_INTERIOR_PROPERTY.create(this);
    private static final IntProperty PLASMIC_MATERIAL_AMOUNT = new IntProperty("plasmic_material_amount");
    private final IntValue plasmicMaterialAmount = PLASMIC_MATERIAL_AMOUNT.create(this);
    private static final BoolProperty HAS_CAGE = new BoolProperty("has_cage");
    private final BoolValue hasCage = HAS_CAGE.create(this);
    private final BoolValue queued = QUEUED.create(this);
    private final BoolValue regenerating = REGENERATING.create(this);

    @Exclude
    private boolean countdownStarted = false;

    @Exclude
    private List<ItemStack> restorationChestContents;

    public InteriorChangingHandler() {
        super(Id.INTERIOR);
    }

    @Override
    public void onLoaded() {
        plasmicMaterialAmount.of(this, PLASMIC_MATERIAL_AMOUNT);
        hasCage.of(this, HAS_CAGE);
        queuedInterior.of(this, QUEUED_INTERIOR_PROPERTY);
        queued.of(this, QUEUED);
        regenerating.of(this, REGENERATING);

        if (this.isServer() && this.regenerating.get()) {
            this.regenerating.set(false);

            TardisDesktopSchema queued = this.getQueuedInterior();

            if (queued == null)
                return;

            tardis.interiorChangingHandler().queueInteriorChange(queued);
        }
    }

    static {
        TardisEvents.DEMAT.register(tardis -> {
            if (tardis.isGrowth()
                    || tardis.interiorChangingHandler().queued().get())
                return TardisEvents.Interaction.FAIL;

            return TardisEvents.Interaction.PASS;
        });

        TardisEvents.MAT.register(tardis -> {
            if (!tardis.isGrowth())
                return TardisEvents.Interaction.PASS;

            tardis.travel().autopilot(false);
            tardis.getExterior().setType(CategoryRegistry.CAPSULE);
            tardis.getExterior().setVariant(ExteriorVariantRegistry.CAPSULE_DEFAULT);
            return TardisEvents.Interaction.SUCCESS; // force mat even if checks fail
        });

        TardisEvents.LOSE_POWER.register(tardis -> tardis.interiorChangingHandler().queued.set(false));

        ServerPlayNetworking.registerGlobalReceiver(InteriorChangingHandler.CHANGE_DESKTOP,
                ServerTardisManager.receiveTardis(((tardis, server, player, handler, buf, responseSender) -> {
                    TardisDesktopSchema desktop = DesktopRegistry.getInstance().get(buf.readIdentifier());

                    if (tardis == null || desktop == null)
                        return;

                    // nuh uh no interior changing during flight
                    if (tardis.travel().getState() != TravelHandler.State.LANDED)
                        return;

                    tardis.interiorChangingHandler().queueInteriorChange(desktop);
                    tardis.alarm().enable();
                })));
    }

    public BoolValue queued() {
        return queued;
    }

    public int plasmicMaterialAmount() {
        return plasmicMaterialAmount.get();
    }

    public boolean hasCage() {
        return hasCage.get();
    }

    public void setHasCage(boolean value) {
        hasCage.set(value);
    }

    public void setPlasmicMaterialAmount(int amount) {
        plasmicMaterialAmount.set(amount);
    }

    public void addPlasmicMaterial(int amount) {
        plasmicMaterialAmount.set(Math.min(plasmicMaterialAmount() + amount, MAX_PLASMIC_MATERIAL_AMOUNT));
    }

    public BoolValue regenerating() {
        return regenerating;
    }

    public TardisDesktopSchema getQueuedInterior() {
        return DesktopRegistry.getInstance().get(queuedInterior.get());
    }

    public void queueInteriorChange(TardisDesktopSchema schema) {
        if (!this.canQueue())
            return;

        if (tardis.fuel().getCurrentFuel() < (MIN_FUEL_COST * tardis.travel().instability())) {
            tardis.asServer().world().getPlayers().forEach(player -> {
                player.sendMessage(
                        Text.translatable("tardis.message.interiorchange.not_enough_fuel").formatted(Formatting.RED),
                        true);
            });

            return;
        }
        if (tardis.subsystems().isEnabled()) {
            tardis.asServer().world().getPlayers().forEach(player -> {
                int count = 0;

                for (SubSystem subSystem : tardis.subsystems()) {
                    if (subSystem.isEnabled())
                        count++;
                }

                player.sendMessage(
                        Text.translatable("tardis.message.interiorchange.subsystems_enabled", count)
                                .formatted(Formatting.RED), false);
            });
        }

        AITMod.LOGGER.info("Queueing interior change for {} to {}", this.tardis, schema);

        this.queuedInterior.set(schema.id());
        this.queued.set(true);

        TravelHandler travel = this.tardis.travel();

        if (travel.getState() == TravelHandler.State.FLIGHT && !travel.isCrashing() && !tardis.isGrowth())
            travel.crash();

        restorationChestContents = new ArrayList<>();

        for (SubSystem system : tardis.subsystems()) {
            if (system.isEnabled())
                continue;

            restorationChestContents.addAll(system.toStacks());
            AITMod.LOGGER.debug("Storing Subsystem, {} ({}) => {}", system.getId(), system.isEnabled(), system.toStacks());
        }
    }

    private void changeInterior() {
        tardis.getDesktop().changeInterior(this.getQueuedInterior(), true, true)
                .thenRun(() -> {
                    this.queued.set(false);
                    this.regenerating.set(false);

                    if (tardis.hasGrowthExterior()) {
                        TravelHandler travel = tardis.travel();

                        travel.autopilot(true);
                        travel.forceDemat();
                        this.replaceAllConsolesWithGrowth();
                    } else {
                        tardis.removeFuel(MIN_FUEL_COST * tardis.travel().instability());
                    }

                    TardisUtil.sendMessageToLinked(tardis.asServer(), Text.translatable("tardis.message.interiorchange.success", tardis.stats().getName(), tardis.getDesktop().getSchema().name()));
                    this.tardis.getDesktop().getConsolePos().stream().findFirst().ifPresent(blockPos -> {
                        System.out.println("unfortunately a bar" + restorationChestContents);
                        if (restorationChestContents == null || restorationChestContents.isEmpty()) {
                            AITMod.LOGGER.debug("No contents to save in recovery inventory in console for {}", this.tardis);
                            return;
                        }
                        if (this.tardis.asServer().world().getBlockEntity(blockPos) instanceof ConsoleBlockEntity consoleBlockEntity) {
                            consoleBlockEntity.getInventory().addAll(restorationChestContents);
                        }
                    });//createChestAtInteriorDoor(restorationChestContents);

                    ParticleEffect particle = ParticleTypes.CLOUD;
                    tardis.door().setDoorParticles(particle);
                    Scheduler.get().runTaskLater(() -> {
                        tardis.door().setDoorParticles(null);
                    }, TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, 3);
                }).execute();
    }

    /**
     * Replaces the console with air, and places soul sand beneath it.
     * @param cPos The position of the console to replace.
     */
    private void replaceConsoleWithGrowth(BlockPos cPos) {
        ServerWorld world = tardis.asServer().world();

        if (!(world.getBlockEntity(cPos) instanceof ConsoleBlockEntity console))
            return;

        world.setBlockState(cPos, Blocks.AIR.getDefaultState());
        world.setBlockState(cPos.down(), Blocks.SOUL_SAND.getDefaultState());

        console.onBroken();
    }

    /**
     * Replaces all consoles with growth.
     * @see #replaceConsoleWithGrowth(BlockPos)
     */
    private void replaceAllConsolesWithGrowth() {
        for (BlockPos cPos : tardis.getDesktop().getConsolePos()) {
            replaceConsoleWithGrowth(cPos);
        }
    }

    private void createChestAtInteriorDoor(List<ItemStack> contents) {
        if (contents == null || contents.isEmpty()) {
            AITMod.LOGGER.debug("No contents to save in recovery chest for {}", this.tardis);
            return;
        }

        DirectedBlockPos door = this.tardis.getDesktop().getDoorPos();

        CachedDirectedGlobalPos safe = CachedDirectedGlobalPos.create(
                this.tardis.asServer().world(),
                door.getPos().offset(door.toMinecraftDirection(), 2),
                door.getRotation()
        );

        // vars are ew, but fully qualifying the package name is worse.
        var ref = new dev.drtheo.queue.api.util.Value<BlockPos>(null);

        SafePosSearch.wrapSafe(safe, SafePosSearch.Kind.MEDIAN, true,
                result -> this.finishCreatingChest(result, contents));
    }

    private void finishCreatingChest(CachedDirectedGlobalPos safe, List<ItemStack> contents) {
        // set block to chest
        if (!(safe.getWorld().getBlockState(safe.getPos()).isAir())) {
            AITMod.LOGGER.error("Failed to create recovery chest at {} for {}", safe, this.tardis);
            return;
        }

        Direction doorDir = tardis.getDesktop().getDoorPos().toMinecraftDirection();
        safe.getWorld().setBlockState(safe.getPos(), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, doorDir.getOpposite()), Block.NOTIFY_ALL);

        // set chest contents
        ChestBlockEntity chest = (ChestBlockEntity) safe.getWorld().getBlockEntity(safe.getPos());
        List<ItemStack> overflow = new ArrayList<>(contents);

        for (int i = 0; i < 27 && !overflow.isEmpty(); i++) {
            chest.setStack(i, overflow.remove(0));
        }

        AITMod.LOGGER.debug("Created recovery chest at {} for {}", safe, this.tardis);

        if (!overflow.isEmpty())
            createChestAtInteriorDoor(overflow);
    }

    @Override
    public void tick(MinecraftServer server) {
        boolean isQueued = this.queued.get();

        if (server.getTicks() % 10 == 0 && this.tardis.isGrowth()) {
            this.generateInteriorWithItem();

            if (!isQueued) {
                if (server.getTicks() % 200 == 0 && this.hasEnoughPlasmicMaterial())
                    this.tardis.asServer().world().getPlayers().forEach(player ->
                            player.sendMessage(HINT_TEXT, true));

                if (this.tardis.door().isClosed()) {
                    this.tardis.door().openDoors();
                } else {
                    this.tardis.door().setLocked(false);
                }
            }
        }

        if (!isQueued)
            return;

        if (!this.canQueue()) {
            this.queued.set(false);
            this.regenerating.set(false);

            tardis.alarm().disable();
            return;
        }

        if (!TardisUtil.isInteriorEmpty(tardis.asServer())) {
            if (this.regenerating.get()) {
                PlayerEntity target = TardisUtil.getAnyPlayerInsideInterior(tardis.asServer().world());

                if (this.tardis().subsystems().lifeSupport().isEnabled()) {
                    TardisUtil.teleportOutside(tardis.asServer(), target);
                } else {
                    target.damage(AITDamageTypes.of(target.getWorld(), AITDamageTypes.INTERIOR_CHANGE), Float.MAX_VALUE);
                }
            }
        }

        if (!this.regenerating.get() && !this.countdownStarted) {
            this.startRegeneratingCountdown();
        }
    }

    private ServerAlarmHandler.Countdown startRegeneratingCountdown() {
        ServerAlarmHandler.Countdown cd = new ServerAlarmHandler.Countdown.Builder().bellTolls(5).message("tardis.message.interiorchange.regenerating").thenRun(() -> {
            tardis.getDesktop().startQueue(true);
            Scheduler.get().runTaskLater(this::changeInterior, TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, 5);

            this.regenerating.set(true);
            this.countdownStarted = false;
        });

        this.tardis().alarm().enable(cd);
        this.countdownStarted = true;

        return cd;
    }

    public boolean hasEnoughPlasmicMaterial() {
        return this.plasmicMaterialAmount() == MAX_PLASMIC_MATERIAL_AMOUNT;
    }

    protected void generateInteriorWithItem() {
        if (!hasEnoughPlasmicMaterial()) {
            TardisUtil.sendMessageToInterior(tardis.asServer(), Text.translatable("tardis.message.interiorchange.not_enough_plasmic_material", this.plasmicMaterialAmount()).formatted(Formatting.GRAY));
            return;
        }

        TardisUtil.getEntitiesInInterior(this.tardis, 50).stream()
                .filter(entity -> entity instanceof ItemEntity item
                        && (item.getStack().getItem() == AITItems.PERSONALITY_MATRIX)
                        && entity.isTouchingWater())
                .forEach(entity -> {
                    ItemEntity item = (ItemEntity) entity;
                    ItemStack stack = item.getStack();
                    DirectedGlobalPos position = this.tardis.travel().position();

                    if (position == null)
                        return;

                    this.tardis.setFuelCount(8000);

                    entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT,
                            SoundCategory.BLOCKS, 10.0F, 0.75F);
                    entity.getWorld().playSound(null, position.getPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT,
                            SoundCategory.BLOCKS, 10.0F, 0.75F);

                    this.queueInteriorChange(DesktopRegistry.getInstance().get(AITMod.id("cave")));
                    if (stack.isOf(AITItems.PERSONALITY_MATRIX)) {
                        NbtCompound nbt = stack.getOrCreateNbt();
                        if (nbt.contains("name")) {
                            this.tardis.stats().setName(nbt.getString("name"));
                        }
                    }

                    if (this.queued.get())
                        entity.discard();
                });
    }

    private boolean canQueue() {
        return tardis.isGrowth() || tardis.fuel().hasPower() || tardis.crash().isToxic();
    }
}
