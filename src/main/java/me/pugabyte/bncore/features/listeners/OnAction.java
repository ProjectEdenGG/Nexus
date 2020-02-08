package me.pugabyte.bncore.features.listeners;

import de.tr7zw.itemnbtapi.NBTItem;
import de.tr7zw.itemnbtapi.NBTTileEntity;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.koda.Koda;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.utils.Utils.colorize;

public class OnAction implements Listener {
	public OnAction() {
		BNCore.registerListener(this);
	}

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

		if (Utils.randomInt(1, 3) == 1)
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
		Nerd nerd = new Nerd(player);

		switch (WorldGroup.get(player.getWorld())) {
			case MINIGAMES:
				if (nerd.isVanished())
					new JsonBuilder()
							.next("You've joined the gameworld vanished. Click here to unvanish and join the minigames channel.")
							.suggest("/unvanishgameworld")
							.send(player);
				else
					Tasks.wait(20, () -> Bukkit.dispatchCommand(player, "ch join m"));
				break;
			case CREATIVE:
				if (nerd.isVanished())
					new JsonBuilder()
							.next("You've joined creative vanished. Click here to unvanish and join the creative channel.")
							.suggest("/unvanishcreative")
							.send(player);
				else
					Tasks.wait(20, () -> Bukkit.dispatchCommand(player, "ch join c"));
				break;
			case SKYBLOCK:
			case SURVIVAL:
				Tasks.wait(10, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ptime reset " + player.getName()));
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
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "undisguiseplayer " + player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "petadmin remove " + player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mpet remove " + player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ptime reset " + player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wings reset " + player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "speed walk 1 " + player.getName());
			});

		if (player.getWorld().getName().equalsIgnoreCase("staff_world"))
			Tasks.wait(20, () -> Bukkit.dispatchCommand(player, "cheats off"));
	}
}
