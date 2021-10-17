package gg.projecteden.nexus.features.events.mobevents.types;

import gg.projecteden.nexus.features.events.mobevents.MobEventUtils;
import gg.projecteden.nexus.features.events.mobevents.annotations.Type;
import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobEventType;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobOptions;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.models.difficulty.DifficultyUser.Difficulty;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Type(MobEventType.BLOOD_MOON)
public class BloodMoon extends IMobEvent {

	public BloodMoon() {
		super.initialize();

		this.name = "Blood Moon";
		this.mobOptionsList = Arrays.asList(
			new MobOptions(EntityType.ZOMBIE, 25, 50, 30),
			new MobOptions(EntityType.ZOMBIE_VILLAGER, 25, 10, 30),
			new MobOptions(EntityType.HUSK, 25, 25, 30),
			new MobOptions(EntityType.SKELETON, 15, 20, 30),
			new MobOptions(EntityType.STRAY, 15, 20, 30),
			new MobOptions(EntityType.SPIDER, 15, 10, 30),
			new MobOptions(EntityType.CREEPER, 10, 8, 30, Difficulty.HARD),
			new MobOptions(EntityType.PHANTOM, 10, 10, 30, Difficulty.HARD),
			new MobOptions(EntityType.WITCH, 10, 5, 30, Difficulty.EXPERT)
		);
	}

	@Override
	protected boolean extraChecks(Player player, MobOptions mobOptions, DifficultyUser user) {
		return !MobEventUtils.failChance(user.getDifficulty(), 75, 5);
	}

	@Override
	public Location handleLocation(Location location, MobOptions mobOptions) {
		if (mobOptions.getEntityType().equals(EntityType.PHANTOM)) {
			location = location.clone().add(0, 25, 0);
			if (!MaterialTag.ALL_AIR.isTagged(location.getBlock()))
				return null;
		}

		return location;
	}
}
