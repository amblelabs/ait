package dev.amble.ait.client.sounds.flight;

import dev.amble.ait.client.sounds.LoopingSound;
import dev.amble.ait.core.entities.FlightTardisEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.sounds.flight.FlightSound;

import java.util.HashMap;
import java.util.Map;

// Duzo's code kills me internally so I rewrote this. - Loqor
public class ExteriorFlightSound extends LoopingSound {
    private final PlayerEntity player;

    public static final Map<PlayerEntity, ExteriorFlightSound> INSTANCES = new HashMap<>();

    public ExteriorFlightSound(FlightSound data, SoundCategory soundCategory, PlayerEntity playerEntity) {
        super(data.sound(), soundCategory);
        this.setPosition(playerEntity.getPos());
        this.repeat = true;
        this.repeatDelay = 0;
        this.attenuationType = AttenuationType.NONE;
        this.player = playerEntity;
        this.volume = 1.0f;
    }

    public ClientTardis tardis() {
        if (this.player.getVehicle() instanceof FlightTardisEntity flight) {
            return flight.tardis().get().asClient();
        }
        return null;
    }

    @Override
    public void tick() {
        System.out.println("am i even getting ticked??");

        if (player.getVehicle() instanceof FlightTardisEntity flight) {
            this.setPosition(player.getPos());
            ClientTardis tardis = tardis();
            if (tardis == null) {
                MinecraftClient.getInstance().getSoundManager().stop(this);
                return;
            }
            this.pitch = FlightSoundPlayer.getRandomPitch(tardis);
        } else {
            MinecraftClient.getInstance().getSoundManager().stop(this);
            return;
        }
        //this.setDone();
    }
}
