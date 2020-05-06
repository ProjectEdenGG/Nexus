package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.models.back.Back;
import me.pugabyte.bncore.models.back.BackService;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class ResourceWorld implements Listener {

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		if (event.getPlayer().getWorld().getName().startsWith("resource")) {
			List<Material> materials = new ShopService().getMarket().getProducts(Shop.ShopGroup.RESOURCE).stream()
					.filter(product -> product.getExchangeType() == Shop.ExchangeType.BUY)
					.map(product -> product.getItem().getType())
					.collect(Collectors.toList());

			// Crafting materials
			materials.add(Material.CLAY_BALL);
			materials.add(Material.GRAVEL);
			materials.add(Material.GLOWSTONE_DUST);
			materials.add(Material.ICE);
			materials.add(Material.PACKED_ICE);

			for (Material material : materials) {
				if (player.getInventory().contains(material)) {
					player.sendMessage(colorize("&cYou can not go to the resource world with &e" + camelCase(material.name()) + "&c. " +
							"Please remove it from your inventory before continuing."));

					Back back = new BackService().get(player);
					Optional<Location> backLocation = back.getLocations().stream().filter(location -> !location.getWorld().getName().startsWith("resource")).findFirst();
					if (backLocation.isPresent())
						player.teleport(backLocation.get());
					else
						new WarpService().get("spawn", WarpType.NORMAL).teleport(player);
				}
			}
		}
	}

	@EventHandler
	public void onWorldChange(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;
		Player player = (Player) event.getPlayer();

		if (event.getPlayer().getWorld().getName().startsWith("resource")) {
			event.setCancelled(true);
			player.sendMessage(colorize("&cYou can't open your enderchest while in the resource world, due to restrictions in place to keep the /market balanced"));
		}
	}

}
