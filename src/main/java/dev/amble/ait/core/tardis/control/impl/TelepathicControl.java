package dev.amble.ait.core.tardis.control.impl;

import java.util.ArrayList;
import java.util.List;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.queue.api.ActionQueue;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NameTagItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.drinks.DrinkUtil;
import dev.amble.ait.core.item.HandlesItem;
import dev.amble.ait.core.item.HypercubeItem;
import dev.amble.ait.core.item.KeyItem;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.likes.ItemOpinion;
import dev.amble.ait.core.likes.ItemOpinionRegistry;
import dev.amble.ait.core.lock.LockedDimensionRegistry;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementManager;
import dev.amble.ait.core.tardis.handler.SiegeHandler;
import dev.amble.ait.core.tardis.handler.distress.DistressCall;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.handler.travel.TravelUtil;
import dev.amble.ait.core.tardis.util.AsyncLocatorUtil;
import dev.amble.ait.data.Loyalty;

public class TelepathicControl extends Control {

    public static final int RADIUS = 256;

    public TelepathicControl() {
        super(AITMod.id("telepathic_circuit"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        if (tardis.stats().security().get() && !KeyItem.hasMatchingKeyInInventory(player, tardis))
            return Result.FAILURE;

        ItemStack held = player.getMainHandStack();
        Item type = held.getItem();

        if (type == Items.BRICK) {
            tardis.siege().texture().set(SiegeHandler.BRICK_TEXTURE);
            return Result.FAILURE;
        }

        if (type == Items.STONE) {
            tardis.siege().texture().set(SiegeHandler.DEFAULT_TEXTURRE);
            return Result.FAILURE;
        }

        if (type == Items.OBSERVER) {
            tardis.siege().texture().set(SiegeHandler.APERTURE_TEXTURE);
            return Result.FAILURE;
        }

        if (type == Items.QUARTZ_BLOCK) {
            tardis.siege().texture().set(SiegeHandler.COMPANION_TEXTURE);
            return Result.FAILURE;
        }

        if (type instanceof LinkableItem linker) {
            if (linker instanceof SonicItem || linker instanceof HandlesItem)
                return Result.FAILURE;

            linker.link(held, tardis);
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS,
                    1.0F, 1.0F);
            return Result.SUCCESS_ALT;
        }

        if (type instanceof NameTagItem) {
            if (!held.hasCustomName())
                return Result.FAILURE;

            tardis.stats().setName(held.getName().getString());
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1F, 1.0F);

            if (!player.isCreative())
                held.decrement(1);

            return Result.SUCCESS;
        }

        if (type instanceof HypercubeItem) {
            DistressCall call = HypercubeItem.getCall(held, world.getServer().getTicks());

            if (call == null) {
                // create new call
                call = DistressCall.create(tardis, held.hasCustomName() ? held.getName().getString() : "SOS", true);
                HypercubeItem.setCall(held, call);
            }

            if (call.canSend(tardis.getUuid())) {
                call.send(tardis.getUuid(), held);
            }

            // receive and process call
            call.summon(tardis, held);
            return Result.SUCCESS;
        }

        if (held.isOf(Items.NETHER_STAR) && tardis.loyalty().get(player).isOf(Loyalty.Type.PILOT)) {
            tardis.selfDestruct().boom();

            if (!(tardis.selfDestruct().isQueued()))
                return Result.FAILURE;

            if (!player.isCreative())
                held.decrement(1);

            return Result.SUCCESS;
        }

        if (isLiquid(held))
            return spillLiquid(tardis, world, console, player);

        if (LockedDimensionRegistry.tryUnlockDimension(player, held, tardis.asServer()))
            return Result.SUCCESS;

        ItemOpinion opinion = ItemOpinionRegistry.getInstance().get(held.getItem()).orElse(null);
        if (opinion != null && tardis.opinions().contains(opinion) && (player.experienceLevel >= opinion.cost() || player.isCreative())) {
            opinion.apply(tardis.asServer(), player);

            player.getServerWorld().playSound(null, console, AITSounds.TARDIS_BLING, SoundCategory.AMBIENT, 0.25f, 1f);
            player.getServerWorld().spawnParticles((opinion.likes()) ? ParticleTypes.HEART : ParticleTypes.ANGRY_VILLAGER, console.toCenterPos().getX(),
                    console.toCenterPos().getY() + 1, console.toCenterPos().getZ(), 1, 0f, 1F, 0f, 5.0F);

            return Result.SUCCESS;
        }

        Text text = Text.translatable("tardis.message.control.telepathic.choosing");
        player.sendMessage(text, true);

        CachedDirectedGlobalPos globalPos = tardis.travel().position();

        locateStructureOfInterest(player, tardis, globalPos.getWorld(), globalPos.getPos());
        return Result.SUCCESS;
    }

    public static boolean isLiquid(ItemStack held) {
        return (held.isOf(AITItems.MUG) && DrinkUtil.getDrink(held) != DrinkUtil.EMPTY)
                || held.isOf(Items.LAVA_BUCKET) || held.isOf(Items.WATER_BUCKET) || held.isOf(Items.MILK_BUCKET);
    }

    public static Result spillLiquid(Tardis tardis, ServerWorld world, BlockPos console, @Nullable ServerPlayerEntity player) {
        /*
            This is an example of how to use the travel queue.
            This code enqueues a crash to be performed after dematerialization.
             */

        TravelHandler travel = tardis.travel();
        TravelHandlerBase.State state = travel.getState();

        ActionQueue drinkAction = new ActionQueue();

        drinkAction.thenRun(() -> {
            // This is called after the dematerialization is complete
            travel.speed(travel.maxSpeed().get());
            travel.crash();
            tardis.crash().addRepairTicks(1500);

            world.spawnParticles(ParticleTypes.SMALL_FLAME, console.toCenterPos().getX() + 0.5f, console.toCenterPos().getY() + 1.25, console.toCenterPos().getZ() + 0.5f,
                    5 * 10, 0, 0, 0, 0.1f * 10);

            world.spawnParticles(ParticleTypes.EXPLOSION, console.toCenterPos().getX() + 0.5f, console.toCenterPos().getY() + 1.25, console.toCenterPos().getZ() + 0.5f,
                    5 * 10, 0, 0, 0, 0.1f * 10);


            world.playSound(null, console, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.playSound(null, console, AITSounds.SIEGE_ENABLE, SoundCategory.BLOCKS, 1.0f, 1.0f);

            if (player != null) {
                TardisCriterions.BRAND_NEW.trigger(player);
            }
        });

        if (state == TravelHandlerBase.State.LANDED) {
            boolean hadAutopilot = travel.autopilot();

            travel.autopilot(true);

            TravelUtil.randomPos(tardis, 100000, 100000, cached -> {
                tardis.travel().destination(cached);
                tardis.removeFuel(0.1d * IncrementManager.increment(tardis) * tardis.travel().instability());
            });

            travel.dematerialize().ifPresent(tr -> {
                // These only run if the dematerialization is successful

                tr.thenRun(drinkAction);

                // This is called just before the dematerialization starts
                tardis.alarm().enable();

                world.spawnParticles(ParticleTypes.SMALL_FLAME, console.toCenterPos().getX() + 0.5f, console.toCenterPos().getY() + 1.25, console.toCenterPos().getZ() + 0.5f,
                        5 * 10, 0, 0, 0, 0.1f * 10);

                world.spawnParticles(ParticleTypes.EXPLOSION, console.toCenterPos().getX() + 0.5f, console.toCenterPos().getY() + 1.25, console.toCenterPos().getZ() + 0.5f,
                        5 * 10, 0, 0, 0, 0.1f * 10);

                world.playSound(null, console, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.playSound(null, console, AITSounds.SIEGE_ENABLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            });

            travel.autopilot(hadAutopilot);

            return Result.SUCCESS;
        }

        if (state == TravelHandlerBase.State.FLIGHT) {
            drinkAction.execute();

            return Result.SUCCESS;
        }

        return Result.FAILURE;
    }

    public static void locateStructureOfInterest(ServerPlayerEntity player, Tardis tardis, ServerWorld world,
                                                 BlockPos source) {
        if (world.getRegistryKey() == World.NETHER) {
            getStructureViaChunkGen(player, tardis, world, source, RADIUS, StructureKeys.FORTRESS);
        } else if (world.getRegistryKey() == World.END) {
            getStructureViaChunkGen(player, tardis, world, source, RADIUS, StructureKeys.END_CITY);
        } else if (world.getRegistryKey() == World.OVERWORLD) {
            getStructureViaWorld(player, tardis, world, source, RADIUS, StructureTags.VILLAGE);
        } else {
            Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
            // get a list of all the registry entries
            List<RegistryEntry<Structure>> structures = new ArrayList<>();

            for (int i = 0; i < registry.size() - 1; i++) {
                structures.add(registry.getEntry(i).orElseThrow());
            }

            locateWithChunkGenAsync(player, tardis, RegistryEntryList.of(structures), world, source, RADIUS);
        }
    }

    public static void getStructureViaChunkGen(ServerPlayerEntity player, Tardis tardis, ServerWorld world,
                                               BlockPos pos, int radius, RegistryKey<Structure> key) {
        Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);

        if (registry.getEntry(key).isPresent())
            locateWithChunkGenAsync(player, tardis, RegistryEntryList.of(registry.getEntry(key).get()), world, pos,
                    radius);
    }

    public static void getStructureViaWorld(ServerPlayerEntity player, Tardis tardis, ServerWorld world, BlockPos pos,
                                            int radius, TagKey<Structure> key) {
        locateWithWorldAsync(player, tardis, key, world, pos, radius);
    }

    @Override
    public boolean requiresPower() {
        return false;
    }

    @Override
    public long getDelayLength(Tardis tardis) {
        return 120;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.TELEPATHIC_CIRCUITS;
    }

    public static void locateWithChunkGenAsync(ServerPlayerEntity player, Tardis tardis,
                                               RegistryEntryList<Structure> structureList, ServerWorld world, BlockPos center, int radius) {
        AsyncLocatorUtil.locate(world, structureList, center, radius, false).thenOnServerThread(pos -> {
            BlockPos newPos = pos != null ? pos.getFirst() : null;
            if (newPos != null) {
                tardis.travel().forceDestination(cached -> cached.pos(newPos.withY(75)));
                tardis.removeFuel(500 * tardis.travel().getHammerUses());
                player.sendMessage(Text.translatable("tardis.message.control.telepathic.success"), true);
            } else {
                player.sendMessage(Text.translatable("tardis.message.control.telepathic.failed"), true);
            }
        });
    }

    public static void locateWithWorldAsync(ServerPlayerEntity player, Tardis tardis, TagKey<Structure> structureTagKey,
                                            ServerWorld world, BlockPos center, int radius) {
        AsyncLocatorUtil.locate(world, structureTagKey, center, radius, false).thenOnServerThread(pos -> {
            if (pos != null) {
                tardis.travel().forceDestination(cached -> cached.pos(pos.withY(75)));
                tardis.removeFuel(500 * tardis.travel().instability());
                player.sendMessage(Text.translatable("tardis.message.control.telepathic.success"), true);
            } else {
                player.sendMessage(Text.translatable("tardis.message.control.telepathic.failed"), true);
            }
        });
    }

}
