package gg.projecteden.nexus.features.listeners.events;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerBlockDigEvent extends PlayerEvent {
	private static final HandlerList handlerList = new HandlerList();
	private final Block block;
	private final PlayerDigType digType;

	public PlayerBlockDigEvent(@NotNull Player who, Block block, PlayerDigType digType) {
		super(who);
		this.block = block;
		this.digType = digType;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

	static {
		Nexus.getProtocolManager().addPacketListener(new PacketAdapter(Nexus.getInstance(), Client.BLOCK_DIG) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				try {
					final BlockPosition pos = event.getPacket().getBlockPositionModifier().getValues().get(0);
					final PlayerDigType digType = event.getPacket().getPlayerDigTypes().getValues().get(0);

					final Location location = new Location(event.getPlayer().getWorld(), pos.getX(), pos.getY(), pos.getZ());
					Tasks.sync(() -> new PlayerBlockDigEvent(event.getPlayer(), location.getBlock(), digType).callEvent());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

}
