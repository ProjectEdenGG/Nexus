package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;

public interface ITall extends IRequireSupport {
	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}
}
