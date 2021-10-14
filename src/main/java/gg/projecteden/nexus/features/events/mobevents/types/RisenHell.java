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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import java.util.Arrays;

@Type(MobEventType.RISEN_HELL)
public class RisenHell extends IMobEvent {

	public RisenHell() {
		this.name = "Risen Hell";
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
		if (entity instanceof Slime slime) {
			int minSize = 0;
			int maxSize = 2;
			switch (user.getDifficulty()) {
				case HARD:
					++minSize;
					++maxSize;
				case EXPERT:
					++minSize;
					++maxSize;
			}

			slime.setSize(RandomUtils.randomInt(minSize, maxSize));
		}

		if (entity instanceof PiglinAbstract piglinAbstract) {
			piglinAbstract.setImmuneToZombification(true);
		}

		if (entity instanceof Hoglin hoglin) {
			hoglin.setImmuneToZombification(true);
		}

		return entity;
	}

	@Override
	public Location handleLocation(Location location, MobOptions mobOptions) {
		if (mobOptions.getEntityType().equals(EntityType.GHAST)) {
			location = location.toHighestLocation().add(0, RandomUtils.randomInt(5, 15), 0);
			if (!MaterialTag.ALL_AIR.isTagged(location.getBlock()))
				return null;
		}

		return location;
	}
}
