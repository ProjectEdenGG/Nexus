package gg.projecteden.nexus.features.wither;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.utils.TimeUtils.Time;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static gg.projecteden.nexus.features.wither.WitherChallenge.currentFight;

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
		CompletableFuture<Location> completableFuture = new CompletableFuture<>();

		hideAllPlayers();

		for (Player player : currentFight.alivePlayers()) {
			player.setGameMode(GameMode.SPECTATOR);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Time.SECOND.x(3), 1, true));
			player.teleport(new Location(Bukkit.getWorld("events"), -150.50, 77.00, -82.50, .00F, 20.00F));

			new TitleBuilder()
				.players(player)
				.title("&8&k%%%% &4&lWither &8&k%%%%")
				.subtitle(currentFight.getDifficulty().getTitle())
				.fade(15).stay(25)
				.send();
		}

		Location cageLoc = WitherChallenge.cageLoc.clone();
		final WitherSkeleton witherSkeleton = cageLoc.getWorld().spawn(cageLoc, WitherSkeleton.class);
		witherSkeleton.setAI(false);

		AtomicInteger lightningIndex = new AtomicInteger();
		for (int i = 0; i < 5; i++) {
			Tasks.wait(Time.SECOND.x(waitSeconds += 1), () -> {
				strike(LIGHTNING_LOCATIONS[lightningIndex.getAndIncrement()]);
				strike(LIGHTNING_LOCATIONS[lightningIndex.getAndIncrement()]);
			});
		}

		Tasks.wait(Time.SECOND.x(waitSeconds += 1), () -> strike(LIGHTNING_LOCATIONS[10]));

		AtomicReference<Wither> wither = new AtomicReference<>();

		Tasks.wait(Time.SECOND.x(waitSeconds += 2), () -> {
			strike(LIGHTNING_LOCATIONS[11]);
			BlockUtils.getBlocksInRadius(cageLoc, 3).forEach(block -> {
				FallingBlock fallingBlock = block.getLocation().getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
				fallingBlock.setDropItem(false);
				fallingBlock.setHurtEntities(false);
				fallingBlocks.add(fallingBlock);
				double x = (int) (block.getX() - cageLoc.getX()) * (RandomUtils.randomDouble(.2) + 1);
				double z = (int) (block.getZ() - cageLoc.getZ()) * (RandomUtils.randomDouble(.2) + 1);
				fallingBlock.setVelocity(new Vector(x, 1.25, z).multiply(1.25));
				for (Block face : BlockUtils.getAdjacentBlocks(block))
					if (MaterialTag.NEEDS_SUPPORT.isTagged(face.getType()))
						face.setType(Material.AIR);
				block.setType(Material.AIR);
			});
			for (Player player : currentFight.alivePlayers())
				player.playSound(LIGHTNING_LOCATIONS[11], Sound.ENTITY_WITHER_SPAWN, 1, 1);

			witherSkeleton.remove();
			Wither witherEntity = cageLoc.getWorld().spawn(cageLoc, Wither.class, SpawnReason.CUSTOM);
			witherEntity.setAI(false);
			witherEntity.setGravity(false);
			if (witherEntity.getBossBar() != null)
				witherEntity.getBossBar().setVisible(false);
			wither.set(witherEntity);
			for (int i = 0; i < 5; i++)
				cageLoc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, cageLoc, 1, 1, 1, 1);
		});

		Tasks.wait(Time.SECOND.x(waitSeconds += 2), () -> {
			fallingBlocks.forEach(Entity::remove);
			Nexus.unregisterListener(this);
			showAllPlayers();
			wither.get().remove();
			for (Player player : currentFight.alivePlayers()) {
				player.setGameMode(GameMode.SURVIVAL);
				player.teleport(new Location(Bukkit.getWorld("events"), -150.50, 69.00, -80.50, .00F, .00F));
			}
		});
		Tasks.wait(Time.SECOND.x(waitSeconds) + 2, () -> completableFuture.complete(cageLoc));
		return completableFuture;
	}

	public void hideAllPlayers() {
		for (Player player1 : currentFight.alivePlayers())
			for (Player player2 : currentFight.alivePlayers())
				if (!player1.equals(player2))
					player1.hidePlayer(Nexus.getInstance(), player2);
	}

	public void showAllPlayers() {
		for (Player player1 : currentFight.alivePlayers())
			for (Player player2 : currentFight.alivePlayers())
				if (!player1.equals(player2))
					player1.showPlayer(Nexus.getInstance(), player2);
	}

	public void strike(Location location) {
		location.getBlock().setType(Material.SOUL_FIRE);
		location.getWorld().strikeLightningEffect(location);
		for (Player player : currentFight.alivePlayers()) {
			player.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.MASTER, 1, 1);
			player.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 1, 1);
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
		if (!currentFight.getParty().contains(event.getPlayer().getUniqueId())) return;
		event.setCancelled(true);
	}

}
