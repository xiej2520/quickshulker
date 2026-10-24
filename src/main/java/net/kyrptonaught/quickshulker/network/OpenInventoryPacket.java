package net.kyrptonaught.quickshulker.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class OpenInventoryPacket {
    public static final ResourceLocation OPEN_INV = new ResourceLocation(QuickShulkerMod.MOD_ID, "openinv");

    public static void send(ServerPlayer player) {
        ServerPlayNetworking.send(player, OPEN_INV, new FriendlyByteBuf(Unpooled.buffer()));
    }

}
