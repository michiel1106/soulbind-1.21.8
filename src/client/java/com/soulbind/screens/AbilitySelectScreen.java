package com.soulbind.screens;

import com.soulbind.SoulBind;
import com.soulbind.abilities.Ability;
import com.soulbind.abilities.importantforregistering.AbilityType;
import com.soulbind.packets.SoulmateInvitePacketC2S;
import com.soulbind.util.ModUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.UUID;

public class AbilitySelectScreen extends Screen{



    private static final Identifier WINDOW_BACKGROUND = SoulBind.identifier("choose_origin/background");
    private static final Identifier WINDOW_BORDER = SoulBind.identifier("choose_origin/border");
    private static final Identifier WINDOW_SCROLL_BAR_SLOT = SoulBind.identifier("choose_origin/scroll_bar/slot");
    private static final Identifier WINDOW_SCROLL_BAR = SoulBind.identifier("choose_origin/scroll_bar");
    private static final Identifier WINDOW_SCROLL_BAR_PRESSED = SoulBind.identifier("choose_origin/scroll_bar/pressed");

    protected static final int WINDOW_WIDTH = 176;
    protected static final int WINDOW_HEIGHT = 182;


    private String playername;
    protected final boolean showDirtBackground;

    // --- Your entries to display ---
    private final List<String> abilityEntries;

    // For scrolling
    private int scrollPos = 0;
    private int currentMaxScroll = 0;
    private boolean dragScrolling = false;
    private double mouseDragStart = 0;
    private int scrollDragStart = 0;
    private int selectedIndex = -1;



    protected int guiTop, guiLeft;
    private ButtonWidget confirmButton;
    // Constants for entry rendering
    private static final int ENTRY_HEIGHT = 40;
    private static final int VISIBLE_ENTRIES = 2;
    private static final int ENTRY_START_Y = 20;
    private static final int ENTRY_X = 20;
    private static final int ENTRY_WIDTH = WINDOW_WIDTH - 40;

    public AbilitySelectScreen(Text title, List<String> abilityEntries, String playername) {
        super(title);
        this.abilityEntries = abilityEntries;
        this.showDirtBackground = false;
        this.playername = playername;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - WINDOW_WIDTH) / 2;
        this.guiTop = (this.height - WINDOW_HEIGHT) / 2;

        int totalHeight = abilityEntries.size() * ENTRY_HEIGHT;
        int visibleHeight = VISIBLE_ENTRIES * ENTRY_HEIGHT;
        this.currentMaxScroll = Math.max(0, totalHeight - visibleHeight);

        // Add Confirm button
        int buttonWidth = 100;
        int buttonHeight = 20;
        int buttonX = this.width / 2 - buttonWidth / 2;
        int buttonY = this.guiTop + WINDOW_HEIGHT + 10;

        this.confirmButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Confirm"), (onpress) -> {
            if (selectedIndex >= 0 && selectedIndex < abilityEntries.size()) {
                String abilityId = abilityEntries.get(selectedIndex);
                AbilityType abilityType = ModUtils.getAbilityTypeById(abilityId);

                if (abilityType != null) {
                    Ability abilityInstance = abilityType.createInstance();
                    // Now you have the current Ability instance, do whatever you want
                    CustomPayload customPayload = new SoulmateInvitePacketC2S(playername, abilityInstance.getId());

                    ClientPlayNetworking.send(customPayload);

                    // Example: your code here
                }

            }
        }).dimensions(buttonX, buttonY, buttonWidth, buttonHeight).build());



        this.confirmButton.active = false;

        // You can also add a "Cancel" button or "Back" button as needed
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

        int startEntryIndex = scrollPos / ENTRY_HEIGHT;
        int endEntryIndex = Math.min(abilityEntries.size(), startEntryIndex + VISIBLE_ENTRIES + 1);

        for (int i = startEntryIndex; i < endEntryIndex; i++) {
            int entryY = guiTop + ENTRY_START_Y + (i * ENTRY_HEIGHT) - scrollPos;

            if (entryY + ENTRY_HEIGHT < guiTop || entryY > guiTop + WINDOW_HEIGHT) {
                // Entry is completely above or below the window; skip rendering it
                continue;
            }

            boolean hovered = mouseRelX >= ENTRY_X && mouseRelX < ENTRY_X + ENTRY_WIDTH &&
                    mouseRelY >= entryY - guiTop && mouseRelY < entryY - guiTop + ENTRY_HEIGHT;

            // Highlight hovered
            if (hovered) {
                context.fill(guiLeft + ENTRY_X, entryY, guiLeft + ENTRY_X + ENTRY_WIDTH, entryY + ENTRY_HEIGHT, 0x88000000);
            }

            // Highlight selected
            if (i == selectedIndex) {
                context.fill(guiLeft + ENTRY_X, entryY, guiLeft + ENTRY_X + ENTRY_WIDTH, entryY + ENTRY_HEIGHT, 0x88000000);
            }

            String abilityEntry = abilityEntries.get(i);
            AbilityType abilityType = ModUtils.getAbilityTypeById(abilityEntry);

            Identifier image;
            if (abilityType != null) {
                Ability instance = abilityType.createInstance();
                image = instance.getImage();
            } else {
                image = new Ability().getImage();
            }

            int headSize = ENTRY_HEIGHT;
            int headX = guiLeft + ENTRY_X;
            int headY = entryY;

            context.drawTexture(RenderPipelines.GUI_TEXTURED, image, headX, headY, 0, 0, 30, 30, 64, 64, 64, 64);

            int textX = headX + headSize - 8;
            if (abilityType != null) {
                Ability instance = abilityType.createInstance();

                context.drawText(
                        textRenderer,
                        instance.getName().formatted(i == selectedIndex ? Formatting.GOLD : (hovered ? Formatting.YELLOW : Formatting.WHITE)),
                        textX,
                        entryY,
                        0xFFFFFFFF,
                        false
                );

                int descY = entryY + 9;
                for (OrderedText line : instance.getDescription()) {
                    context.drawText(textRenderer, line, textX, descY, 0xFFAAAAAA, false);
                    descY += 12;
                }
            }
        }

        // Draw scrollbar
        renderScrollbar(context, mouseX, mouseY);
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

        int mouseRelX = (int) mouseX - guiLeft;
        int mouseRelY = (int) mouseY - guiTop;

        if (mouseRelX >= ENTRY_X && mouseRelX < ENTRY_X + ENTRY_WIDTH) {
            int relativeY = mouseRelY + scrollPos - ENTRY_START_Y;
            if (relativeY >= 0) {
                int clickedIndex = relativeY / ENTRY_HEIGHT;
                if (clickedIndex >= 0 && clickedIndex < abilityEntries.size()) {
                    if (button == 0) { // Left click = select entry
                        this.selectedIndex = clickedIndex;
                        this.confirmButton.active = true;
                        // Optional: play click sound here
                        return true;
                    }
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
