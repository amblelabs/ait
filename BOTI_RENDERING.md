# Cross-Dimensional BOTI Rendering System

## Overview

This document describes the cross-dimensional BOTI (Bigger On The Inside) rendering system implemented for the AIT mod. This system allows the client to render blocks from the TARDIS interior dimension even when the player is in the overworld, creating the iconic "bigger on the inside" effect.

## Architecture

### Components

#### 1. ProxyChunk (`dev.amble.ait.client.boti.ProxyChunk`)

A lightweight chunk representation that caches block states and block entities from server chunks.

**Key Features:**
- Stores a 16×16×16 section of blocks
- Supports NBT serialization/deserialization for network transmission
- Validates incoming data to prevent crashes from malicious packets
- Efficient palette-based storage format

**Security:**
- Validates `bitsPerEntry` (must be 1-64) to prevent division by zero
- Validates palette indices to prevent array out of bounds
- Error logging for debugging corrupted data

#### 2. ProxyClientWorld (`dev.amble.ait.client.boti.ProxyClientWorld`)

A virtual world that implements `BlockRenderView` to provide block data for rendering.

**Key Features:**
- Singleplayer: Direct ServerWorld access for real-time updates
- Multiplayer: Cached chunk data (to be implemented)
- LRU-style cache eviction (max 256 chunks) to prevent memory leaks
- Cached validity checks to reduce overhead

**Performance:**
- Maximum cache size: 256 chunks (~4MB)
- Validity check interval: 1 second
- Coordinate support: ±2 million blocks (sufficient for TARDIS interiors)

#### 3. WorldGeometryRenderer (`dev.loqor.client.WorldGeometryRenderer`)

Enhanced world geometry renderer that supports rendering from any `BlockRenderView`.

**New Methods:**
- `renderFromDimension(RegistryKey<World>, BlockPos, MatrixStack, float)` - Entry point for cross-dimensional rendering
- `renderWithWorld(BlockRenderView, ...)` - Internal render using any BlockView
- `rebuildGeometryFromWorld(BlockRenderView, ...)` - Geometry building from BlockView
- `buildSectionFromWorld(BlockRenderView, ...)` - Section building from BlockView

**Features:**
- Double-buffered rendering (no visual flashing)
- Frustum culling based on door direction
- View bobbing cancellation
- Async geometry building

#### 4. TardisDoorBOTI (`dev.amble.ait.client.boti.TardisDoorBOTI`)

Updated to use the cross-dimensional rendering system.

**Changes:**
- Constructs TARDIS dimension key from TARDIS UUID
- Calls `renderFromDimension()` instead of `render()`
- Sets door facing for proper frustum culling

## How It Works

### Singleplayer

1. **Initialization**: `ProxyClientWorld` is created for the TARDIS interior dimension
2. **Validation**: Checks if integrated server is available and dimension exists
3. **Rendering**: Directly accesses `ServerWorld` for real-time block data
4. **Geometry Building**: Builds VBOs from the block data asynchronously
5. **Display**: Renders the geometry through the BOTI portal

### Multiplayer (TODO)

1. **Chunk Request**: Client sends `BOTIChunkRequestC2SPacket` to server
2. **Serialization**: Server serializes chunk section to NBT (palette + data array)
3. **Transmission**: Server sends `BOTIDataS2CPacket` to client
4. **Caching**: Client updates `ProxyClientWorld` cache with received data
5. **Rendering**: Uses cached data to build and render geometry

## NBT Format

### Chunk Data Structure

```
{
  "block_states": {
    "palette": [           // List of unique block states
      {                    // BlockState NBT (via BlockState.CODEC)
        "Name": "minecraft:stone",
        "Properties": {...}
      },
      ...
    ],
    "data": [long array],  // Packed palette indices
    "bitsPerEntry": int    // Bits per palette entry (4-15)
  },
  "block_entities": {      // Optional
    "x_y_z": {             // Block entity NBT keyed by local coords
      ...
    }
  }
}
```

## Usage

### For Singleplayer

No additional setup required. The system automatically detects singleplayer mode and accesses the server world directly.

### For Multiplayer (Future)

1. Implement packet handling in `BOTIDataS2CPacket.handle()`
2. Call `ProxyClientWorld.updateChunkData()` with received NBT
3. Mark `WorldGeometryRenderer` dirty to trigger rebuild

## Performance Considerations

### Memory Usage

- **Per Chunk**: ~16KB (palette + indices + block entities)
- **Max Cache**: 256 chunks = ~4MB
- **Eviction**: Simple FIFO (can be improved to LRU)

### CPU Usage

- **Geometry Building**: Async with double-buffering
- **Block Updates**: Only rebuild when needed (dirty flag)
- **Validity Checks**: Cached with 1-second interval

### Rendering

- **Frustum Culling**: Skips sections outside view
- **Door Culling**: Skips blocks behind door plane
- **Occlusion Culling**: Skips fully surrounded blocks

## Known Limitations

1. **Multiplayer**: Not yet implemented (marked with TODOs)
2. **Block Entities**: Not fully supported in multiplayer (NBT deserialization needed)
3. **Lighting**: Uses full brightness (15) for simplicity
4. **Coordinate Range**: Limited to ±2 million blocks (cache key packing)

## Future Work

### High Priority
- [ ] Implement multiplayer packet handling
- [ ] Block entity deserialization for multiplayer
- [ ] Proper LRU cache eviction strategy

### Medium Priority
- [ ] Dynamic lighting support
- [ ] Translucent block rendering
- [ ] Block entity rendering optimization

### Low Priority
- [ ] Coordinate range extension
- [ ] Cache size auto-tuning
- [ ] Profiling and optimization

## Testing Checklist

- [x] Singleplayer: Can see TARDIS interior through exterior door
- [ ] Interior updates in real-time when blocks change
- [ ] No performance degradation
- [ ] Works with different TARDIS interior designs
- [ ] Door rotation culling works correctly
- [ ] View bobbing is properly cancelled
- [ ] Geometry updates smoothly without flashing
- [ ] Green screen mode still works
- [ ] Vortex rendering during flight still works

## Troubleshooting

### Interior not rendering

1. Check if in singleplayer mode
2. Verify TARDIS dimension exists
3. Check console for error messages
4. Ensure door position is valid

### Performance issues

1. Reduce render distance in `WorldGeometryRenderer` constructor
2. Check cache size (should not exceed 256 chunks)
3. Monitor async build future completion
4. Check for geometry rebuild loops

### Visual artifacts

1. Verify double-buffering is working
2. Check frustum culling logic
3. Ensure door facing is set correctly
4. Verify projection matrix is valid

## Contributing

When contributing to this system:

1. Maintain backward compatibility with existing BOTI features
2. Add validation for all NBT deserialization
3. Document NBT format changes
4. Test both singleplayer and multiplayer (when implemented)
5. Profile performance impact
6. Add appropriate TODO comments for future work

## References

- **BOTI System**: Original portal rendering system
- **MultiDim API**: Dimension management (TardisServerWorld)
- **Fabric Networking**: Packet system (TODO: multiplayer)
- **Minecraft Rendering**: Block rendering and VBOs
