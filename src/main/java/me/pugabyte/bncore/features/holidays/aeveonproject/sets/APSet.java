package me.pugabyte.bncore.features.holidays.aeveonproject.sets;

import me.pugabyte.bncore.features.holidays.annotations.Region;

import java.util.List;

public interface APSet {

	default String getRegion() {
		return getClass().getAnnotation(Region.class).value();
	}

	List<String> getUpdateRegions();
}
