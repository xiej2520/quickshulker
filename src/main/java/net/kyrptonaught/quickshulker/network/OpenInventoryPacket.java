package net.kyrptonaught.quickshulker.network;

import io.netty.buffer.Unpooled;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.resource.Identifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

public class OpenInventoryPacket {
    public static final Identifier OPEN_INV = new Identifier(QuickShulkerMod.MOD_ID, "openinv");

    public static void send(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, OPEN_INV.toString(), new PacketByteBuf(Unpooled.buffer()));
    }

}
