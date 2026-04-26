package com.reis.telegraph.gui;

import com.reis.telegraph.config.TelegraphConfig;
import com.reis.telegraph.network.PacketHandler;
import com.reis.telegraph.network.packets.SendMessagePacket;
import com.reis.telegraph.network.packets.SetChannelPacket;
import com.reis.telegraph.network.packets.SetStationNamePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TelegraphScreen extends Screen {

    private final BlockPos machinePos;
    private final int currentChannel;
    private final String initialStationName;
    private final int lastSignalQuality;

    private EditBox messageBox;
    private EditBox channelBox;
    private EditBox stationNameBox;

    private static final int BG_WIDTH  = 220;
    private static final int BG_HEIGHT = 165; // expanded from 120 to fit station name + quality bar

    public TelegraphScreen(BlockPos machinePos, int currentChannel,
                           String stationName, int lastSignalQuality) {
        super(Component.translatable("gui.telegraph.title"));
        this.machinePos = machinePos;
        this.currentChannel = currentChannel;
        this.initialStationName = stationName == null ? "" : stationName;
        this.lastSignalQuality = lastSignalQuality;
    }

    @Override
    protected void init() {
        int left = (width - BG_WIDTH) / 2;
        int top  = (height - BG_HEIGHT) / 2;

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

        // Station name input
        stationNameBox = new EditBox(font, left + 10, top + 100, 160, 20,
                Component.translatable("gui.telegraph.station_name_placeholder"));
        stationNameBox.setMaxLength(32);
        stationNameBox.setValue(initialStationName);
        stationNameBox.setHint(Component.translatable("gui.telegraph.station_name_placeholder"));
        addWidget(stationNameBox);

        // Send button — shifted down to make room for station name row
        addRenderableWidget(Button.builder(
                Component.translatable("gui.telegraph.send"),
                btn -> onSendClicked()
        ).pos(left + 60, top + 133).size(100, 20).build());
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
        int top  = (height - BG_HEIGHT) / 2;

        // Background panel
        graphics.fill(left, top, left + BG_WIDTH, top + BG_HEIGHT, 0xCC000000);
        graphics.renderOutline(left, top, BG_WIDTH, BG_HEIGHT, 0xFF888888);

        // Title
        graphics.drawCenteredString(font, title, width / 2, top + 8, 0xFFFFFF);

        // Channel label
        graphics.drawString(font,
                Component.translatable("gui.telegraph.channel_label"),
                left + 10, top + 53, 0xAAAAAA);

        // Station name label
        graphics.drawString(font,
                Component.translatable("gui.telegraph.station_name_label"),
                left + 10, top + 88, 0xAAAAAA);

        // Signal quality bar — shown only after first send (quality >= 0) and when effects are on
        if (lastSignalQuality >= 0 && TelegraphConfig.ENABLE_QUALITY_EFFECTS.get()) {
            int barX = left + 10;
            int barY = top + 158;
            int barW = Math.max(1, lastSignalQuality); // 1–100 px wide
            int barColor = lastSignalQuality >= 60 ? 0xFF55FF55
                         : lastSignalQuality >= 30 ? 0xFFFFAA00 : 0xFFFF5555;
            graphics.fill(barX, barY, barX + barW, barY + 5, barColor);
            graphics.renderOutline(barX, barY, 100, 5, 0xFF555555);
            graphics.drawString(font,
                    Component.translatable("gui.telegraph.quality_label", lastSignalQuality),
                    left + 115, barY, 0xAAAAAA);
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        messageBox.render(graphics, mouseX, mouseY, partialTick);
        channelBox.render(graphics, mouseX, mouseY, partialTick);
        stationNameBox.render(graphics, mouseX, mouseY, partialTick);
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
        PacketHandler.sendToServer(new SetStationNamePacket(machinePos,
                stationNameBox.getValue().trim()));
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
