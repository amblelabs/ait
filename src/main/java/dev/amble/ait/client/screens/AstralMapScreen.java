package dev.amble.ait.client.screens;

import java.util.*;

import dev.amble.ait.client.screens.widget.CallbackCheckboxWidget;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blocks.AstralMapBlock;
import dev.amble.ait.core.util.WorldUtil;
import net.minecraft.util.Language;
import org.lwjgl.glfw.GLFW;

public class AstralMapScreen extends Screen {

    private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/astral_map.png");
    private static final Text SEARCH_TEXT = Text.translatable("gui.socialInteractions.search_hint")
            .formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
    int bgHeight = 190;
    int bgWidth = 324;
    int left, top;

    private TextFieldWidget searchBox;
    private AstralMapListWidget entryList;
    private CallbackCheckboxWidget showStructuresCheckbox;
    private CallbackCheckboxWidget showBiomesCheckbox;

    private static final Text SHOW_STRUCTURES_MESSAGE = Text.translatable("screen.ait.astral_map.show_structures");
    private static final Text SHOW_BIOMES_MESSAGE = Text.translatable("screen.ait.astral_map.show_biomes");

    public enum DisplayedCategories {
        STRUCTURES,
        BIOMES,
        BOTH,
    }

    public AstralMapScreen() {
        super(Text.translatable("screen." + AITMod.MOD_ID + ".astral_map"));
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        this.top = (this.height - this.bgHeight) / 2; // this means everything's centered and scaling, same for below
        this.left = (this.width - this.bgWidth) / 2;
        super.init();

        this.searchBox = new TextFieldWidget(this.client.textRenderer, this.left + 12,
                this.top + 13, this.bgWidth - 26, 15, SEARCH_TEXT);
        this.searchBox.setPlaceholder(SEARCH_TEXT);
        this.searchBox.setText("");
        this.searchBox.setChangedListener(this::onSearchChange);

        this.showStructuresCheckbox = new CallbackCheckboxWidget(this.left + 11, this.top + 33,
                20, 20, SHOW_STRUCTURES_MESSAGE, true, this::onShowStructuresChecked);
        this.showBiomesCheckbox = new CallbackCheckboxWidget(
                this.left + this.client.textRenderer.getWidth(SHOW_STRUCTURES_MESSAGE) + 38, this.top + 33,
                20, 20, SHOW_BIOMES_MESSAGE, true, this::onShowBiomesChecked);
        this.entryList = new AstralMapListWidget(width, height, top + 63,
                (height + bgHeight) / 2 - 9, 13);

        this.addSelectableChild(searchBox);
        this.addDrawableChild(showStructuresCheckbox);
        this.addDrawableChild(showBiomesCheckbox);
        this.addSelectableChild(entryList);
        this.setInitialFocus(searchBox);
    }

    private void onSearchChange(String currentSearch) {
        this.entryList.setCurrentSearch(currentSearch);
    }

    private void onShowStructuresChecked(CallbackCheckboxWidget checkbox) {
        boolean showBothCategories = checkbox.isChecked() || !this.showBiomesCheckbox.isChecked();
        this.showBiomesCheckbox.active = showBothCategories;
        this.entryList.setShownCategory(showBothCategories ? DisplayedCategories.BOTH : DisplayedCategories.BIOMES);
    }

    private void onShowBiomesChecked(CallbackCheckboxWidget checkbox) {
        boolean showBothCategories = checkbox.isChecked() || !this.showStructuresCheckbox.isChecked();
        this.showStructuresCheckbox.active = showBothCategories;
        this.entryList.setShownCategory(showBothCategories ? DisplayedCategories.BOTH : DisplayedCategories.STRUCTURES);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.searchBox.isFocused() && this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            var entry = this.entryList.getFocused();
            if (entry != null) {
                this.exit(entry);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void exit(AstralMapListWidget.Entry entry) {
        var packetByteBuf = PacketByteBufs.create().writeIdentifier(entry.identifier);
        packetByteBuf.writeBoolean(AstralMapBlock.structureIds.contains(entry.identifier));
        ClientPlayNetworking.send(AstralMapBlock.REQUEST_SEARCH, packetByteBuf);
        this.client.setScreen(null);
    }

    @Override
    public void tick() {
        super.tick();
        this.searchBox.tick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(RenderLayer.getEndGateway(), this.left + 4, this.top + 4, this.left + this.bgWidth - 4,
                this.top + this.bgHeight - 4, 0xFFFFFF);
        context.fill(left + 2, top + 62, left + bgWidth - 4, top + bgHeight - 4, 0xAA000000);
        context.drawTexture(TEXTURE, left, top, 0, 0, bgWidth, bgHeight, bgWidth, bgHeight);

        this.searchBox.render(context, mouseX, mouseY, delta);
        this.entryList.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    /**
     * Widget to display a searchable list of biomes and structures to locate
     */
     class AstralMapListWidget extends AlwaysSelectedEntryListWidget<AstralMapListWidget.Entry> {

        private DisplayedCategories shownCategory = DisplayedCategories.BOTH;
        private final List<Entry> entries = new ArrayList<>();
        private final Map<String, String> mods = new HashMap<>();
        private String currentSearch = "";

        // Used to check if the mouse has moved so that the top element is always focused when searching,
        // but moving the mouse will switch focus to the element under the mouse.
        private int lastMouseX, lastMouseY;
        private boolean shouldHover;

        public AstralMapListWidget(int width, int height, int top, int bottom, int elementHeight) {
            super(AstralMapScreen.this.client, width, height, top, bottom, elementHeight);
            this.setRenderHorizontalShadows(false);
            this.setRenderBackground(false);

            this.refreshEntries();
            this.replaceEntries(this.entries);
            this.setFocused(this.getFirst());
        }

        public String getModName(String modId) {
            if (mods.containsKey(modId)) return mods.get(modId);
            String modName = FabricLoader.getInstance().getModContainer(modId)
                    .map(mod -> mod.getMetadata().getName()).orElse(modId);
            mods.put(modId, modName);
            return modName;
        }

        public void refreshEntries() {
            this.entries.clear();
            if (shownCategory != DisplayedCategories.STRUCTURES) {
                for (Identifier id : client.world.getRegistryManager().get(RegistryKeys.BIOME).getIds()) {
                    this.entries.add(new Entry(id, true));
                }
            }
            if (shownCategory != DisplayedCategories.BIOMES) {
                for (Identifier id : AstralMapBlock.structureIds) {
                    this.entries.add(new Entry(id, false));
                }
            }
            this.entries.sort((e1, e2) -> e1.text.getString().compareToIgnoreCase(e2.text.getString()));
        }

        public void setCurrentSearch(String currentSearch) {
            this.currentSearch = currentSearch;
            this.refreshEntries();
            String[] splitSearch = currentSearch.split(" ");
            for (String substring : splitSearch) {
                if (substring.isEmpty()) break;
                // Allow users to search by mod by prefixing their search with "@"
                if (substring.charAt(0) == '@') {
                    if (substring.length() < 2) continue;
                    this.entries.removeIf(entry -> !entry.identifier.getNamespace()
                            .contains(substring.substring(1).strip().toLowerCase(Locale.ROOT)));
                } else {
                    this.entries.removeIf(entry -> !entry.text.toString().toLowerCase(Locale.ROOT)
                            .contains(substring.toLowerCase(Locale.ROOT)));
                }
            }
            this.replaceEntries(this.entries);
            if (!this.children().isEmpty()) {
                this.setFocused(this.getFirst());
            }
            if (!currentSearch.isEmpty()) {
                this.setScrollAmount(0);
            }
        }

        public void setShownCategory(DisplayedCategories shownCategory) {
            this.shownCategory = shownCategory;
            this.setCurrentSearch(this.currentSearch);
            this.replaceEntries(this.entries);
            if (shownCategory != DisplayedCategories.BOTH) {
                this.setScrollAmount(0);
            }
        }

        @Override
        public int getRowWidth() {
            return 290 + (this.getMaxScroll() > 0 ? 0 : 10); // Adjust for scrollbar width
        }

        @Override
        protected int getScrollbarPositionX() {
            return (this.width + bgWidth) / 2 - 18;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            // Adjust for scrollbar width
            this.left = this.getMaxScroll() > 0 ? -5 : 0;

            if (this.lastMouseX == 0 && this.lastMouseY == 0) {
                this.lastMouseX = mouseX;
                this.lastMouseY = mouseY;
            }
            super.render(context, mouseX, mouseY, delta);
            // Avoid the mouse switching focus when searching if it isn't moving
            this.shouldHover = (mouseX != this.lastMouseX || mouseY != this.lastMouseY);
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }

        /**
         * Entry representing a biome or structure
         */
        class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {

            private final Identifier identifier;
            private final Text text;
            private final String modName;

            public Entry(Identifier identifier, boolean isBiome) {
                this.identifier = identifier;
                // Only biomes have actual translation keys and some modded ones might not
                if (isBiome) {
                    String translationKey = identifier.toTranslationKey("biome");
                    if (Language.getInstance().hasTranslation(translationKey)) {
                        this.text = Text.translatable(translationKey);
                    } else {
                        this.text = Text.literal(identifierToName(identifier));
                    }
                } else {
                    this.text = Text.literal(identifierToName(identifier));
                }
                this.modName = getModName(identifier.getNamespace());
            }

            public static String identifierToName(Identifier id) {
                try {
                    return WorldUtil.fakeTranslate(id.getPath());
                } catch (Exception e) {
                    return id.toString();
                }
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth,
                               int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                int modNameWidth = client.textRenderer.getWidth(this.modName);
                context.drawText(client.textRenderer, this.modName,
                        x + getRowWidth() - modNameWidth - 4, y, Colors.GRAY, false);

                context.drawText(client.textRenderer, this.text, x + 2, y, Colors.WHITE, false);

                if (hovered && AstralMapListWidget.this.shouldHover) {
                    AstralMapListWidget.this.setFocused(this);
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    AstralMapScreen.this.exit(this);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public Text getNarration() {
                return this.text;
            }
        }

    }
}
