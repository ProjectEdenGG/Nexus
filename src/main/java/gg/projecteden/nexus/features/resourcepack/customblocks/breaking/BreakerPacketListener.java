package gg.projecteden.nexus.features.resourcepack.customblocks.breaking;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.protection.ProtectionUtils;
import lombok.SneakyThrows;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class BreakerPacketListener {

	public BreakerPacketListener() {
		Nexus.getProtocolManager().addPacketListener(listener);
	}

	private final PacketAdapter listener = new PacketAdapter(Nexus.getInstance(),
		ListenerPriority.LOW, PacketType.Play.Client.BLOCK_DIG) {
		@Override
		public void onPacketReceiving(final PacketEvent event) {
			final Player player = event.getPlayer();
			if (player.getGameMode() == GameMode.CREATIVE)
				return;

			final PacketContainer packet = event.getPacket();
			final StructureModifier<BlockPosition> dataTemp = packet.getBlockPositionModifier();
			final StructureModifier<EnumWrappers.Direction> dataDirection = packet.getDirections();
			final StructureModifier<EnumWrappers.PlayerDigType> data = packet.getEnumModifier(EnumWrappers.PlayerDigType.class, 2);
			EnumWrappers.PlayerDigType type;
			try {
				type = data.getValues().getFirst();
			} catch (IllegalArgumentException exception) {
				type = EnumWrappers.PlayerDigType.SWAP_HELD_ITEMS;
			}

			final BlockPosition pos = dataTemp.getValues().getFirst();
			final World world = player.getWorld();
			final Block block = world.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
			final Location location = block.getLocation();
			final BlockFace blockFace = dataDirection.size() > 0 ? BlockFace.valueOf(dataDirection.read(0).name()) : BlockFace.UP;

			final ItemStack item = player.getInventory().getItemInMainHand();

			if (!ProtectionUtils.canBreak(player, block))
				return;

			CustomBlock customBlock = CustomBlock.from(block);
			if (customBlock == null)
				return;

			DamagedBlock damagedBlock = Breaker.get(location);
			if (type == PlayerDigType.START_DESTROY_BLOCK && damagedBlock == null) {
				damagedBlock = Breaker.startTracking(block, player, item);
			}

			if (damagedBlock == null)
				return;

			event.setCancelled(true);
			final DamagedBlock finalDamagedBlock = damagedBlock;

			switch (type) {
				case START_DESTROY_BLOCK ->
					Tasks.sync(() -> Breaker.addSlowDig(player, finalDamagedBlock.getBreakTicks()));
				case STOP_DESTROY_BLOCK -> Tasks.sync(() -> {
					Breaker.removeSlowDig(player);
					finalDamagedBlock.breakBlock(player);
					for (Player _player : world.getNearbyPlayers(location, 16, 16, 16))
						sendBlockBreak(_player, location, 10);
				});
				default -> Tasks.sync(() -> {
					Breaker.removeSlowDig(player);
					finalDamagedBlock.remove();
				});
			}
		}
	};

	@SneakyThrows
	private void sendBlockBreak(final Player player, final Location location, final int stage) {
		final PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
		packet.getIntegers().write(0, location.hashCode()).write(1, stage);
		packet.getBlockPositionModifier().write(0, new BlockPosition(location.toVector()));

		ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
	}
}
