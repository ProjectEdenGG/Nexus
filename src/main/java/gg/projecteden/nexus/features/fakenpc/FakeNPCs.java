package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.framework.features.Feature;

/*
	TODO:
		- Delete
		- Set Hologram
		- Interaction
		- LookClose
		- SkinLayers
		- Cleanup Commands
		- Database + ID System + Selection System
 */

public class FakeNPCs extends Feature {

	@Override
	public void onStart() {
		new FakeNPCManager();
	}

	@Override
	public void onStop() {
		for (FakeNPC fakeNpc : FakeNPCManager.getFakeNpcs()) {
			FakeNPCManager.despawn(fakeNpc);
		}
	}
}
