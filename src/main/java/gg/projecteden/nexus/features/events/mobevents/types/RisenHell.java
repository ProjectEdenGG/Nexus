package gg.projecteden.nexus.features.events.mobevents.types;

import gg.projecteden.nexus.features.events.mobevents.MobEventUtils;
import gg.projecteden.nexus.features.events.mobevents.annotations.Type;
import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobEventType;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobOptions;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Type(MobEventType.RISEN_HELL)
public class RisenHell extends IMobEvent {

	public RisenHell() {
		this.name = "Risen Hell";
		this.mobOptionsList = Arrays.asList(
			new MobOptions(EntityType.ZOMBIFIED_PIGLIN, 25, 15, 30),
			new MobOptions(EntityType.SKELETON, 15, 20, 30),
			new MobOptions(EntityType.MAGMA_CUBE, 15, 15, 30),
			new MobOptions(EntityType.WITHER_SKELETON, 10, 10, 30),
			new MobOptions(EntityType.BLAZE, 10, 10, 30),
			new MobOptions(EntityType.GHAST, 5, 3, 30)
		);
	}

	@Override
	protected boolean extraChecks(Player player, MobOptions mobOptions, DifficultyUser user) {
		return !MobEventUtils.failChance(user.getDifficulty(), 50, 10);
	}

	@Override
	public Location handleLocation(Location location, MobOptions mobOptions) {
		if (mobOptions.getEntityType().equals(EntityType.GHAST)) {
			location = location.add(0, 50, 0);
			if (!MaterialTag.ALL_AIR.isTagged(location.getBlock()))
				return null;
		}

		return location;
	}
}
