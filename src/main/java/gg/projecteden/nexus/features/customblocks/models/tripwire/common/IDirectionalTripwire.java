package gg.projecteden.nexus.features.customblocks.models.tripwire.common;


import gg.projecteden.nexus.features.customblocks.models.common.IDirectional;

public interface IDirectionalTripwire extends ICustomTripwire, IDirectional {

	default DirectionalConfig getDirectionalConfig() {
		return getClass().getAnnotation(DirectionalConfig.class);
	}


}
