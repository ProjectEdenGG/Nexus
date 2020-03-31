package me.pugabyte.bncore.features.minigames.mechanics;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.annotations.Railgun;
import me.pugabyte.bncore.features.minigames.models.annotations.Scoreboard;
import me.pugabyte.bncore.features.minigames.models.arenas.MurderArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.UnbalancedTeamMechanic;
import me.pugabyte.bncore.features.minigames.models.scoreboards.MinigameScoreboard.Type;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Tasks.Countdown;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.Utils.getBlockHit;

@Railgun
@Scoreboard(teams = false, sidebarType = Type.MATCH)
public class Murder extends UnbalancedTeamMechanic {

	@Override
	public String getName() {
		return "Murder";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.IRON_SWORD);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		MurderArena arena = event.getMatch().getArena();

		event.getMatch().getTasks().repeat(Time.SECOND.x(10), Time.SECOND.x(3), () -> {
			for (Location loc : arena.getScrapPoints()) {
				if (arena.getSpawnChance() != 0)
					if (Utils.randomInt(1, arena.getSpawnChance()) != 1)
						continue;
				loc.getWorld().dropItemNaturally(loc, scrap);
			}
		});

		List<Minigamer> list = event.getMatch().getAliveMinigamers();

		// Don't try to process any further if there's only one player; causes crashes
		if (list.size() < 2) return;

		assignGunner(event.getMatch());
		sendAssignMessages(event.getMatch());

		for (Minigamer minigamer : list) {
			if (isMurderer(minigamer))
				minigamer.getMatch().getTasks().repeat(2, 5, () -> {
					double dist = 1000;
					Player target = null;
					if (minigamer.getMatch() == null) return;

					// Find the closest player by looping all minigame
					// players and saving the shortest distance
					for (Minigamer _minigamer : minigamer.getMatch().getMinigamers())
						if (_minigamer != minigamer)
							if (minigamer.getPlayer().getLocation().distance(_minigamer.getPlayer().getLocation()) < dist) {
								// New shortest distance, save data
								dist = minigamer.getPlayer().getLocation().distance(_minigamer.getPlayer().getLocation());
								target = _minigamer.getPlayer();
							}

					if (target != null)
						// Set compass location to nearest player
						minigamer.getPlayer().setCompassTarget(target.getLocation());
				});
		}
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		Minigamer victim = event.getMinigamer();
		Minigamer attacker = event.getAttacker();
		spawnCorpse(victim);

		// If the attacker was a gunner and the victim was not the murderer, intoxicate
		if (isGunner(attacker) && !isMurderer(victim))
			intoxicate(attacker.getPlayer());

		// Drop a gun if they had one
		if (isGunner(victim))
			victim.getPlayer().getLocation().getWorld().dropItem(victim.getPlayer().getLocation(), gun);

		event.setDeathMessage(victim.getColoredName() + " &3died");
		victim.tell("You were killed!");
		super.onDeath(event);
	}

	@Override
	public void announceWinners(Match match) {
		boolean murdererAlive = match.getAliveTeams().stream().anyMatch(team -> team.getColor() == ChatColor.RED);

		String broadcast = "";
		if (!murdererAlive)
			broadcast = "The murderer has been stopped by the &9gunner &3on";
		else
		if (match.getTimer().getTime() != 0)
			broadcast = "The &cMurderer &3has won";
		else
			broadcast = "The &9innocents &3have won";


		Minigames.broadcast(broadcast + " &e" + match.getArena().getDisplayName());
	}

	@Override
	public void onDamage(MinigamerDamageEvent event) {
		super.onDamage(event);

		if (event.getOriginalEvent() != null && event.getOriginalEvent() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent originalEvent = (EntityDamageByEntityEvent) event.getOriginalEvent();
			if (
					isMurderer(event.getAttacker()) &&
					originalEvent.getCause() == DamageCause.ENTITY_ATTACK &&
					originalEvent.getEntityType() == EntityType.PLAYER &&
					event.getAttacker().getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_SWORD
			) {
				// Staby-stab
				event.setCancelled(true);
				kill(event.getMinigamer());
			}
		}
	}

	public Map<String, Integer> getScoreboardLines(Match match) {
		return new HashMap<String, Integer>() {{
			match.getMinigamers().stream().filter(Minigamer::isAlive)
					.forEach(minigamer -> put(minigamer.getName(), 0));
		}};
	}

	public void spawnCorpse(Minigamer minigamer) {
		// TODO: Sleeping NPC?
		Player player = minigamer.getPlayer();
		ArmorStand armorStand = player.getWorld().spawn(player.getLocation().add(0, -1.4, 0), ArmorStand.class);
		armorStand.setGravity(false);
		armorStand.setVisible(false);
		armorStand.setHelmet(new ItemBuilder(Material.SKULL_ITEM).skullType(SkullType.PLAYER).skullOwner(player).build());
		// armorStand.setDisableSlots?
	}

	private void assignGunner(Match match) {
		Minigamer gunner = Utils.getRandomElement(match.getAliveMinigamers().stream()
				.filter(minigamer -> !isMurderer(minigamer))
				.collect(Collectors.toList()));

		if (gunner != null)
			gunner.getPlayer().getInventory().setItem(1, gun);
	}

	private void sendAssignMessages(Match match) {
		match.getAliveMinigamers().forEach(minigamer -> {
			if (isMurderer(minigamer))
				minigamer.tell("You are the &cmurderer&3! Kill everyone in your path, but don't get caught!");
			else if (isGunner(minigamer))
				minigamer.tell("You are the &6gunner&3! Find the murderer and shoot them with your gun.");
			else
				minigamer.tell("You are an &9innocent&3! Try to find scraps to craft a gun.");
		});
	}

	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		// Checks
		if (player.getInventory().getItemInMainHand() == null) return;

		List<Action> actions = Arrays.asList(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
		if (!actions.contains(event.getAction())) return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			List<Material> allowedRedstone = Arrays.asList(Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.LEVER);
			if (allowedRedstone.contains(event.getClickedBlock().getType()))
				return;

			if ("RavensNestEstate".equalsIgnoreCase(minigamer.getMatch().getArena().getName()))
				return;
		}

		event.setCancelled(true);

		// Gunner shooting handled in AnnotationsListener (see @Railgun)

		switch (player.getInventory().getItemInMainHand().getType()) {
			case IRON_SWORD:
				throwKnife(minigamer);
				return;
			case TRIPWIRE_HOOK:
				retrieveKnife(minigamer);
				return;
			case EYE_OF_ENDER:
				useBloodlust(minigamer);
				return;
			case ENDER_PEARL:
				useTeleporter(minigamer);
				return;
			case SUGAR:
				useAdrenaline(minigamer);
		}
	}

	private void retrieveKnife(Minigamer minigamer) {
		Player player = minigamer.getPlayer();
		if (!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains(" in ")) {
			player.getWorld().strikeLightningEffect(player.getLocation());
			player.getInventory().setItem(1, knife);
		}
	}

	private void useBloodlust(Minigamer minigamer) {
		minigamer.tell("You used bloodlust!");
		minigamer.getMatch().broadcast("The murderer used bloodlust!");
		minigamer.getPlayer().getInventory().remove(Material.EYE_OF_ENDER);

		Countdown countdown =Tasks.Countdown.builder()
				.duration(20 * 14)
				.onSecond(i -> {
					if (i % 2 == 0)
						minigamer.getMatch().getAliveMinigamers().forEach(_minigamer -> {
							Player player = _minigamer.getPlayer();
							player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BREATH, SoundCategory.MASTER, 2F, 0.1F);
							player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 1, false, false));
							SkriptFunctions.redTint(player, 0.5, 10);
						});
				})
				.start();

		minigamer.getMatch().getTasks().register(countdown.getTaskId());
	}

	private void useAdrenaline(Minigamer minigamer) {
		Player player = minigamer.getPlayer();
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 2));
		player.getInventory().remove(Material.SUGAR);
	}

	private void useTeleporter(Minigamer minigamer) {
		Team innocentTeam = getInnocentTeam(minigamer.getMatch());
		innocentTeam.toSpawnpoints(minigamer.getMatch());

		minigamer.tell("You used the teleporter!");
		minigamer.getMatch().broadcast("The murderer used the teleporter!");
		minigamer.getPlayer().getInventory().remove(Material.ENDER_PEARL);
	}

	private void throwKnife(Minigamer minigamer) {
		Player player = minigamer.getPlayer();
		player.launchProjectile(Arrow.class);
		player.getInventory().remove(Material.IRON_SWORD);

		// Retrieval mechanics
		if (player.getInventory().contains(Material.TRIPWIRE_HOOK)) {
			player.getInventory().remove(Material.TRIPWIRE_HOOK);
			player.getInventory().setItem(1, retriever);
			// Start countdown
			ItemMeta meta = player.getInventory().getItem(1).getItemMeta();
			meta.setDisplayName(ChatColor.YELLOW + "Retrieve the knife in " + ChatColor.RED + "10");
			player.getInventory().getItem(1).setItemMeta(meta);
			// Sync delayed task that cancels once it reaches 0
			new Retriever(player).runTaskTimer(BNCore.getInstance(), 0, 20);
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		// Make sure the shooter & victim are players
		if (!(event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player)) return;
		Minigamer attacker = PlayerManager.get((Player) event.getEntity().getShooter());
		Minigamer victim = null;
		if (event.getHitEntity() != null && event.getHitEntity() instanceof Player)
			victim = PlayerManager.get((Player) event.getHitEntity());

		if (!attacker.isPlaying(this)) return;
		if (!isMurderer(attacker)) return;

		// If it was an arrow, it was from a knife throw, so we want to spawn a knife item
		if (event.getEntityType() == EntityType.ARROW) {
			World world = attacker.getPlayer().getWorld();
			Block hitBlock = getBlockHit(event);
			if (hitBlock != null) {
				world.dropItem(hitBlock.getLocation(), knife);
				event.getEntity().remove();
			} else if (event.getHitEntity() != null) {
				world.dropItem(event.getHitEntity().getLocation(), knife);
			}
		}

		if (victim != null)
			kill(victim);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		// Only allow iron ingots to be dropped
		if (event.getItemDrop().getItemStack().getType() != Material.IRON_INGOT)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		event.setCancelled(true);

		// Picking up scrap
		if (event.getItem().getItemStack().getType() == Material.IRON_INGOT) {

			if (isGunner(player)) return;

			boolean isMurderer = isMurderer(player);

			// Fake pick up
			event.getItem().remove();

			int amount = 0;
			try {
				amount = player.getInventory().getItem(8).getAmount();
			} catch (Exception ignore) {}

			if (amount == 0) {
				// First scrap
				player.getInventory().setItem(8, (isMurderer) ? fakeScrap : scrap);
				if (!isMurderer)
					minigamer.tell("You collected a scrap " + ChatColor.GRAY + "(1/10)");
				return;
			}

			if (amount == 9) {
				if (isMurderer(player)) return;

				// Craft a gun
				player.getInventory().remove(Material.IRON_INGOT);
				player.getInventory().setItem(1, gun);
				minigamer.tell("You collected enough scrap to craft a gun!");
				return;
			}

			// Normal pick up
			player.getInventory().addItem((isMurderer) ? fakeScrap : scrap);
			if (!isMurderer)
				minigamer.tell("You collected a scrap " + ChatColor.GRAY + "(" + (amount + 1) + "/10)");
			return;
		}

		// Picking up gun
		if (event.getItem().getItemStack().getType() == Material.IRON_HOE) {
			if (isInnocent(player)) {
				// Innocent found a gun
				event.getItem().remove();
				player.getInventory().remove(Material.IRON_INGOT);
				player.getInventory().setItem(1, gun);
			}

			return;
		}

		// Picking up knife
		if (event.getItem().getItemStack().getType() == Material.IRON_SWORD) {
			if (isMurderer(player)) {
				// If they already have a knife, don't pick it up
				if (player.getInventory().contains(Material.IRON_SWORD)) return;

				if (player.getInventory().contains(Material.TRIPWIRE_HOOK))
					// They didn't use their retriever, move it back to their inventoryContents
					player.getInventory().setItem(17, retriever);

				// Put the knife in the correct slot
				event.getItem().remove();
				player.getInventory().setItem(1, knife);
			}
		}
	}

	@EventHandler
	public void onItemMerge(ItemMergeEvent event) {
		if (WGUtils.getRegionsLikeAt(event.getTarget().getLocation(), "murder_.*").size() > 0)
			event.setCancelled(true);
	}

	public static class Retriever extends BukkitRunnable {
		private Player player;
		private int time;

		public Retriever(Player player) {
			this.player = player;
			String[] name = player.getInventory().getItem(1).getItemMeta().getDisplayName().split(" in ");
			this.time = Integer.parseInt(ChatColor.stripColor(name[1]));
		}

		@Override
		public void run() {
			ItemStack item = player.getInventory().getItem(1);

			// If its not the retriever item, they found the knife, so cancel
			if (item == null || item.getType() != Material.TRIPWIRE_HOOK) {
				this.cancel();
				return;
			}

			ItemMeta meta = item.getItemMeta();

			if (time > 0) {
				meta.setDisplayName(ChatColor.YELLOW + "Retrieve the knife in " + ChatColor.RED + time--);
				player.getInventory().getItem(1).setItemMeta(meta);
			} else {
				meta.setDisplayName(ChatColor.YELLOW + "Retrieve the knife");
				player.getInventory().getItem(1).setItemMeta(meta);
				this.cancel();
			}
		}
	}

	public static void intoxicate(Player player) {
		// Remove and drop gun
		player.getInventory().remove(Material.IRON_HOE);
		player.getLocation().getWorld().dropItem(player.getLocation(), gun);

		// Make drunk
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 1200, 2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1200, 4));
		// Glass bottle will signify that they are 'drunk' and can't pick up guns
		player.getInventory().setItem(17, new ItemStack(Material.GLASS_BOTTLE));
	}

	private Team getInnocentTeam(Match match) {
		Minigamer murderer = getMurderer(match);
		Optional<Team> first = match.getArena().getTeams().stream().filter(team -> team != murderer.getTeam()).findFirst();
		return first.orElseThrow(() -> new MinigameException("Could not find innocents team"));
	}

	public boolean isMurderer(Minigamer minigamer) {
		if (minigamer == null) return false;
		return isMurderer(minigamer.getPlayer());
	}

	public boolean isMurderer(Player player) {
		return player.getInventory().contains(Material.COMPASS);
	}

	public boolean isGunner(Minigamer minigamer) {
		if (minigamer == null) return false;
		return isGunner(minigamer.getPlayer());
	}

	public boolean isGunner(Player player) {
		return player.getInventory().contains(Material.IRON_HOE);
	}

	public boolean isDrunk(Minigamer minigamer) {
		if (minigamer == null) return false;
		return isDrunk(minigamer.getPlayer());
	}

	public boolean isDrunk(Player player) {
		return player.getInventory().contains(Material.GLASS_BOTTLE);
	}

	public boolean isInnocent(Minigamer minigamer) {
		if (minigamer == null) return false;
		return isInnocent(minigamer.getPlayer());
	}

	public boolean isInnocent(Player player) {
		return !(isGunner(player) || isMurderer(player) || isDrunk(player));
	}

	private Minigamer getMurderer(Match match) {
		for (Minigamer minigamer : match.getAliveMinigamers())
			if (isMurderer(minigamer.getPlayer()))
				return minigamer;

		return null;
	}

	@Getter
	public static ItemStack knife = new ItemBuilder(Material.IRON_SWORD)
			.name("&cKnife")
			.lore("&fUse this to murder innocents!", "&eLeft-click &fto attack", "&eRight-click &fto throw")
			.build();

	@Getter
	public static ItemStack gun = new ItemBuilder(Material.IRON_HOE)
			.name("&eGun")
			.lore("&fUse this to kill the murderer!", "&eRight-click &fto shoot")
			.build();

	@Getter
	public static ItemStack scrap = new ItemBuilder(Material.IRON_INGOT)
			.name("&eScrap")
			.lore("&fCollect 10 to craft a gun!")
			.build();

	@Getter
	public static ItemStack fakeScrap = new ItemBuilder(Material.IRON_INGOT)
			.name("&eFake Scrap")
			.lore("&fUse this to fool the innocents!")
			.build();

	@Getter
	public static ItemStack compass = new ItemBuilder(Material.COMPASS)
			.name("&eLocator")
			.lore("&fPoints to the closest innocent")
			.build();

	@Getter
	public static ItemStack teleporter = new ItemBuilder(Material.ENDER_PEARL)
			.name("&eTeleporter")
			.lore("&eRight-click &fto teleport all innocents to random locations")
			.build();

	@Getter
	public static ItemStack adrenaline = new ItemBuilder(Material.SUGAR)
			.name("&eAdrenaline")
			.lore("&eRight-click &fto receive a speed boost")
			.build();

	@Getter
	public static ItemStack bloodlust = new ItemBuilder(Material.EYE_OF_ENDER)
			.name("&eBloodlust")
			.lore("&eRight-click &fto highlight all players, for a penalty!")
			.build();

	@Getter
	public static ItemStack retriever = new ItemBuilder(Material.TRIPWIRE_HOOK)
			.name("&eRetrieve knife")
			.lore("&eRight-click &fto retrieve the knife, for a penalty!")
			.build();

}
