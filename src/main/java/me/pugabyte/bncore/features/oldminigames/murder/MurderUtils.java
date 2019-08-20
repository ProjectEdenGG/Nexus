package me.pugabyte.bncore.features.oldminigames.murder;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import me.pugabyte.bncore.features.oldminigames.murder.runnables.CorpseTeleporter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class MurderUtils {
	private final static Minigames mg = Minigames.plugin;

	public static boolean isPlayingMurder(Player player) {
		try {
			MinigamePlayer minigamePlayer = mg.getPlayerData().getMinigamePlayer(player);
			if (minigamePlayer.isInMinigame()) {
				Minigame minigame = minigamePlayer.getMinigame();
				if (minigame.hasStarted()) {
					return (minigame.getGametypeName().equalsIgnoreCase("Murder"));
				}
			}
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isMurderer(Player player) {
		return player.getInventory().contains(Material.COMPASS);
	}

	public static boolean isGunner(Player player) {
		return player.getInventory().contains(Material.IRON_HOE);
	}

	public static boolean isDrunk(Player player) {
		return player.getInventory().contains(Material.GLASS_BOTTLE);
	}

	public static void kill(Player victim) {
		ArmorStand armorStand = victim.getWorld().spawn(victim.getLocation(), ArmorStand.class);
		SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		meta.setOwner(victim.getName());
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		skull.setItemMeta(meta);
		armorStand.setHelmet(skull);
		armorStand.setGravity(false);
		armorStand.setVisible(false);
		// armorStand.setDisableSlots?
		new CorpseTeleporter(armorStand);

		Bukkit.getServer().dispatchCommand(victim, "mgm quit");
		victim.sendMessage(Murder.PREFIX + "You were killed!");
	}

	public static void intoxicate(Player player) {
		// Remove and drop gun
		player.getInventory().remove(Material.IRON_HOE);
		player.getLocation().getWorld().dropItem(player.getLocation(), MurderUtils.getGun());

		// Make drunk
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 1200, 2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1200, 4));
		// Glass bottle will signify that they are 'drunk' and can't pick up guns
		player.getInventory().setItem(17, new ItemStack(Material.GLASS_BOTTLE));
	}

	public static ItemStack getKnife() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Knife");
		ArrayList<String> lores = new ArrayList<>();
		lores.add(ChatColor.WHITE + "Use this to murder innocents!");
		lores.add(ChatColor.YELLOW + "Left-click" + ChatColor.WHITE + " to attack");
		lores.add(ChatColor.YELLOW + "Right-click" + ChatColor.WHITE + " to throw");
		meta.setLore(lores);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getGun() {
		ItemStack item = new ItemStack(Material.IRON_HOE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Gun");
		ArrayList<String> lores = new ArrayList<>();
		lores.add(ChatColor.WHITE + "Use this to kill the murderer!");
		lores.add(ChatColor.YELLOW + "Right-click" + ChatColor.WHITE + " to shoot");
		meta.setLore(lores);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getScrap() {
		ItemStack item = new ItemStack(Material.IRON_INGOT);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Scrap");
		ArrayList<String> lores = new ArrayList<>();
		lores.add(ChatColor.WHITE + "Collect 10 to craft a gun!");
		meta.setLore(lores);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getFakeScrap() {
		ItemStack item = new ItemStack(Material.IRON_INGOT);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Fake Scrap");
		ArrayList<String> lores = new ArrayList<>();
		lores.add(ChatColor.WHITE + "Use this to fool the innocents!");
		meta.setLore(lores);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getCompass() {
		ItemStack item = new ItemStack(Material.COMPASS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Locator");
		ArrayList<String> lores = new ArrayList<>();
		lores.add(ChatColor.WHITE + "Points to the closest innocent");
		meta.setLore(lores);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getTeleporter() {
		ItemStack item = new ItemStack(Material.ENDER_PEARL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Teleporter");
		ArrayList<String> lores = new ArrayList<>();
		lores.add(ChatColor.YELLOW + "Right-click" + ChatColor.WHITE + " to teleport all innocents");
		meta.setLore(lores);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getAdrenaline() {
		ItemStack item = new ItemStack(Material.SUGAR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Adrenaline");
		ArrayList<String> lores = new ArrayList<>();
		lores.add(ChatColor.YELLOW + "Right-click" + ChatColor.WHITE + " to receive a speed boost");
		meta.setLore(lores);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getRetriever() {
		ItemStack item = new ItemStack(Material.EYE_OF_ENDER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Retrieve knife");
		ArrayList<String> lores = new ArrayList<>();
		lores.add(ChatColor.YELLOW + "Right-click" + ChatColor.WHITE + " to retrieve the knife, for a penalty!");
		meta.setLore(lores);
		item.setItemMeta(meta);

		return item;
	}
}