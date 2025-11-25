package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Cabin;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Intro;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waypoints;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waypoints.WaypointTarget;
import gg.projecteden.nexus.features.hub.Hub;
import gg.projecteden.nexus.features.quests.tasks.EnteringRegionQuestTask;
import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask;
import gg.projecteden.nexus.features.quests.tasks.InteractQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask.TaskBuilder;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestTask implements IQuestTask {
	BOARD_THE_TRAIN(EnteringRegionQuestTask.builder()
		.objective("Board the train")
		.talkTo(Pugmas25NPC.TICKET_MASTER_HUB)
		.dialog(dialog -> dialog
			.npc("Hello! Where would you like to travel to?")
			.player("1 Ticket to " + Pugmas25.EVENT_NAME + ", please.")
			.npc("Oh, it's wonderful there this time of year. Here you go.")
			.give(Pugmas25QuestItem.TRAIN_TICKET)
		)
		.reminder(dialog -> dialog
			.npc("You already bought a ticket, make sure to board the train!")
		)
		.enterRegion(Hub.getWorld(), Pugmas25Intro.TRANSITION_REGION_REGEX)
	),

	CHECK_IN(InteractQuestTask.builder()
		.objective("Talk to the ticket master")
		.talkTo(Pugmas25NPC.TICKET_MASTER)
		.dialog(dialog -> dialog
			.npc("Welcome to Pugmas Village, Project Eden's Christmas celebration!")
			.npc("There's plenty to see and do around here, but before you dive in, you'll want to check in at the inn.")
			.npc("Just follow the signs along the north western path and you'll find it in no time.")
			.npc("Here, this should help you find your way.")
			.give(Pugmas25QuestItem.COMPASS)
			.npc("Enjoy your stay!")
			.thenRun(quester -> Pugmas25Waypoints.showWaypoint(quester.getOnlinePlayer(), WaypointTarget.INN, ColorType.LIGHT_RED))
		)
		.reminder(dialog -> dialog
			.npc("Have you checked into the inn yet?")
		)
		.then()
		.objective("Check in at the inn")
		.talkTo(Pugmas25NPC.INN_KEEPER)
		.dialog(dialog -> dialog
			.player("Hello, I'd like to rent a room.")
			.npc("Greetings! Ah, I'm sorry, but our last room was just reserved.")
			.player("Oh... I just arrived in town, and I don't really have anywhere else to go. I was hoping to warm up and rest a bit.")
			.npc("You know what... I can get you a room, just not here.")
			.npc("My grandpa owns a cabin in town, not too far from here, but he's away for the season.")
			.npc("You could stay there if you'd like?")
			.player("That's incredibly kind of you... are you sure? I wouldn't want to impose.")
			.npc("Not at all! Really, someone should be keeping an eye on it anyway. It's been locked up for weeks and is starting to look a little abandoned.")
			.player("Well... if you're certain, then I'd be grateful to take you up on that offer.")
			.npc("Wonderful! Here, take this key. The cabin is directly west of the Great Tree, you can't miss it.")
			.thenRun(quester -> {
				new Pugmas25UserService().edit(quester, user -> user.setUnlockedCabin(true));
				quester.sendMessage("&7&o[You can now enter the cabin]");
				Pugmas25Waypoints.showWaypoint(quester.getOnlinePlayer(), WaypointTarget.CABIN, ColorType.LIGHT_RED);
			})
		)
		.reminder(dialog -> dialog
			.npc("Have you found the cabin yet?")
			.npc("It's directly west of the Great Tree, you can't miss it.")
			.thenRun(quester -> Pugmas25Waypoints.showWaypoint(quester.getOnlinePlayer(), WaypointTarget.CABIN, ColorType.LIGHT_RED))
		)
	),

	ENTER_THE_CABIN(EnteringRegionQuestTask.builder()
		.objective("Find and enter the cabin")
		.enterRegion(Pugmas25.get().getWorld(), Pugmas25Cabin.DOOR_REGION)
	),

	// TODO: TEST
	SNOWMEN(GatherQuestTask.builder()
		.talkTo(Pugmas25NPC.KID)
		.dialog(dialog -> dialog
			.npc("Hi! Um... could you help me with something?")
			.player("Sure, what's going on?")
			.npc("My mom told me to decorate all the snowmen with a top hat and a monocle.")
			.npc("But I'm too short to reach their heads, and the snow is too slippery to climb.")
			.player("So you want me to help you out?")
			.npc("Yes! Could you put the hats and monocles on all the snowmen for me?")
			.npc("If you do, I'll give you something cool! Promise!")
			.give(Pugmas25QuestItem.BOX_OF_DECORATIONS)
		)
		.reminder(dialog -> dialog
			.npc("Did you decorate the snowmen yet?")
			.npc("They all need a top hat and a monocle, just like my mom said.")
			.npc("I can't reach their heads... so I'm counting on you!")
		)
		.onClick(Pugmas25NPC.KID, dialog -> dialog.giveIfMissing(Pugmas25QuestItem.BOX_OF_DECORATIONS))
		.gather(Pugmas25QuestItem.BOX_OF_DECORATIONS_EMPTY)
		.complete(dialog -> dialog
			.npc("You did it! All the snowmen look amazing!")
			.player("Happy to help.")
			.npc("Thank you so much! Here's your reward, just like I promised!")
			.npc("My mom's gonna be so proud!")
			.take(Pugmas25QuestItem.BOX_OF_DECORATIONS_EMPTY.get())
		)
		.reward(Pugmas25QuestReward.SNOWMEN)
	),


	;

	private final TaskBuilder<?, ?, ?> task;

	@Override
	public TaskBuilder<?, ?, ?> builder() {
		return task;
	}
}
