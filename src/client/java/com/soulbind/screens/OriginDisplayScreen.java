package com.soulbind.screens;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.soulbind.SoulBind;
import com.soulbind.abilities.importantforregistering.AbilityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.network.listener.ClientPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class OriginDisplayScreen extends Screen {
    private static final Identifier WINDOW_BACKGROUND = SoulBind.identifier("choose_origin/background");
    private static final Identifier WINDOW_BORDER = SoulBind.identifier("choose_origin/border");
    private static final Identifier WINDOW_SCROLL_BAR_SLOT = SoulBind.identifier("choose_origin/scroll_bar/slot");
    private static final Identifier WINDOW_SCROLL_BAR = SoulBind.identifier("choose_origin/scroll_bar");
    private static final Identifier WINDOW_SCROLL_BAR_PRESSED = SoulBind.identifier("choose_origin/scroll_bar/pressed");

    protected static final int WINDOW_WIDTH = 176;
    protected static final int WINDOW_HEIGHT = 182;

    protected final boolean showDirtBackground;

    // --- Your entries to display ---
    private final List<String> entries;

    // For scrolling
    private int scrollPos = 0;
    private int currentMaxScroll = 0;
    private boolean dragScrolling = false;
    private double mouseDragStart = 0;
    private int scrollDragStart = 0;
    private final Map<String, Identifier> skins = new HashMap<>();


    protected int guiTop, guiLeft;

    // Constants for entry rendering
    private static final int ENTRY_HEIGHT = 30;
    private static final int VISIBLE_ENTRIES = 4;
    private static final int ENTRY_START_Y = 20;
    private static final int ENTRY_X = 20;
    private static final int ENTRY_WIDTH = WINDOW_WIDTH - 40;

    public OriginDisplayScreen(Text title, List<String> entries) {
        super(title);
        this.entries = entries;
        this.showDirtBackground = false;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - WINDOW_WIDTH) / 2;
        this.guiTop = (this.height - WINDOW_HEIGHT) / 2;


        for (String playerName : entries) {
            CompletableFuture.runAsync(() -> {
                try {
                    UUID uuid = getOnlinePlayerUUID(playerName);
                    if (uuid != null) {
                        SkinTextures skinTextures = client.getSkinProvider().getSkinTextures(new GameProfile(uuid, playerName));
                        if (skinTextures != null) {
                            skins.put(playerName, skinTextures.texture());
                        }
                    }
                } catch (Exception e) {
                   e.printStackTrace();
                }
            });
        }

        int totalHeight = entries.size() * ENTRY_HEIGHT;
        int visibleHeight = VISIBLE_ENTRIES * ENTRY_HEIGHT;
        this.currentMaxScroll = Math.max(0, totalHeight - visibleHeight);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (showDirtBackground) {
            super.renderBackground(context, mouseX, mouseY, delta);
        } else {
            this.renderInGameBackground(context);
        }
    }

    @Override
    public void renderInGameBackground(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, 1678774288, -2112876528);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        super.render(context, mouseX, mouseY, delta);
        renderOriginWindow(context, mouseX, mouseY, delta);
    }

    protected void renderOriginWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw window background and border
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WINDOW_BACKGROUND, guiLeft, guiTop, WINDOW_WIDTH, WINDOW_HEIGHT);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WINDOW_BORDER, guiLeft, guiTop, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Draw title centered above window
        context.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, guiTop - 15, 0xFFFFFF);

        // Draw entries (scrollable)
        int y = guiTop + ENTRY_START_Y - scrollPos;
        int mouseRelX = mouseX - guiLeft;
        int mouseRelY = mouseY - guiTop;

        // Render visible entries only
        int startEntryIndex = scrollPos / ENTRY_HEIGHT;
        int endEntryIndex = Math.min(entries.size(), startEntryIndex + VISIBLE_ENTRIES + 1);

        for (int i = startEntryIndex; i < endEntryIndex; i++) {
            int entryY = guiTop + ENTRY_START_Y + (i * ENTRY_HEIGHT) - scrollPos;

            boolean hovered = mouseRelX >= ENTRY_X && mouseRelX < ENTRY_X + ENTRY_WIDTH &&
                    mouseRelY >= entryY - guiTop && mouseRelY < entryY - guiTop + ENTRY_HEIGHT;

            if (hovered) {
                context.fill(guiLeft + ENTRY_X, entryY, guiLeft + ENTRY_X + ENTRY_WIDTH, entryY + ENTRY_HEIGHT, 0x88000000);
            }

            String playerName = entries.get(i);


            Identifier skinTexture;



            if (skins.containsKey(playerName)) {
                skinTexture = skins.get(playerName);
            } else {
                skinTexture = DefaultSkinHelper.getTexture();
            }

            int headSize = ENTRY_HEIGHT;
            int headX = guiLeft + ENTRY_X;
            int headY = entryY;

            // Draw face layer (8x8)
            context.drawTexture(RenderPipelines.GUI_TEXTURED, skinTexture,
                    headX, headY,
                    8, 8,
                    headSize, headSize,
                    8, 8,
                    64, 64);

            // Draw hat layer (40x8)
            context.drawTexture(RenderPipelines.GUI_TEXTURED, skinTexture,
                    headX, headY,
                    40, 8,
                    headSize, headSize,
                    8, 8,
                    64, 64);

            // Draw player name
            int textX = headX + headSize + 5;
            context.drawText(textRenderer,
                    Text.literal(playerName).formatted(hovered ? Formatting.YELLOW : Formatting.WHITE),
                    textX, entryY + (ENTRY_HEIGHT - 8) / 2, 0xFFFFFFFF, false);
        }
        // Draw scrollbar
        renderScrollbar(context, mouseX, mouseY);
    }


    public static GameProfile getGameProfileByName(String playerName) {
        MinecraftClient mc = MinecraftClient.getInstance();

        for (PlayerListEntry playerListEntry : mc.getNetworkHandler().getPlayerList()) {
            // Get the display name as a string
            String displayName = playerListEntry.getDisplayName() != null ?
                    playerListEntry.getDisplayName().getString() : playerListEntry.getProfile().getName();

            if (displayName.equalsIgnoreCase(playerName)) {
                return playerListEntry.getProfile();
            }
        }

        // Return null if player not found
        return null;
    }

    protected void renderScrollbar(DrawContext context, int mouseX, int mouseY) {
        if (currentMaxScroll <= 0) return;

        // Draw scrollbar slot
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WINDOW_SCROLL_BAR_SLOT, guiLeft + 155, guiTop + 35, 8, 134);

        int scrollbarY = 36;
        int maxScrollbarOffset = 141;

        scrollbarY += (int) ((maxScrollbarOffset - scrollbarY) * (scrollPos / (float) currentMaxScroll));

        Identifier scrollBarTexture = this.dragScrolling || canDragScroll(mouseX, mouseY, scrollbarY) ? WINDOW_SCROLL_BAR_PRESSED : WINDOW_SCROLL_BAR;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, scrollBarTexture, guiLeft + 156, guiTop + scrollbarY, 6, 27);
    }

    protected boolean canDragScroll(double mouseX, double mouseY, int scrollBarY) {
        return (mouseX >= guiLeft + 156 && mouseX < guiLeft + 156 + 6)
                && (mouseY >= guiTop + scrollBarY && mouseY < guiTop + scrollBarY + 27);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if clicked on scrollbar drag handle
        if (currentMaxScroll > 0) {
            int scrollBarY = 36 + (int)((141 - 36) * (scrollPos / (float) currentMaxScroll));
            if (canDragScroll(mouseX, mouseY, scrollBarY)) {
                this.dragScrolling = true;
                this.scrollDragStart = scrollBarY;
                this.mouseDragStart = mouseY;
                return true;
            }
        }

        // Check if clicked on an entry
        int mouseRelX = (int) mouseX - guiLeft;
        int mouseRelY = (int) mouseY - guiTop;
        if (mouseRelX >= ENTRY_X && mouseRelX < ENTRY_X + ENTRY_WIDTH) {
            int relativeY = mouseRelY + scrollPos - ENTRY_START_Y;
            if (relativeY >= 0) {
                int clickedIndex = relativeY / ENTRY_HEIGHT;
                if (clickedIndex >= 0 && clickedIndex < entries.size()) {
                    // Open new screen for that entry

                    List<String> stringStream = Arrays.stream(AbilityType.values()).map(AbilityType::asString).toList();

                    if (this.client != null) {
                        this.client.setScreen(new AbilitySelectScreen(Text.empty(), stringStream, entries.get(clickedIndex)));
                    }

                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.dragScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!dragScrolling) {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        int delta = (int) (mouseY - mouseDragStart);
        int newScrollPos = MathHelper.clamp(scrollDragStart + delta, 36, 141);

        float part = (newScrollPos - 36) / (float)(141 - 36);
        this.scrollPos = (int)(part * currentMaxScroll);

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        this.scrollPos -= (int)vertical * ENTRY_HEIGHT;
        this.scrollPos = MathHelper.clamp(scrollPos, 0, currentMaxScroll);
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }


    public static UUID getOnlinePlayerUUID(String name) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) return null;

        for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
            if (entry.getProfile().getName().equalsIgnoreCase(name)) {
                return entry.getProfile().getId();
            }
        }
        return null;
    }

    // --- Simple detail screen for entry ---

    public static class EntryDetailScreen extends Screen {
        private final Text entryText;
        private final Screen parent;

        public EntryDetailScreen(Text entryText, Screen parent) {
            super(Text.literal("Entry Details"));
            this.entryText = entryText;
            this.parent = parent;
        }

        @Override
        protected void init() {
            this.addDrawableChild(ButtonWidget.builder(Text.of("Back"), button -> this.client.setScreen(parent))
                    .dimensions(this.width / 2 - 50, this.height - 40, 100, 20).build());
        }

        public void renderBackground(DrawContext context) {
            context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context);
            context.drawCenteredTextWithShadow(this.textRenderer, entryText, this.width / 2, this.height / 2 - 10, 0xFFFFFF);
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }
    }
}