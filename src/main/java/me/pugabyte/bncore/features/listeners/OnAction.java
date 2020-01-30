package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class OnAction implements Listener {
	public OnAction() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onHorseLikeDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof AbstractHorse) {
			if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlaceChest(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("rank.guest"))
			return;

		if (!event.getBlockPlaced().getType().equals(Material.CHEST))
			return;

		String msgFormat = "&3&l[&bPM&3&l] &eFrom &3KodaBear &b&l> &e";
		player.sendMessage(msgFormat + "Your chest is protected with LWC! Use /lwcinfo to learn more. Use /cmodify <player> to allow someone else to use it.");
	}

	@EventHandler
	public void onPlaceFurnace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("rank.guest"))
			return;

		if (!event.getBlockPlaced().getType().equals(Material.FURNACE))
			return;

		String msgFormat = "&3&l[&bPM&3&l] &eFrom &3KodaBear &b&l> &e";
		player.sendMessage(msgFormat + "Your furnace is protected with LWC! Use /lwcinfo to learn more. Use /cmodify <player> to allow someone else to use it.");
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		Nerd nerd = new Nerd(player);

		switch (WorldGroup.get(player.getWorld())) {
			case MINIGAMES:
				if (nerd.isVanished())
					SkriptFunctions.json(player, "You've joined the gameworld vanished. Click here to unvanish and join the minigames channel.||sgt:/unvanishgameworld");
				else
					Tasks.wait(20, () -> Bukkit.dispatchCommand(player, "ch join m"));
				break;
			case CREATIVE:
				if (nerd.isVanished())
					SkriptFunctions.json(player, "You've joined creative vanished. Click here to unvanish and join the creative channel.||sgt:/unvanishcreative");
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

		if (event.getFrom().getName().equalsIgnoreCase("donortrial")) {
			Tasks.wait(20, () -> {
				player.sendMessage("Removing pets, disguises and ptime changes");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "undisguiseplayer " + player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "petadmin remove " + player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mpet remove " + player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ptime reset " + player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wings reset " + player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "speed walk 1 " + player.getName());
			});
		}

		if (player.getWorld().getName().equalsIgnoreCase("staff_world")) {
			Tasks.wait(20, () -> Bukkit.dispatchCommand(player, "nocheats"));
		}
	}
}
