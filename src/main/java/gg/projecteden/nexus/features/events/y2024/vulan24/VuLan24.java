package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24Entity;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestTask;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.vulan24.VuLan24User;
import gg.projecteden.nexus.models.warps.WarpType;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@QuestConfig(
	tasks = VuLan24QuestTask.class,
	npcs = VuLan24NPC.class,
	entities = VuLan24Entity.class,
	items = VuLan24QuestItem.class,
	rewards = VuLan24QuestReward.class,
	start = @Date(m = 8, d = 1, y = 2024), // TODO If ready in time, change to 7/15
	end = @Date(m = 8, d = 31, y = 2024),
	world = "vu_lan",
	region = "vu_lan",
	warpType = WarpType.VULAN24
)
@NoArgsConstructor
@Disabled
public class VuLan24 extends EdenEvent {

	public static VuLan24 get() {
		return Features.get(VuLan24.class);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		final Player player = event.getPlayer();
		if (!shouldHandle(player))
			return;

		final Quest quest = VuLan24User.of(player).getQuest();
		if (quest == null)
			Quest.builder()
				.tasks(VuLan24QuestTask.MAIN)
				.assign(player)
				.start();
	}
}
