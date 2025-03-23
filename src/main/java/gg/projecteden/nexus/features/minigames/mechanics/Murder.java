package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MinigameMessageType;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchStatisticsClass;
import gg.projecteden.nexus.features.minigames.models.annotations.Railgun;
import gg.projecteden.nexus.features.minigames.models.annotations.Scoreboard;
import gg.projecteden.nexus.features.minigames.models.arenas.MurderArena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.exceptions.MinigameException;
import gg.projecteden.nexus.features.minigames.models.matchdata.MurderMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard.Type;
import gg.projecteden.nexus.features.minigames.models.statistics.MurderStatistics;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks.Countdown;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Railgun
@Scoreboard(teams = false, sidebarType = Type.MINIGAMER)
@MatchStatisticsClass(MurderStatistics.class)
public class Murder extends TeamMechanic {

	private static final TextColor DRUNKARD_COLOR = TextColor.color(0xAD7A13);

	@Override
	public @NotNull String getName() {
		return "Murder";
	}

	@Override
	public @NotNull String getDescription() {
		return "Try to find and stop the villager who is secretly murdering your fellow villagers";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.IRON_SWORD);
	}

	@Override
	public boolean canDropItem(@NotNull ItemStack item) {
		return item.getType() == Material.IRON_INGOT;
	}

	@Override
	public boolean usesAutoBalancing() {
		return false;
	}

	@Override
	public boolean usesTeamChannels() {
		return false;
	}

	@Override
	public boolean hideTeamLoadoutColors() {
		return true;
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);

		List<Minigamer> list = event.getMatch().getAliveMinigamers();

		// Don't try to process any further if there's only one player; causes crashes
		if (list.size() < 2) return;

		assignGunner(event.getMatch());
		sendAssignMessages(event.getMatch());

		for (Minigamer minigamer : list) {
			if (isMurderer(minigamer))
				minigamer.getMatch().getTasks().repeat(2, 5, () -> {
					if (minigamer.getMatch() == null)
						return;

					Minigamer target = Collections.min(minigamer.getMatch().getAliveMinigamers(), Comparator.comparingDouble(_minigamer -> {
						if (_minigamer == minigamer || !_minigamer.isAlive())
							return Double.MAX_VALUE;

						return Distance.distance(minigamer, _minigamer).get();
					}));

					if (target != null)
						// Set compass location to nearest player
						minigamer.getOnlinePlayer().setCompassTarget(target.getLocation());
				});
			else {
				minigamer.getOnlinePlayer().setFoodLevel(3);
				minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.WEAKNESS).infinite().maxAmplifier().ambient(true));
			}
		}
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		Minigamer victim = event.getMinigamer();
		Minigamer attacker = event.getAttacker();
		spawnCorpse(victim);

		// If the attacker was a gunner and the victim was not the murderer, intoxicate
		if (isGunner(attacker) && !isMurderer(victim))
			intoxicate(attacker.getPlayer());

		// Drop a gun if they had one
		if (isGunner(victim))
			victim.getPlayer().getLocation().getWorld().dropItem(victim.getPlayer().getLocation(), gun);

		victim.tell("You were killed!");
		super.onDeath(event);
	}

	@Override
	public boolean allowChat(MinigameMessageType type) {
		return type != MinigameMessageType.DEATH && type != MinigameMessageType.QUIT;
	}

	@Override
	public void announceWinners(@NotNull Match match) {
		MurderMatchData matchData = match.getMatchData();
		Minigamer murderer = getMurderer(match);
		Minigamer hero = matchData.getHero();

		JsonBuilder builder = new JsonBuilder();
		if (!murderer.isAlive()) {
			builder.next(murderer.getNickname(), NamedTextColor.RED)
				.next(" has been stopped by ")
				.next(hero.getNickname(), NamedTextColor.BLUE)
				.next(" on ");

			matchData.getMatch().getMatchStatistics().award(MurderStatistics.GUNNER_SHUTDOWNS, hero);
			match.getAliveMinigamers().stream()
				.filter(minigamer -> isInnocent(minigamer) || isGunner(minigamer))
				.forEach(minigamer -> {
					match.getMatchStatistics().award(MurderStatistics.INNOCENT_WINS, minigamer);
					match.getMatchStatistics().award(MurderStatistics.WINS, minigamer);
				});
		}
		else if (match.getTimer().getTime() != 0) {
			builder.next(murderer.getNickname(), NamedTextColor.RED).next(" has won on ");
			matchData.getMatch().getMatchStatistics().award(MurderStatistics.MURDERER_WINS, murderer);

			matchData.getMatch().getMatchStatistics().award(MurderStatistics.WINS, murderer);
		}
		else {
			builder.content("The ")
				.next("innocents", NamedTextColor.BLUE)
				.next(" have won ");

			match.getAliveMinigamers().stream()
				.filter(minigamer -> isInnocent(minigamer) || isGunner(minigamer))
				.forEach(minigamer -> {
					match.getMatchStatistics().award(MurderStatistics.INNOCENT_WINS, minigamer);
					match.getMatchStatistics().award(MurderStatistics.WINS, minigamer);
				});
		}

		Minigames.broadcast(builder.next(match.getArena()).build());
	}

	@Override
	public void onDamage(@NotNull MinigamerDamageEvent event) {
		super.onDamage(event);
		event.setCancelled(true);

		if (event.getOriginalEvent() != null && event.getOriginalEvent() instanceof EntityDamageByEntityEvent originalEvent) {
			if (
					isMurderer(event.getAttacker()) &&
					originalEvent.getCause() == DamageCause.ENTITY_ATTACK &&
					originalEvent.getEntityType() == EntityType.PLAYER &&
					event.getAttacker().getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_SWORD
			) {
				event.getAttacker().getMatch().getMatchStatistics().award(MurderStatistics.MURDERER_KILLS, event.getAttacker());
				// Staby-stab
				kill(event.getMinigamer());
			}
		}
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Minigamer minigamer) {
		Match match = minigamer.getMatch();
		List<Minigamer> allMinigamers = match.getAllMinigamers();
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>(allMinigamers.size());

		if (!match.isStarted()) {
			match.getMinigamers().stream().map(mg -> mg.getNickname())
				.forEach(mg -> lines.put(mg, Integer.MIN_VALUE));
			return lines;
		}

		if (minigamer.isAlive()) {
			for (Minigamer target : allMinigamers)
				lines.put(target.getNickname(), Integer.MIN_VALUE);
		} else {
			allMinigamers.stream().filter(this::isMurderer).forEach(mg -> lines.put("&c" + mg.getNickname(), Integer.MIN_VALUE));
			allMinigamers.stream().filter(this::isGunner).forEach(mg -> lines.put("&6" + mg.getNickname(), Integer.MIN_VALUE));
			allMinigamers.stream().filter(mg -> !isGunner(mg) && !isMurderer(mg) && mg.isAlive()).forEach(mg -> lines.put(mg.getNickname(), getScrapCount(mg)));
			allMinigamers.stream().filter(mg -> !isGunner(mg) && !isMurderer(mg) && !mg.isAlive()).forEach(mg -> lines.put("&8&m&o" + mg.getNickname(), Integer.MIN_VALUE));
		}
		return lines;
	}

	@Override
	public @Nullable JsonBuilder getNameplate(@NotNull Minigamer target, @NotNull Minigamer viewer) {
		// don't show any useful information if viewer is alive
		if (viewer.isAlive())
			return new JsonBuilder(target.getNickname());

		// render murderer/gunner status for spectators
		JsonBuilder nameplate = new JsonBuilder().content(target.getNickname());
		if (isMurderer(target))
			nameplate.color(NamedTextColor.RED);
		else if (isGunner(target))
			nameplate.color(NamedTextColor.GOLD);
		else if (isDrunk(target))
			nameplate.color(DRUNKARD_COLOR);
		else
			nameplate.next(" (" + getScrapCount(target) + "/10)", NamedTextColor.GRAY);
		return nameplate;
	}

	@Override
	public boolean shouldShowNameplate(@NotNull Minigamer target, @NotNull Minigamer viewer) {
		if (!super.shouldShowNameplate(target, viewer)) return false;
		return viewer.getPlayer().hasLineOfSight(target.getPlayer());
	}

	public void spawnCorpse(Minigamer minigamer) {
		// TODO: Sleeping NPC?
		Player player = minigamer.getOnlinePlayer();
		Location location = player.getLocation().add(0, -1.4, 0);

		minigamer.getMatch().spawn(location, ArmorStand.class, _armorStand -> {
			_armorStand.setGravity(false);
			_armorStand.setVisible(false);
			_armorStand.setInvulnerable(true);

			if (_armorStand.getEquipment() != null)
				_armorStand.getEquipment().setHelmet(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player).build());

			_armorStand.setDisabledSlots(EquipmentSlot.values());
		});
	}

	private void assignGunner(Match match) {
		Minigamer gunner = RandomUtils.randomElement(match.getAliveMinigamers().stream()
				.filter(minigamer -> !isMurderer(minigamer))
				.collect(Collectors.toList()));

		if (gunner != null) {
			MurderMatchData matchData = match.getMatchData();
			gunner.getPlayer().getInventory().setItem(1, gun);
		}
	}

	private void sendAssignMessages(Match match) {
		match.getAliveMinigamers().forEach(minigamer -> {
			if (isMurderer(minigamer)) {
				((MurderMatchData) match.getMatchData()).setMurderer(minigamer);
				minigamer.tell("You are the &cmurderer&3! Kill everyone in your path, but don't get caught!");
			} else if (isGunner(minigamer))
				minigamer.tell("You are the &6gunner&3! Find the murderer and shoot them with your gun.");
			else
				minigamer.tell("You are an &9innocent&3! Try to find scraps to craft a gun.");
		});
	}

	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this)) return;

		if (!ActionGroup.RIGHT_CLICK.applies(event)) return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
			List<Material> allowedRedstone = new ArrayList<>() {{
				add(Material.LEVER);
			}};
			allowedRedstone.addAll(MaterialTag.BUTTONS.getValues());
			if (allowedRedstone.contains(event.getClickedBlock().getType()))
				return;

			if ("RavensNestEstate".equalsIgnoreCase(minigamer.getMatch().getArena().getName()))
				return;
		}

		event.setCancelled(true);

		// Gunner shooting handled in AnnotationsListener (see @Railgun)

		switch (player.getInventory().getItemInMainHand().getType()) {
			case IRON_SWORD -> throwKnife(minigamer);
			case TRIPWIRE_HOOK -> retrieveKnife(minigamer);
			case ENDER_EYE -> useBloodlust(minigamer);
			case ENDER_PEARL -> useTeleporter(minigamer);
			case SUGAR -> useAdrenaline(minigamer);
		}
	}

	private void retrieveKnife(Minigamer minigamer) {
		Player player = minigamer.getOnlinePlayer();
		if (!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains(" in ")) {
			player.getWorld().strikeLightningEffect(player.getLocation());
			player.getInventory().setItem(1, knife);
		}
	}

	private void useBloodlust(Minigamer minigamer) {
		minigamer.tell("You used bloodlust!");
		minigamer.getMatch().broadcast("The murderer used bloodlust!");
		minigamer.getOnlinePlayer().getInventory().remove(Material.ENDER_EYE);

		minigamer.getMatch().getTasks().countdown(Countdown.builder()
				.duration(20 * 14)
				.onSecond(i -> {
					if (i % 2 == 0)
						minigamer.getMatch().getAliveMinigamers().forEach(_minigamer -> {
							Player player = _minigamer.getOnlinePlayer();
							player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BREATH, SoundCategory.MASTER, 2F, 0.1F);
							player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.GLOWING).duration(TickTime.SECOND.x(2)).build());
							// TODO SkriptFunctions.redTint(player, 0.5, 10);
						});
				}));
	}

	private void useAdrenaline(Minigamer minigamer) {
		Player player = minigamer.getOnlinePlayer();
		player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.SPEED).duration(80).amplifier(2).build());
		player.getInventory().remove(Material.SUGAR);
	}

	private void useTeleporter(Minigamer minigamer) {
		Team innocentTeam = getInnocentTeam(minigamer.getMatch());
		innocentTeam.toSpawnpoints(minigamer.getMatch());

		minigamer.tell("You used the teleporter!");
		minigamer.getMatch().broadcast("The murderer used the teleporter!");
		minigamer.getOnlinePlayer().getInventory().remove(Material.ENDER_PEARL);
	}

	private void throwKnife(Minigamer minigamer) {
		Player player = minigamer.getOnlinePlayer();
		player.launchProjectile(Arrow.class);
		player.getInventory().remove(Material.IRON_SWORD);

		// Retrieval mechanics
		if (player.getInventory().contains(Material.TRIPWIRE_HOOK)) {
			player.getInventory().remove(Material.TRIPWIRE_HOOK);
			player.getInventory().setItem(1, retriever);
			// Start countdown
			ItemMeta meta = player.getInventory().getItem(1).getItemMeta();
			meta.setDisplayName(colorize("&eRetrieve the knife in &c10"));
			player.getInventory().getItem(1).setItemMeta(meta);
			// Sync delayed task that cancels once it reaches 0
			new Retriever(player).runTaskTimer(Nexus.getInstance(), 0, 20);
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		// Make sure the shooter & victim are players
		if (!(event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player)) return;
		Minigamer attacker = Minigamer.of((Player) event.getEntity().getShooter());
		Minigamer victim = null;
		if (event.getHitEntity() != null && event.getHitEntity() instanceof Player)
			victim = Minigamer.of(event.getHitEntity());

		if (!attacker.isPlaying(this)) return;
		if (!isMurderer(attacker)) return;

		// If it was an arrow, it was from a knife throw, so we want to spawn a knife item
		if (event.getEntityType() == EntityType.ARROW) {
			World world = attacker.getPlayer().getWorld();
			Block hitBlock = LocationUtils.getBlockHit(event);
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
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		// Only allow iron ingots to be dropped
		if (event.getItemDrop().getItemStack().getType() != Material.IRON_INGOT)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isIn(this)) return;

		event.setCancelled(true);

		// prevent dead players from picking things up
		if (minigamer.isDead() || minigamer.isSpectating()) return;

		// Picking up scrap
		if (event.getItem().getItemStack().getType() == Material.IRON_INGOT) {

			if (isGunner(player)) return;

			boolean isMurderer = isMurderer(player);

			// Fake pick up
			event.getItem().remove();
			new SoundBuilder(Sound.ENTITY_ITEM_PICKUP).location(player).volume(0.5).play();

			int amount = 0;
			try {
				amount = player.getInventory().all(Material.IRON_INGOT).values().stream().mapToInt(ItemStack::getAmount).sum();
				player.getInventory().remove(Material.IRON_INGOT);
			} catch (Exception ex) {
				ex.printStackTrace();
				player.sendMessage("There was an error while trying to count your existing iron ingots");
			}

			if (amount == 64)
				return;

			switch (amount) {
				case 0 -> { // First scrap
					player.getInventory().setItem(8, (isMurderer) ? fakeScrap : scrap);
					if (!isMurderer)
						minigamer.tell("You collected a scrap &7(1/10)");
					return;
				}

				case 9 -> { // Craft a gun
					if (isMurderer(player))
						return;
					player.getInventory().setItem(1, gun);
					minigamer.tell("You collected enough scrap to craft a gun!");
					return;
				}

				default -> { // Normal pick up
					if (isMurderer) {
						player.getInventory().setItem(8, fakeScrap);
					} else {
						player.getInventory().setItem(8, scrap.clone().add(amount));
						minigamer.tell("You collected a scrap &7(" + (amount + 1) + "/10)");
					}

					return;
				}
			}
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
		if (new WorldGuardUtils(event.getEntity()).getRegionsLikeAt("murder_.*", event.getTarget().getLocation()).size() > 0)
			event.setCancelled(true);
	}

	public static class Retriever extends BukkitRunnable {
		private Player player;
		private int time;

		public Retriever(Player player) {
			this.player = player;
			String[] name = player.getInventory().getItem(1).getItemMeta().getDisplayName().split(" in ");
			this.time = Integer.parseInt(StringUtils.stripColor(name[1]));
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
				meta.setDisplayName(colorize("&eRetrieve the knife in &c" + time--));
				player.getInventory().getItem(1).setItemMeta(meta);
			} else {
				meta.setDisplayName(colorize("&eRetrieve the knife"));
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
		player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.NAUSEA).duration(1200).amplifier(2).build());
		player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.SLOWNESS).duration(1200).amplifier(4).build());
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
		return isMurderer(minigamer.getOnlinePlayer());
	}

	public boolean isMurderer(Player player) {
		return player.getInventory().contains(Material.COMPASS);
	}

	public boolean isGunner(Minigamer minigamer) {
		if (minigamer == null) return false;
		return isGunner(minigamer.getOnlinePlayer());
	}

	public boolean isGunner(Player player) {
		return player.getInventory().contains(Material.IRON_HOE);
	}

	public boolean isDrunk(Minigamer minigamer) {
		if (minigamer == null) return false;
		return isDrunk(minigamer.getOnlinePlayer());
	}

	public boolean isDrunk(Player player) {
		return player.getInventory().contains(Material.GLASS_BOTTLE);
	}

	public boolean isInnocent(Minigamer minigamer) {
		if (minigamer == null) return false;
		return isInnocent(minigamer.getOnlinePlayer());
	}

	public boolean isInnocent(Player player) {
		return !(isGunner(player) || isMurderer(player) || isDrunk(player));
	}

	private Minigamer getMurderer(Match match) {
		return ((MurderMatchData)match.getMatchData()).getMurderer();
	}

	private Set<Minigamer> getGunners(Match match) {
		return match.getAliveMinigamers().stream().filter(this::isGunner).collect(Collectors.toSet());
	}

	private int getScrapCount(Minigamer minigamer) {
		ItemStack scrapItem = minigamer.getOnlinePlayer().getInventory().getItem(8);
		return scrapItem == null ? 0 : scrapItem.getAmount();
	}

	@EventHandler
	public void onTimeTick(MatchTimerTickEvent event) {
		if (!event.getMatch().isMechanic(this))
			return;

		Match match = event.getMatch();
		MurderArena arena = match.getArena();

		match.getMinigamers().forEach(minigamer -> {
			JsonBuilder component = new JsonBuilder("You are ", NamedTextColor.DARK_AQUA);
			if (!minigamer.isAlive())
				component.next("dead", NamedTextColor.RED);
			else if (isMurderer(minigamer))
				component.rawNext("the ").next("Murderer", NamedTextColor.RED);
			else if (isGunner(minigamer))
				component.rawNext("a ").next("Gunner", NamedTextColor.GOLD);
			else if (isDrunk(minigamer))
				component.rawNext("a ").next("Drunkard", DRUNKARD_COLOR);
			else
				component.rawNext("an ").next("Innocent", NamedTextColor.BLUE);
			sendActionBarWithTimer(minigamer, component);
		});

		// calculate formula
		List<Location> scrapPoints = arena.getScrapPoints();
		// spawns 1 scrap every 4 seconds on average at the start of the game, increasing in quantity as the round progresses
		double spawnChancePerPoint = ((1d/6d)/scrapPoints.size()) + (match.getMinigamers().size()-arena.getMinPlayers())/1500d;
		// drop scraps
		scrapPoints.forEach(location -> {
			if (RandomUtils.getRandom().nextDouble() < spawnChancePerPoint)
				location.getWorld().dropItemNaturally(location, scrap);
		});
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
	public static ItemStack bloodlust = new ItemBuilder(Material.ENDER_EYE)
			.name("&eBloodlust")
			.lore("&eRight-click &fto highlight all players, for a penalty!")
			.build();

	@Getter
	public static ItemStack retriever = new ItemBuilder(Material.TRIPWIRE_HOOK)
			.name("&eRetrieve knife")
			.lore("&eRight-click &fto retrieve the knife, for a penalty!")
			.build();

}
