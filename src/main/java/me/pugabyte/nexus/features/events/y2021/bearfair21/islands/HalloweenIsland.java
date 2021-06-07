package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.HalloweenIsland.HalloweenNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

// TODO BF21: Quest + Dialog
@Region("halloween")
@NPCClass(HalloweenNPCs.class)
public class HalloweenIsland implements Listener, BearFair21Island {
	static BearFair21UserService userService = new BearFair21UserService();

	public enum HalloweenNPCs implements BearFair21TalkingNPC {
		JOSE(BearFair21NPC.JOSE) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				QuestStage questStage = user.getQuestStage_Halloween();
				if (questStage.isInProgress() && questStage != QuestStage.STEPS_DONE)
					questStage = QuestStage.STARTED;
				switch (questStage) {
					case NOT_STARTED, STARTED -> {
						script.add("Welcome <player> to our small village.");
						script.add("wait 40");
						script.add("Ah, I wish you would see a more happy side of the town but me madre recently left us.");
						script.add("wait 60");
						script.add("My son is having a birthday and he looked really forward towards &oAna&f's, his grandmother's homemade cookies.");
						script.add("wait 60");
						script.add("It won’t feel like a real birthday without them..");
						script.add("wait 60");
						script.add("Hmmm... I know you just came here. But, can you help us get the recipe so we can bake some?");
						script.add("wait 60");
						script.add("I heard &oSantiago&f, our village priest talk about someone visiting the underworld recently.");
						script.add("wait 60");
						script.add("You can find him at the church.");

						user.setQuestStage_Halloween(QuestStage.STARTED);
						userService.save(user);
						return script;
					}
					case STEPS_DONE -> {
						script.add("Aaah madres cookies!! I see you got them!");
						script.add("wait 40");
						script.add("These are so good. I can’t wait to eat them all!");
						script.add("wait 40");
						script.add("My son’s birthday party? Oh yes.. I will totally share these cookies with him.. Yes..");
						script.add("wait 60");
						script.add("Here, have this as a thank you for bringing me.. I mean us these cookies!");
						// TODO: Give reward
						script.add("wait 60");
						script.add("You’re always welcome here again, amigo!");

						user.setQuestStage_Halloween(QuestStage.COMPLETE);
						userService.save(user);
						return script;
					}
				}

				script.add("TODO - Greeting");
				return script;
			}
		},
		SANTIAGO(BearFair21NPC.SANTIAGO) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				switch (user.getQuestStage_Halloween()) {
					case STARTED, STEP_ONE -> {
						script.add("Welcome. What’s your name, child?");
						script.add("wait 40");
						script.add("Nice to meet you, <player>. How may I help you?");
						script.add("wait 40");
						script.add("Ah, I see. You need to visit &oAna&f. Yes, she left us very recently. So her spirit should still be somewhere nearby.");
						script.add("wait 80");
						script.add("If you want to see her, you need to follow her path to the underworld. Please, wish her well from me.");

						user.setQuestStage_Halloween(QuestStage.STEP_ONE);
						userService.save(user);
						return script;
					}
				}

				script.add("TODO - Greeting");
				return script;
			}
		},
		ANA(BearFair21NPC.ANA) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				switch (user.getQuestStage_Halloween()) {
					case STEP_ONE, STEP_TWO -> {
						script.add("Ohohoho. I haven’t felt this alive in so long. My body feels so light and young.");
						script.add("wait 40");
						script.add("Hmm? Oh hello little one.");
						script.add("wait 40");
						script.add("Ah, my son [José] sent you? Aaah, mm yes my cookies. He did love them a lot.");
						script.add("wait 40");
						script.add("The recipe? Jajaja, that recipe is my little secret. But you know what, if you get me the ingredients I will make some for you.");
						script.add("wait 80");
						script.add("I need a carton of milk, some chocolate and bag of flour. Look around in the houses down here!");

						user.setQuestStage_Halloween(QuestStage.STEP_TWO);
						userService.save(user);
						return script;
					}
					case STEP_FIVE -> {
						script.add("You found the ingredients. That’s good.");
						script.add("wait 40");
						script.add("Give me a moment and I will make some cookies.");
						script.add("wait 40");
						script.add("....");
						script.add("wait 40");
						script.add("...");
						script.add("wait 40");
						script.add("Aaaand, done. Here. Please bring these to my son, &oJosé&f.");
						// TODO: Give Cookies
						script.add("wait 40");
						script.add("Take care, young one!");

						user.setQuestStage_Halloween(QuestStage.STEPS_DONE);
						userService.save(user);
						return script;
					}
				}

				script.add("TODO - Greeting");
				return script;
			}
		},

		;

		private final BearFair21NPC npc;
		private final List<String> script;

		@Override
		public List<String> getScript(BearFair21User user) {
			return this.script;
		}

		@Override
		public String getName() {
			return this.npc.getName();
		}

		@Override
		public int getNpcId() {
			return this.npc.getId();
		}

		HalloweenNPCs(BearFair21NPC npc) {
			this.npc = npc;
			this.script = new ArrayList<>();
		}
	}


}
