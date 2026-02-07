package net.kyrptonaught.quickshulker.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.resource.Identifier;
import net.minecraft.network.PacketByteBuf;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

// sent by client when they open an quickshulker item
public class OpenShulkerPacket {
    private static final Identifier OPEN_SHULKER_PACKET = new Identifier(QuickShulkerMod.PACKET_ID, "open_shulker");
    public static void registerReceivePacket() {
        ServerPlayNetworking.registerListener(String.valueOf(OPEN_SHULKER_PACKET), (server, serverPlayNetworkHandler, player, packetByteBuf) -> {
            int playerInvIndex = packetByteBuf.readInt();
            server.submit(() -> Util.openItem(player, playerInvIndex));
            return true;
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendOpenPacket(int playerInvIndex) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(playerInvIndex);
        ClientPlayNetworking.send(String.valueOf(OPEN_SHULKER_PACKET), new PacketByteBuf(buf));
    }
}