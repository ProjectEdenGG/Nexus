package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class ResourceWorld implements Listener {

	@EventHandler
	public void onWorldChange(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (event.getFrom().getWorld().getName().startsWith("resource")) return;

		if (event.getTo().getWorld().getName().startsWith("resource")) {
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

			ArrayList<Material> rejectedMaterials = new ArrayList<>();
			boolean appendMessage = false;

			for (Material material : materials) {
				if (player.getInventory().contains(material)) {
					rejectedMaterials.add(material);
					event.setCancelled(true);
					appendMessage = true;
				}
			}

			if (rejectedMaterials.size() != 0) {
				player.sendMessage(colorize("&cYou can not go to the resource world with the below items, " +
						"please remove them from your inventory before continuing:"));
				for (Material material : rejectedMaterials) {
					player.sendMessage(colorize("&e- " + camelCase(material.name())));
				}
			}

			rejectedMaterials.clear();

			ItemStack[] items = player.getInventory().getContents();
			for (ItemStack item : items) {
				if (item == null || Utils.isNullOrAir(item.getType())) continue;
				if (!MaterialTag.SHULKER_BOXES.isTagged(item.getType())) continue;
				if (!(item.getItemMeta() instanceof BlockStateMeta)) continue;

				ShulkerBox shulkerBox = (ShulkerBox) ((BlockStateMeta) item.getItemMeta()).getBlockState();
				ItemStack[] contents = shulkerBox.getInventory().getContents();
				for (ItemStack content : contents) {
					if (content == null || Utils.isNullOrAir(content.getType())) continue;
					if (materials.contains(content.getType())) {
						rejectedMaterials.add(content.getType());
						event.setCancelled(true);
					}
				}
			}

			if (rejectedMaterials.size() != 0) {
				if (appendMessage) {
					for (Material material : rejectedMaterials) {
						player.sendMessage(colorize("&e- " + camelCase(material.name()) + " (in shulkerbox)"));
					}
				} else {
					player.sendMessage(colorize("&cYou can not go to the resource world with the below items, " +
							"please remove them from your shulkerbox before continuing:"));
					for (Material material : rejectedMaterials) {
						player.sendMessage(colorize("&e- " + camelCase(material.name()) + " (in shulkerbox)"));
					}
				}
			}
		}
	}

	@EventHandler
	public void onOpenEnderChest(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;
		if (event.getInventory().getType() != InventoryType.ENDER_CHEST) return;
		Player player = (Player) event.getPlayer();

		if (event.getPlayer().getWorld().getName().startsWith("resource")) {
			event.setCancelled(true);
			player.sendMessage(colorize("&cYou can't open your enderchest while in the resource world, due to restrictions in place to keep the /market balanced"));
		}
	}

}
