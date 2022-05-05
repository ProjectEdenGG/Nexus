package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.utils.Env;

import java.util.List;

/*
	TODO:
		- placing vanilla blocks on custom blocks is funky, rotates the vanilla block
		- Tripwire implementation:
			- Sounds --> probably same treatment as note/wood blocks
				- place sound doesnt play
				- break sound plays, default sound
			- Directional placing
				- note blocks need the blockface of the block they are placed against (current & intended)
				- tripwire needs the facing of the player
			- Placing string needs to place TRIPWIRE and not TALL_SUPPORT
		- Sounds --> Do some more testing
			- PlayerAnimationEvent, only play hit sound when left clicking a block
				= Maybe keep track of the players last interaction type?
		- Vanished handling, proper interactions and what not
		- Future Conversions on chunk generate/load, itemstacks & blocks
		- Lots of testing
		- //
		- Appropriate tool & mining speed --> CustomBlockBreaking
		- //
		- Cannot Fix:
			- Custom blocks may flash when placing blocks near them (clientside only) --> Titan
			- Players arm will swing on interact w/ custom blocks (clientside only?) --> Titan
 */

@Environments(Env.TEST)
public class CustomBlocks extends Feature {
	@Override
	public void onStart() {
		new CustomBlocksListener();
	}

	public static void debug(String message) {
		List<Dev> devs = List.of(Dev.WAKKA, Dev.GRIFFIN);
		for (Dev dev : devs) {
			if (dev.isOnline())
				dev.send(message);
		}
	}
}
