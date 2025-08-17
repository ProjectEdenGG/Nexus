package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2022.easter22.Easter22;
import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask.TaskBuilder;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.easter22.Easter22User;
import gg.projecteden.nexus.models.easter22.Easter22UserService;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BlockRegenJob;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Easter22QuestTask implements IQuestTask {
	MAIN(GatherQuestTask.builder()
		.talkTo(Easter22Entity.EASTER_BUNNY)
		.dialog(dialog -> dialog
			.npc("Well this is not good, not good at all")
			.player("Hi!")
			.npc("I mean it couldn't have just grown legs and walked away... or I suppose it could have?")
			.npc("nope that's just creepy, we are avoiding that train of thought-")
			.player("Um, excuse me?")
			.npc("It's not easy to lose, it's a paintbrush after all")
			.player("Hello? Can you not hear me or something?")
			.npc("It has to be around here somewhere, I just know it is.")
			.player("Okay then, I guess not")
			.player("Maybe if I find their paintbrush, they'll finally notice I'm trying to talk with them")
		)
		.objective("Find the Easter Bunny's paintbrush")
		.gather(Easter22QuestItem.EASTERS_PAINTBRUSH)
		.onClick(Easter22Entity.EASTERS_PAINTBRUSH, dialog -> dialog.giveIfMissing(Easter22QuestItem.EASTERS_PAINTBRUSH))
		.reminder(dialog -> dialog
			.player("They still don't seem to notice I'm here, I should really find that paintbrush")
		)
		.complete(dialog -> dialog
			.player("Hey, is this the paintbrush you're looking for?")
			.npc("Wait where did you find that?!")
			.npc("I was looking for it everywhere, like I literally thought I would never see it agai-  wait")
			.npc("How did you know I was looking for my paintbrush... Are you psychic or something?")
			.player("It wasn't that much of a secret, you were literally mumbling about it when I came over to introduce myself.")
			.npc("Omg I'm so sorry! I didn't even know, I must've been stuck in my own head again.")
			.player("Don't worry about it")
		)

		.then()
		.talkTo(Easter22Entity.EASTER_BUNNY)
		.dialog(dialog -> dialog
			.npc("Anyways, Hi I'm Easter! I'm assuming you are interested in the egg hunt?")
			.player("Definitely!")
			.npc("Awesome! I would warn you about the difficulty but it seems you have a talent when it comes to finding things...")
		)
		.objective("Find all 20 eggs")
		.check(quester -> Easter22User.of(quester).getFound().size() >= Easter22.TOTAL_EASTER_EGGS)
		.onEntityInteract(Easter22Entity.EASTER_EGG, event -> {
			event.setCancelled(true);
			Location location = event.getRightClicked().getLocation().toBlockLocation();
			new Easter22UserService().edit(event.getPlayer(), user -> user.found(location));
		})
		.reminder(dialog -> dialog
			.npc("I appreciate your enthusiasm but you haven't found them all yet! Come back when you've gotten all 20.")
		)
		.complete(dialog -> dialog
			.player("So, about that reward?")
			.npc("Wait, are you telling me you actually found them all?!")
			.npc("I knew you were good but managing to complete the egg hunt? That's something else!")
			.npc("Take this bunny ear headband, its a symbol of your new role as a junior holiday bunny!")
			.reward(Easter22QuestReward.BUNNY_EARS_COSTUME)
		)

		.then()
		.talkTo(Easter22Entity.EASTER_BUNNY)
		.dialog(dialog -> dialog
			.npc("Though now that I think about it... are you willing to do one more thing for me?")
			.player("Sure")
			.npc("Don't worry, I've got something extra for you if you manage to pull this off!")
			.npc("Do you by chance have a paintbrush and pristine egg on you?")
			.player("I do not unfortunately")
			.npc("Oh that's not good-  you're gonna need those")
			.npc("Bring me 3 oxeye daisies, 3 cornflowers, a stick, and an egg and ill be able to make them for you!")
			.player("You got it!")
		)
		.onClick(Easter22NPC.DAMIEN, dialog -> dialog
			// TODO How to only run once?
			.npc("Hi! Welcome to the bakery, what can I get you today?")
			.npc("I just put some carrot sugar cookies into the oven but other than that you'll find all the usual suspects.")
			.player("Oh that sounds really good- need to stay focused though")
			.player("I'm actually here to ask if you had any extra eggs I could have")
			.npc("Oh of course! Here you go!")
			.giveIfMissing(Material.EGG)
			.player("Thank you! How much do I owe you?")
			.npc("Nothing, It's on the house, I have tons more where that one came from.")
			.player("Thank you so much!")
			.npc("No problem, just make sure to come back and buy something sometime.")
		)
		.onBlockInteract(List.of(Material.OXEYE_DAISY, Material.CORNFLOWER), Action.LEFT_CLICK_BLOCK, (event, block) -> {
			new BlockRegenJob(block.getLocation(), block.getType()).schedule(gg.projecteden.nexus.utils.RandomUtils.randomInt(3 * 60, 6 * 60));
			block.breakNaturally();
		})
		.onBlockInteract(List.of(Material.OAK_LEAVES, Material.JUNGLE_LEAVES), Action.RIGHT_CLICK_BLOCK, (event, block) -> {
			final Player player = event.getPlayer();
			if (!PlayerUtils.playerHas(player, Material.STICK)) {
				final String coords = StringUtils.xyzFloored(block.getLocation()).replace(" ", "-");
				if (CooldownService.isOnCooldown(player, "easter22-stick-" + coords, TickTime.MINUTE))
					return;

				if (RandomUtils.chanceOf(20)) {
					PlayerUtils.giveItem(player, Material.STICK);
					new SoundBuilder(Sound.ITEM_BONE_MEAL_USE).receiver(player).play();
				} else {
					PlayerUtils.send(player, "&7Hmm... no sticks in this bush");
					new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(player).volume(.15).play();
				}
			}
		})
		.reminder(dialog -> dialog
			.npc("Do you have them yet? Remember you can pick the daisies and cornflowers around the village, get the stick from a bush, " +
				"and I'm sure Damien from the bakery has extra eggs.")
		)
		.gather(Map.of(Material.OXEYE_DAISY, 3, Material.CORNFLOWER, 3, Material.STICK, 1, Material.EGG, 1))
		.complete(dialog ->  dialog
			.npc("Perfect! Let me just weave these together with a bit of Easter holiday magic...")
		)

		.then()
		.talkTo(Easter22Entity.EASTER_BUNNY)
		.dialog(dialog -> dialog
			.npc("Done! Take this and paint that egg for me okay.")
			.give(Easter22QuestItem.PAINTBRUSH, Easter22QuestItem.PRISTINE_EGG)
			.player("Alright!")
		)
		.objective("Paint the egg")
		.reminder(dialog ->  dialog
			.npc("Hm you having trouble with inspiration? I promise whatever you do will be great, just combine " +
				"the paintbrush and the egg in a crafting station!")
		)
		.gather(Easter22QuestItem.PAINTED_EGG)
		.complete(dialog -> dialog
			.npc("Omg it's wonderful!!")
			.npc("That settles it, I knew there was something special about you!")
			.npc("Take this Easter Basket trophy, you've earned it!")
			.reward(Easter22QuestReward.EASTER_BASKET_TROPHY)
			.npc("You really have impressed me, you should be proud of yourself.")
			.npc("Oh also! When you have time you should definitely head over to the general store")
			.npc("Basil has some really cool holiday decor I think you may be interested in.")
			.player("Okay, thanks Easter!")
			.npc("I think it's I who should be thanking you after all you've done for me!")
			.npc("Enjoy the rest of your time here!")
		)
		.then()
		.talkTo(Easter22NPC.BASIL)
	),
	;

	private final TaskBuilder<?, ?, ?> task;

	@Override
	public TaskBuilder<?, ?, ?> builder() {
		return task;
	}
}
