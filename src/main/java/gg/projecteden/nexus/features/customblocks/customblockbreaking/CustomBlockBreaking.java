package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.annotations.Disabled;
import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.utils.Env;
import lombok.Getter;

/*
	Doesn't affect anything that insta-breaks, like flowers/saplings/etc
 */
@Disabled
@Environments(Env.TEST)
public class CustomBlockBreaking extends Feature {
	@Getter
	private static final BrokenBlocksManager manager = new BrokenBlocksManager();


	@Override
	public void onStart() {
		new BreakListener();
	}


}
