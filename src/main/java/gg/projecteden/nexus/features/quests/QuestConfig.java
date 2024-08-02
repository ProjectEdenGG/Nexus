package gg.projecteden.nexus.features.quests;

import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.models.warps.WarpType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QuestConfig {

	Class<? extends Enum<? extends IQuest>> quests();
	Class<? extends Enum<? extends IQuestTask>> tasks();
	Class<? extends Enum<? extends InteractableNPC>> npcs();
	Class<? extends Enum<? extends InteractableEntity>> entities();
	Class<? extends Enum<? extends QuestItem>> items();
	Class<? extends Enum<? extends QuestReward>> rewards();

	Class<? extends Effects> effects();

	Date start();
	Date end();

	String world();
	String region();

	WarpType warpType();

}
