package gg.projecteden.nexus.features.events.mobevents.types;

import gg.projecteden.nexus.features.events.mobevents.MobEventUtils;
import gg.projecteden.nexus.features.events.mobevents.annotations.Type;
import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobEventType;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobOptions;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.models.difficulty.DifficultyUser.Difficulty;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.Arrays;

import static gg.projecteden.utils.StringUtils.right;

@Type(MobEventType.RISEN_HELL)
public class RisenHell extends IMobEvent implements Listener {

	public RisenHell() {
		super.initialize();

		this.name = "Risen Hell";
		this.ignoreLight = true;
		this.mobOptionsList = Arrays.asList(
			new MobOptions(EntityType.PIGLIN, 25, 15, 30),
			new MobOptions(EntityType.SKELETON, 15, 20, 30),
			new MobOptions(EntityType.MAGMA_CUBE, 15, 15, 30),
			new MobOptions(EntityType.WITHER_SKELETON, 10, 10, 30, Difficulty.HARD),
			new MobOptions(EntityType.HOGLIN, 5, 5, 30, Difficulty.HARD),
			new MobOptions(EntityType.GHAST, 15, 3, 30, Difficulty.HARD),
			new MobOptions(EntityType.PIGLIN_BRUTE, 10, 5, 30, Difficulty.EXPERT),
			new MobOptions(EntityType.BLAZE, 15, 10, 30, Difficulty.EXPERT)
		);
	}

	@Override
	protected boolean extraChecks(Player player, MobOptions mobOptions, DifficultyUser user) {
		return !MobEventUtils.failChance(user.getDifficulty(), 75, 5);
	}

	@Override
	protected Entity handleEntity(Entity entity, DifficultyUser user, MobOptions mobOptions) {
		if (entity instanceof Slime slime)
			MobEventUtils.slimeSize(slime, user.getDifficulty());

		if (entity instanceof PiglinAbstract piglinAbstract)
			piglinAbstract.setImmuneToZombification(true);

		if (entity instanceof Hoglin hoglin)
			hoglin.setImmuneToZombification(true);

		if (entity instanceof Ageable ageable) {
			boolean change = false;
			if (entity instanceof Hoglin)
				change = true;
			if (entity instanceof PiglinAbstract)
				change = true;

			if (change)
				ageable.setAdult();
		}

		return entity;
	}

	@Override
	public Location handleLocation(Location location, MobOptions mobOptions) {
		Block block = location.getBlock();
		if (!isIgnoreLight()) {
			if ((block.getLightFromSky() < 15) && block.getLightFromBlocks() > 7)
				return null;
		}

		if (mobOptions.getEntityType().equals(EntityType.GHAST)) {
			location = location.toHighestLocation().add(0, RandomUtils.randomInt(5, 15), 0);
			if (!MaterialTag.ALL_AIR.isTagged(location.getBlock()))
				return null;
		}

		return location;
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason().equals(SpawnReason.RAID))
			return;

		World world = event.getLocation().getWorld();
		if (!world.getEnvironment().equals(Environment.NETHER))
			return;

		if (!this.getModifier().applies(world))
			return;

		Entity entity = event.getEntity();
		if (!(entity instanceof Monster))
			return;

		final String bits = String.valueOf(entity.getUniqueId().getLeastSignificantBits());
		int least2 = Integer.parseInt(right(bits, 2));
		if (least2 >= 50)
			event.setCancelled(true);
	}
}
