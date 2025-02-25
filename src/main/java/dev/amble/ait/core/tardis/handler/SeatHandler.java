package dev.amble.ait.core.tardis.handler;




public class SeatHandler {
    public static void init() {
        /*ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof ArmorStandEntity seat) {
                BlockPos blockPos = seat.getBlockPos();
                BlockEntity block = world.getBlockEntity(blockPos);

                if (block instanceof ExteriorBlockEntity exterior) {
                    seat.remove(Entity.RemovalReason.DISCARDED);
                    exterior.setSeatEntity(null);
                }
            }
        });*/
        /*TardisEvents.DEMAT.register((tardis) -> {
            ServerWorld world = tardis.travel().position().getWorld();
            if (entity instanceof ArmorStandEntity seat) {
                BlockPos blockPos = seat.getBlockPos();
                BlockEntity block = world.getBlockEntity(blockPos);

                seat.remove(Entity.RemovalReason.DISCARDED);
                exterior.setSeatEntity(null);
            }
            return TardisEvents.Interaction.PASS;
        });*/
    }
}
