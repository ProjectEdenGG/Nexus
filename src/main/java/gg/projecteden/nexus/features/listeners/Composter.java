package gg.projecteden.nexus.features.listeners;

import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Composter implements Listener {

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (!handleBambooComposting(event.getItem(), event.getClickedBlock()))
			return;

		if (event.getHand() == null)
			return;

		switch (event.getHand()) {
			case HAND -> event.getPlayer().swingMainHand();
			case OFF_HAND -> event.getPlayer().swingOffHand();
		}
	}

	@EventHandler
	public void on(InventoryMoveItemEvent event) {
		if (!(event.getDestination().getHolder() instanceof BlockInventoryHolder holder))
			return;

		handleBambooComposting(event.getItem(), holder.getBlock());
	}

	public boolean handleBambooComposting(ItemStack item, Block block) {
		if (Nullables.isNullOrAir(item))
			return false;

		if (Nullables.isNullOrAir(block))
			return false;

		if (item.getType() != Material.BAMBOO)
			return false;

		if (block.getType() != Material.COMPOSTER)
			return false;

		if (!(block.getBlockData() instanceof Levelled composter))
			return false;

		if (composter.getLevel() >= composter.getMaximumLevel())
			return false;

		item.subtract();

		final boolean increase = RandomUtils.chanceOf(30);
		if (increase) {
			composter.setLevel(composter.getLevel() + 1);
			block.setBlockData(composter);
		}

		final var packet = new ClientboundLevelEventPacket(1500, NMSUtils.toNMS(block.getLocation()), composter.getLevel(), true);
		OnlinePlayers.where()
			.world(block.getWorld())
			.radius(block.getLocation(), 100)
			.forEach(player -> PacketUtils.sendPacket(player, PacketContainer.fromPacket(packet)));

		return true;
	}

}
