package io.hyonsu06.core;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedParticle;
import org.bukkit.Particle;

import static io.hyonsu06.Main.plugin;

public class NoParticle {
    public static NoParticle instance;

    public static NoParticle instance() {
        if (instance == null) {
            instance = new NoParticle();
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public NoParticle() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Play.Server.WORLD_PARTICLES) {
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                if (event.getPacketType() != PacketType.Play.Server.WORLD_PARTICLES)
                    return;
                if (((WrappedParticle<?>) packet.getNewParticles().read(0)).getParticle() == Particle.DAMAGE_INDICATOR)
                    event.setCancelled(true);
            }
        });
    }
}
