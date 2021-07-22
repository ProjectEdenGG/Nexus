package gg.projecteden.nexus.features.events.aeveonproject.sets;

import gg.projecteden.nexus.features.events.annotations.Region;

import java.util.List;

public interface APSet {

	default String getRegion() {
		try {
			return getClass().getAnnotation(Region.class).value();
		} catch (Exception ignored) {
		}
		return null;
	}

	List<String> getUpdateRegions();

	boolean isActive();

	void setActive(boolean bool);
}
