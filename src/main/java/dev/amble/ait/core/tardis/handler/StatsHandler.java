package dev.amble.ait.core.tardis.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.time.Instant;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.amble.lib.register.unlockable.Unlockable;
import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.joml.Vector3f;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.boti.BOTIChunkVBO;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.sounds.flight.FlightSound;
import dev.amble.ait.core.sounds.flight.FlightSoundRegistry;
import dev.amble.ait.core.sounds.travel.map.TravelSoundMap;
import dev.amble.ait.core.tardis.handler.travel.AnimatedTravelHandler;
import dev.amble.ait.core.tardis.util.network.s2c.BOTISyncS2CPacket;
import dev.amble.ait.core.tardis.vortex.reference.VortexReference;
import dev.amble.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import dev.amble.ait.core.util.Lazy;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.dbl.DoubleProperty;
import dev.amble.ait.data.properties.dbl.DoubleValue;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.registry.impl.DesktopRegistry;

public class StatsHandler extends KeyedTardisComponent {

    private static final Identifier NAME_PATH = AITMod.id("tardis_names.json");
    private static List<String> NAME_CACHE;

    private static final Property<String> NAME = new Property<>(Property.STR, "name", "");
    private static final Property<String> PLAYER_CREATOR_NAME = new Property<>(Property.STR, "player_creator_name",
            "");
    private static final Property<Long> DATE = new Property<>(Property.LONG, "date", 0L);
    private static final Property<String> DATE_TIME_ZONE = new Property<>(Property.STR, "date_time_zone", "");
    private static final Property<RegistryKey<World>> SKYBOX = new Property<>(Property.WORLD_KEY, "skybox",
            World.END);
    private static final Property<Direction> SKYBOX_DIRECTION = new Property<>(Property.DIRECTION, "skybox_direction",
            Direction.NORTH);
    private static final Property<HashSet<String>> UNLOCKS = new Property<>(Property.STR_SET, "unlocks",
            new HashSet<>());

    private static final Property<Identifier> FLIGHT_FX = new Property<>(Property.IDENTIFIER, "flight_fx", new Identifier(""));
    private static final Property<Identifier> VORTEX_FX = new Property<>(Property.IDENTIFIER, "vortex_fx", new Identifier(""));
    private static final BoolProperty SECURITY = new BoolProperty("security", false);
    private static final BoolProperty HAIL_MARY = new BoolProperty("hail_mary", false);
    private static final BoolProperty RECEIVE_CALLS = new BoolProperty("receive_calls", true);
    private static final DoubleProperty TARDIS_X_SCALE = new DoubleProperty("tardis_x_scale", 1);
    private static final DoubleProperty TARDIS_Y_SCALE = new DoubleProperty("tardis_y_scale", 1);
    private static final DoubleProperty TARDIS_Z_SCALE = new DoubleProperty("tardis_z_scale", 1);
    private static final Property<BlockPos> TARGET_POS = new Property<>(Property.BLOCK_POS, "target_pos", BlockPos.ORIGIN);
    private static final Property<RegistryKey<World>> TARGET_WORLD = new Property<>(Property.WORLD_KEY, "target_world", World.OVERWORLD);


    private final Value<String> tardisName = NAME.create(this);
    private final Value<String> playerCreatorName = PLAYER_CREATOR_NAME.create(this);
    private final Value<Long> dateCreated = DATE.create(this);
    private final Value<String> dateTimeZone = DATE_TIME_ZONE.create(this);
    private final Value<RegistryKey<World>> skybox = SKYBOX.create(this);
    private final Value<Direction> skyboxDirection = SKYBOX_DIRECTION.create(this);
    private final Value<HashSet<String>> unlocks = UNLOCKS.create(this);
    private final BoolValue security = SECURITY.create(this);
    private final BoolValue hailMary = HAIL_MARY.create(this);
    private final BoolValue receiveCalls = RECEIVE_CALLS.create(this);
    private final Value<Identifier> flightId = FLIGHT_FX.create(this);
    private final Value<Identifier> vortexId = VORTEX_FX.create(this);
    private final DoubleValue tardisXScale = TARDIS_X_SCALE.create(this);
    private final DoubleValue tardisYScale = TARDIS_Y_SCALE.create(this);
    private final DoubleValue tardisZScale = TARDIS_Z_SCALE.create(this);
    private final Value<BlockPos> targetPos = TARGET_POS.create(this);
    private final Value<RegistryKey<World>> targetWorld = TARGET_WORLD.create(this);

    @Exclude
    private Lazy<TravelSoundMap> travelFxCache;
    @Exclude
    private Lazy<FlightSound> flightFxCache;
    @Exclude
    private Lazy<VortexReference> vortexFxCache;
    @Exclude
    public BakedModel chunkModel = null;
    @Exclude
    public Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();

    public StatsHandler() {
        super(Id.STATS);
    }

    @Override
    public void onCreate() {
        this.markDateCreated();
        this.setName("Type 50 TT Capsule");
        this.setXScale(1.0f);
        this.setYScale(1.0f);
        this.setZScale(1.0f);
    }

    @Override
    public void onLoaded() {
        skybox.of(this, SKYBOX);
        skyboxDirection.of(this, SKYBOX_DIRECTION);
        unlocks.of(this, UNLOCKS);
        tardisName.of(this, NAME);
        playerCreatorName.of(this, PLAYER_CREATOR_NAME);
        dateCreated.of(this, DATE);
        dateTimeZone.of(this, DATE_TIME_ZONE);
        security.of(this, SECURITY);
        hailMary.of(this, HAIL_MARY);
        receiveCalls.of(this, RECEIVE_CALLS);
        flightId.of(this, FLIGHT_FX);
        vortexId.of(this, VORTEX_FX);
        tardisXScale.of(this, TARDIS_X_SCALE);
        tardisYScale.of(this, TARDIS_Y_SCALE);
        tardisZScale.of(this, TARDIS_Z_SCALE);
        targetPos.of(this, TARGET_POS);
        targetWorld.of(this, TARGET_WORLD);
        vortexId.addListener((id) -> {
            if (this.vortexFxCache != null)
                this.vortexFxCache.invalidate();
            else this.getVortexEffects();
        });
        flightId.addListener((id) -> {
            if (this.flightFxCache != null)
                this.flightFxCache.invalidate();
            else this.getFlightEffects();
        });

        for (Iterator<TardisDesktopSchema> it = DesktopRegistry.getInstance().iterator(); it.hasNext();) {
            this.unlock(it.next(), false);
        }
    }

    public boolean isUnlocked(Unlockable unlockable) {
        return this.unlocks.get().contains(unlockable.id().toString());
    }

    public void unlock(Unlockable unlockable) {
        this.unlock(unlockable, true);
    }

    private void unlock(Unlockable unlockable, boolean sync) {
        this.unlocks.flatMap(strings -> {
            strings.add(unlockable.id().toString());
            return strings;
        }, sync);
    }

    public Value<RegistryKey<World>> skybox() {
        return skybox;
    }

    public Value<Direction> skyboxDirection() {
        return skyboxDirection;
    }

    public String getName() {
        String name = tardisName.get();

        if (name == null) {
            name = getRandomName();
            this.setName(name);
        }

        return name;
    }

    public BoolValue security() {
        return security;
    }

    public BoolValue hailMary() {
        return hailMary;
    }
    public BoolValue receiveCalls() {
        return this.receiveCalls;
    }

    public String getPlayerCreatorName() {
        String name = playerCreatorName.get();

        if (name == null) {
            name = getRandomName();
            this.setPlayerCreatorName(name);
        }

        return name;
    }

    public void setName(String name) {
        tardisName.set(name);
    }

    public void setPlayerCreatorName(String name) {
        playerCreatorName.set(name);
    }

    public static String getRandomName() {
        if (StatsHandler.shouldGenerateNames())
            StatsHandler.loadNames();

        if (NAME_CACHE == null)
            return "";

        return NAME_CACHE.get(AITMod.RANDOM.nextInt(NAME_CACHE.size()));
    }

    public static boolean shouldGenerateNames() {
        return (NAME_CACHE == null || NAME_CACHE.isEmpty());
    }

    private static void loadNames() {
        if (NAME_CACHE == null)
            NAME_CACHE = new ArrayList<>();

        NAME_CACHE.clear();

        try {
            Optional<Resource> resource = ServerLifecycleHooks.get().getResourceManager().getResource(NAME_PATH);

            if (resource.isEmpty()) {
                AITMod.LOGGER.error("ERROR in tardis_names.json:");
                AITMod.LOGGER.error("Missing Resource");
                return;
            }

            InputStream stream = resource.get().getInputStream();

            JsonArray list = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonArray();

            for (JsonElement element : list) {
                NAME_CACHE.add(element.getAsString());
            }
        } catch (IOException e) {
            AITMod.LOGGER.error("ERROR in tardis_names.json", e);
        }
    }

    public Date getDateCreated() {
        if (dateCreated.get() == null) {
            AITMod.LOGGER.error("{} was missing creation date! Resetting to now", tardis.getUuid().toString());
            markDateCreated();
        }

        // parse a Date from the dateCreated, and add to the hours the difference between this time zone and the time zone stored in the dateTimeZone
        try {
            Instant instant = Instant.ofEpochSecond(dateCreated.get());
            //System.out.println(Instant.now().getEpochSecond());
            TimeZone timeZone = TimeZone.getTimeZone(dateTimeZone.get());
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(instant.toEpochMilli());
            return calendar.getTime();
        } catch (Exception e) {
            AITMod.LOGGER.error("Error parsing creation date for {}", tardis.getUuid().toString(), e);
            return Date.from(Instant.now());
        }
    }

    private float getXScale() {
        double v = tardisXScale.get();
        return (float) v;
    }

    private float getYScale() {
        double v = tardisYScale.get();
        return (float) v;
    }

    private float getZScale() {
        double v = tardisZScale.get();
        return (float) v;
    }

    /**
     * The scale of the TARDIS.
     * @see AnimatedTravelHandler#getScale()
     */
    public Vector3f getScale() {
        return new Vector3f(this.getXScale(), this.getYScale(), this.getZScale());
    }

    public void setXScale(double scale) {
        this.tardisXScale.set(scale);
    }

    public void setYScale(double scale) {
        this.tardisYScale.set(scale);
    }

    public void setZScale(double scale) {
        this.tardisZScale.set(scale);
    }

    public String getCreationString() {
        return DateFormat.getDateTimeInstance(DateFormat.LONG, 3).format(this.getDateCreated());
    }

    public void markDateCreated() {
        // set the creation date to now, along with the time zone, and store it in a computer-readable string format
       dateCreated.set(Instant.now().getEpochSecond());
       dateTimeZone.set(DateFormat.getTimeInstance(DateFormat.LONG).getTimeZone().getID());
    }

    public void markPlayerCreatorName() {
        playerCreatorName.set(this.getPlayerCreatorName());
    }

    public FlightSound getFlightEffects() {
        if (this.flightFxCache == null) {
            this.flightFxCache = new Lazy<>(this::createFlightEffectsCache);
        }

        return this.flightFxCache.get();
    }

    private FlightSound createFlightEffectsCache() {
        return FlightSoundRegistry.getInstance().getOrFallback(this.flightId.get());
    }

    public VortexReference getVortexEffects() {
        if (this.vortexFxCache == null) {
            this.vortexFxCache = new Lazy<>(this::createVortexEffectsCache);
        }

        return this.vortexFxCache.get();
    }

    private VortexReference createVortexEffectsCache() {
        return VortexReferenceRegistry.getInstance().getOrFallback(this.vortexId.get());
    }

    public void setVortexEffects(VortexReference current) {
        this.vortexId.set(current.id());

        if (this.vortexFxCache != null)
            this.vortexFxCache.invalidate();
    }

    public void setFlightEffects(FlightSound current) {
        this.flightId.set(current.id());

        if (this.flightFxCache != null)
            this.flightFxCache.invalidate();
    }

    // BOTI Related Code
    public RegistryKey<World> getTargetWorld() {
        return this.targetWorld.get();
    }

    public void setTargetWorld(ExteriorBlockEntity exteriorBlockEntity, RegistryKey<World> targetWorld, BlockPos targetPos, boolean markDirty) {
        this.targetWorld.set(targetWorld);
        this.targetPos.set(targetPos);
        this.chunkModel = null;
        if (this.blockEntities != null)
            this.blockEntities.clear();
        if (markDirty && exteriorBlockEntity.getWorld() != null && !exteriorBlockEntity.getWorld().isClient()) {
            exteriorBlockEntity.getWorld().updateListeners(exteriorBlockEntity.getPos(), exteriorBlockEntity.getCachedState(), exteriorBlockEntity.getCachedState(), 3);
            ServerLifecycleHooks.get().getPlayerManager().getPlayerList().forEach(player -> {
                ServerPlayNetworking.send(player,
                        new BOTISyncS2CPacket(exteriorBlockEntity.getPos(), targetWorld, targetPos));
            });
        }
    }

    @Environment(value = EnvType.CLIENT)
    public BlockPos targetPos() {
        return this.targetPos.get();
    }

    @Exclude
    @Environment(value = EnvType.CLIENT)
    public BOTIChunkVBO botiChunkVBO;

    @Exclude
    @Environment(value = EnvType.CLIENT)
    public Map<BlockPos, BlockState> posState = new HashMap<>();

    @Exclude
    public NbtCompound chunkData = new NbtCompound();

    @Environment(value = EnvType.CLIENT)
    public void updateMap(Map<BlockPos, BlockState> statePos) {
        posState = statePos;
    }

    //TODO: Make sure I didn't just comment out some important code idfk atp
//    @Environment(value = EnvType.CLIENT)
//    public void updateChunkModel(ExteriorBlockEntity exteriorBlockEntity, NbtCompound chunkData) {
//        if (exteriorBlockEntity == null || exteriorBlockEntity.getWorld() == null || !exteriorBlockEntity.getWorld().isClient())
//            return;
//
//        if (botiChunkVBO == null) botiChunkVBO = new BOTIChunkVBO();
//
//        botiChunkVBO.setTargetPos(this.targetPos.get());
//        botiChunkVBO.updateBlockMap(this.posState);
//    }


    @Environment(value = EnvType.CLIENT)
    public void updateChunkModel(ExteriorBlockEntity exteriorBlockEntity) {
        if (exteriorBlockEntity == null || exteriorBlockEntity.getWorld() == null || !exteriorBlockEntity.getWorld().isClient())
            return;

        if (botiChunkVBO == null) botiChunkVBO = new BOTIChunkVBO();

        botiChunkVBO.setTargetPos(this.targetPos.get());
        botiChunkVBO.updateBlockMap(this.posState);

//        new UpdateBOTIChunkModelThread(exteriorBlockEntity).start();

        botiChunkVBO.updateChunkModel(exteriorBlockEntity);

        // Might need these at some point idfk
//        MinecraftClient mc = MinecraftClient.getInstance();
//        BlockRenderManager blockRenderManager = mc.getBlockRenderManager();
//        List<BakedQuad> quads = new ArrayList<>();
        ChunkPos chunkPos = new ChunkPos(targetPos.get());
        int baseY = targetPos.get().getY() & ~15;
//        BlockState[][][] sectionStates = new BlockState[16][16][16];
        this.blockEntities.clear();

        BlockState[][][] sectionStates = new BlockState[16][16][16];

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos pos = new BlockPos(chunkPos.getStartX() + x, baseY + y, chunkPos.getStartZ() + z);
                    BlockState state = getBlockStateFromChunkNBT(chunkData, pos);
                    sectionStates[x][y][z] = state != null ? state : Blocks.AIR.getDefaultState();

                    String key = x + "_" + y + "_" + z;
                    if (chunkData.contains("block_entities") && chunkData.getCompound("block_entities").contains(key)) {
                        try {
                            NbtCompound nbt = chunkData.getCompound("block_entities").getCompound(key);
                            BlockEntity blockEntity = BlockEntity.createFromNbt(pos, sectionStates[x][y][z], nbt);
                            if (blockEntity != null) {
                                if (blockEntity instanceof AbstractLinkableBlockEntity abstractLinkableBlockEntity &&
                                        exteriorBlockEntity.tardis() != null) {
                                    abstractLinkableBlockEntity.link(exteriorBlockEntity.tardis().get());
                                }
                                BlockPos relativePos = pos.subtract(new BlockPos(chunkPos.getStartX() + 8, baseY, chunkPos.getStartZ() + 8));
                                this.blockEntities.put(relativePos, blockEntity);
                            }
                        } catch (Exception e) {
                            AITMod.LOGGER.error("Failed to load block entity at {} \n {}", pos, e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public List<BakedQuad> translateQuads(List<BakedQuad> quads, int xOffset, int yOffset, int zOffset) {
        List<BakedQuad> translated = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            int[] vertexData = quad.getVertexData().clone();
            for (int i = 0; i < vertexData.length; i += 8) {
                float x = Float.intBitsToFloat(vertexData[i]) + xOffset;
                float y = Float.intBitsToFloat(vertexData[i + 1]) + yOffset;
                float z = Float.intBitsToFloat(vertexData[i + 2]) + zOffset;
                vertexData[i] = Float.floatToRawIntBits(x);
                vertexData[i + 1] = Float.floatToRawIntBits(y);
                vertexData[i + 2] = Float.floatToRawIntBits(z);
            }
            translated.add(new BakedQuad(vertexData, quad.getColorIndex(), quad.getFace(), quad.getSprite(), quad.hasShade()));
        }
        return translated;
    }


    private BlockState getBlockStateFromChunkNBT(NbtCompound chunkData, BlockPos pos) {
        if (chunkData.contains("block_states")) {
            NbtCompound blockStates = chunkData.getCompound("block_states");
            if (blockStates.contains("palette") && blockStates.contains("data")) {
                NbtList palette = blockStates.getList("palette", NbtCompound.COMPOUND_TYPE);
                long[] data = blockStates.getLongArray("data");
                if (data.length == 0 || palette.isEmpty()) {
                    System.out.println("Empty data or palette in chunk NBT for pos " + pos +
                            ": data=" + data.length + ", palette=" + palette.size());
                    return Blocks.AIR.getDefaultState();
                }

                int bitsPerEntry = blockStates.contains("bitsPerEntry") ? blockStates.getInt("bitsPerEntry") :
                        Math.max(2, (int) Math.ceil(Math.log(palette.size()) / Math.log(2)));
                int x = pos.getX() & 15;
                int y = pos.getY() & 15;
                int z = pos.getZ() & 15;
                int index = y * 256 + z * 16 + x;

                int entriesPerLong = 64 / bitsPerEntry;
                int longIndex = index / entriesPerLong;
                if (longIndex >= data.length) {
                    System.out.println("Long index out of bounds: " + longIndex + " >= " + data.length +
                            " (index=" + index + ", bitsPerEntry=" + bitsPerEntry + ")");
                    return Blocks.AIR.getDefaultState();
                }
                int offset = (index % entriesPerLong) * bitsPerEntry;
                long value = (data[longIndex] >> offset) & ((1L << bitsPerEntry) - 1);

                if (value >= 0 && value < palette.size()) {
                    NbtCompound stateTag = palette.getCompound((int) value);
                    return BlockState.CODEC.parse(NbtOps.INSTANCE, stateTag)
                            .result().orElse(Blocks.AIR.getDefaultState());
                } else {
                    System.out.println("State value out of palette bounds at " + pos + ": " + value + " >= " + palette.size());
                    return Blocks.AIR.getDefaultState();
                }
            } else {
                System.out.println("Missing palette or data in block_states: " + blockStates);
            }
        } else {
//            System.out.println("No block_states in chunk data: " + chunkData);
        }
        return Blocks.AIR.getDefaultState();
    }
}
