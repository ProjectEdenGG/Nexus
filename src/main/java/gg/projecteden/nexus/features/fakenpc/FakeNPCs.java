package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC.Hologram;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC.Hologram.VisibilityType;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPCService;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUser;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUserService;
import gg.projecteden.nexus.utils.Tasks;

import java.util.List;

/*
	TODO:
		- if look close target is very close, dont change yaw
		- Interaction Events
		- Hologram radius after introduction
		- SkinLayers
		- Cleanup Commands
		- Database + ID System + Selection System
		- Different types & settings for each type
 */

public class FakeNPCs extends Feature {

	@Override
	public void onStart() {
		for (FakeNPC npc : new FakeNPCService().cacheAll())
			npc.init();

		tasks();
	}

	@Override
	public void onStop() {
		for (FakeNPC npc : new FakeNPCService().getCache().values())
			npc.despawn();
	}

	private void tasks() {
		Tasks.repeat(0, TickTime.SECOND.x(1), () -> {
			final List<FakeNPCUser> users = new FakeNPCUserService().getOnline();
			for (FakeNPC fakeNPC : new FakeNPCService().getCache().values()) {
				for (FakeNPCUser user : users) {
					// NPC
					boolean npcVisible = fakeNPC.isSpawned();
					boolean isNear = FakeNPCUtils.isInSameWorld(user, fakeNPC);
					boolean playerCanSeeNPC = user.canSeeNPC(fakeNPC);

					if (npcVisible) {
						if (!playerCanSeeNPC && isNear)
							user.show(fakeNPC);
						else if (playerCanSeeNPC && !isNear)
							user.hide(fakeNPC);
					} else if (playerCanSeeNPC)
						user.hide(fakeNPC);

					// HOLOGRAM
					Hologram hologram = fakeNPC.getHologram();
					if (hologram == null)
						fakeNPC.createHologram();
					boolean hologramVisible = hologram.isSpawned();
					VisibilityType visibilityType = hologram.getVisibilityType();
					boolean typeApplies = visibilityType.applies(fakeNPC, user);
					boolean playerCanSeeHologram = user.canSeeHologram(fakeNPC);

					if (hologramVisible) {
						if (!playerCanSeeHologram && typeApplies)
							user.showHologram(fakeNPC);
						else if (playerCanSeeHologram && !typeApplies)
							user.hideHologram(fakeNPC);
					} else if (playerCanSeeHologram)
						user.hideHologram(fakeNPC);
				}
			}
		});

		// Look Close
		Tasks.repeat(0, TickTime.TICK, () -> {
			for (FakeNPCUser user : new FakeNPCUserService().getOnline()) {
				user.getVisibleNPCs().forEach(fakeNPC -> {
					if (!fakeNPC.isLookClose())
						return;

					if (!fakeNPC.canSee(user))
						return;

					FakeNPCPacketUtils.lookAt(fakeNPC, user.getOnlinePlayer());
				});
			}
		});
	}

}
