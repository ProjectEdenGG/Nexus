package me.pugabyte.bncore.features.oldminigames.murder;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import me.pugabyte.bncore.ItemStackBuilder;
import me.pugabyte.bncore.features.oldminigames.murder.runnables.CorpseTeleporter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
		return new ItemStackBuilder(Material.IRON_SWORD)
				.name("&cKnife")
				.lore("&fUse this to murder innocents!", "&eLeft-click &fto attack", "&eRight-click &fto throw")
				.build();
	}

	public static ItemStack getGun() {
		return new ItemStackBuilder(Material.IRON_HOE)
				.name("&eGun")
				.lore("&fUse this to kill the murderer!", "&eRight-click &fto shoot")
				.build();
	}

	public static ItemStack getScrap() {
		return new ItemStackBuilder(Material.IRON_INGOT)
				.name("&eScrap")
				.lore("&fCollect 10 to craft a gun!")
				.build();
	}

	public static ItemStack getFakeScrap() {
		return new ItemStackBuilder(Material.IRON_INGOT)
				.amount(9)
				.name("&eFake Scrap")
				.lore("&fUse this to fool the innocents!")
				.build();
	}

	public static ItemStack getCompass() {
		return new ItemStackBuilder(Material.COMPASS)
				.name("&eLocator")
				.lore("&fPoints to the closest innocent")
				.build();
	}

	public static ItemStack getTeleporter() {
		return new ItemStackBuilder(Material.ENDER_PEARL)
				.name("&eTeleporter")
				.lore("&eRight-click &fto teleport all innocents to random locations")
				.build();
	}

	public static ItemStack getAdrenaline() {
		return new ItemStackBuilder(Material.SUGAR)
				.name("&eAdrenaline")
				.lore("&eRight-click &fto receive a speed boost")
				.build();
	}

	public static ItemStack getBloodlust() {
		return new ItemStackBuilder(Material.EYE_OF_ENDER)
				.name("&eBloodlust")
				.lore("&eRight-click &fto highlight all players, for a penalty!")
				.build();
	}

	public static ItemStack getRetriever() {
		return new ItemStackBuilder(Material.TRIPWIRE_HOOK)
				.name("&eRetrieve knife")
				.lore("&eRight-click &fto retrieve the knife, for a penalty!")
				.build();
	}
}