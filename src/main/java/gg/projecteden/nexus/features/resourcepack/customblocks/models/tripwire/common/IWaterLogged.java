package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common;

public interface IWaterLogged extends ICustomTripwire {

	default WaterLoggedConfig getWaterLoggedConfig() {
		return getClass().getAnnotation(WaterLoggedConfig.class);
	}

	default boolean isWaterLoggedNorth_NS() {
		return getWaterLoggedConfig().north_NS();
	}

	default boolean isWaterLoggedSouth_NS() {
		return getWaterLoggedConfig().south_NS();
	}

	default boolean isWatterLoggedEast_NS() {
		return getWaterLoggedConfig().east_NS();
	}

	default boolean isWaterLoggedWest_NS() {
		return getWaterLoggedConfig().west_NS();
	}

	default boolean isWaterLoggedAttached_NS() {
		return getWaterLoggedConfig().attached_NS();
	}

	default boolean isWaterLoggedDisarmed_NS() {
		return getWaterLoggedConfig().disarmed_NS();
	}

	default boolean isWaterLoggedPowered_NS() {
		return getWaterLoggedConfig().powered_NS();
	}

	default boolean isWaterLoggedIgnorePowered() {
		return getWaterLoggedConfig().ignorePowered();
	}
}
