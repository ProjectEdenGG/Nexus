package gg.projecteden.nexus.features.listeners;

import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Composter implements Listener {

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (!handleBamboo(event.getPlayer(), event.getItem(), event.getClickedBlock()))
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

		handleBamboo(null, event.getItem(), holder.getBlock());
	}

	private boolean handleBamboo(@Nullable Player player, ItemStack item, Block block) {
		if (Nullables.isNullOrAir(item))
			return false;

		if (Nullables.isNullOrAir(block))
			return false;

		if (item.getType() != Material.BAMBOO)
			return false;

		return compostItem(player, item, block, 30);
	}

	public static boolean compostItem(@Nullable Player player, ItemStack item, Block block, int chance) {
		if (player != null && player.isSneaking())
			return false;

		if (block.getType() != Material.COMPOSTER)
			return false;

		if (!(block.getBlockData() instanceof Levelled composter))
			return false;

		if (composter.getLevel() >= composter.getMaximumLevel())
			return false;

		if (player != null)
			ItemUtils.subtract(player, item);
		else
			item.subtract();

		final boolean increase = RandomUtils.chanceOf(chance);
		if (increase) {
			composter.setLevel(composter.getLevel() + 1);
			block.setBlockData(composter);
			block.getWorld().playSound(block.getLocation(), Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 1.0f, 1.0f);
		} else {
			block.getWorld().playSound(block.getLocation(), Sound.BLOCK_COMPOSTER_FILL, 1.0f, 1.0f);
		}

		block.getWorld().spawnParticle(Particle.COMPOSTER, block.getLocation().toCenterLocation(), 10, 0.25, 0.1, 0.25);

		final var packet = new ClientboundLevelEventPacket(1500, NMSUtils.toNMS(block.getLocation()), composter.getLevel(), true);
		OnlinePlayers.where()
			.world(block.getWorld())
			.radius(block.getLocation(), 100)
			.forEach(_player -> PacketUtils.sendPacket(_player, PacketContainer.fromPacket(packet)));

		return true;
	}

}
