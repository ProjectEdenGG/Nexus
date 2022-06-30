package gg.projecteden.nexus.features.events.y2022.halloween22;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.y2022.halloween22.quests.Halloween22Entity;
import gg.projecteden.nexus.features.events.y2022.halloween22.quests.Halloween22NPC;
import gg.projecteden.nexus.features.events.y2022.halloween22.quests.Halloween22QuestItem;
import gg.projecteden.nexus.features.events.y2022.halloween22.quests.Halloween22QuestReward;
import gg.projecteden.nexus.features.events.y2022.halloween22.quests.Halloween22QuestTask;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.warps.WarpType;

@QuestConfig(
	tasks = Halloween22QuestTask.class,
	npcs = Halloween22NPC.class,
	entities = Halloween22Entity.class,
	items = Halloween22QuestItem.class,
	rewards = Halloween22QuestReward.class,
	start = @Date(m = 10, d = 15, y = 2022),
	end = @Date(m = 11, d = 10, y = 2022),
	world = "events",
	region = "halloween22",
	warpType = WarpType.HALLOWEEN22
)
public class Halloween22 extends EdenEvent {

	public static Halloween22 get() {
		return Features.get(Halloween22.class);
	}

}
