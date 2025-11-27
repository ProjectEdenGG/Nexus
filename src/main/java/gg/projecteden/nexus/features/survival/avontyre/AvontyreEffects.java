package gg.projecteden.nexus.features.survival.avontyre;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.bigdoors.BigDoorManager;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationAxis;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationType;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Light;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

// TODO Watermill bonk?
public class AvontyreEffects extends Effects {
	private final AmbientSounds ambientSounds = new AmbientSounds();

	@Override
	public World getWorld() {
		return Survival.getWorld();
	}

	@Override
	public void onStop() {
		super.onStop();
		ambientSounds.onStop();
	}

	@Override
	public void sounds() {
		ambientSounds.onStart();

		// Watermill
		Location watermill = location(206, 66, 148);
		SoundBuilder watermillSound = new SoundBuilder(CustomSound.AMBIENT_WATERMILL).category(SoundCategory.AMBIENT).location(watermill).volume(1.25);
		Tasks.repeat(0, TickTime.TICK.x(46), watermillSound::play);

		SoundBuilder waterSound = new SoundBuilder(Sound.BLOCK_WATER_AMBIENT).category(SoundCategory.AMBIENT).location(watermill).volume(1.5);
		Tasks.repeat(0, TickTime.TICK.x(32), waterSound::play);

		// Millstone
		Location millstone = location(204, 72, 138);
		SoundBuilder millstoneSound = new SoundBuilder(CustomSound.AMBIENT_MILLSTONE).category(SoundCategory.AMBIENT).location(millstone).volume(0.75);
		Tasks.repeat(0, TickTime.TICK.x(72), millstoneSound::play);
	}

	@Override
	public void particles() {
		List<ParticleBuilder> particles = new ArrayList<>() {{
			add(new ParticleBuilder(Particle.DRIPPING_DRIPSTONE_WATER).location(location(90.12, 66.3, 118.5)));
			add(new ParticleBuilder(Particle.ENCHANT).location(location(212.5, 65.5, 41.5)));
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

	@Override
	public List<RotatingStand> getRotatingStands() {
		return new ArrayList<>() {{
			add(new RotatingStand("87c21044-2dbd-4b2c-82cf-1a1d0c18bb3d", StandRotationAxis.HORIZONTAL, StandRotationType.NEGATIVE, true));  // Water Mill
			add(new RotatingStand("ac5b56bb-4d69-4c4e-92b1-07cdd4863ebd", StandRotationAxis.HORIZONTAL, StandRotationType.NEGATIVE, true)); // Log 1
			add(new RotatingStand("b33e4ac3-f4ce-4ca7-8c28-04f2baeec6ac", StandRotationAxis.HORIZONTAL, StandRotationType.NEGATIVE, true)); // Gear 1

			add(new RotatingStand("4ffede7e-73f6-4a7b-9f95-fb0fc5b9a559", StandRotationAxis.VERTICAL, StandRotationType.POSITIVE, true)); // Gear 2
			add(new RotatingStand("af314dc3-1e8b-42a7-96b0-7e70d27fb64c", StandRotationAxis.VERTICAL, StandRotationType.POSITIVE, true)); // Log 2
			add(new RotatingStand("d2da009a-958e-4e57-b0e8-83cc7f04dfda", StandRotationAxis.VERTICAL, StandRotationType.POSITIVE, true)); // Millstone
			add(new RotatingStand("bbba5656-ec4d-4626-9b01-f80f3e623219", StandRotationAxis.VERTICAL, StandRotationType.POSITIVE, true)); // Star projector
		}};
	}

	//

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

		new SoundBuilder(Sound.BLOCK_BELL_USE).location(location(138.5, 59, 64.5)).play();
	}

	@EventHandler
	public void onCryptDoorInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		Location location = block.getLocation();
		if (Survival.isNotAtSpawn(location))
			return;

		if (Survival.worldguard().getRegionsLikeAt("spawn_crypt_trigger", location).isEmpty())
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
		new SoundBuilder(Sound.BLOCK_STONE_BUTTON_CLICK_ON).location(block).volume(0.5).pitch(0.6).play();

		// Door
		BigDoorManager.toggleDoor(door);
		Tasks.wait(10, () -> new SoundBuilder(CustomSound.STONE_DOOR).location(door.getEngine()).volume(0.5).pitch(1.75).play());

		// Reset
		Tasks.wait(TickTime.SECOND.x(6), () -> {
			light.setLevel(0);
			lightBlock.setBlockData(light);

			skull.setPlayerProfile(skullOff.getPlayerProfile());
			skull.update();
			block.setBlockData(rotatable);
			new SoundBuilder(Sound.BLOCK_STONE_BUTTON_CLICK_ON).location(block).volume(0.5).pitch(0.5).play();

			Tasks.wait(10, () -> new SoundBuilder(CustomSound.STONE_DOOR).location(door.getEngine()).volume(0.5).pitch(1.75).play());
		});
	}
}
