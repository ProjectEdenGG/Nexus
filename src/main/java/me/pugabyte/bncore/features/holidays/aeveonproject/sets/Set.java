package me.pugabyte.bncore.features.holidays.aeveonproject.sets;

import me.pugabyte.bncore.features.holidays.annotations.Region;

public interface Set {

	default String getRegion() {
		return getClass().getAnnotation(Region.class).value();
	}
}
