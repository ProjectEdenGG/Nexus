package me.pugabyte.bncore.features.oldminigames.murder;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.EndMinigameEvent;
import au.com.mineauz.minigames.events.QuitMinigameEvent;
import au.com.mineauz.minigames.events.StartMinigameEvent;
import au.com.mineauz.minigames.minigame.Minigame;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.oldminigames.MinigameUtils;
import me.pugabyte.bncore.features.oldminigames.murder.runnables.Locator;
import me.pugabyte.bncore.features.oldminigames.murder.runnables.Retriever;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static me.pugabyte.bncore.features.oldminigames.murder.Murder.PREFIX;

@SuppressWarnings("unused")
public class MurderListener implements Listener {
	private static HashMap<Player, Integer> locators = new HashMap<>();
	private final String MINIGAMEPREFIX = ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE;


	MurderListener() {
		BNCore.registerListener(this);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onMinigameQuit(QuitMinigameEvent event) {
		try {
			if (!event.getMinigame().getGametypeName().equalsIgnoreCase("Murder")) {
				return;
			}
		} catch (NullPointerException ex) {
			return;
		}

		Player player = event.getPlayer();

		// Drop a gun if they had one
		if (MurderUtils.isGunner(player)) {
			player.getLocation().getWorld().dropItem(player.getLocation(), MurderUtils.getGun());
		}

		// Just a bit of cleanup
		for (PotionEffect _pe : player.getActivePotionEffects()) {
			player.removePotionEffect(_pe.getType());
		}

		MinigameUtils.resetExp(player);
		player.setFoodLevel(10);

		// Turn off compass scheduler
		try {
			Bukkit.getScheduler().cancelTask(locators.get(player));
		} catch (NullPointerException ex) {
			// Ignore
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onMinigameEnd(EndMinigameEvent event) {
		try {
			if (!event.getMinigame().getGametypeName().equalsIgnoreCase("Murder")) {
				return;
			}
		} catch (NullPointerException ex) {
			return;
		}

		Minigame minigame = event.getMinigame();
		String minigameString = minigame.getAsString().toLowerCase();

		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "skmurder stop " + minigameString);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "skmurder clearentities " + minigameString);

		try {
			// Turn off compass schedulers
			for (MinigamePlayer _minigamePlayer : event.getWinners()) {
				Bukkit.getScheduler().cancelTask(locators.get(_minigamePlayer.getPlayer()));
			}

			for (MinigamePlayer _minigamePlayer : event.getLosers()) {
				Bukkit.getScheduler().cancelTask(locators.get(_minigamePlayer.getPlayer()));
			}
		} catch (Exception e) {
			// Ignore
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onMinigameStart(StartMinigameEvent event) {
		if (event.getMinigame().getGametypeName() == null) return;
		if (!event.getMinigame().getGametypeName().equalsIgnoreCase("Murder")) return;

		Minigame minigame = event.getMinigame();
		String minigameString = minigame.getAsString().toLowerCase();

		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "skmurder start " + minigameString);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "skmurder clearentities " + minigameString);

		List<MinigamePlayer> list = event.getPlayers();

		// Don't try to process any further if
		// there's only one player; causes crashes
		if (list.size() < 2) return;

		assignGunner(list);
		BNCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BNCore.getInstance(), () ->
						MinigameUtils.shufflePlayers(minigame),
				2L);
		for (MinigamePlayer player : list)
			BNCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BNCore.getInstance(), () ->
							MinigameUtils.resetExp(player.getPlayer()),
					2L);


		for (MinigamePlayer _minigamePlayer : list) {
			Player _player = _minigamePlayer.getPlayer();
			sendAssignMessages(_player);

			_player.getInventory().setHeldItemSlot(0);
			MinigameUtils.resetExp(_player);
			locators.put(_player, Locator.run(minigame, _player));
		}
	}

	private void sendAssignMessages(Player _player) {
		if (!(MurderUtils.isMurderer(_player) || MurderUtils.isGunner(_player))) {
			_player.sendMessage(MINIGAMEPREFIX + "You are an " + ChatColor.BLUE + "innocent"
					+ ChatColor.WHITE + "! Try to find scraps to craft a gun.");
		}

		if (MurderUtils.isMurderer(_player)) {
			_player.sendMessage(MINIGAMEPREFIX + "You are the " + ChatColor.RED + "murderer"
					+ ChatColor.WHITE + "! Kill everyone in your path, but don't get caught!");
		}
	}

	private void assignGunner(List<MinigamePlayer> list) {
		Random rand = new Random();
		while (true) {
			int n = rand.nextInt(list.size());
			if (!MurderUtils.isMurderer(list.get(n).getPlayer())) {
				list.get(n).getPlayer().getInventory().setItem(1, MurderUtils.getGun());
				list.get(n).getPlayer().sendMessage(MINIGAMEPREFIX + "You are the " + ChatColor.GOLD + "gunner"
						+ ChatColor.WHITE + "! Find the murderer and shoot them with your gun.");
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		try {
			if (!MurderUtils.isPlayingMurder(player)) {
				return;
			}
		} catch (NullPointerException ex) {
			return;
		}

		// Checks
		if (player.getInventory().getItemInMainHand() == null) return;

		if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			List<Material> allowedRedstone = Arrays.asList(Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.LEVER);
			if (allowedRedstone.contains(event.getClickedBlock().getType()))
				return;

			String minigame = Minigames.plugin.getPlayerData().getMinigamePlayer(player).getMinigame().getName(false);
			if ("RavensNestEstate".equalsIgnoreCase(minigame))
				return;
		}

		event.setCancelled(true);

		// Gunner shooting handled in MinigameListener, along with Quake and Dogfighting

		switch (player.getInventory().getItemInMainHand().getType()) {
			case IRON_SWORD:
				throwKnife(player);
				return;
			case EYE_OF_ENDER:
				retrieveKnife(player);
				return;
			case ENDER_PEARL:
				useTeleporter(player);
				return;
			case SUGAR:
				useAdrenaline(player);
		}
	}

	private void retrieveKnife(Player player) {
		if (!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains(" in ")) {
			player.getWorld().strikeLightningEffect(player.getLocation());
			player.getInventory().setItem(1, MurderUtils.getKnife());
		}
	}

	private void useAdrenaline(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 2));
		player.getInventory().remove(Material.SUGAR);
	}

	private void useTeleporter(Player player) {
		Minigame minigame = Minigames.plugin.getPlayerData().getMinigamePlayer(player).getMinigame();
		MinigameUtils.shufflePlayers(minigame, true);

		player.sendMessage(PREFIX + "You used the teleporter!");
		player.getInventory().remove(Material.ENDER_PEARL);
	}

	private void throwKnife(Player player) {
		Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(2))
				.toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
		Arrow arrow = player.getWorld().spawn(loc, Arrow.class);
		arrow.setShooter(player);
		arrow.setVelocity(player.getEyeLocation().getDirection().multiply(2));
		player.getInventory().remove(Material.IRON_SWORD);

		// Retrieval mechanics
		if (player.getInventory().contains(Material.EYE_OF_ENDER)) {
			player.getInventory().remove(Material.EYE_OF_ENDER);
			player.getInventory().setItem(1, MurderUtils.getRetriever());
			// Start countdown
			ItemMeta meta = player.getInventory().getItem(1).getItemMeta();
			meta.setDisplayName(ChatColor.YELLOW + "Retrieve the knife in " + ChatColor.RED + "10");
			player.getInventory().getItem(1).setItemMeta(meta);
			// Sync delayed task that cancels once it reaches 0
			new Retriever(player).runTaskTimer(BNCore.getInstance(), 0, 20);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHit(ProjectileHitEvent event) {
		try {
			// Make sure the shooter & victim are players
			if (!(event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player)) return;
			Player attacker = (Player) event.getEntity().getShooter();
			Player victim = null;
			if (event.getHitEntity() != null && event.getHitEntity() instanceof Player) {
				victim = (Player) event.getHitEntity();
			}

			if (!MurderUtils.isPlayingMurder(attacker)) return;

			// If it was an arrow, it was from a knife throw, so we want to  join a knife item
			if (event.getEntityType() == EntityType.ARROW) {
				if (event.getHitBlock() != null) {
					attacker.getLocation().getWorld().dropItem(event.getHitBlock().getLocation(), MurderUtils.getKnife());
					event.getEntity().remove();
				} else if (event.getHitEntity() != null) {
					attacker.getLocation().getWorld().dropItem(event.getHitEntity().getLocation(), MurderUtils.getKnife());
				}
			}

			if (victim != null) {
				MurderUtils.kill(victim);
			}
		} catch (NullPointerException ex) {
			// Ignore
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDamage(EntityDamageByEntityEvent event) {
		// Players only
		if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) return;

		Player victim = (Player) event.getEntity();
		Player attacker = (Player) event.getDamager();
		if (!MurderUtils.isPlayingMurder(victim) || !MurderUtils.isPlayingMurder(attacker)) {
			return;
		}

		// If the attacker was a gunner and the victim was not the murderer, intoxicate
		if (MurderUtils.isGunner(attacker) && !MurderUtils.isMurderer(victim)) {
			MurderUtils.intoxicate(attacker);
		}

		if (event.getCause() == DamageCause.ENTITY_ATTACK &&
				event.getEntityType() == EntityType.PLAYER &&
				MurderUtils.isMurderer(attacker) &&
				attacker.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD) {
			// Staby-stab
			event.setCancelled(true);
			MurderUtils.kill(victim);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		MinigamePlayer minigamePlayer = Minigames.plugin.getPlayerData().getMinigamePlayer(player);
		Minigame minigame = minigamePlayer.getMinigame();

		if (!MurderUtils.isPlayingMurder(player)) {
			return;
		}

		if (MurderUtils.isMurderer(player)) {
			// Shot
			Bukkit.dispatchCommand(player, "mgm quit");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDrop(PlayerDropItemEvent event) {
		try {
			Player player = event.getPlayer();
			if (!MurderUtils.isPlayingMurder(player)) return;

			// Only allow iron ingots to be dropped
			if (event.getItemDrop().getItemStack().getType() != Material.IRON_INGOT) {
				event.setCancelled(true);
			}
		} catch (Exception ex) {
			// Ignore
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		try {
			Player player = (Player) event.getEntity();

			if (!MurderUtils.isPlayingMurder(player)) return;

			// Always cancel
			event.setCancelled(true);

			// Picking up scrap
			if (event.getItem().getItemStack().getType() == Material.IRON_INGOT) {

				if (MurderUtils.isGunner(player)) return;

				// Fake pick up
				event.getItem().remove();

				if (MurderUtils.isMurderer(player)) {
					if (!player.getInventory().contains(Material.IRON_INGOT)) {
						// They don't have their fake scrap, give them one
						player.getInventory().setItem(8, MurderUtils.getFakeScrap());
					}
					return;
				}

				int amount = 0;
				try {
					amount = player.getInventory().getItem(8).getAmount();
				} catch (Exception e) {
					// Ignore
				}

				if (amount == 0) {
					// First scrap
					player.getInventory().setItem(8, MurderUtils.getScrap());
					player.sendMessage(PREFIX + "You collected a scrap " + ChatColor.GRAY + "(1/10)");
					return;
				}

				if (amount == 9) {
					// Craft a gun
					player.getInventory().remove(Material.IRON_INGOT);
					player.getInventory().setItem(1, MurderUtils.getGun());
					player.sendMessage(PREFIX + "You collected enough scrap to craft a gun!");
					return;
				}

				// Normal pick up
				player.getInventory().addItem(MurderUtils.getScrap());
				player.sendMessage(PREFIX + "You collected a scrap " + ChatColor.GRAY + "(" + (amount + 1) + "/10)");
				return;
			}

			// Picking up gun
			if (event.getItem().getItemStack().getType() == Material.IRON_HOE) {
				if (!(MurderUtils.isGunner(player) || MurderUtils.isMurderer(player) || MurderUtils.isDrunk(player))) {
					// Innocent found a gun
					event.getItem().remove();
					player.getInventory().remove(Material.IRON_INGOT);
					player.getInventory().setItem(1, MurderUtils.getGun());
				}

				return;
			}

			// Picking up knife
			if (event.getItem().getItemStack().getType() == Material.IRON_SWORD) {
				if (MurderUtils.isMurderer(player)) {
					// If they already have a knife, don't pick it up
					if (player.getInventory().contains(Material.IRON_SWORD)) return;

					if (player.getInventory().contains(Material.EYE_OF_ENDER)) {
						// They didn't use their retriever, move it back to their inventoryContents
						player.getInventory().setItem(17, MurderUtils.getRetriever());
					}

					// Put the knife in the correct slot
					event.getItem().remove();
					player.getInventory().setItem(1, MurderUtils.getKnife());
				}

			}
		} catch (Exception ex) {
			// Ignore
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryMove(InventoryInteractEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (!MurderUtils.isPlayingMurder(player)) {
				return;
			}
			event.setCancelled(true);
		}
	}
}
