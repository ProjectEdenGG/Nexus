package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.framework.features.Feature;

import java.util.UUID;

// TODO:
//  - Looking at players
//	- Setting skins of accounts
//	- Skin layers --> requires metadata packets --> https://wiki.vg/Entity_metadata#Player
//  - Interaction evets

public class FakeNPCs extends Feature {

	@Override
	public void onStart() {
		new FakeNPCManager();
	}

	@Override
	public void onStop() {
		for (UUID uuid : FakeNPCManager.getPlayerFakeNPCs().keySet()) {
			for (FakeNPC fakeNPC : FakeNPCManager.getPlayerFakeNPCs().get(uuid)) {
				FakeNPCPacketUtils.despawnFakeNPC(uuid, fakeNPC);
			}
		}
	}
}
