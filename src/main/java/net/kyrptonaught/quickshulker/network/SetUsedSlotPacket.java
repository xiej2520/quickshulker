package net.kyrptonaught.quickshulker.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Identifier;

// Server tells client to set used slot after opening container
public class SetUsedSlotPacket {
    public static final Identifier SET_USED_SLOT_PACKET = new Identifier(QuickShulkerMod.MOD_ID, "set_used_slot_packet");

    public static void sendUsedSlotPacket(ServerPlayerEntity player, int playerInvIndex) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(playerInvIndex);
        ServerPlayNetworking.send(player, SET_USED_SLOT_PACKET, buf);
    }
}
