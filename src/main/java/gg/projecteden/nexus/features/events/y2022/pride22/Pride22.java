package gg.projecteden.nexus.features.events.y2022.pride22;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.y2022.pride22.quests.Pride22Entity;
import gg.projecteden.nexus.features.events.y2022.pride22.quests.Pride22NPC;
import gg.projecteden.nexus.features.events.y2022.pride22.quests.Pride22Quest;
import gg.projecteden.nexus.features.events.y2022.pride22.quests.Pride22QuestItem;
import gg.projecteden.nexus.features.events.y2022.pride22.quests.Pride22QuestReward;
import gg.projecteden.nexus.features.events.y2022.pride22.quests.Pride22QuestTask;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.warps.WarpType;

@QuestConfig(
	quests = Pride22Quest.class,
	tasks = Pride22QuestTask.class,
	npcs = Pride22NPC.class,
	entities = Pride22Entity.class,
	items = Pride22QuestItem.class,
	rewards = Pride22QuestReward.class,
	start = @Date(m = 6, d = 1, y = 2022),
	end = @Date(m = 6, d = 30, y = 2022),
	world = "events",
	region = "pride22",
	warpType = WarpType.PRIDE22
)
public class Pride22 extends EdenEvent {

	public static Pride22 get() {
		return Features.get(Pride22.class);
	}

}
