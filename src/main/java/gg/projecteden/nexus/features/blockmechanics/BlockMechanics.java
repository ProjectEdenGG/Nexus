package gg.projecteden.nexus.features.blockmechanics;

import gg.projecteden.nexus.features.blockmechanics.blocks.JackOLantern;
import gg.projecteden.nexus.features.blockmechanics.blocks.Netherrack;
import gg.projecteden.nexus.framework.features.Feature;

public class BlockMechanics extends Feature {

	@Override
	public void onStart() {
		new BlockMechanicsListener();

		// blocks
		new JackOLantern();
		new Netherrack();
	}
}
