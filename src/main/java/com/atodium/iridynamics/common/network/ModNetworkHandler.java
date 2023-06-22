package com.atodium.iridynamics.common.network;

import com.atodium.iridynamics.Iridynamics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ModNetworkHandler {
    public static final String VERSION = Iridynamics.VERSION;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(Iridynamics.rl("main"), () -> VERSION, VERSION::equals, VERSION::equals);
    private static int id = 0;

    public static void init() {
        register(CapabilityUpdatePacket.class, CapabilityUpdatePacket::encode, CapabilityUpdatePacket::new, CapabilityUpdatePacket::handle);
        register(ModularGuiOpenPacket.class, ModularGuiOpenPacket::encode, ModularGuiOpenPacket::new, ModularGuiOpenPacket::handle);
        register(ModularGuiTaskPacket.class, ModularGuiTaskPacket::encode, ModularGuiTaskPacket::new, ModularGuiTaskPacket::handle);
        register(ModularToClientPacket.class, ModularToClientPacket::encode, ModularToClientPacket::new, ModularToClientPacket::handle);
        register(ModularToServerPacket.class, ModularToServerPacket::encode, ModularToServerPacket::new, ModularToServerPacket::handle);
    }

    private static <T> void register(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, NetworkEvent.Context> handler) {
        CHANNEL.registerMessage(id++, type, encoder, decoder, (packet, supplier) -> {
            NetworkEvent.Context context = supplier.get();
            context.setPacketHandled(true);
            handler.accept(packet, context);
        });
    }
}