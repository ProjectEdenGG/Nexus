package gg.projecteden.nexus.features.wither.models;

import com.destroystokyo.paper.ParticleBuilder;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.commands.staff.CheatsCommand;
import gg.projecteden.nexus.features.commands.staff.HealCommand;
import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.features.wither.BeginningCutscene;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.features.wither.WitherChallenge.Difficulty;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.witherarena.WitherArenaConfig;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Mob;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public abstract class WitherFight implements Listener {

	public UUID host;
	public List<UUID> party = new ArrayList<>();
	public List<UUID> alivePlayers = new ArrayList<>();
	public List<UUID> spectators = new ArrayList<>();
	public boolean started = false;
	public Wither wither;
	public boolean shouldRegen = true;
	public List<Location> playerPlacedBlocks = new ArrayList<>();
	public List<Blaze> blazes = new ArrayList<>();
	public boolean gotStar = false;
	public List<Integer> tasks = new ArrayList<>();

	// Logging to get info
	private static boolean logInfo = true;
	private Map<UUID, Double> lowestHealth = new HashMap<>();


	public abstract WitherChallenge.Difficulty getDifficulty();

	public abstract void spawnWither(Location location);

	public abstract boolean shouldGiveStar();

	public abstract List<ItemStack> getAlternateDrops();

	public void log(String message) {
		Nexus.log("WitherLog: " + message);
	}

	public void start() {
		Nexus.registerListener(this);
		new BeginningCutscene().run().thenAccept(location -> {
			JsonBuilder message = new JsonBuilder(WitherChallenge.PREFIX + "The fight has started! &e&lClick here to spectate")
				.command("/wither spectate").hover("&eYou will be teleported to the wither arena");

			if (WitherArenaConfig.isBeta())
				Broadcast.staffIngame().message(message).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();
			else
				Broadcast.ingame().message(message).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();

			spawnWither(location);
			new WorldEditUtils("events").set("witherarena-door", BlockTypes.NETHER_BRICKS);
			new WorldEditUtils("events").set("witherarena-lobby", BlockTypes.NETHERRACK);
			tasks.add(Tasks.repeat(TickTime.SECOND.x(5), TickTime.SECOND.x(5), () -> {
				if (!shouldRegen)
					return;

				if (wither.getTarget() == null)
					wither.setTarget(getRandomAlivePlayer());
				if (wither.hasLineOfSight(wither.getTarget()))
					return;

				List<Block> blocks = BlockUtils.getBlocksInRadius(wither.getLocation(), 4);
				for (Block block : blocks) {
					if (block.getType() == Material.BEDROCK || block.getType() == Material.AIR) continue;
					Location loc = block.getLocation();
					for (Block face : BlockUtils.getAdjacentBlocks(block))
						if (MaterialTag.NEEDS_SUPPORT.isTagged(face.getType()))
							face.setType(Material.AIR);
					new ParticleBuilder(Particle.BLOCK_CRUMBLE)
						.location(loc)
						.data(block.getBlockData())
						.count(10)
						.extra(.1)
						.spawn();
					loc.getWorld().playSound(loc, Sound.BLOCK_NETHER_BRICKS_BREAK, 1f, 1f);
					block.setType(Material.AIR);
				}
			}));

			if (logInfo) {
				for (UUID uuid : getAlivePlayers()) {
					Player player = PlayerUtils.getOnlinePlayer(uuid);
					log(player.getName() + "'s starting inventory: " + StringUtils.paste(Json.toString(Json.serialize(Arrays.asList(player.getInventory().getContents())))));
					lowestHealth.put(uuid, player.getHealth());
				}
			}

			tasks.add(new AntiCamping(this).start());
			tasks.add(Tasks.repeat(TickTime.SECOND, 5, () -> spectators().forEach(player -> player.setGameMode(GameMode.SPECTATOR))));
		});
	}

	public static void subtitle(Player player, String subtitle) {
		new TitleBuilder().players(player).subtitle(subtitle).fade(10).stay(40).send();
	}

	public boolean isInParty(Player player) {
		return party != null && party.contains(player.getUniqueId());
	}

	public boolean isAlive(Player player) {
		return alivePlayers != null && alivePlayers.contains(player.getUniqueId());
	}

	public boolean isSpectating(Player player) {
		return spectators != null && spectators.contains(player.getUniqueId());
	}

	public OfflinePlayer getHostOfflinePlayer() {
		return PlayerUtils.getPlayer(host);
	}

	public Player getHostPlayer() {
		return getHostOfflinePlayer().getPlayer();
	}

	public Player getRandomAlivePlayer() {
		return RandomUtils.randomElement(alivePlayers());
	}

	public List<Player> alivePlayers() {
		return OnlinePlayers.where().include(alivePlayers).get();
	}

	public List<Player> spectators() {
		return OnlinePlayers.where().include(spectators).get();
	}

	public void sendSpectatorsToSpawn() {
		spectators().forEach(player -> {
			Warps.survival(player);
			player.setGameMode(GameMode.SURVIVAL);
		});
	}

	public void broadcastToParty(String message) {
		for (UUID player : party)
			PlayerUtils.send(player, WitherChallenge.PREFIX + message);
		for (UUID player : spectators)
			PlayerUtils.send(player, WitherChallenge.PREFIX + message);
	}

	public void teleportPartyToArena() {
		started = true;
		alivePlayers = party;
		for (Player player : alivePlayers())
			player.teleportAsync(new Location(Bukkit.getWorld("events"), -150.5, 69, -114.5))
				.thenRun(() -> CheatsCommand.off(player, true, "WitherFight#teleportPartyToArena"));
	}

	public void giveItems() {
		OfflinePlayer itemReceiver = getHostOfflinePlayer();
		boolean shouldGiveStar = shouldGiveStar();
		if (getDifficulty() == WitherChallenge.Difficulty.CORRUPTED && shouldGiveStar)
			if (getAlternateDrops() != null)
				PlayerUtils.giveItemsAndMailExcess(itemReceiver, getAlternateDrops(), null, WorldGroup.SURVIVAL);

		if (shouldGiveStar) {
			gotStar = true;
			PlayerUtils.giveItemsAndMailExcess(itemReceiver, Collections.singleton(new ItemStack(Material.NETHER_STAR)), null, WorldGroup.SURVIVAL);
			broadcastToParty("&3Congratulations! You have gotten a wither star for this fight!");
		} else {
			broadcastToParty("&cUnfortunately, you did not get a star this time." +
				(getDifficulty() != Difficulty.CORRUPTED ? " You can try a harder difficulty for a higher chance" : ""));
			if (getAlternateDrops() != null)
				PlayerUtils.giveItemsAndMailExcess(itemReceiver, getAlternateDrops(), null, WorldGroup.SURVIVAL);
		}
		logFinal();
	}

	public void logFinal() {
		if (logInfo) {
			DecimalFormat format = new DecimalFormat("#.0");
			for (UUID uuid : getAlivePlayers()) {
				Player player = PlayerUtils.getOnlinePlayer(uuid);
				log(player.getName() + "'s ending inventory: " + StringUtils.paste(Json.toString(Json.serialize(Arrays.asList(player.getInventory().getContents())))));
			}
			getParty().stream().filter(uuid -> !getAlivePlayers().contains(uuid)).forEach(uuid -> log(PlayerUtils.getPlayer(uuid).getName() + " died"));
			getParty().forEach(uuid -> log(PlayerUtils.getPlayer(uuid).getName() + "'s lowest health: " + format.format(lowestHealth.get(uuid))));
		}
	}

	public void spawnPiglins(int amount) {
		for (int i = 0; i < amount; i++) {
			Location location = getMobSpawnLocation();
			PigZombie piglin = location.getWorld().spawn(location, PigZombie.class);
			piglin.setAdult();
			piglin.setCanPickupItems(false);
			piglin.setTarget(RandomUtils.randomElement(this.alivePlayers()));
			if (RandomUtils.chanceOf(20))
				piglin.setBaby();
		}
	}

	private @NotNull Location getMobSpawnLocation() {
		WorldGuardUtils worldguard = new WorldGuardUtils("events");
		final Region region = worldguard.getRegion("witherarena-pigmen");
		final List<BlockVector3> blocks = worldguard.getAllBlocks(region);

		final Location location = Utils.tryCalculate(100, () -> {
			Location spawnLocation = worldguard.toLocation(RandomUtils.randomElement(blocks));
			if (spawnLocation.getBlock().getType().isEmpty() && spawnLocation.getBlock().getRelative(BlockFace.UP).getType().isEmpty())
				return spawnLocation;
			return null;
		});

		if (location != null)
			return location;

		Nexus.warn("[Wither] Could not find location to spawn piglin");
		return worldguard.toLocation(region.getMinimumPoint());
	}

	public void spawnHoglins(int amount) {
		for (int i = 0; i < amount; i++) {
			Location location = getMobSpawnLocation();
			Hoglin hoglin = location.getWorld().spawn(location, Hoglin.class);
			hoglin.setAdult();
			hoglin.setCanPickupItems(false);
			hoglin.setImmuneToZombification(true);
			hoglin.setTarget(RandomUtils.randomElement(this.alivePlayers()));
		}
	}

	public void spawnBrutes(int amount) {
		for (int i = 0; i < amount; i++) {
			Location location = getMobSpawnLocation();
			PiglinBrute brute = location.getWorld().spawn(location, PiglinBrute.class);
			brute.setCanPickupItems(false);
			brute.setImmuneToZombification(true);
			brute.setTarget(RandomUtils.randomElement(this.alivePlayers()));
		}
	}

	public List<Blaze> spawnBlazes(int amount, int radius) {
		List<Location> locations = new ArrayList<>();
		for (int iteration = 0; iteration < amount; iteration++) {
			double angle = 360.0 / amount * iteration;
			angle = Math.toRadians(angle);
			double x = Math.cos(angle) * radius;
			double z = Math.sin(angle) * radius;
			locations.add(wither.getLocation().clone().add(x, 0, z));
		}

		for (Location location : locations) {
			for (int i = 0; i < 5; i++)
				new ParticleBuilder(Particle.LAVA)
					.count(25)
					.location(location)
					.spawn();
		}

		List<Blaze> blazes = new ArrayList<>();
		for (Location location : locations) {
			Item item = location.getWorld().spawn(location, Item.class);
			item.setGravity(false);
			item.setInvulnerable(true);
			item.setItemStack(new ItemStack(Material.STONE_BUTTON));
			item.setCanPlayerPickup(false);
			item.setCanMobPickup(false);
			item.setWillAge(false);
			item.setVelocity(new Vector(0, 0, 0));
			Blaze blaze = location.getWorld().spawn(location, Blaze.class);
			blaze.setGravity(false);
			blazes.add(blaze);
			item.addPassenger(blaze);
		}
		announceBlazeShield();
		return blazes;
	}

	public void announceBlazeShield() {
		broadcastToParty("&cThe wither has spawned a blaze shield. &eKill them to continue the fight!");
	}

	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event) {
		if (!isInRegion(event.getEntity().getLocation()))
			return;

		if (event.getTarget() instanceof Player player && isAlive(player))
			return;

		event.setTarget(getRandomAlivePlayer());
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if (!isInRegion(event.getEntity().getLocation()))
			return;

		if (!(event.getEntity() instanceof Mob mob))
			return;

		mob.setTarget(getRandomAlivePlayer());
	}

	static {
		Tasks.repeat(TickTime.SECOND, TickTime.SECOND, () -> {
			if (WitherChallenge.currentFight == null)
				return;

			if (!WitherChallenge.currentFight.isStarted())
				return;

			if (Nullables.isNullOrEmpty(WitherChallenge.currentFight.getAlivePlayers()))
				return;

			for (Entity entity : WitherChallenge.getEntities()) {
				if (!(entity instanceof Mob mob))
					continue;

				if (mob.getTarget() != null)
					if (mob.getTarget() instanceof Player player)
						if (WitherChallenge.currentFight.getAlivePlayers().contains(player.getUniqueId()))
							continue;

				mob.setTarget(WitherChallenge.currentFight.getRandomAlivePlayer());
			}
		});
	}

	@EventHandler
	public void onKillEntity(EntityDeathEvent event) {
		if (!isInRegion(event.getEntity().getLocation()))
			return;

		if (event.getEntityType() == EntityType.PLAYER)
			return;

		event.getDrops().clear();
		event.setDroppedExp(0);
		if (event.getEntityType() != EntityType.BLAZE)
			return;

		Blaze blaze = (Blaze) event.getEntity();
		if (!blazes.contains(blaze))
			return;

		if (blaze.getVehicle() != null)
			blaze.getVehicle().remove();

		blazes.remove(blaze);
		if (blazes.size() > 0)
			return;

		onKillBlazeShield().thenRun(() -> {
			wither.setAI(true);
			wither.setGravity(true);
			wither.setInvulnerable(false);
			shouldRegen = true;
		});
	}

	public CompletableFuture<Void> onKillBlazeShield() {
		return CompletableFuture.completedFuture(null);
	}

	@EventHandler
	public void onWitherRegen(EntityRegainHealthEvent event) {
		if (!event.getEntity().equals(wither))
			return;

		if (shouldRegen)
			return;

		event.setCancelled(true);
	}

	public void processPlayerQuit(Player player, String reason) {
		player.setGameMode(GameMode.SURVIVAL);
		if (alivePlayers.size() == 1) {
			tasks.forEach(Tasks::cancel);
			int partySize = party.size();

			String message = "&e" + Nickname.of(getHostOfflinePlayer()) +
				(partySize > 1 ? " and " + (partySize - 1) + " other" + ((partySize - 1 > 1) ? "s" : "") + " &3have" : " &3has") +
				" lost to the Wither in " + getDifficulty().getTitle() + " &3mode";

			if (WitherArenaConfig.isBeta())
				Broadcast.staff().prefix("Wither").message(message).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();
			else
				Broadcast.all().prefix("Wither").message(message).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();

			logFinal();
			WitherChallenge.reset();
		} else {
			WitherChallenge.currentFight.broadcastToParty("&e" + Nickname.of(player) + " &chas " + reason + " and is out of the fight!");
			wither.setTarget(getRandomAlivePlayer());
		}
		alivePlayers.remove(player.getUniqueId());
		HealCommand.healPlayer(player);
		Tasks.wait(5, () -> Warps.survival(player));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onDeath(PlayerDeathEvent event) {
		if (WitherChallenge.currentFight == null)
			return;

		Player player = event.getEntity();
		if (!isAlive(player))
			return;

		event.setCancelled(true);
		event.deathMessage(null);
		processPlayerQuit(player, "died");
	}

	@EventHandler
	public void onKillWither(EntityDeathEvent event) {
		if (event.getEntityType() != EntityType.WITHER)
			return;

		Wither wither = (Wither) event.getEntity();
		if (!wither.equals(this.wither)) {
			event.setDroppedExp(0);
			event.getDrops().clear();
			return;
		}

		tasks.forEach(Tasks::cancel);
		giveItems();

		int partySize = party.size();

		String message = "&e" + Nickname.of(getHostOfflinePlayer()) +
			(partySize > 1 ? " and " + (partySize - 1) + gg.projecteden.nexus.utils.StringUtils.plural(" other", partySize - 1) + " &3have" : " &3has") +
			" successfully beaten the Wither in " + getDifficulty().getTitle() + " &3mode " + (gotStar ? "and got" : "but did not get") + " the star";

		if (WitherArenaConfig.isBeta())
			Broadcast.staff().prefix("Wither").message(message).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();
		else
			Broadcast.all().prefix("Wither").message(message).muteMenuItem(MuteMenuItem.BOSS_FIGHT).send();

		new WorldGuardUtils("events").getEntitiesInRegion("witherarena").forEach(e -> {
			if (e.getType() != EntityType.PLAYER)
				e.remove();
		});

		for (Player player : alivePlayers())
			HealCommand.healPlayer(player);

		Tasks.wait(TickTime.SECOND.x(10), () -> {
			started = false;
			WitherChallenge.currentFight.alivePlayers().forEach(Warps::survival);
			WitherChallenge.currentFight.sendSpectatorsToSpawn();
			WitherChallenge.reset();
		});
	}

	public boolean isInRegion(Location location) {
		return new WorldGuardUtils(location.getWorld()).isInRegion(location, "witherarena");
	}

	@EventHandler
	public void onPlaceWater(PlayerBucketEmptyEvent event) {
		if (!isInRegion(event.getBlock().getLocation()))
			return;

		if (event.getBucket() != Material.WATER_BUCKET)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onWitherSkeletonDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof WitherSkeleton))
			return;

		if (event.getDamager() instanceof Player || event.getDamager() instanceof Arrow)
			return;

		if (!isInRegion(event.getEntity().getLocation()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onSuffocate(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.SUFFOCATION)
			return;

		if (!isInRegion(event.getEntity().getLocation()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onFallingBlockLand(EntityChangeBlockEvent event) {
		if (!(event.getEntity() instanceof FallingBlock))
			return;

		if (!isInRegion(event.getEntity().getLocation()))
			return;

		event.setCancelled(true);
		event.getEntity().remove();
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		if (!isInRegion(event.getBlock().getLocation()))
			return;

		if (!isAlive(event.getPlayer()))
			return;

		playerPlacedBlocks.add(event.getBlock().getLocation());
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		if (!isInRegion(event.getBlock().getLocation()))
			return;

		if (!isAlive(event.getPlayer()))
			return;

		if (playerPlacedBlocks.contains(event.getBlock().getLocation()))
			return;

		if (MaterialTag.FIRE.isTagged(event.getBlock().getType()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		if (!isInRegion(event.getBlock().getLocation()))
			return;

		if (playerPlacedBlocks.contains(event.getBlock().getLocation()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onWitherExplodeBlock(EntityChangeBlockEvent event) {
		if (!isInRegion(event.getBlock().getLocation()))
			return;

		if (playerPlacedBlocks.contains(event.getBlock().getLocation()))
			return;

		if (event.getTo() != Material.AIR)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onRemoveMapFromFrame(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof ItemFrame))
			return;

		if (!isInRegion(event.getEntity().getLocation()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		if (!(event.getEntity() instanceof ItemFrame))
			return;

		if (!isInRegion(event.getEntity().getLocation()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onItemFrameBreakByEntity(HangingBreakByEntityEvent event) {
		if (!(event.getEntity() instanceof ItemFrame))
			return;

		if (!isInRegion(event.getEntity().getLocation()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent event) {
		if (!isInRegion(event.getEntity().getLocation()))
			return;

		event.blockList().removeIf(block -> !playerPlacedBlocks.contains(block.getLocation()));
	}

	@EventHandler
	public void onMcMMOXpGainEvent(McMMOPlayerXpGainEvent event) {
		if (!isInRegion(event.getPlayer().getLocation()))
			return;

		event.setRawXpGained(0F);
		event.setCancelled(true);
	}

	@EventHandler
	public void onClickEntityWithBlock(PlayerInteractAtEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame))
			return;

		if (!isInRegion(event.getRightClicked().getLocation()))
			return;

		event.setCancelled(true);
		if (!event.getPlayer().getInventory().getItemInMainHand().getType().isBlock())
			return;

		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		event.getRightClicked().getLocation().getBlock().setType(item.getType());
		item.setAmount(item.getAmount() - 1);
		event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;
		
		if (!getAlivePlayers().contains(player.getUniqueId()))
			return;

		double health = player.getHealth() - event.getFinalDamage();
		if (lowestHealth.get(player.getUniqueId()) < health)
			return;

		lowestHealth.put(player.getUniqueId(), health);
	}

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		if (event.getEntity().getItemStack().getType() != Material.WITHER_ROSE)
			return;

		if (!isInRegion(event.getLocation()))
			return;

		event.setCancelled(true);
	}

	public enum CounterAttack {
		KNOCKBACK {
			@Override
			public void execute(Player player) {
				Location witherLocation = WitherChallenge.currentFight.wither.getLocation();
				Location playerLocation = player.getLocation();
				int x = (int) (playerLocation.getX() - witherLocation.getX());
				int z = (int) (playerLocation.getZ() - witherLocation.getZ());
				player.setVelocity(new Vector(x, .5, z).multiply(1.25));
			}
		},
		BLINDNESS {
			@Override
			public void execute(Player player) {
				player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(TickTime.SECOND.x(5)).amplifier(0).ambient(true).build());
			}
		},
		CONFUSION {
			@Override
			public void execute(Player player) {
				player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.NAUSEA).duration(TickTime.SECOND.x(10)).amplifier(0).ambient(true).build());
			}
		},
		TAKE_POTIONS {
			@Override
			public void execute(Player player) {
				for (PotionEffect effect : player.getActivePotionEffects()) {
					if (effect.getType().equals(PotionEffectType.WITHER))
						continue;

					player.removePotionEffect(effect.getType());
					subtitle(player, "&8&kbbb &4&lStripped Potion Effects &8&kbbb");
				}
			}
		},
		HUNGER {
			@Override
			public void execute(Player player) {
				player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.HUNGER).duration(TickTime.SECOND.x(10)).amplifier(1).ambient(true).build());
			}
		},
		LEVITATION {
			@Override
			public void execute(Player player) {
				player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.LEVITATION).duration(TickTime.SECOND.x(5)).amplifier(0).ambient(true).build());
			}
		},
		WITHER_EFFECT {
			@Override
			public void execute(Player player) {
				player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.WITHER).duration(TickTime.SECOND.x(10)).amplifier(0).ambient(true).build());
			}
		},
		DUPLICATE {
			@Override
			public void execute(List<Player> players) {
				Location witherLoc = WitherChallenge.currentFight.wither.getLocation();
				spawnMinion(witherLoc.clone().add(3, 0, 0));
				spawnMinion(witherLoc.clone().add(-3, 0, 0));
			}

			public Wither spawnMinion(Location location) {
				Wither wither = location.getWorld().spawn(location, Wither.class, SpawnReason.CUSTOM);
				EntityUtils.setHealth(wither, 1);
				wither.setCustomName("Minion");
				wither.setCustomNameVisible(true);
				if (wither.getBossBar() != null)
					wither.getBossBar().setVisible(false);
				return wither;
			}
		},
		TNT {
			@Override
			public void execute(List<Player> players) {
				Location witherLoc = WitherChallenge.currentFight.wither.getLocation();
				TNTPrimed tnt = witherLoc.getWorld().spawn(witherLoc, TNTPrimed.class);
				tnt.setFuseTicks(50);
			}
		};

		public void execute(List<Player> players) {
			for (Player player : players)
				execute(player);
		}

		public void execute(Player player) {}
	}

}
