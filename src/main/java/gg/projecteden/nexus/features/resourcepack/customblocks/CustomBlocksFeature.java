package gg.projecteden.nexus.features.resourcepack.customblocks;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.resourcepack.customblocks.breaking.Breaker;
import gg.projecteden.nexus.features.resourcepack.customblocks.listeners.CustomBlockListener;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.worldedit.WrappedWorldEdit;
import gg.projecteden.nexus.framework.features.Feature;

/*
	TODO:
		- Resolve Test server bug signs
		- Remove "TODO CUSTOM BLOCKS: REMOVE"
		- Finish FloweringMossBlock
		- On place block in region, if denied, there should be an error
 */

/*
	TODO POST RELEASE:
		- Tripwire implementation:
			- Tripwire blocks are being replaced to cross, if you're standing inside of them when you break them
			- tripwire cross is spawnable, and also spawns paper ?
			- Breaking tripwire needs properly update nearby tripwire crosses to tripwire if suitable, and fix database issue
			- SendBlockChange
			-
			- Misc:
				- Add lotus lilly flower & how to obtain
				- flower + fungus cover -> how to obtain --> maybe make bonemeal spawn it?
				- Make fungus cover 3d?
		- Maybe add more advanced worldedit handling, such as setting directionals, and other "block states"
 */

@Environments(Env.TEST) // TODO CUSTOM BLOCKS: REMOVE
public class CustomBlocksFeature extends Feature {
	@Override
	public void onStart() {
		CustomBlock.init();
		new CustomBlockListener();

		Breaker.init();

		WrappedWorldEdit.init();
		WrappedWorldEdit.registerParser();
	}

	public enum BlockAction {
		UNKNOWN,
		INTERACT,
		HIT,
		BREAK,
		PLACE,
		FALL,
	}
}
