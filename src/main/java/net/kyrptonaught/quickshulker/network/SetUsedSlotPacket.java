package net.kyrptonaught.quickshulker.network;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.core.api.util.NamespacedIdentifier;
import net.ornithemc.osl.networking.api.ChannelIdentifiers;
import net.ornithemc.osl.networking.api.ChannelRegistry;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

// Server tells client to set used slot after opening container
public class SetUsedSlotPacket {
    public static final NamespacedIdentifier SET_USED_SLOT_PACKET = ChannelRegistry.register(ChannelIdentifiers.from(QuickShulkerMod.PACKET_ID, "set_used_slot"), true, false);

    public static void sendUsedSlotPacket(ServerPlayerEntity player, int playerInvIndex) {
        ServerPlayNetworking.send(player, SET_USED_SLOT_PACKET, buffer -> buffer.writeInt(playerInvIndex));
    }
}
