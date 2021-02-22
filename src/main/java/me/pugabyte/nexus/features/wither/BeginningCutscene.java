package me.pugabyte.nexus.features.wither;

import com.destroystokyo.paper.Title;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BeginningCutscene implements Listener {

	public BeginningCutscene() {
		run();
	}

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

	public void run() {
		Nexus.registerListener(this);
		int waitSeconds = 3;

		hideAllPlayers();
		List<Player> players = new ArrayList<>();
		for (UUID uuid : Wither.activePlayers)
			if (PlayerUtils.getPlayer(uuid).isOnline())
				players.add(PlayerUtils.getPlayer(uuid).getPlayer());

		for (Player player : players) {
			player.setGameMode(GameMode.SPECTATOR);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Time.SECOND.x(3), 1, true));
			player.teleport(new Location(Bukkit.getWorld("events"), -150.50, 77.00, -82.50, .00F, 20.00F));
			player.sendTitle(Title.builder()
					.fadeIn(15)
					.fadeOut(15)
					.stay(25)
					.title(StringUtils.colorize("&8&k%%%% &4&lWither &8&k%%%%"))
					.subtitle(Wither.difficulty.getTitle())
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

		Tasks.wait(Time.SECOND.x(waitSeconds += 2), () -> {
			spawnFire(LIGHTNING_LOCATIONS[11]);
			for (UUID uuid : Wither.activePlayers) {
				OfflinePlayer player = PlayerUtils.getPlayer(uuid);
				if (!player.isOnline()) continue;
				player.getPlayer().playSound(LIGHTNING_LOCATIONS[11], Sound.ENTITY_WITHER_SPAWN, 1, 1);
				witherSkeleton.remove();
			}
			for (int i = 0; i < 5; i++)
				cageLoc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, cageLoc, 1, 1, 1, 1);
		});

		Tasks.wait(Time.SECOND.x(waitSeconds + 2), () -> {
			Nexus.unregisterListener(this);
			showAllPlayers();
			for (UUID uuid : Wither.activePlayers)
				if (PlayerUtils.getPlayer(uuid).isOnline()) {
					PlayerUtils.getPlayer(uuid).getPlayer().setGameMode(GameMode.SURVIVAL);
					PlayerUtils.getPlayer(uuid).getPlayer().teleport(new Location(Bukkit.getWorld("events"), -150.50, 69.00, -80.50, .00F, .00F));
				}
			Wither.reset();
		});
	}

	public void hideAllPlayers() {
		List<Player> players = new ArrayList<>();
		for (UUID uuid : Wither.activePlayers)
			if (PlayerUtils.getPlayer(uuid).isOnline())
				players.add(PlayerUtils.getPlayer(uuid).getPlayer());

		for (Player player1 : players)
			for (Player player2 : players)
				if (!player1.equals(player2))
					player1.hidePlayer(Nexus.getInstance(), player2);
	}

	public void showAllPlayers() {
		List<Player> players = new ArrayList<>();
		for (UUID uuid : Wither.activePlayers)
			if (PlayerUtils.getPlayer(uuid).isOnline())
				players.add(PlayerUtils.getPlayer(uuid).getPlayer());

		for (Player player1 : players)
			for (Player player2 : players)
				if (!player1.equals(player2))
					player1.showPlayer(Nexus.getInstance(), player2);
	}

	public void spawnFire(Location location) {
		location.getBlock().setType(Material.SOUL_FIRE);
		location.getWorld().strikeLightningEffect(location);
		for (UUID uuid : Wither.activePlayers) {
			OfflinePlayer player = PlayerUtils.getPlayer(uuid);
			if (!player.isOnline()) continue;
			player.getPlayer().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1, 1);
			player.getPlayer().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
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
		if (!Wither.activePlayers.contains(event.getPlayer().getUniqueId())) return;
		event.setCancelled(true);
	}

}
