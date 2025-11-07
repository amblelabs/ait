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
    public static final UUID THEO = UUID.fromString("2bd06cb3-2e37-4e53-a3e9-3ad29a55c455");
    public static final UUID OURO = UUID.fromString("07e6b550-be92-4422-a269-345593df5a10");
    public static final UUID CLASSIC = UUID.fromString("ba21f64b-35e3-4b4f-b04c-9ceb814ad533");
    public static final UUID ADDIE = UUID.fromString("ae93f403-bf4a-4fb9-89db-2826ae9a4508");
    public static final UUID VENWHOVIAN = UUID.fromString("a77b585c-368d-4285-b536-42fd612a6e1e"); // Not sure if he should get one either - Loqor
    public static final UUID MONKE = UUID.fromString("b8d4a6f0-93be-4e8a-b521-a1906a737c1a");
    public static final UUID PAN = UUID.fromString("70c90ff6-46e7-4481-987b-53dd79595a4a");
    public static final UUID SATURN = UUID.fromString("2a3ed4e8-40e8-44a3-9ed7-dcfe88a8badf");
    public static final UUID MAGGIE = UUID.fromString("162fe408-5e3e-4a88-a04e-ab8f468484eb");
    public static final UUID DIAN = UUID.fromString("7f001733-90b1-4cd7-87a1-42c97b2c3275");
    public static final UUID RHYNO = UUID.fromString("d892f861-dd01-4047-981c-c26b5d75990b"); // im not sure about rhyno but he has the team role - monke
    public static final UUID TREE = UUID.fromString("eb9e8f5b-fc61-4cc6-bba8-6def3d84630a");
    public static final UUID ECHO = UUID.fromString("b74877fb-cc7a-4e89-b900-09ec522e0ca9");
    public static final UUID NYX = UUID.fromString("5a9bb737-ceb2-4d45-876c-b0e4531a811f");
    public static final UUID LAKE = UUID.fromString("12a4e062-da90-4797-a788-c42fd18c94d7");



    // junior devs not sure about these ones
    public static final UUID CROW = UUID.fromString("d6ec02fa-f335-47cb-8081-80270bd5f5ab"); // crow
    public static final UUID K_KING = UUID.fromString("73df41fb-7ffb-417e-beab-f9589aaffe74"); // K_king
    public static final UUID COSMIC = UUID.fromString("e0c801c3-4de4-47ad-b51b-ef6dc1b78eff"); // cosmic_fire
    public static final UUID NANO = UUID.fromString("bc130267-59b4-4d96-8397-0dd9b209a45f"); // nanowu


    // WOW NOT EVEN ME :SOB: - Tendo // sorry - monke

    public static final Set<UUID> PLAYERS = Set.of(
            LOQOR, DUZO, THEO, OURO, CLASSIC, ADDIE, VENWHOVIAN, MONKE, PAN, SATURN, MAGGIE, CROW, K_KING, DIAN, RHYNO, TREE, ECHO, NYX, LAKE, COSMIC, NANO
    );

    public static boolean isDev(UUID uuid) {
        return PLAYERS.contains(uuid);
    }
}
