package dev.amble.ait.tardis.v2;

import dev.amble.ait.api.tardis.v2.data.TDataRegistry;
import dev.amble.ait.tardis.v2.data.*;

public class AITData {

    public static void init() {
        TDataRegistry.register(AlarmData.ID);
        TDataRegistry.register(DesktopData.ID);
        TDataRegistry.register(DoorData.ID);
        TDataRegistry.register(EmergencyPowerData.ID);
        TDataRegistry.register(EngineData.ID);
        TDataRegistry.register(FuelData.ID);
        TDataRegistry.register(GrowthData.ID);
        TDataRegistry.register(RepairData.ID);
        TDataRegistry.register(SiegeData.ID);
        TDataRegistry.register(SonicData.ID);
        TDataRegistry.register(TravelData.ID);

        TDataRegistry.freeze();
    }
}
