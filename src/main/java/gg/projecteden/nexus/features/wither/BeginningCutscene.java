package gg.projecteden.nexus.features.wither;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BeginningCutscene implements Listener {

	public BeginningCutscene() {
		Nexus.registerListener(this);
		run();
	}

	public final List<FallingBlock> fallingBlocks = new ArrayList<>();
	public static final Location[] LIGHTNING_LOCATIONS = {
			// Group 1
			WitherChallenge.location(-158, 69, -82),
			WitherChallenge.location(-144, 69, -82),
			// Group 2
			WitherChallenge.location(-164, 69, -76),
			WitherChallenge.location(-138, 69, -76),
			// Group 3
			WitherChallenge.location(-164, 69, -69),
			WitherChallenge.location(-138, 69, -69),
			// Group 4
			WitherChallenge.location(-164, 69, -62),
			WitherChallenge.location(-138, 69, -62),
			// Group 5
			WitherChallenge.location(-158, 69, -56),
			WitherChallenge.location(-144, 69, -56),
			// Final
			WitherChallenge.location(-151, 69, -55.5),
			// Alter
			WitherChallenge.location(-150.5, 71, -68.5)
	};

	public CompletableFuture<Location> run() {
		int waitSeconds = 3;
		CompletableFuture<Location> completableFuture = new CompletableFuture<>();

		hideAllPlayers();

		for (Player player : WitherChallenge.currentFight.alivePlayers()) {
			player.setGameMode(GameMode.SPECTATOR);
			player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(TickTime.SECOND.x(3)).ambient(true).build());
			player.teleport(WitherChallenge.location(-150.5, 77, -82.5, 0, 20));

			new TitleBuilder()
				.players(player)
				.title("&8&k%%%% &4&lWither &8&k%%%%")
				.subtitle(WitherChallenge.currentFight.getDifficulty().getTitle())
				.fade(15).stay(25)
				.send();
		}

		Location cageLoc = WitherChallenge.cageLoc.clone();
		final WitherSkeleton witherSkeleton = cageLoc.getWorld().spawn(cageLoc, WitherSkeleton.class);
		witherSkeleton.setAI(false);

		AtomicInteger lightningIndex = new AtomicInteger();
		for (int i = 0; i < 5; i++) {
			Tasks.wait(TickTime.SECOND.x(waitSeconds += 1), () -> {
				strike(LIGHTNING_LOCATIONS[lightningIndex.getAndIncrement()]);
				strike(LIGHTNING_LOCATIONS[lightningIndex.getAndIncrement()]);
			});
		}

		Tasks.wait(TickTime.SECOND.x(waitSeconds += 1), () -> strike(LIGHTNING_LOCATIONS[10]));

		AtomicReference<Wither> wither = new AtomicReference<>();

		Tasks.wait(TickTime.SECOND.x(waitSeconds += 2), () -> {
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
			for (Player player : WitherChallenge.currentFight.alivePlayers())
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

		Tasks.wait(TickTime.SECOND.x(waitSeconds += 2), () -> {
			fallingBlocks.forEach(Entity::remove);
			Nexus.unregisterListener(this);
			showAllPlayers();
			wither.get().remove();
			for (Player player : WitherChallenge.currentFight.alivePlayers()) {
				player.setGameMode(GameMode.SURVIVAL);
				player.teleport(WitherChallenge.location(-150.5, 69, -80.5));
			}
		});
		Tasks.wait(TickTime.SECOND.x(waitSeconds) + 2, () -> completableFuture.complete(cageLoc));
		return completableFuture;
	}

	public void hideAllPlayers() {
		for (Player player1 : WitherChallenge.currentFight.alivePlayers())
			for (Player player2 : WitherChallenge.currentFight.alivePlayers())
				if (!player1.equals(player2))
					player1.hidePlayer(Nexus.getInstance(), player2);
	}

	public void showAllPlayers() {
		for (Player player1 : WitherChallenge.currentFight.alivePlayers())
			for (Player player2 : WitherChallenge.currentFight.alivePlayers())
				if (!player1.equals(player2))
					player1.showPlayer(Nexus.getInstance(), player2);
	}

	public void strike(Location location) {
		location.getBlock().setType(Material.SOUL_FIRE);
		location.getWorld().strikeLightningEffect(location);
		for (Player player : WitherChallenge.currentFight.alivePlayers()) {
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
		if (!WitherChallenge.currentFight.isInParty(event.getPlayer()))
			return;

		event.setCancelled(true);
	}

}
