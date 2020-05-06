package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.models.back.Back;
import me.pugabyte.bncore.models.back.BackService;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

		switch (WorldGroup.get(player)) {
			case MINIGAMES:
				Tasks.wait(5, () -> joinMinigames(player));
				break;
			case CREATIVE:
				Tasks.wait(5, () -> joinCreative(player));
				break;
			case SKYBLOCK:
			case SURVIVAL:
				Tasks.wait(10, () -> Utils.runConsoleCommand("ptime reset " + player.getName()));
				if (WorldGroup.get(event.getFrom()).equals(WorldGroup.CREATIVE) || WorldGroup.get(event.getFrom()).equals(WorldGroup.EVENT)) {
					if (!player.hasPermission("essentials.speed"))
						Utils.runCommandAsOp(player, "flyspeed 1");
					if (!player.hasPermission("essentials.fly"))
						player.setFlying(false);
				}
				break;
		}

		if (event.getFrom().getName().equalsIgnoreCase("donortrial"))
			Tasks.wait(20, () -> {
				player.sendMessage("Removing pets, disguises and ptime changes");
				Utils.runConsoleCommand("undisguiseplayer " + player.getName());
				Utils.runConsoleCommand("petadmin remove " + player.getName());
				Utils.runConsoleCommand("mpet remove " + player.getName());
				Utils.runConsoleCommand("ptime reset " + player.getName());
				Utils.runConsoleCommand("wings reset " + player.getName());
				Utils.runConsoleCommand("speed walk 1 " + player.getName());
			});

		if (player.getWorld().getName().equalsIgnoreCase("staff_world"))
			Tasks.wait(20, () -> Utils.runCommand(player, "cheats off"));

		if (player.getWorld().getName().equals("survival_nether")) {
			Tasks.wait(5, () -> {
				player.sendMessage("");
				player.sendMessage(colorize("&4Warning: &cThis nether world will be re-set in 1.16 " +
						"due to the nether update, so don't build anything you don't want to lose!"));
				player.sendMessage("");
			});
		}
	}

	public void joinMinigames(Player player) {
		Utils.runCommand(player, "ch join m");
	}

	public void joinCreative(Player player) {
		Utils.runCommand(player, "ch join c");
	}
}
