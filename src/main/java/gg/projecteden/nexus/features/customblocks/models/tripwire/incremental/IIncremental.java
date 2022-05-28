package gg.projecteden.nexus.features.customblocks.models.tripwire.incremental;

import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICustomTripwire;

import java.util.List;

public interface IIncremental extends ICustomTripwire {

	List<Integer> getModelIdList();

}
