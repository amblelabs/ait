package dev.amble.ait.core.devteam;

import dev.amble.ait.AITMod;
import dev.amble.verification.BetaVerification;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ClientDevTeam {

    public static final Verify VERIFY = new Verify();

    public static class Verify extends BetaVerification {

        public static Text TEXT_PLAY = Text.translatable("text.ait.beta.play");
        public static Text TEXT_TOOLTIP = Text.translatable("text.ait.beta.play.tooltip");
        public static Text TEXT_BROWSER = Text.translatable("text.ait.beta.play.browser");

        private String branch;

        public Verify() {
            super(AITMod.class, AITMod.MOD_ID);
        }

        public String getBranch() {
            return branch;
        }

        @Override
        protected BuildType getBuildTypeForBranch(String branch) {
            // some-branch-name-dev
            int idx = branch.lastIndexOf('-');
            this.branch = idx != -1 ? branch.substring(0, idx) : branch;
            return super.getBuildTypeForBranch(branch);
        }

        @Override
        public boolean isLocked() {
            return !DevTeam.isDev() && super.isLocked();
        }
    }
}