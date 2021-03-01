package me.pugabyte.nexus.features.wither;

import com.destroystokyo.paper.Title;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class BeginningCutscene implements Listener {

	public BeginningCutscene() {
		Nexus.registerListener(this);
		run();
	}

	public final List<FallingBlock> fallingBlocks = new ArrayList<>();
	public static final Location[] LIGHTNING_LOCATIONS = {
			// Group 1
			new Location(Bukkit.getWorld("events"), -158.00, 69.00, -82.00, .00F, .00F),
			new Location(Bukkit.getWorld("events"), -144.00, 69.00, -82.00, .00F, .00F),
			// Group 2
			new Location(Bukkit.getWorld("events"), -164.00, 69.00, -76.00, .00F, .00F),
			new Location(Bukkit.getWorld("events"), -138.00, 69.00, -76.00, .00F, .00F),
			// Group 3
			new Location(Bukkit.getWorld("events"), -164.00, 69.00, -69.00, .00F, .00F),
			new Location(Bukkit.getWorld("events"), -138.00, 69.00, -69.00, .00F, .00F),
			// Group 4
			new Location(Bukkit.getWorld("events"), -164.00, 69.00, -62.00, .00F, .00F),
			new Location(Bukkit.getWorld("events"), -138.00, 69.00, -62.00, .00F, .00F),
			// Group 5
			new Location(Bukkit.getWorld("events"), -158.00, 69.00, -56.00, .00F, .00F),
			new Location(Bukkit.getWorld("events"), -144.00, 69.00, -56.00, .00F, .00F),
			// Final
			new Location(Bukkit.getWorld("events"), -151.00, 69.00, -55.00, .00F, .00F),
			// Alter
			new Location(Bukkit.getWorld("events"), -151.00, 71.00, -69.00, .00F, .00F)
	};

	public CompletableFuture<Location> run() {
		int waitSeconds = 3;
		CompletableFuture<Location> completableFuture = new CompletableFuture();

		hideAllPlayers();

		for (Player player : uuidToPlayers()) {
			player.setGameMode(GameMode.SPECTATOR);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Time.SECOND.x(3), 1, true));
			player.teleport(new Location(Bukkit.getWorld("events"), -150.50, 77.00, -82.50, .00F, 20.00F));
			player.sendTitle(Title.builder()
					.fadeIn(15)
					.fadeOut(15)
					.stay(25)
					.title(StringUtils.colorize("&8&k%%%% &4&lWither &8&k%%%%"))
					.subtitle(WitherChallenge.currentFight.getDifficulty().getTitle())
					.build());
		}

		Location cageLoc = new Location(Bukkit.getWorld("events"), -151.00, 76.00, -69.00, 180F, .00F);
		final WitherSkeleton witherSkeleton = cageLoc.getWorld().spawn(cageLoc, WitherSkeleton.class);
		witherSkeleton.setAI(false);

		Tasks.wait(Time.SECOND.x(waitSeconds += 1), () -> {
			spawnFire(LIGHTNING_LOCATIONS[0]);
			spawnFire(LIGHTNING_LOCATIONS[1]);
		});
		Tasks.wait(Time.SECOND.x(waitSeconds += 1), () -> {
			spawnFire(LIGHTNING_LOCATIONS[2]);
			spawnFire(LIGHTNING_LOCATIONS[3]);
		});
		Tasks.wait(Time.SECOND.x(waitSeconds += 1), () -> {
			spawnFire(LIGHTNING_LOCATIONS[4]);
			spawnFire(LIGHTNING_LOCATIONS[5]);
		});
		Tasks.wait(Time.SECOND.x(waitSeconds += 1), () -> {
			spawnFire(LIGHTNING_LOCATIONS[6]);
			spawnFire(LIGHTNING_LOCATIONS[7]);
		});
		Tasks.wait(Time.SECOND.x(waitSeconds += 1), () -> {
			spawnFire(LIGHTNING_LOCATIONS[8]);
			spawnFire(LIGHTNING_LOCATIONS[9]);
		});
		Tasks.wait(Time.SECOND.x(waitSeconds += 1), () -> spawnFire(LIGHTNING_LOCATIONS[10]));

		AtomicReference<Wither> wither = new AtomicReference<>();

		Tasks.wait(Time.SECOND.x(waitSeconds += 2), () -> {
			spawnFire(LIGHTNING_LOCATIONS[11]);
			BlockUtils.getBlocksInRadius(cageLoc, 2).forEach(block -> {
				FallingBlock fallingBlock = block.getLocation().getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
				fallingBlock.setDropItem(false);
				fallingBlock.setHurtEntities(false);
				fallingBlocks.add(fallingBlock);
				int x = (int) (block.getX() - cageLoc.getX());
				int z = (int) (block.getZ() - cageLoc.getZ());
				fallingBlock.setVelocity(new Vector(x, 1.25, z).multiply(1.25));
				block.setType(Material.AIR);
			});
			for (Player player : uuidToPlayers())
				player.playSound(LIGHTNING_LOCATIONS[11], Sound.ENTITY_WITHER_SPAWN, 1, 1);

			witherSkeleton.remove();
			Wither witherEntity = cageLoc.getWorld().spawn(cageLoc, Wither.class);
			witherEntity.setAI(false);
			witherEntity.setGravity(false);
			wither.set(witherEntity);
			for (int i = 0; i < 5; i++)
				cageLoc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, cageLoc, 1, 1, 1, 1);
		});

		Tasks.wait(Time.SECOND.x(waitSeconds + 2), () -> {
			fallingBlocks.forEach(Entity::remove);
			Nexus.unregisterListener(this);
			showAllPlayers();
			wither.get().remove();
			for (Player player : uuidToPlayers()) {
				player.setGameMode(GameMode.SURVIVAL);
				player.teleport(new Location(Bukkit.getWorld("events"), -150.50, 69.00, -80.50, .00F, .00F));
			}
			completableFuture.complete(cageLoc);
		});
		return completableFuture;
	}

	public void hideAllPlayers() {
		for (Player player1 : uuidToPlayers())
			for (Player player2 : uuidToPlayers())
				if (!player1.equals(player2))
					player1.hidePlayer(Nexus.getInstance(), player2);
	}

	public void showAllPlayers() {
		for (Player player1 : uuidToPlayers())
			for (Player player2 : uuidToPlayers())
				if (!player1.equals(player2))
					player1.showPlayer(Nexus.getInstance(), player2);
	}

	public List<Player> uuidToPlayers() {
		List<Player> players = new ArrayList<>();
		for (UUID uuid : WitherChallenge.currentFight.getParty()) {
			OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
			if (offlinePlayer.getPlayer() != null)
				players.add(offlinePlayer.getPlayer());
		}
		return players;
	}

	public void spawnFire(Location location) {
		location.getBlock().setType(Material.SOUL_FIRE);
		location.getWorld().strikeLightningEffect(location);
		for (Player player : uuidToPlayers()) {
			player.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1, 1);
			player.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
		}
	}

	/**
	 * Don't worry Griffin, I promise this is only active while the cutscene is active.
	 * I unregister this class as a listener everytime it finishes the animation so this listener
	 * is only active for roughly 10 seconds at a time. Players are in spectator mode
	 * and I don't want them to fly out of the lobby, and other methods just seemed weird.
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!WitherChallenge.currentFight.getAlivePlayers().contains(event.getPlayer())) return;
		event.setCancelled(true);
	}

}
