package net.kyrptonaught.quickshulker.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

// Server tells client to set used slot after opening container
public class SetUsedSlotPacket {
    public static final ResourceLocation SET_USED_SLOT_PACKET = new ResourceLocation(QuickShulkerMod.MOD_ID, "set_used_slot_packet");

    public static void sendUsedSlotPacket(ServerPlayer player, int playerInvIndex) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(playerInvIndex);
        ServerPlayNetworking.send(player, SET_USED_SLOT_PACKET, buf);
    }
}
