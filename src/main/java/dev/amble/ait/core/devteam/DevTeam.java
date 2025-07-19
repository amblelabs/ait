package dev.amble.ait.core.devteam;

import java.util.Set;
import java.util.UUID;

/**
 * @author Loqor
 * Don't worry, we're not pulling a Dalek Mod and forcing creative for ourselves. Lol. - Loqor
 */
public class DevTeam {
    public static final UUID LOQOR = UUID.fromString("ad504e7c-22a0-4b3f-94e3-5b6ad5514cb6");
    public static final UUID DUZO = UUID.fromString("743a01d8-25e1-4da2-af2b-643587fe86e7");
    public static final UUID OURO = UUID.fromString("07e6b550-be92-4422-a269-345593df5a10");
    public static final UUID CLASSIC = UUID.fromString("ba21f64b-35e3-4b4f-b04c-9ceb814ad533");
    public static final UUID KRANG = UUID.fromString("248808c9-4057-4d36-938b-3554d2e2123e");
    public static final UUID ADDIE = UUID.fromString("ae93f403-bf4a-4fb9-89db-2826ae9a4508");
    public static final UUID JELLY = UUID.fromString("5a9bb737-ceb2-4d45-876c-b0e4531a811f"); // Not sure if he should get one - Loqor
    public static final UUID VENWHOVIAN = UUID.fromString("a77b585c-368d-4285-b536-42fd612a6e1e"); // Not sure if he should get one either - Loqor

    public static final Set<UUID> PLAYERS = Set.of(
            LOQOR, DUZO, OURO, CLASSIC, KRANG, ADDIE, JELLY, VENWHOVIAN
    );

    public static boolean isDev(UUID uuid) {
        return PLAYERS.contains(uuid);
    }
}
