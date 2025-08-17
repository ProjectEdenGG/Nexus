package gg.projecteden.nexus.features.fakenpc;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.fakenpc.events.FakeNPCLeftClickEvent;
import gg.projecteden.nexus.features.fakenpc.events.FakeNPCRightClickEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC.Hologram;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC.Hologram.VisibilityType;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPCService;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUser;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUserService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

/*
	TODO:
		- if look close target is very close, dont change yaw
		- Hologram radius after introduction
		- SkinLayers
		- Different types & settings for each type
 */
@Disabled
@NoArgsConstructor
public class FakeNPCs extends Feature implements Listener {
	@Override
	public void onStart() {

		for (FakeNPC npc : new FakeNPCService().cacheAll()) {
			npc.createEntity();
		}

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
				boolean npcVisible = fakeNPC.isSpawned();
				Hologram hologram = fakeNPC.getHologram();
				if (hologram == null)
					break;

				boolean hologramVisible = hologram.isSpawned();
				VisibilityType visibilityType = hologram.getVisibilityType();

				for (FakeNPCUser user : users) {
					// NPC
					boolean isNear = FakeNPCUtils.isInSameWorld(user, fakeNPC); // integrate w/ clientside NPCs
					boolean playerCanSeeNPC = user.canSeeNPC(fakeNPC);

					if (npcVisible) {
						if (!playerCanSeeNPC && isNear)
							user.show(fakeNPC);
						else if (playerCanSeeNPC && !isNear)
							user.hide(fakeNPC);
					} else if (playerCanSeeNPC)
						user.hide(fakeNPC);

					// HOLOGRAM
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

		// Look Close TODO: Move to LookCloseTrait
		Tasks.repeat(0, TickTime.TICK, () -> {
			for (FakeNPCUser user : new FakeNPCUserService().getOnline()) {
				user.getVisibleNPCs().forEach(fakeNPC -> {
					if (!FakeNPCUtils.canLookClose(fakeNPC, user))
						return;

					FakeNPCPacketUtils.lookAt(fakeNPC, user.getOnlinePlayer());
				});
			}
		});
	}

	@EventHandler
	public void on(PlayerUseUnknownEntityEvent event) {
		if (!event.getHand().equals(EquipmentSlot.HAND))
			return;

		FakeNPC fakeNPC = new FakeNPCService().getCache().values().stream()
			.filter(_fakeNPC -> _fakeNPC.getEntity() != null && _fakeNPC.getBukkitEntity().getEntityId() == event.getEntityId())
			.findFirst()
			.orElse(null);

		if (fakeNPC == null)
			return;

		Player player = event.getPlayer();
		if (event.isAttack()) {
			if (CooldownService.isOnCooldown(player, "FakeNPC-LeftClickEvent", TickTime.TICK.x(2)))
				return;

			new FakeNPCLeftClickEvent(fakeNPC, player).callEvent();
		} else {
			if (CooldownService.isOnCooldown(player, "FakeNPC-RightClickEvent", TickTime.TICK.x(2)))
				return;

			new FakeNPCRightClickEvent(fakeNPC, player).callEvent();
		}
	}

	@EventHandler
	public void on(FakeNPCRightClickEvent event) {
		FakeNPCUserService userService = new FakeNPCUserService();
		FakeNPCUser user = userService.get(event.getClicker());
		FakeNPC npc = event.getNpc();
		if (user.hasInteracted(npc))
			return;

		user.getNpcSettings().get(npc.getUuid()).setInteracted(true);
		userService.save(user);
	}

}
