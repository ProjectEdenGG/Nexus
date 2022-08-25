package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.framework.features.Feature;

/*
	TODO:
		- Interaction Events
		- Hologram Radius after interact
		- SkinLayers
		- Cleanup Commands
		- Database + ID System + Selection System
		- Different types & settings for each type
 */

public class FakeNPCs extends Feature {

	@Override
	public void onStart() {
		new FakeNPCManager();
	}

	@Override
	public void onStop() {
		for (FakeNPC fakeNpc : FakeNPCManager.getNPCList()) {
			fakeNpc.despawn();
		}
	}
}
