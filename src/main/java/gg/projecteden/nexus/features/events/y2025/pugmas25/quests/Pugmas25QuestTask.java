package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Cabin;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Intro;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waypoints;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waypoints.WaypointTarget;
import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask;
import gg.projecteden.nexus.features.quests.tasks.InteractQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask.TaskBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationInteractData;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestTask implements IQuestTask {
	// TODO: TEST
	INTRO(InteractQuestTask.builder()
		.talkTo(Pugmas25NPC.TICKET_MASTER_HUB)
		.dialog(dialog -> dialog
			.npc("Hello! Where would you like to travel to?")
			.player("1 Ticket to " + Pugmas25.EVENT_NAME + ", please.")
			.npc("Oh, it's wonderful there this time of year. Here you go.")
			.give(Pugmas25QuestItem.TRAIN_TICKET)
		)
		.objective("Board the train")
		.reminder(dialog -> dialog
			.npc("You already bought a ticket, make sure to board the train!")
		)
		.then()
		.talkTo(Pugmas25NPC.TICKET_MASTER)
		.dialog(dialog -> dialog
			.npc("Welcome to Pugmas Village, Project Eden's Christmas celebration!")
			.npc("There's plenty to see and do around here, but before you dive in, you'll want to check in at the inn.")
			.npc("Just follow the signs along the north western path and you'll find it in no time.")
			.npc("Enjoy your stay!")
			.thenRun(quester -> Pugmas25Waypoints.showWaypoint(quester.getOnlinePlayer(), WaypointTarget.INN, ColorType.LIGHT_RED))
		)
		.objective("Check in at the inn")
		.reminder(dialog -> dialog
			.npc("Have you checked into the inn yet?")
		)
		.then()
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
		.objective("Find and enter the cabin")
		.reminder(dialog -> dialog
			.npc("Have you found the cabin yet?")
			.npc("It's directly west of the Great Tree, you can't miss it.")
			.thenRun(quester -> Pugmas25Waypoints.showWaypoint(quester.getOnlinePlayer(), WaypointTarget.CABIN, ColorType.LIGHT_RED))
		)
		.then()
		.onRegionEntering(Pugmas25Cabin.DOOR_REGION, event -> {
			Pugmas25UserService userService = new Pugmas25UserService();
			Pugmas25User user = userService.get(event.getPlayer());

			if (!user.isUnlockedCabin()) {
				event.setCancelled(true);
				if (!CooldownService.isOnCooldown(event.getPlayer().getUniqueId(), "pugmas25_cabin_locked", TickTime.SECOND.x(2), false))
					user.sendMessage(Pugmas25.PREFIX + "&cYou cannot enter this cabin right now");
			} else {
				if (!user.isEnteredCabin()) {
					user.setEnteredCabin(true);
					userService.save(user);
				}
			}

			// TODO: MARK QUEST AS COMPLETE
			//completeQuest(new QuesterService().get(user), Pugmas25Quest.INTRO); // untested
		})
	),

	// TODO: TEST
	ADVENT(InteractQuestTask.builder()
		.talkTo(Pugmas25NPC.ELF)
		.dialog(dialog -> dialog
			.npc("Ah! A new visitor beneath the Great Tree! Welcome to Pugmas, traveler!")
			.player("This place is huge... what's going on here?")
			.npc("Pugmas brings many wonders: the village, fairgrounds, warm springs, hidden caves, and more! But you're here for the Advent, aren't you?")
			.player("Advent? How does that work?")
			.npc("Each day until the 25th, a magical present unlocks. Find it, and you may unwrap its holiday magic!")
			.player("What if I come across a present from another day?")
			.npc("You may find any day early, but its magic won’t unlock until its rightful day arrives. The enchantments are very particular.")
			.player("And if I miss a day? Life happens.")
			.npc("The Great Tree is kind! On the 25th, every unopened present unlocks at once. A perfect chance to catch up.")
			.player("What about the final present on the 25th?")
			.npc("That one is special. You must open all the earlier days first—only then will the Great Tree reveal the final gift.")
			.player("Sounds exciting! Where do I start?")
			.npc("Your first present is already hiding somewhere out there. Go on, let the hunt begin, and may the Great Tree guide your steps!")
			.thenRun(quester -> new Pugmas25UserService().edit(quester, user -> user.advent().setUnlockedQuest(true)))
		).reminder(dialog -> dialog
			.npc("Back again? Remember, one Advent Present unlocks each day until the 25th.")
			.npc("You can find future presents early, but you can’t open them until their day.")
			.npc("Miss a day? All unopened ones unlock on the 25th, just open them to unlock the final present.")
		)
		.objective("Find and open all advent presents")
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

	NUTCRACKER(InteractQuestTask.builder()
		.onBlockInteract(Material.BARRIER, Action.RIGHT_CLICK_BLOCK, ((event, block) -> {
			var data = new DecorationInteractData(block, BlockFace.UP);
			if (ItemModelType.NUTCRACKER_SHORT.is(data.getDecoration().getItem(event.getPlayer()))) {
				Pugmas25ConfigService configService = new Pugmas25ConfigService();
				Pugmas25Config config = configService.get0();
				if (config.getNutCrackerLocations().contains(block.getLocation()))
					new Pugmas25UserService().edit(event.getPlayer(), user -> user.getFoundNutCrackers().add(block.getLocation()));
			}
		}))),


	;

	private final TaskBuilder<?, ?, ?> task;

	@Override
	public TaskBuilder<?, ?, ?> builder() {
		return task;
	}


	public static void completeQuest(Quester quester, Pugmas25Quest pugmasQuest) {
		quester.getQuests().forEach(quest -> {
			if (quest.getQuest() == pugmasQuest) {
				quester.sendMessage("Completing quest");
				quest.complete();
			}
		});
	}
}
