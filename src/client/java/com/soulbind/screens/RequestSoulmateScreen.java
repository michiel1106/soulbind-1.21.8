package com.soulbind.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;

public class RequestSoulmateScreen extends Screen {
    private List<String> stringList;

    public RequestSoulmateScreen(Text title, List<String> stringList) {
        super(title);
        this.stringList = stringList;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        int baseY = this.height / 2 - (stringList.size() * 10); // move starting point upward so list centers nicely


        context.drawText(MinecraftClient.getInstance().textRenderer, "aaaaa", mouseX, mouseY, 0xFFFFFF, false);

        System.out.println(mouseX + " " + mouseY);

        for (int i = 0; i < stringList.size(); i++) {
            String string = stringList.get(i);
            int x = this.width / 2 - MinecraftClient.getInstance().textRenderer.getWidth(string) / 2; // center text
            int y = baseY + i * 20;

           // System.out.println("string: " + string + " at: " + x + " " + y);

            context.drawItem(new ItemStack(Items.ACACIA_FENCE), 402, 230);


            context.drawText(MinecraftClient.getInstance().textRenderer, string, x, y, 0xFFFFFF, true);
        }




    }


}
