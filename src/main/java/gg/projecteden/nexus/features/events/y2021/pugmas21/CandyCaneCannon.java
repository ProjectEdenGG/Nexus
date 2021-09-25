package gg.projecteden.nexus.features.events.y2021.pugmas21;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.PlayerUtils;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.util.Vector;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

public class CandyCaneCannon implements Listener {

	public CandyCaneCannon() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		final ItemStack crossbow = event.getBow();
		if (isNullOrAir(crossbow))
			return;

		if (!(crossbow.getItemMeta() instanceof CrossbowMeta meta))
			return;

		if (CustomModelData.of(crossbow) != 1)
			return;

		final ItemStack ammo = meta.getChargedProjectiles().iterator().next();
		final CandyCane candyCane = CandyCane.of(ammo);
		if (candyCane == null)
			return;

		final Entity arrow = event.getProjectile();
		final Vector velocity = arrow.getVelocity();
		final Location location = arrow.getLocation();
		arrow.remove();
		final Snowball snowball = location.getWorld().spawn(location, Snowball.class);
		snowball.setVelocity(velocity.multiply(.75));
		snowball.setItem(candyCane.item());
		snowball.setSilent(true);
	}

	@EventHandler
	public void onEntityLoadCrossbow(EntityLoadCrossbowEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		final ItemStack crossbow = event.getCrossbow();
		if (isNullOrAir(crossbow))
			return;

		if (!(crossbow.getItemMeta() instanceof CrossbowMeta meta))
			return;

		final int customModelData = CustomModelData.of(crossbow);
		if (customModelData != 1)
			return;

		event.setConsumeItem(false);

		final CandyCane candyCane = findCandyCane(player);
		if (candyCane == null) {
			PlayerUtils.send(player, "&cYou are out of candy cane ammo!");
			event.setCancelled(true);
			return;
		}

		meta.addChargedProjectile(candyCane.item());
		PlayerUtils.removeItem(player, candyCane.item());
	}

	private enum CandyCane {
		RED,
		GREEN,
		BLUE,
		;

		private int customModelData() {
			return ordinal() + 1;
		}

		private ItemStack item() {
			return new ItemBuilder(Material.ARROW).customModelData(customModelData()).build();
		}

		public static CandyCane of(ItemStack item) {
			if (isNullOrAir(item))
				return null;

			if (item.getType() != Material.ARROW)
				return null;

			return of(CustomModelData.of(item));
		}

		public static CandyCane of(int customModelData) {
			for (CandyCane candyCane : values())
				if (candyCane.customModelData() == customModelData)
					return candyCane;

			return null;
		}
	}

	private CandyCane findCandyCane(Player player) {
		final CandyCane offHand = CandyCane.of(player.getInventory().getItemInOffHand());
		if (offHand != null)
			return offHand;

		for (ItemStack item : player.getInventory().getContents()) {
			final CandyCane candyCane = CandyCane.of(item);
			if (candyCane != null)
				return candyCane;
		}

		return null;
	}

}
