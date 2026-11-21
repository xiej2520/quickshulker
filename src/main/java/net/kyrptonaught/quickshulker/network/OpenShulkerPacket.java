package net.kyrptonaught.quickshulker.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.OpenableItemUtil;
import net.ornithemc.osl.core.api.util.NamespacedIdentifier;
import net.ornithemc.osl.networking.api.ChannelIdentifiers;
import net.ornithemc.osl.networking.api.ChannelRegistry;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

// sent by client when they open a quickshulker item
public class OpenShulkerPacket {
    private static final NamespacedIdentifier OPEN_SHULKER_PACKET = ChannelRegistry.register(ChannelIdentifiers.from(QuickShulkerMod.PACKET_ID, "open_shulker"), false, true);

    public static void registerReceivePacket() {
        ServerPlayNetworking.registerListener(OPEN_SHULKER_PACKET, (ctx, buffer) -> {
            ctx.ensureOnMainThread();
            int playerInvIndex = buffer.readInt();
            OpenableItemUtil.openItem(ctx.player(), playerInvIndex);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendOpenPacket(int playerInvIndex) {
        ClientPlayNetworking.send(OPEN_SHULKER_PACKET, buffer -> buffer.writeInt(playerInvIndex));
    }
}
