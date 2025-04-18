package com.dikiytechies.enhancedpearls.network;

import com.dikiytechies.enhancedpearls.EnhancedPearls;
import com.dikiytechies.enhancedpearls.network.c2s.ClGridInitPacket;
import com.dikiytechies.enhancedpearls.network.c2s.ClTeleportPacket;
import com.dikiytechies.enhancedpearls.network.s2c.SrGridInitPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class ModPackets {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel channel;

    public static void init() {
        channel = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(EnhancedPearls.MOD_ID, "channel"))
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .simpleChannel();

        int packetIndex = 0;
        // Whenever you add a new network packet, you have to register it here like so:
        channel.registerMessage(packetIndex++, ClTeleportPacket.class,
                ClTeleportPacket::encode, ClTeleportPacket::decode, ClTeleportPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        channel.registerMessage(packetIndex++, ClGridInitPacket.class,
                ClGridInitPacket::encode, ClGridInitPacket::decode, ClGridInitPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        channel.registerMessage(packetIndex++, SrGridInitPacket.class,
                SrGridInitPacket::encode, SrGridInitPacket::decode, SrGridInitPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }



    public static void sendToServer(Object msg) {
        channel.sendToServer(msg);
    }

    public static void sendToClient(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) {
            channel.send(PacketDistributor.PLAYER.with(() -> player), msg);
        }
    }

    public static void sendToClientsTracking(Object msg, Entity entity) {
        channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
    }

    public static void sendToClientsTrackingAndSelf(Object msg, Entity entity) {
        channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }
}
