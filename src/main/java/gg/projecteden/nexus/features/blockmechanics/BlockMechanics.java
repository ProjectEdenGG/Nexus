package gg.projecteden.nexus.features.blockmechanics;

import gg.projecteden.nexus.features.blockmechanics.mechanics.BlockFireToggle;
import gg.projecteden.nexus.features.blockmechanics.mechanics.FurnaceExperience;
import gg.projecteden.nexus.features.blockmechanics.mechanics.JackOLanternToggle;
import gg.projecteden.nexus.framework.features.Feature;

public class BlockMechanics extends Feature {

	@Override
	public void onStart() {
		new JackOLanternToggle();
		new BlockFireToggle();
		new FurnaceExperience();
	}
}
