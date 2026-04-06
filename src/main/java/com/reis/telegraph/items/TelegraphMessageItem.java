package com.reis.telegraph.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TelegraphMessageItem extends Item {

    public TelegraphMessageItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                 List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        String sender = tag.getString("SenderName");
        String senderStation = tag.getString("SenderStation");
        int channel = tag.getInt("Channel");
        long timestamp = tag.getLong("Timestamp");
        String message = tag.getString("MessageText");

        if (!sender.isEmpty()) {
            tooltip.add(Component.translatable("gui.telegraph.from", sender)
                    .withStyle(ChatFormatting.GRAY));
        }
        if (!senderStation.isEmpty()) {
            tooltip.add(Component.translatable("gui.telegraph.station", senderStation)
                    .withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable("gui.telegraph.channel", channel)
                .withStyle(ChatFormatting.GRAY));

        long day = timestamp / 24000L + 1;
        tooltip.add(Component.translatable("gui.telegraph.day", day)
                .withStyle(ChatFormatting.DARK_GRAY));

        if (!message.isEmpty()) {
            String preview = message.length() > 40 ? message.substring(0, 40) + "..." : message;
            tooltip.add(Component.literal("\"" + preview + "\"")
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            openReadScreen(stack);
        }
        return InteractionResultHolder.success(stack);
    }

    // Called client-side only — separated to avoid classloading issues
    private void openReadScreen(ItemStack stack) {
        net.minecraft.client.Minecraft.getInstance()
                .setScreen(new com.reis.telegraph.gui.TelegraphReadScreen(stack));
    }

    /** Utility: build a Telegram ItemStack from raw data */
    public static ItemStack create(String message, String sender, String senderStation,
                                   int channel, long timestamp) {
        ItemStack stack = new ItemStack(com.reis.telegraph.registration.ModItems.TELEGRAM.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("MessageText", message);
        tag.putString("SenderName", sender);
        tag.putString("SenderStation", senderStation == null ? "" : senderStation);
        tag.putInt("Channel", channel);
        tag.putLong("Timestamp", timestamp);
        stack.setTag(tag);
        return stack;
    }
}
