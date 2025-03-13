package gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import lombok.Getter;

@Environments(Env.TEST)
public class CustomBlockBreaking {

	// Maybe look into https://github.com/oraxen/oraxen/tree/master/core/src/main/java/io/th0rgal/oraxen/utils/breaker

	/*
		Logic mismatch between Vanilla and Custom Blocks due to IHarvestable#isUsingCorrectTool
		If looking at vanilla planks, can harvest is false, but looking at custom blocks, can harvest is true.
		/customblocks getBlockHardness
	 */

	@Getter
	private static final BrokenBlocksManager manager = new BrokenBlocksManager();

	public static void init() {
		new BreakListener();
	}


}
