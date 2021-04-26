package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.Fishing;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class BearFair21FishingCommand extends CustomCommand {
	public BearFair21FishingCommand(CommandEvent event) {
		super(event);
	}

	@Path("test")
	void test() {
		giveItem(Fishing.getLoot());
	}

	@Path("stats category")
	void statsCategory() {
		for (FishingLootCategory category : FishingLootCategory.values()) {
			send(category.name() + " " + category.getWeight() + " | " + category.getChance() + "%");
		}
	}

	@Path("stats loot")
	void statsLoot() {
		for (FishingLoot loot : FishingLoot.values()) {
			send(loot.name() + " " + loot.getWeight() + " | " + loot.getChance() + "%");
		}
	}
}
