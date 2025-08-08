package com.soulbind.screens;

import com.soulbind.SoulBind;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class OriginDisplayScreen extends Screen {

    private static final Identifier WINDOW_BACKGROUND = SoulBind.identifier("choose_origin/background");
    private static final Identifier WINDOW_BORDER = SoulBind.identifier("choose_origin/border");
    private static final Identifier WINDOW_NAME_PLATE = SoulBind.identifier("choose_origin/name_plate");
    private static final Identifier WINDOW_SCROLL_BAR = SoulBind.identifier("choose_origin/scroll_bar");
    private static final Identifier WINDOW_SCROLL_BAR_PRESSED = SoulBind.identifier("choose_origin/scroll_bar/pressed");
    private static final Identifier WINDOW_SCROLL_BAR_SLOT = SoulBind.identifier("choose_origin/scroll_bar/slot");

    protected static final int WINDOW_WIDTH = 176;
    protected static final int WINDOW_HEIGHT = 182;



    protected final boolean showDirtBackground;



    private boolean dragScrolling = false;

    private double mouseDragStart = 0;

    private int currentMaxScroll = 0;
    private int scrollDragStart = 0;

    protected int guiTop, guiLeft;
    protected int scrollPos = 0;

    public OriginDisplayScreen(Text title, List<String> stringList) {
        super(title);
        this.showDirtBackground = false;
    }




    @Override
    protected void init() {

        super.init();

        this.guiLeft = (this.width - WINDOW_WIDTH) / 2;
        this.guiTop = (this.height - WINDOW_HEIGHT) / 2;


    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {

        if (showDirtBackground) {
            super.renderBackground(context, mouseX, mouseY, delta);
        }

        else {
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
        this.renderOriginWindow(context, mouseX, mouseY, delta);

    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.dragScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        boolean mouseClicked = super.mouseClicked(mouseX, mouseY, button);
        if (cannotScroll()) {
            return mouseClicked;
        }

        this.dragScrolling = false;

        int scrollBarY = 36;
        int maxScrollBarOffset = 141;

        scrollBarY += (int) ((maxScrollBarOffset - scrollBarY) * (scrollPos / (float) currentMaxScroll));
        if (!canDragScroll(mouseX, mouseY, scrollBarY)) {
            return mouseClicked;
        }

        this.dragScrolling = true;
        this.scrollDragStart = scrollBarY;
        this.mouseDragStart = mouseY;

        return true;

    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

        boolean mouseDragged = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        if (!dragScrolling) {
            return mouseDragged;
        }

        int delta = (int) (mouseY - mouseDragStart);
        int newScrollPos = Math.max(36, Math.min(141, scrollDragStart + delta));

        float part = (newScrollPos - 36) / (float) (141 - 36);
        this.scrollPos = (int) (part * currentMaxScroll);

        return mouseDragged;

    }

    @Override
    public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {

        int newScrollPos = this.scrollPos - (int) vertical * 4;
        this.scrollPos = MathHelper.clamp(newScrollPos, 0, this.currentMaxScroll);

        return super.mouseScrolled(x, y, horizontal, vertical);

    }



    protected void renderScrollbar(DrawContext context, int mouseX, int mouseY) {

        if (cannotScroll()) {
            return;
        }

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WINDOW_SCROLL_BAR_SLOT, guiLeft + 155, guiTop + 35, 8, 134);

        int scrollbarY = 36;
        int maxScrollbarOffset = 141;

        scrollbarY += (int) ((maxScrollbarOffset - scrollbarY) * (scrollPos / (float) currentMaxScroll));

        Identifier scrollBarTexture = this.dragScrolling || canDragScroll(mouseX, mouseY, scrollbarY) ? WINDOW_SCROLL_BAR_PRESSED : WINDOW_SCROLL_BAR;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, scrollBarTexture, guiLeft + 156, guiTop + scrollbarY, 6, 27);

    }

    protected boolean cannotScroll() {
        return currentMaxScroll <= 0;
    }

    protected boolean canDragScroll(double mouseX, double mouseY, int scrollBarY) {
        return (mouseX >= guiLeft + 156 && mouseX < guiLeft + 156 + 6)
                && (mouseY >= guiTop + scrollBarY && mouseY < guiTop + scrollBarY + 27);
    }



    protected boolean isWithinWindowBoundaries(int mouseX, int mouseY) {
        return (mouseX >= guiLeft && mouseX < guiLeft + WINDOW_WIDTH)
                && (mouseY >= guiTop && mouseY < guiTop + WINDOW_HEIGHT);
    }

    protected Text getTitleText() {
        return Text.of("Origins");
    }

    protected void renderOriginWindow(DrawContext context, int mouseX, int mouseY, float delta) {

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WINDOW_BACKGROUND, guiLeft, guiTop, -4, WINDOW_WIDTH, WINDOW_HEIGHT);

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WINDOW_BORDER, guiLeft, guiTop, 2, WINDOW_WIDTH, WINDOW_HEIGHT);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, WINDOW_NAME_PLATE, guiLeft + 10, guiTop + 10, 2, 150, 26);



    }






}
