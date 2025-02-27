package gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.framework.features.Feature;
import lombok.Getter;

@Environments(Env.TEST)
public class CustomBlockBreaking extends Feature {
	@Getter
	private static final BrokenBlocksManager manager = new BrokenBlocksManager();

	/*
		Logic mismatch between Vanilla and Custom Blocks due to IHarvestable#isUsingCorrectTool
		If looking at vanilla planks, can harvest is false, but looking at custom blocks, can harvest is true.
		/customblocks getBlockHardness
	 */
	@Override
	public void onStart() {
		//new BreakListener();
	}

}
