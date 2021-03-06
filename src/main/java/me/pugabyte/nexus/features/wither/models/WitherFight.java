package me.pugabyte.nexus.features.wither.models;

import com.destroystokyo.paper.ParticleBuilder;
import lombok.Data;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.menus.mutemenu.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.warps.Warps;
import me.pugabyte.nexus.features.wither.BeginningCutscene;
import me.pugabyte.nexus.features.wither.WitherChallenge;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.WorldGroup;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
public abstract class WitherFight implements Listener {

	public UUID host;
	public List<UUID> party;
	public List<UUID> alivePlayers;
	public Wither wither;
	public boolean started = false;
	public List<Location> playerPlacedBlocks = new ArrayList<>();
	public List<Blaze> blazes = new ArrayList<>();
	public boolean shouldRegen = true;
	public boolean gotStar = false;

	public abstract WitherChallenge.Difficulty getDifficulty();

	public abstract void spawnWither(Location location);

	public abstract boolean shouldGiveStar();

	public abstract List<ItemStack> getAlternateDrops();

	public void start() {
		started = true;
		Nexus.registerListener(this);
		new BeginningCutscene().run().thenAccept(this::spawnWither);
	}

	public OfflinePlayer getHostOfflinePlayer() {
		return PlayerUtils.getPlayer(host);
	}

	public Player getHostPlayer() {
		return getHostOfflinePlayer().getPlayer();
	}

	public void broadcastToParty(String message) {
		for (UUID player : party)
			PlayerUtils.send(player, WitherChallenge.PREFIX + message);
	}

	public void teleportPartyToArena() {
		alivePlayers = party;
		for (UUID uuid : party) {
			Player player = PlayerUtils.getPlayer(uuid).getPlayer();
			if (player != null)
				player.teleport(new Location(Bukkit.getWorld("events"), -150.50, 69.00, -114.50, .00F, .00F));
		}
	}

	public void giveItems() {
		OfflinePlayer itemReceiver = getHostOfflinePlayer();
		if (getDifficulty() == WitherChallenge.Difficulty.CORRUPTED)
			if (getAlternateDrops() != null)
				PlayerUtils.giveItemsAndDeliverExcess(itemReceiver, getAlternateDrops(), null, WorldGroup.SURVIVAL);
		if (shouldGiveStar()) {
			gotStar = true;
			PlayerUtils.giveItemsAndDeliverExcess(itemReceiver, Collections.singleton(new ItemStack(Material.NETHER_STAR)), null, WorldGroup.SURVIVAL);
			broadcastToParty("&3Congratulations! You have gotten a wither star for this fight!");
		} else {
			broadcastToParty("&cUnfortunately, you did not get a star this time. You can try a harder difficulty for a higher chance");
			if (getAlternateDrops() != null)
				PlayerUtils.giveItemsAndDeliverExcess(itemReceiver, getAlternateDrops(), null, WorldGroup.SURVIVAL);
		}
	}

	public void spawnPiglins(int amount) {
		WorldGuardUtils utils = new WorldGuardUtils("events");
		for (int i = 0; i < amount; i++) {
			Location location;
			do {
				location = utils.getRandomBlock("witherarena-pigmen").getLocation();
			} while (location.getBlock().getType() != Material.AIR);
			PigZombie piglin = location.getWorld().spawn(location, PigZombie.class);
			piglin.setCanPickupItems(false);
			piglin.setTarget(PlayerUtils.getPlayer(RandomUtils.randomElement(alivePlayers)).getPlayer());
		}
	}

	public void spawnHoglins(int amount) {
		WorldGuardUtils utils = new WorldGuardUtils("events");
		for (int i = 0; i < amount; i++) {
			Location location;
			do {
				location = utils.getRandomBlock("witherarena-pigmen").getLocation();
			} while (location.getBlock().getType() != Material.AIR);
			Hoglin hoglin = location.getWorld().spawn(location, Hoglin.class);
			hoglin.setCanPickupItems(false);
			hoglin.setImmuneToZombification(true);
			hoglin.setTarget(PlayerUtils.getPlayer(RandomUtils.randomElement(alivePlayers)).getPlayer());
		}
	}

	public void spawnBrutes(int amount) {
		WorldGuardUtils utils = new WorldGuardUtils("events");
		for (int i = 0; i < amount; i++) {
			Location location;
			do {
				location = utils.getRandomBlock("witherarena-pigmen").getLocation();
			} while (location.getBlock().getType() != Material.AIR);
			PiglinBrute brute = location.getWorld().spawn(location, PiglinBrute.class);
			brute.setCanPickupItems(false);
			brute.setImmuneToZombification(true);
			brute.setTarget(PlayerUtils.getPlayer(RandomUtils.randomElement(alivePlayers)).getPlayer());
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
	public void onKillEntity(EntityDeathEvent event) {
		if (!isInRegion(event.getEntity().getLocation())) return;
		if (event.getEntityType() == EntityType.PLAYER) return;
		event.getDrops().clear();
		event.setDroppedExp(0);
		if (event.getEntityType() != EntityType.BLAZE) return;
		Blaze blaze = (Blaze) event.getEntity();
		if (!blazes.contains(blaze)) return;

		if (blaze.getVehicle() != null)
			blaze.getVehicle().remove();

		blazes.remove(blaze);
		if (blazes.size() > 0) return;
		wither.setAI(true);
		wither.setGravity(true);
		wither.setInvulnerable(false);
		shouldRegen = true;
	}

	@EventHandler
	public void onWitherRegen(EntityRegainHealthEvent event) {
		if (event.getEntity() != wither) return;
		if (shouldRegen) return;
		if (event.getEntity() != wither) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (WitherChallenge.currentFight == null) return;
		Player player = event.getEntity();
		if (!alivePlayers.contains(player.getUniqueId())) return;
		alivePlayers.remove(player.getUniqueId());
		event.setCancelled(true);
		Warps.spawn(player);
		if (alivePlayers.size() == 0) {
			int partySize = party.size();
			Chat.broadcastIngame(WitherChallenge.PREFIX + "&e" + getHostOfflinePlayer().getName() +
					(partySize > 1 ? " and " + (partySize - 1) + " other" + ((partySize - 1 > 1) ? "s" : "") + " &3have" : " &3has") +
					" lost to the Wither in " + getDifficulty().getTitle() + " &3mode", MuteMenuItem.EVENT_ANNOUNCEMENTS);
			Chat.broadcastDiscord("**[Wither]** " + getHostOfflinePlayer().getName() +
					(partySize > 1 ? " and " + (partySize - 1) + " other" + ((partySize - 1 > 1) ? "s" : "") + " have" : " has") +
					" lost to the Wither in " + StringUtils.camelCase(getDifficulty().name()) + " mode");
			WitherChallenge.reset();
		} else
			WitherChallenge.currentFight.broadcastToParty("&e" + player.getName() + " &chas died and is out of the fight!");
	}

	@EventHandler
	public void onKillWither(EntityDeathEvent event) {
		if (event.getEntityType() != EntityType.WITHER) return;
		Wither wither = (Wither) event.getEntity();
		if (wither != this.wither) return;

		giveItems();

		int partySize = party.size();
		Chat.broadcastIngame(WitherChallenge.PREFIX + "&e" + getHostOfflinePlayer().getName() +
				(partySize > 1 ? " and " + (partySize - 1) + " other" + ((partySize - 1 > 1) ? "s" : "") + " &3have" : " &3has") +
				" successfully beaten the Wither in " +
				getDifficulty().getTitle() + " &3mode. " + (gotStar ? "They got the star for this fight." : "They did not get a star for this fight."), MuteMenuItem.EVENT_ANNOUNCEMENTS);
		Chat.broadcastDiscord("**[Wither]** " + getHostOfflinePlayer().getName() +
				(partySize > 1 ? " and " + (partySize - 1) + " other" + ((partySize - 1 > 1) ? "s" : "") + " have" : " has") +
				" successfully beaten the Wither in " + StringUtils.camelCase(getDifficulty().name()) + " mode. " + (gotStar ? "They got the star for this fight." : "They did not get a star for this fight."));

		Tasks.wait(Time.SECOND.x(10), () -> {
			WitherChallenge.currentFight.getAlivePlayers().forEach(uuid -> {
				OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
				if (offlinePlayer.getPlayer() != null)
					Warps.spawn(offlinePlayer.getPlayer());
			});
			WitherChallenge.reset();
		});
	}

	public boolean isInRegion(Location location) {
		return new WorldGuardUtils(location.getWorld()).isInRegion(location, "witherarena");
	}

	@EventHandler
	public void onFallingBlockLand(EntityChangeBlockEvent event) {
		if (!(event.getEntity() instanceof FallingBlock)) return;
		if (!isInRegion(event.getEntity().getLocation())) return;
		event.setCancelled(true);
		event.getEntity().remove();
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		if (!isInRegion(event.getBlock().getLocation())) return;
		if (!alivePlayers.contains(event.getPlayer().getUniqueId())) return;
		playerPlacedBlocks.add(event.getBlock().getLocation());
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		if (!isInRegion(event.getBlock().getLocation())) return;
		if (!alivePlayers.contains(event.getPlayer().getUniqueId())) return;
		if (playerPlacedBlocks.contains(event.getBlock().getLocation())) return;
		if (event.getBlock().getType() == Material.FIRE) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		if (!isInRegion(event.getBlock().getLocation())) return;
		if (playerPlacedBlocks.contains(event.getBlock().getLocation())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onWitherExplodeBlock(EntityChangeBlockEvent event) {
		if (!isInRegion(event.getBlock().getLocation())) return;
		if (playerPlacedBlocks.contains(event.getBlock().getLocation())) return;
		if (event.getTo() != Material.AIR) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onRemoveMapFromFrame(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof ItemFrame)) return;
		if (!isInRegion(event.getEntity().getLocation())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		if (!(event.getEntity() instanceof ItemFrame)) return;
		if (!isInRegion(event.getEntity().getLocation())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onItemFrameBreakByEntity(HangingBreakByEntityEvent event) {
		if (!(event.getEntity() instanceof ItemFrame)) return;
		if (!isInRegion(event.getEntity().getLocation())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent event) {
		if (!isInRegion(event.getEntity().getLocation())) return;
		event.blockList().removeIf(block -> !playerPlacedBlocks.contains(block.getLocation()));
	}

	@EventHandler
	public void onClickEntityWitherBlock(PlayerInteractAtEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame)) return;
		if (!isInRegion(event.getRightClicked().getLocation())) return;
		event.setCancelled(true);
		if (!event.getPlayer().getInventory().getItemInMainHand().getType().isBlock()) return;
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		event.getRightClicked().getLocation().getBlock().setType(item.getType());
		item.setAmount(item.getAmount() - 1);
		event.getPlayer().updateInventory();
	}

	public enum CounterAttack {
		KNOCKBACK {
			@Override
			public void execute(List<UUID> uuids) {
				Location witherLoc = WitherChallenge.currentFight.wither.getLocation();
				for (UUID uuid : uuids) {
					if (PlayerUtils.getPlayer(uuid).getPlayer() == null) continue;
					Player player = PlayerUtils.getPlayer(uuid).getPlayer();
					Location playerLoc = player.getLocation();
					int x = (int) (playerLoc.getX() - witherLoc.getX());
					int z = (int) (playerLoc.getZ() - witherLoc.getZ());
					player.setVelocity(new Vector(x, .5, z).multiply(1.25));
				}
			}
		},
		BLINDNESS {
			@Override
			public void execute(List<UUID> uuids) {
				for (UUID uuid : uuids)
					if (PlayerUtils.getPlayer(uuid).getPlayer() != null)
						PlayerUtils.getPlayer(uuid).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Time.SECOND.x(5), 0, true));
			}
		},
		CONFUSION {
			@Override
			public void execute(List<UUID> uuids) {
				for (UUID uuid : uuids) {
					if (PlayerUtils.getPlayer(uuid).getPlayer() != null)
						PlayerUtils.getPlayer(uuid).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Time.SECOND.x(10), 0, true));
				}
			}
		},
		TAKE_POTIONS {
			@Override
			public void execute(List<UUID> uuids) {
				for (UUID uuid : uuids) {
					OfflinePlayer player = PlayerUtils.getPlayer(uuid);
					if (player.getPlayer() == null) continue;
					for (PotionEffect effect : player.getPlayer().getActivePotionEffects())
						player.getPlayer().removePotionEffect(effect.getType());
				}
				WitherChallenge.currentFight.broadcastToParty("The wither has stripped all potion effects from the party");
			}
		},
		HUNGER {
			@Override
			public void execute(List<UUID> uuids) {
				for (UUID uuid : uuids)
					if (PlayerUtils.getPlayer(uuid).getPlayer() != null)
						PlayerUtils.getPlayer(uuid).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Time.SECOND.x(10), 0, true));
			}
		},
		LEVITATION {
			@Override
			public void execute(List<UUID> uuids) {
				for (UUID uuid : uuids)
					if (PlayerUtils.getPlayer(uuid).getPlayer() != null)
						PlayerUtils.getPlayer(uuid).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Time.SECOND.x(5), 0, true));
			}
		},
		WITHER_EFFECT {
			@Override
			public void execute(List<UUID> uuids) {
				for (UUID uuid : uuids)
					if (PlayerUtils.getPlayer(uuid).getPlayer() != null)
						PlayerUtils.getPlayer(uuid).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Time.SECOND.x(10), 0, true));
			}
		},
		DUPLICATE {
			@Override
			public void execute(List<UUID> uuids) {
				Location witherLoc = WitherChallenge.currentFight.wither.getLocation();
				Wither wither1 = witherLoc.getWorld().spawn(witherLoc.clone().add(3, 0, 0), Wither.class);
				wither1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);
				wither1.setHealth(1);
				wither1.setCustomName("Minion");
				wither1.setTarget(PlayerUtils.getPlayer(RandomUtils.randomElement(uuids)).getPlayer());

				Wither wither2 = witherLoc.getWorld().spawn(witherLoc.clone().add(-3, 0, 0), Wither.class);
				wither2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);
				wither2.setHealth(1);
				wither2.setCustomName("Minion");
				wither2.setTarget(PlayerUtils.getPlayer(RandomUtils.randomElement(uuids)).getPlayer());
			}
		},
		TNT {
			@Override
			public void execute(List<UUID> uuids) {
				Location witherLoc = WitherChallenge.currentFight.wither.getLocation();
				TNTPrimed tnt = witherLoc.getWorld().spawn(witherLoc, TNTPrimed.class);
				tnt.setFuseTicks(100);
			}
		};

		public abstract void execute(List<UUID> uuids);
	}

}
