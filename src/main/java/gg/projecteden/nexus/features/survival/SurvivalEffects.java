package gg.projecteden.nexus.features.survival;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.bigdoors.BigDoorManager;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.models.bigdoor.BigDoorConfig.DoorAction;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SurvivalEffects extends Effects {
	public static final String baseRegion = "spawn";

	public static WorldGuardUtils worldguard = null;
	AmbientSounds ambientSounds = new AmbientSounds();

	@Override
	public void onStart() {
		super.onStart();

		worldguard = new WorldGuardUtils(getWorld());
	}

	@Override
	public void onStop() {
		super.onStop();

		ambientSounds.onStop();
	}

	@Override
	public void sounds() {
		super.sounds();

		ambientSounds.onStart();
	}

	@Override
	public void particles() {
		List<ParticleBuilder> particles = List.of(
			new ParticleBuilder(Particle.DRIPPING_DRIPSTONE_WATER)
				.location(loc(90.12, 66.3, 118.5))
		);

		Tasks.repeat(0, 2, () -> {
			for (ParticleBuilder particleBuilder : particles) {
				final Location location = particleBuilder.location();
				if (location == null || !location.isChunkLoaded())
					continue;

				if (RandomUtils.chanceOf(75))
					if (hasPlayersNearby(location, 25))
						particleBuilder.spawn();
			}
		});
	}

	@Override
	public World getWorld() {
		return Bukkit.getWorld("survival");
	}

	@EventHandler
	public void onOpenBankDoor(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		if (!(block.getBlockData() instanceof Door door))
			return;

		if (door.isOpen())
			return;

		ProtectedRegion region = worldguard.getRegionLike(baseRegion + "_bank_door");
		if (!worldguard.isInRegion(block.getLocation(), region))
			return;

		new SoundBuilder(Sound.BLOCK_BELL_USE).location(loc(138.5, 59, 64.5)).play();
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		BigDoorManager.tryToggleDoor(event.getRegion(), event.getPlayer(), baseRegion, DoorAction.OPEN);
	}

	@EventHandler
	public void on(PlayerLeftRegionEvent event) {
		BigDoorManager.tryToggleDoor(event.getRegion(), event.getPlayer(), baseRegion, DoorAction.CLOSE);
	}
}
