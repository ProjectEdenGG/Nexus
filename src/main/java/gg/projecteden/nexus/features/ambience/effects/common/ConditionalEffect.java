package gg.projecteden.nexus.features.ambience.effects.common;

import gg.projecteden.nexus.features.ambience.Wind;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.nexus.models.ambience.Variables.TimeQuadrant;
import gg.projecteden.nexus.utils.BiomeTag;
import org.bukkit.World.Environment;

public interface ConditionalEffect {

	default boolean isDimension(AmbienceUser user, Environment environment) {
		return user.getVariables().getDimension().equals(environment);
	}

	default boolean isBiome(AmbienceUser user, BiomeTag... biomeTags) {
		for (BiomeTag biomeTag : biomeTags) {
			if (biomeTag.isTagged(user.getVariables().getBiome()))
				return true;
		}

		return false;
	}

	default boolean isUnderground(AmbienceUser user) {
		return !user.getVariables().isExposed();
	}

	default boolean isSubmerged(AmbienceUser user) {
		return user.getVariables().isSubmerged();
	}

	default boolean isRaining(AmbienceUser user) {
		return user.getVariables().isRaining();
	}

	default boolean isThundering(AmbienceUser user) {
		return user.getVariables().isThundering();
	}

	default boolean isStorming(AmbienceUser user) {
		return isRaining(user) || isThundering(user);
	}

	default boolean isTimeQuadrant(AmbienceUser user, TimeQuadrant quadrant) {
		TimeQuadrant timeQuadrant = user.getVariables().getTimeQuadrant();
		if (timeQuadrant == null)
			return false;

		return timeQuadrant.equals(quadrant);
	}

	default boolean isWindBlowing() {
		return Wind.isBlowing();
	}
}
