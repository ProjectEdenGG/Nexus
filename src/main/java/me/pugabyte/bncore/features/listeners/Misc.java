package me.pugabyte.bncore.features.listeners;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTTileEntity;
import me.clip.placeholderapi.PlaceholderAPI;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.GameMode;
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
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

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

		updateWorldGroupPlaceholder(player);

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
	}

	public void updateWorldGroupPlaceholder(Player player) {
		PlaceholderAPI.setPlaceholders(player, StringUtils.camelCase(WorldGroup.get(player).name()));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.wait(5, () -> {
			updateWorldGroupPlaceholder(event.getPlayer());
			WorldGroup worldGroup = WorldGroup.get(event.getPlayer());
			if (worldGroup == WorldGroup.MINIGAMES)
				joinMinigames(event.getPlayer());
			else if (worldGroup == WorldGroup.CREATIVE)
				joinCreative(event.getPlayer());
		});
	}

	public void joinMinigames(Player player) {
		Utils.runCommand(player, "ch join m");
	}

	public void joinCreative(Player player) {
		Utils.runCommand(player, "ch join c");
	}
}
