package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Utils;

@Depends(Nameplates.class)
public class Listeners extends Feature {

	@Override
	public void onStart() {
		Utils.registerListeners(getClass().getPackageName());
	}

}
