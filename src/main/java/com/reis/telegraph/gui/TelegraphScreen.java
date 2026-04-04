package com.reis.telegraph.gui;

import com.reis.telegraph.network.PacketHandler;
import com.reis.telegraph.network.packets.SendMessagePacket;
import com.reis.telegraph.network.packets.SetChannelPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class TelegraphScreen extends Screen {

    private final BlockPos machinePos;
    private final int currentChannel;

    private EditBox messageBox;
    private EditBox channelBox;

    private static final int BG_WIDTH = 220;
    private static final int BG_HEIGHT = 120;

    public TelegraphScreen(BlockPos machinePos, int currentChannel) {
        super(Component.translatable("gui.telegraph.title"));
        this.machinePos = machinePos;
        this.currentChannel = currentChannel;
    }

    @Override
    protected void init() {
        int left = (width - BG_WIDTH) / 2;
        int top = (height - BG_HEIGHT) / 2;

        // Message input
        messageBox = new EditBox(font, left + 10, top + 30, 200, 20,
                Component.translatable("gui.telegraph.message_placeholder"));
        messageBox.setMaxLength(256);
        messageBox.setHint(Component.translatable("gui.telegraph.message_placeholder"));
        addWidget(messageBox);
        setInitialFocus(messageBox);

        // Channel input — digits only, max 2 chars
        channelBox = new EditBox(font, left + 10, top + 65, 40, 20,
                Component.literal(String.valueOf(currentChannel)));
        channelBox.setMaxLength(2);
        channelBox.setValue(String.valueOf(currentChannel));
        channelBox.setFilter(s -> s.matches("\\d*")); // digits only
        addWidget(channelBox);

        // Send button
        addRenderableWidget(Button.builder(
                Component.translatable("gui.telegraph.send"),
                btn -> onSendClicked()
        ).pos(left + 60, top + 88).size(100, 20).build());
    }

    private void onSendClicked() {
        String message = messageBox.getValue().trim();
        if (message.isEmpty()) return;

        int channel;
        try {
            channel = Integer.parseInt(channelBox.getValue());
            channel = Math.max(0, Math.min(99, channel));
        } catch (NumberFormatException e) {
            channel = 0;
        }

        PacketHandler.sendToServer(new SendMessagePacket(machinePos, message, channel));
        onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        int left = (width - BG_WIDTH) / 2;
        int top = (height - BG_HEIGHT) / 2;

        // Background panel
        graphics.fill(left, top, left + BG_WIDTH, top + BG_HEIGHT, 0xCC000000);
        graphics.renderOutline(left, top, BG_WIDTH, BG_HEIGHT, 0xFF888888);

        // Title
        graphics.drawCenteredString(font, title, width / 2, top + 8, 0xFFFFFF);

        // Channel label
        graphics.drawString(font,
                Component.translatable("gui.telegraph.channel_label"),
                left + 10, top + 53, 0xAAAAAA);

        super.render(graphics, mouseX, mouseY, partialTick);

        messageBox.render(graphics, mouseX, mouseY, partialTick);
        channelBox.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        int ch;
        try {
            ch = Math.max(0, Math.min(99, Integer.parseInt(channelBox.getValue().trim())));
        } catch (NumberFormatException e) {
            ch = currentChannel;
        }
        PacketHandler.sendToServer(new SetChannelPacket(machinePos, ch));
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
