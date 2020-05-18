package me.pugabyte.bncore.features.listeners;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ChatVisibility;
import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTTileEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.features.warps.Warps;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Misc implements Listener {

	@EventHandler
	public void onHorseLikeDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof AbstractHorse)
			if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))
				event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		if (!(event.getEntity() instanceof Player)) return;

		if (event.getCause() == DamageCause.VOID)
			if (WorldGroup.get(event.getEntity()) == WorldGroup.SURVIVAL)
				Warps.spawn((Player) event.getEntity());
	}

	@EventHandler
	public void onPlayerDamageByPlayer(PlayerDamageByPlayerEvent event) {
		if (event.getVictim().getUniqueId().equals(event.getAttacker().getUniqueId()))
			event.getOriginalEvent().setCancelled(true);
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
		player.sendMessage(colorize(msgFormat + "Your chest is protected with LWC! Use /lwcinfo to learn more. Use &c/trust lock <player> &fto allow someone else to use it."));

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
		player.sendMessage(colorize(msgFormat + "Your furnace is protected with LWC! Use /lwcinfo to learn more. Use &c/trust lock <player> &fto allow someone else to use it."));

		setting.setBoolean(true);
		service.save(setting);
	}

	@EventHandler
	public void onJoinWithChatDisabled(PlayerJoinEvent event) {
		Tasks.wait(Time.SECOND.x(3), () -> {
			Player player = event.getPlayer();
			ChatVisibility setting = player.getClientOption(ClientOption.CHAT_VISIBILITY);
			if (Arrays.asList(ChatVisibility.SYSTEM, ChatVisibility.HIDDEN).contains(setting)) {
				Utils.sendActionBar(player, "&4&lWARNING: &4You have chat disabled! Turn it on in your settings", Time.MINUTE.get());
				player.sendMessage("");
				player.sendMessage(colorize("&4&lWARNING: &4You have chat disabled! Turn it on in your settings"));
				player.sendMessage("");
			}
		});
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

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		switch (WorldGroup.get(player)) {
			case MINIGAMES:
				Tasks.wait(5, () -> joinMinigames(player));
				break;
			case CREATIVE:
				Tasks.wait(5, () -> joinCreative(player));
				break;
			case SKYBLOCK:
			case SURVIVAL:
				Tasks.wait(10, player::resetPlayerTime);
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
				Utils.runConsoleCommand("wings reset " + player.getName());
				Utils.runConsoleCommand("speed walk 1 " + player.getName());
				player.resetPlayerTime();
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

	public static class PlayerDamageByPlayerEvent extends Event {
		@NonNull
		@Getter
		final Player victim;
		@NonNull
		@Getter
		final Player attacker;
		@NonNull
		@Getter
		final EntityDamageByEntityEvent originalEvent;

		@SneakyThrows
		public PlayerDamageByPlayerEvent(Player victim, Player attacker, EntityDamageByEntityEvent event) {
			this.victim = victim;
			this.attacker = attacker;
			this.originalEvent = event;
		}

		private static final HandlerList handlers = new HandlerList();

		public static HandlerList getHandlerList() {
			return handlers;
		}

		@Override
		public HandlerList getHandlers() {
			return handlers;
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		Player attacker = null;
		if (event.getDamager() instanceof Player) {
			attacker = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			if (projectile.getShooter() instanceof Player)
				attacker = (Player) projectile.getShooter();
		}

		if (attacker == null) return;

		PlayerDamageByPlayerEvent newEvent = new PlayerDamageByPlayerEvent((Player) event.getEntity(), attacker, event);
		Utils.callEvent(newEvent);
	}

}
