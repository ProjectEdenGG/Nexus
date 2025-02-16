package gg.projecteden.nexus.features.events.y2021.bearfair21.commands;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.BearFair21Fishing;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.BearFair21FishingLoot;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.BearFair21FishingLoot.FishingLootCategory;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Disabled
@HideFromWiki
@Permission(Group.ADMIN)
public class BearFair21FishingCommand extends CustomCommand {

	public BearFair21FishingCommand(CommandEvent event) {
		super(event);
	}

	@Path("stats category")
	void statsCategory() {
		for (FishingLootCategory category : FishingLootCategory.values()) {
			send(category.name() + " " + category.getWeight() + " | " + category.getChance() + "%");
		}
	}

	@Path("stats loot")
	void statsLoot() {
		for (BearFair21FishingLoot loot : BearFair21FishingLoot.values()) {
			send(loot.name() + " " + loot.getWeight() + " | " + loot.getChance() + "%");
		}
	}

	@Path("simulate <lure> <minutes>")
	void simulateLoot(LureLevel lure, int minutes) {
		int maxSeconds = minutes * 60;
		List<BearFair21FishingLoot> fullCatch = new ArrayList<>();
		for (int seconds = 0; seconds < maxSeconds; seconds++) {
			seconds += lure.getWaitTime();
			fullCatch.add(BearFair21Fishing.getFishingLoot(player()));
		}

		send("FISH: " + getCategorySize(fullCatch, FishingLootCategory.FISH));
		send("JUNK: " + getCategorySize(fullCatch, FishingLootCategory.JUNK));
		send("UNIQUE: " + getCategorySize(fullCatch, FishingLootCategory.UNIQUE));
		send("TREASURE: " + getCategorySize(fullCatch, FishingLootCategory.TREASURE));
		send("==============");
		send("Total: " + fullCatch.size());
	}

	@Path("getLoot")
	void getLoot() {
		for (BearFair21FishingLoot loot : BearFair21FishingLoot.values())
			giveItem(loot.getItem());
	}

	@Getter
	@AllArgsConstructor
	private enum LureLevel {
		NONE(5, 30),
		ONE(5, 25),
		TWO(5, 20),
		THREE(5, 15);

		int min;
		int max;

		public int getWaitTime() {
			return RandomUtils.randomInt(min, max);
		}
	}

	private int getCategorySize(List<BearFair21FishingLoot> loot, FishingLootCategory category) {
		int count = 0;
		for (BearFair21FishingLoot fishingLoot : loot) {
			if (fishingLoot.getCategory().equals(category))
				count++;
		}
		return count;
	}
}
