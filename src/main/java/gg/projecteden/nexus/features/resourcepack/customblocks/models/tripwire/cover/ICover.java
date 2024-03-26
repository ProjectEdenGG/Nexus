package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.cover;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;

public interface ICover extends IRequireSupport {
	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}
}
