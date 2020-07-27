package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.models.tip.Tip;
import me.pugabyte.bncore.models.tip.Tip.TipType;
import me.pugabyte.bncore.models.tip.TipService;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class ResourceWorld implements Listener {

	@EventHandler
	public void onEnterResourceWorld(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (event.getFrom().getWorld().getName().startsWith("resource")) return;

		if (!WorldGroup.get(event.getFrom().getWorld()).equals(WorldGroup.SURVIVAL)) {
			player.sendMessage(colorize("&eYou can only enter the resource world from the Survival world"));
			event.setCancelled(true);
			return;
		}

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

			if (!event.isCancelled()) {
				player.sendMessage(colorize(" &4Warning: &cYou are entering the resource world! This world is regenerated on the " +
						"&c&lfirst of every month, &cso don't leave your stuff here or you will lose it!"));
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.getPlayer().getWorld().getName().startsWith("resource")) return;

		List<Material> materials = new ArrayList<>(Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BARREL));
		materials.addAll(MaterialTag.WOODEN_DOORS.getValues());
		if (!materials.contains(event.getBlockPlaced().getType()))
			return;

		Tip tip = new TipService().get(event.getPlayer());
		if (tip.show(TipType.RESOURCE_WORLD_STORAGE))
			event.getPlayer().sendMessage(colorize(" &4Warning: &cYou are currently building in the resource world! " +
				"This world is regenerated on the &c&lfirst of every month, &cso don't leave your stuff here or you will lose it!"));
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

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().getWorld().getName().startsWith("resource")) {
			switch (event.getMessage().split(" ")[0].replace("playervaults:", "")) {
				case "/pv":
				case "/vc":
				case "/chest":
				case "/vault":
				case "/playervaults":
					event.setCancelled(true);
					event.getPlayer().sendMessage(colorize("&cYou cannot use vaults while in the resource world"));
			}
		}
	}

	/* Find protections from people being dumb

	select
		nerd.name,
		lwc_blocks.name,
		CONCAT("/tppos ", x, " ", y, " ", z, " ", world)
	from bearnation_smp_lwc.lwc_protections
	inner join bearnation_smp_lwc.lwc_blocks
		on lwc_blocks.id = lwc_protections.blockId
	inner join bearnation.nerd
		on lwc_protections.owner = nerd.uuid
	where world in ('resource', 'resource_nether', 'resource_the_end')
		and lwc_blocks.name not like "%DOOR%"
		and lwc_blocks.name not like "%GATE%";

	 */

	// TODO Automation
	/*
	- unload all 3 worlds
	- move the directories to old_<world>
	- remove uuid.dat
	- delete homes
	- create new worlds
	- paste spawn (y = 150)
	- mv setspawn
	- clean light
	- create npc for filid
	- set world border
	- fill chunks
	- dynamap purge
	- delete from bearnation_smp_lwc.lwc_protections where world in ('resource', 'resource_nether', 'resource_the_end');
	 */

}
