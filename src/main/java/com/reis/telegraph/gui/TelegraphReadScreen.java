package com.reis.telegraph.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TelegraphReadScreen extends Screen {

    private final ItemStack stack;

    private static final int BG_WIDTH = 200;
    private static final int BG_HEIGHT = 160;

    public TelegraphReadScreen(ItemStack stack) {
        super(Component.translatable("gui.telegraph.telegram_title"));
        this.stack = stack;
    }

    @Override
    protected void init() {
        // No interactive widgets — read-only
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        int left = (width - BG_WIDTH) / 2;
        int top = (height - BG_HEIGHT) / 2;

        // Background panel
        graphics.fill(left, top, left + BG_WIDTH, top + BG_HEIGHT, 0xCCF5E6C8);
        graphics.renderOutline(left, top, BG_WIDTH, BG_HEIGHT, 0xFF8B6914);

        // Title
        graphics.drawCenteredString(font, title, width / 2, top + 8, 0x3B2000);

        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        String sender = tag.getString("SenderName");
        int channel = tag.getInt("Channel");
        long timestamp = tag.getLong("Timestamp");
        String message = tag.getString("MessageText");

        int textLeft = left + 10;
        int y = top + 24;
        int textWidth = BG_WIDTH - 20;

        // Sender
        if (!sender.isEmpty()) {
            graphics.drawString(font,
                    Component.translatable("gui.telegraph.from", sender).withStyle(ChatFormatting.DARK_GRAY),
                    textLeft, y, 0x3B2000, false);
            y += 12;
        }

        // Channel
        graphics.drawString(font,
                Component.translatable("gui.telegraph.channel", channel).withStyle(ChatFormatting.DARK_GRAY),
                textLeft, y, 0x3B2000, false);
        y += 12;

        // Day
        long day = timestamp / 24000L + 1;
        graphics.drawString(font,
                Component.translatable("gui.telegraph.day", day).withStyle(ChatFormatting.DARK_GRAY),
                textLeft, y, 0x3B2000, false);
        y += 16;

        // Divider
        graphics.fill(textLeft, y, left + BG_WIDTH - 10, y + 1, 0x668B6914);
        y += 6;

        // Message with word wrap
        if (!message.isEmpty()) {
            List<FormattedCharSequence> lines = font.split(
                    Component.literal(message).withStyle(ChatFormatting.BLACK),
                    textWidth);
            for (FormattedCharSequence line : lines) {
                if (y + 10 > top + BG_HEIGHT - 8) break; // clip overflow
                graphics.drawString(font, line, textLeft, y, 0x1A0A00, false);
                y += 10;
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
