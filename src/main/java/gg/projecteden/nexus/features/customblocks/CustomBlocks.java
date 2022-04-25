package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.utils.Env;

import java.util.List;

/*
	TODO:
		- Tripwire implementation:
			- Sounds --> probably same treatment as note/wood blocks
				- place sound doesnt play
				- break sound plays, default sound
			- Directional placing
				- note blocks need the blockface of the block they are placed against (current & intended)
				- tripwire needs the facing of the player
			- Tripwire cross causes endless block updates, crashes server (maybe fixed?)
			- Block updates are not cancelling as intended
			- Observers are still detecting block update changes even tho the data is not actually changing
			- Creative pick block
			- Placing string needs to place CustomBlock Tripwire
			- Lots of testing
		- Appropriate tool & mining speed --> CustomBlockBreaking
		- Sounds --> Do some more testing
		- //
		- Known issues:
			- Custom blocks may flash when placing blocks near them (clientside only) --> Titan
			- Players arm will swing on interact w/ custom blocks --> Titan

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
