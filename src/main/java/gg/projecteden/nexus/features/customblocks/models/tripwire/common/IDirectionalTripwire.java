package gg.projecteden.nexus.features.customblocks.models.tripwire.common;


import gg.projecteden.nexus.features.customblocks.models.common.IDirectional;

public interface IDirectionalTripwire extends ICustomTripwire, IDirectional {

	default DirectionalConfig getDirectionalConfig() {
		return getClass().getAnnotation(DirectionalConfig.class);
	}

	default boolean isNorth_EW() {
		return getDirectionalConfig().north_EW();
	}

	default boolean isSouth_EW() {
		return getDirectionalConfig().south_EW();
	}

	default boolean isEast_EW() {
		return getDirectionalConfig().east_EW();
	}

	default boolean isWest_EW() {
		return getDirectionalConfig().west_EW();
	}

	default boolean isAttached_EW() {
		return getDirectionalConfig().attached_EW();
	}

	default boolean isDisarmed_EW() {
		return getDirectionalConfig().disarmed_EW();
	}

	default boolean isPowered_EW() {
		return getDirectionalConfig().powered_EW();
	}
}
