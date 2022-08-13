package gg.projecteden.nexus.features.survival.avontyre;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.bigdoors.BigDoorManager;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO Watermill bonk?
public class AvontyreEffects extends Effects {
	private final AmbientSounds ambientSounds = new AmbientSounds();

	@Override
	public World getWorld() {
		return Survival.getWorld();
	}

	@Override
	public void onStart() {
		watermill();
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
		List<ParticleBuilder> particles = new ArrayList<>() {{
			add(new ParticleBuilder(Particle.DRIPPING_DRIPSTONE_WATER).location(loc(90.12, 66.3, 118.5)));
			add(new ParticleBuilder(Particle.ENCHANTMENT_TABLE).location(loc(212.5, 65.5, 41.5)));
		}};

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

	private void watermill() {
		Tasks.repeat(0, 1, () -> {
			final Entity entity = Bukkit.getEntity(UUID.fromString("87c21044-2dbd-4b2c-82cf-1a1d0c18bb3d"));
			if (entity == null || !entity.isValid())
				return;

			if (!(entity instanceof ArmorStand watermill))
				return;

			watermill.setRightArmPose(watermill.getRightArmPose().add(0, -0.02, 0));
		});
	}

	@EventHandler
	public void onOpenBankDoor(PlayerInteractEvent event) {
		if (Survival.isNotAtSpawn(event.getPlayer()))
			return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		if (!(block.getBlockData() instanceof Door door))
			return;

		if (door.isOpen())
			return;

		ProtectedRegion region = Survival.worldguard().getRegionLike(Survival.getSpawnRegion() + "_bank_door");
		if (!Survival.worldguard().isInRegion(block.getLocation(), region))
			return;

		new SoundBuilder(Sound.BLOCK_BELL_USE).location(loc(138.5, 59, 64.5)).play();
	}

	@EventHandler
	public void onCryptDoorInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		Location location = block.getLocation();
		if (Survival.isNotAtSpawn(location))
			return;

		if (Survival.worldguard().getRegionsLikeAt("spawn_crypt_trigger", location).size() == 0)
			return;

		nl.pim16aap2.bigDoors.Door door = BigDoorManager.getDoor("spawn_crypt_secret");
		if (door == null)
			return;

		if (door.isOpen())
			return;

		// Light
		Region lightRg = Survival.worldguard().getRegion("spawn_crypt_light");
		Block lightBlock = Survival.worldguard().toLocation(lightRg.getMinimumPoint()).getBlock();
		if (!(lightBlock.getBlockData() instanceof Light light))
			return;

		light.setLevel(8);
		lightBlock.setBlockData(light);

		// Skull
		Region skullOnRg = Survival.worldguard().getRegion("spawn_crypt_skull_on");
		Region skullOffRg = Survival.worldguard().getRegion("spawn_crypt_skull_off");

		BlockState skullOnState = Survival.worldguard().toLocation(skullOnRg.getMinimumPoint()).getBlock().getState();
		BlockState skullOffState = Survival.worldguard().toLocation(skullOffRg.getMinimumPoint()).getBlock().getState();
		if (!(skullOnState instanceof Skull skullOn) || !(skullOffState instanceof Skull skullOff))
			return;

		if (skullOn.getPlayerProfile() == null || skullOff.getPlayerProfile() == null)
			return;

		block.setType(Material.PLAYER_HEAD);
		BlockState blockState = block.getState();
		if (!(blockState instanceof Skull skull))
			return;

		skull.setPlayerProfile(skullOn.getPlayerProfile());
		skull.update();

		Rotatable rotatable = (Rotatable) block.getBlockData();
		rotatable.setRotation(BlockFace.EAST);
		block.setBlockData(rotatable);

		// Door
		BigDoorManager.toggleDoor(door);

		// Reset
		Tasks.wait(TickTime.SECOND.x(6), () -> {
			light.setLevel(0);
			lightBlock.setBlockData(light);

			skull.setPlayerProfile(skullOff.getPlayerProfile());
			skull.update();

			block.setBlockData(rotatable);
		});
	}
}
