package net.kyrptonaught.quickshulker.network;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.resource.Identifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.networking.api.PacketByteBufs;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

// Server tells client to set used slot after opening container
public class SetUsedSlotPacket {
    public static final Identifier SET_USED_SLOT_PACKET = new Identifier(QuickShulkerMod.MOD_ID, "set_used_slot_packet");

    public static void sendUsedSlotPacket(ServerPlayerEntity player, int playerInvIndex) {
        PacketByteBuf buf = PacketByteBufs.make();
        buf.writeInt(playerInvIndex);
        ServerPlayNetworking.send(player, String.valueOf(SET_USED_SLOT_PACKET), buf);
    }
}
