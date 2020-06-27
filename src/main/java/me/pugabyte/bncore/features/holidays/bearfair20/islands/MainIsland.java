package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import lombok.Getter;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.NPCClass;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.Region;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.MainIsland.MainNPCs;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Region("main")
@NPCClass(MainNPCs.class)
public class MainIsland implements Listener, Island {

	public enum MainNPCs implements TalkingNPC {
		WakkaFlocka(2962) {
			@Override
			public List<String> getScript(Player player) {
				List<String> script = new ArrayList<>();
				script.add("Welcome to Bear Fair, Bear Nation's anniversary event!");
				script.add("wait 80");
				script.add("This year features several islands to explore, find easter eggs, and do quests!");
				script.add("wait 80");
				script.add("At the carnival, you can play daily minigames in which you can play to gain Bear Fair Points");
				script.add("wait 80");
				script.add("And at the end of Bear Fair, you can buy unique prizes and perks using those points.");
				script.add("wait 80");
				script.add("To get started with the quests, you must find the evil wicked Witch. Last I heard, she was brewing up something crooked in the forest.");
				script.add("wait 120");
				script.add("And if you need help figuring out where you are, check out this map to my side.");
				return script;
			}
		},
		Witch(2670) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_Pugmas_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Welcome, welcome <player>, the roots of the island informed me of your soon arrival.");
				startQuest.add("wait 80");
				startQuest.add("Wait, shhh, ... yes... no... ok... got it... ok!");
				startQuest.add("wait 120");
				startQuest.add("The roots have spoken. You are here to deliever my pizza.");
				startQuest.add("wait 80");
				startQuest.add("<self> What? Uhh, no??");
				startQuest.add("wait 60");
				startQuest.add("No? Oh, I must've misheard then. Hmmm...");
				startQuest.add("wait 80");
				startQuest.add("Anyways, have you heard!? The fair is in town! I love winning all the little prizes and stuffed animals.");
				startQuest.add("wait 120");
				startQuest.add("Speaking of, I know a recipe that when crafted, summons the most special prize.");
				startQuest.add("wait 80");
				startQuest.add("Would you like to help me craft it?");

				//
				String mainIslandHint = "- A Honey Stroopwafel, I believe the Pastry Chef at the carnival can help you for more info on that.";
				String mgnIslandHint = "- An Arcade Token... Huh? Ax? ... Alex? ... Oh, ok. The roots tell me a boy named \"Axel\" is the one you should look for";
				String pugmasIslandHint = "- A Present, <TODO: starting point>";
				String halloweenIslandHint = "- A Halloween Candy Basket, <TODO: starting point>";
				String sduIslandHint = "- A Anzac Biscuit, <TODO: starting point>";

				List<String> reminderAll = new ArrayList<>();
				List<String> acceptQuest = new ArrayList<>();
				List<String> howToCraft = new ArrayList<>();

				howToCraft.add("TODO: Instructions on how and when to craft it");

				reminderAll.add("The recipe takes 5 unique items, one gathered from each of the islands:");
				reminderAll.add("wait 80");
				reminderAll.add(mainIslandHint);
				reminderAll.add("wait 120");
				reminderAll.add(mgnIslandHint);
				reminderAll.add("wait 120");
				reminderAll.add(pugmasIslandHint);
				reminderAll.add("wait 120");
				reminderAll.add(halloweenIslandHint);
				reminderAll.add("wait 120");
				reminderAll.add(sduIslandHint);
				reminderAll.add("wait 120");
				reminderAll.addAll(howToCraft);

				acceptQuest.add("Thanks, dear!");
				acceptQuest.add("wait 40");
				acceptQuest.addAll(reminderAll);

				//
				if (user.isQuest_Main_Start()) {
					if (step == 1) {
						return acceptQuest;
					}

					List<String> reminder = new ArrayList<>(Collections.singleton("I see you're missing some ingredients. The items you need are:"));
					boolean sendReminder = false;

					if (!user.isQuest_Main_Start()) {
						sendReminder = true;
						reminder.add("wait 120");
						reminder.add(mainIslandHint);
					}

					if (!user.isQuest_MGN_Start()) {
						sendReminder = true;
						reminder.add("wait 120");
						reminder.add(mgnIslandHint);
					}

					if (!user.isQuest_Pugmas_Start()) {
						sendReminder = true;
						reminder.add("wait 120");
						reminder.add(pugmasIslandHint);
					}

					if (!user.isQuest_Halloween_Start()) {
						sendReminder = true;
						reminder.add("wait 120");
						reminder.add(halloweenIslandHint);
					}

					if (!user.isQuest_SDU_Start()) {
						sendReminder = true;
						reminder.add("wait 120");
						reminder.add(sduIslandHint);
					}


					if (sendReminder) {
						reminder.add("wait 120");
						reminder.addAll(howToCraft);
						return reminder;
					} else
						return reminderAll;
				}

				Tasks.wait(640, () -> {
					JsonBuilder json = new JsonBuilder("&f[&aClick to accept quest&f]").command("bearfair quests accept_witch").hover("Accept the Witch's quest");
					json.send(player);
				});
				user.setQuest_Main_Start(true);
				return startQuest;
			}
		};

		@Getter
		private final int npcId;
		@Getter
		private final List<String> script;

		MainNPCs(int npcId) {
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}

		MainNPCs(int npcId, List<String> script) {
			this.npcId = npcId;
			this.script = script;
		}
	}

	public static void acceptWitchQuest(Player player) {
		nextStep(player); // 1
		Talkers.sendScript(player, MainNPCs.Witch);
		nextStep(player); // 2
	}

	private static void nextStep(Player player) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		int step = user.getQuest_Main_Step() + 1;
		user.setQuest_Main_Step(step);
		service.save(user);
	}

}
