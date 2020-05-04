package me.pugabyte.bncore.features.listeners;

import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTTileEntity;
import lombok.SneakyThrows;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.models.back.Back;
import me.pugabyte.bncore.models.back.BackService;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.shop.Shop.ExchangeType;
import me.pugabyte.bncore.models.shop.Shop.ShopGroup;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Misc implements Listener {

	@EventHandler
	public void onHorseLikeDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof AbstractHorse)
			if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlaceChest(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("rank.guest"))
			return;

		if (!event.getBlockPlaced().getType().equals(Material.CHEST))
			return;

		SettingService service = new SettingService();
		Setting setting = service.get(player, "tips.lwc.chest");
		if (setting.getBoolean())
			return;

		String msgFormat = Koda.getDmFormat();
		player.sendMessage(colorize(msgFormat + "Your chest is protected with LWC! Use /lwcinfo to learn more. Use /cmodify <player> to allow someone else to use it."));

		setting.setBoolean(true);
		service.save(setting);
	}

	@EventHandler
	public void onPlaceFurnace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("rank.guest"))
			return;

		if (!event.getBlockPlaced().getType().equals(Material.FURNACE))
			return;

		SettingService service = new SettingService();
		Setting setting = service.get(player, "tips.lwc.furnace");
		if (setting.getBoolean())
			return;

		String msgFormat = Koda.getDmFormat();
		player.sendMessage(colorize(msgFormat + "Your furnace is protected with LWC! Use /lwcinfo to learn more. Use /cmodify <player> to allow someone else to use it."));

		setting.setBoolean(true);
		service.save(setting);
	}

	@EventHandler
	public void onEnderDragonDeath(EntityDeathEvent event) {
		if (!event.getEntityType().equals(EntityType.ENDER_DRAGON))
			return;

		if (Utils.chanceOf(33))
			event.getDrops().add(new ItemStack(Material.DRAGON_EGG));
	}

	@EventHandler
	public void onPlacePotionLauncherHopper(BlockPlaceEvent event) {
		if (!event.getBlockPlaced().getType().equals(Material.HOPPER))
			return;

		NBTItem itemNBT = new NBTItem(event.getItemInHand());
		if (!itemNBT.hasNBTData())
			return;

		if (itemNBT.asNBTString().contains("&8Potion Launcher"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBreakEmptyShulkerBox(BlockBreakEvent event) {
		//TODO: 1.13+ Switch to material tag
		if (!event.getBlock().getType().toString().toLowerCase().contains("shulker_box"))
			return;

		if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			return;

		NBTTileEntity tileEntityNBT = new NBTTileEntity(event.getBlock().getState());
		if (!tileEntityNBT.asNBTString().contains("Items:[")) {
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		if (event.getPlayer().getWorld().getName().startsWith("resource")) {
			List<Material> materials = new ShopService().getMarket().getProducts(ShopGroup.RESOURCE).stream()
					.filter(product -> product.getExchangeType() == ExchangeType.BUY)
					.map(product -> product.getItem().getType())
					.collect(Collectors.toList());

			// Crafting materials
			materials.add(Material.CLAY_BALL);
			materials.add(Material.DIRT);
			materials.add(Material.GRAVEL);
			materials.add(Material.GLOWSTONE_DUST);
			materials.add(Material.ICE);
			materials.add(Material.PACKED_ICE);

			for (Material material : materials) {
				if (player.getInventory().contains(material)) {
					player.sendMessage("&cYou can not go to the resource world with &e" + camelCase(material.name()) + "&c. " +
							"Please remove it from your inventory before continuing.");

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

	private static List<UUID> toSpawn = new ArrayList<>();

	@EventHandler
	@SneakyThrows
	public void onConnect(AsyncPlayerPreLoginEvent event) {
		File file = Paths.get(Bukkit.getServer().getWorlds().get(0).getName() + "/playerdata/" + event.getUniqueId().toString() + ".dat").toFile();
		if (file.exists())
			if (Bukkit.getWorld(new NBTFile(file).getString("SpawnWorld")) == null)
				toSpawn.add(event.getUniqueId());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (toSpawn.contains(event.getPlayer().getUniqueId()))
			new WarpService().get("spawn", WarpType.NORMAL).teleport(event.getPlayer());

		Tasks.wait(5, () -> {
			WorldGroup worldGroup = WorldGroup.get(event.getPlayer());
			if (worldGroup == WorldGroup.MINIGAMES)
				joinMinigames(event.getPlayer());
			else if (worldGroup == WorldGroup.CREATIVE)
				joinCreative(event.getPlayer());
		});

		// Moved home for pork splegg map build
		SettingService settingService = new SettingService();
		if (event.getPlayer().getUniqueId().toString().equalsIgnoreCase("5bff3b47-06f3-4766-9468-edfe19266997")) {
			Setting setting = settingService.get(event.getPlayer(), "s6oobertTP");
			if (!setting.getBoolean()) {
				Utils.runCommand(event.getPlayer(), "home");
				setting.setBoolean(true);
				settingService.save(setting);
			}
		}
	}

	public void joinMinigames(Player player) {
		Utils.runCommand(player, "ch join m");
	}

	public void joinCreative(Player player) {
		Utils.runCommand(player, "ch join c");
	}
}
