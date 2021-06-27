package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.models.Talker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.Quests;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.SummerDownUnderIsland.SummerDownUnderNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.clientside.ClientsideContentManager;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PacketUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

// TODO BF21: serpent
// TODO BF21: track NPCs who have been spoken to about our holy serpent
// TODO BF21: client-side stuff
@Region("summerdownunder")
@NPCClass(SummerDownUnderNPCs.class)
public class SummerDownUnderIsland implements BearFair21Island {
	public static final ItemBuilder FEATHER = new ItemBuilder(Material.FEATHER).name("Wing Feather");
	public static final ItemBuilder SEVEN_FEATHER = FEATHER.clone().amount(7);
	public static final ItemBuilder BRIKKIES = new ItemBuilder(Material.COOKIE).name("Anzac Bikkie").amount(16);
	public static final Talker.TalkingNPC SERPENT = new Talker.TalkingNPC() {
		@Getter
		private final String name = "Rainbow Serpent";
		@Getter
		private final int npcId = -1;
		@Getter
		private final List<String> script = List.of(
			"Hello young one. It is nice to meet you.",
			"wait 40",
			"I must thank you for journeying this far to see me. Departing from that land was a burdensome task, and not one that came easily. But if you are ready to learn and pass on my story, I will be ready to return home.",
			"wait 200",
			"<self> I would like to hear your story, please.",
			"wait 40",
			"Long, long ago in the Dreamtime, the Earth lay flat and still. Nothing moved and nothing grew. One day, I woke from my slumber and came out from under the ground. I was known as the Rainbow Serpent.",
			"wait 190",
			"I travelled for a very long time, far and wide. As I made my way across the land, my body formed mountains, valleys, and rivers. I was the Dreamtime creature who shaped the Earth. After all of my travelling, I grew tired, and so I curled up and went to sleep.",
			"wait 220",
			"After some rest, I returned to the place that I had first appeared and called out to the frogs, \"Come out!\"",
			"wait 100",
			"The frogs woke up very slowly because they had so much water in their bellies. I tickled their stomachs, and the water began to fill the tracks that I had left. This is how the lakes and rivers were formed.",
			"wait 200",
			"After this, water, grass, and trees began to grow. All the other animals that lived in rocks, on the plains, in the trees and the air began to wake up and follow me. They were all happy with the Earth.",
			"wait 200",
			"I made rules that they all had to obey. Some did not like this and began to cause trouble. So I said, \"Those who obey will be rewarded; I shall give them human form. But, for those who don't, they will be punished and turned to stone.\"",
			"wait 210",
			"The tribes of people lived together on the land given to them by me. They knew that the land would always be theirs, as long as they took care of it. They believed that no one should ever take it away from them.",
			"wait 200",
			"Now, young one. Return to the village. Tell the people what you have heard today and I will return, and with me, I shall bring rain.",
			"wait 150",
			"Goodbye for now.",
			"wait 80"
		);
	};
	private static final Set<UUID> serpentTalkingTo = new HashSet<>();

	public SummerDownUnderIsland() {
		Nexus.registerListener(this);
		Tasks.repeat(2, 2, () -> {
			BearFair21.getWGUtils().getEntitiesInRegionByClass("bearfair21_elytra", Player.class)
				.stream().filter(Player::isOnGround)
				.filter(player -> !BearFair21.getWGUtils().isInRegion(player, "bearfair21_elytra_start")
								&& !BearFair21.getWGUtils().isInRegion(player, "bearfair21_elytra_finish"))
				.forEach(this::teleportToElytraCourse);
		});
	}

	private void teleportToElytraCourse(Player player) {
		player.teleportAsync(new Location(player.getWorld(), 1571.5, 194, -388.5, 120, 0));
	}

	@EventHandler
	public void onEnterRegion(PlayerEnteredRegionEvent event) {
		String regionName = event.getRegion().getId();
		if (regionName.equals("bearfair21_summerdownunder_elytra")) {
			if (new BearFair21UserService().get(event.getPlayer()).getQuestStage_SDU() == QuestStage.STEP_SEVEN)
				teleportToElytraCourse(event.getPlayer());
			else
				event.getPlayer().sendMessage(new JsonBuilder("&c&oYou feel as though you are not yet ready to travel deeper into the cave..."));
		} else if (regionName.equals("bearfair21_elytra_dialogue") && !serpentTalkingTo.contains(event.getPlayer().getUniqueId())) {
			serpentTalkingTo.add(event.getPlayer().getUniqueId());
			Talker.runScript(event.getPlayer(), SERPENT).thenRun(() -> {
				new BearFair21UserService().edit(event.getPlayer(), bfuser -> bfuser.setQuestStage_SDU(QuestStage.STEPS_DONE));
				serpentTalkingTo.remove(event.getPlayer().getUniqueId());
				PlayerUtils.runCommand(event.getPlayer(), "bearfair21warps sdu_cave");
			});
		} else if (regionName.equals("bearfair21_elytra_finish")) {
			PlayerUtils.removeItem(event, new ItemStack(Material.ELYTRA));
		} else if (regionName.equals("bearfair21_summerdownunder")) {
			bookContentHandler(event.getPlayer());
		}
	}

	private static ItemFrame bookFrame;

	static void bookContentHandler(Player player) {
		World world = player.getWorld();
		if (bookFrame == null) {
			bookFrame = world.getNearbyEntitiesByType(ItemFrame.class, new Location(world, 169, 98, -175), 1, 1, 1).iterator().next();
			if (bookFrame == null) {
				Nexus.log("Could not find BF21 SDU book item frame");
				return;
			}
		}
		ItemStack content = new BearFair21UserService().get(player).getQuestStage_SDU().ordinal() < QuestStage.STEP_SIX.ordinal() ? null : bookFrame.getItem();
		PacketUtils.updateItemFrame(player, bookFrame, content, -1);
	}

	public enum SummerDownUnderNPCs implements BearFair21TalkingNPC {
		BRUCE(BearFair21NPC.BRUCE) {
			@Override
			public List<String> getScript(BearFair21User user) {
				QuestStage stage = user.getQuestStage_SDU();
				int ordinal = stage.ordinal();
				if (ordinal <= QuestStage.STEP_FIVE.ordinal() || (stage == QuestStage.STEP_FIVE && !user.getOnlinePlayer().getInventory().contains(SEVEN_FEATHER.build()))) {
					if (stage == QuestStage.NOT_STARTED) {
						setStage(user, QuestStage.STARTED);
						setNextNpc(user, KYLIE);
					}
					return List.of(
						"Struth! I’m glad you’re here!",
						"wait 40",
						"<self> Is something wrong?",
						"wait 40",
						"You bet there is! Our crops haven't seen a drought this devastating in... well... EVER!",
						"wait 80",
						"We hardly have enough water to keep our livestock going!",
						"wait 60",
						"<self> Oh no! Do you know what could have caused this?",
						"wait 60",
						"No idea, mate. This is definitely out of the ordinary - not just something that could be the result of a bad year.",
						"wait 100",
						"By all predictions, we were set to have a bonza wet season. The blokes over on channel 9 are as surprised as we are!",
						"wait 100",
						"I'd like ya to question the locals, find out anything ya can so we can get to the bottom of this. We're countin' on ya!",
						"wait 100",
						"<self> Don't worry! I've got this."
					);
				} else if (stage == QuestStage.STEP_FIVE) {
					SummerDownUnderIsland.bookContentHandler(user.getOnlinePlayer());
					ClientsideContentManager.addCategory(user, ClientsideContent.Content.ContentCategory.SERPENT);
					final int delay = 60;
					Tasks.wait(delay, () -> {
						user.getOnlinePlayer().getInventory().removeItemAnySlot(SEVEN_FEATHER.build());
						PlayerUtils.giveItemAndMailExcess(user.getOnlinePlayer(), new ItemStack(Material.ELYTRA), WorldGroup.EVENTS); // prevent player from getting free elytra in survival lol
					});
					List<String> text = new ArrayList<>(setStageGetScript(user, QuestStage.STEP_SIX));
					// reverse order:
					text.add(0, "wait " + delay);
					text.add(0, "Ah right! I had been seein' these around the place. I know just the thing that'll help!");
					return text;
				} else if (ordinal >= QuestStage.STEP_SIX.ordinal() && ordinal < QuestStage.STEPS_DONE.ordinal()) {
					return Collections.singletonList("That should do it. It should help you traverse those caves a bit better. Head down there and let me know if you find anything to make sense of all this.");
				} else if (stage == QuestStage.STEPS_DONE) {
					return Collections.singletonList("Thank you <player>. It really dodes humble you some.");
				} else if (stage == QuestStage.FOUND_ALL) {
					List<String> text = new ArrayList<>(setStageGetScript(user, QuestStage.COMPLETE));
					text.add("wait 40");
					text.add("Please, take this. It's the least we could do for ya. See ya 'round, mate.");
					Tasks.wait(99, () -> Quests.giveKey(user));
					return text;
				} else {
					return List.of(
						"You beauty! The rain really did come back! Thank you <player>!",
						"wait 60",
						"<self> Yay! I'm glad I could help!"
					);
				}
			}
		},
		KYLIE(BearFair21NPC.KYLIE) {
			@Override
			public List<String> getScript(BearFair21User user) {
				QuestStage stage = user.getQuestStage_SDU();
				int ordinal = stage.ordinal();
				if (stage == QuestStage.NOT_STARTED)
					return greeting;
				else if (ordinal < QuestStage.STEP_TWO.ordinal()) {
					if (stage == QuestStage.STARTED)
						setStage(user, QuestStage.STEP_ONE);
					else if (user.getOnlinePlayer().getInventory().containsAtLeast(new ItemStack(Material.WHEAT), 10))
						return setStageGetScript(user, QuestStage.STEP_TWO);

					return List.of(
						"Hey there! A little kookaburra told me you're the one goin' 'round trynna help us out here! Thank you so much!",
						"wait 60",
						"<self> No problem! Is there anything I can do for you?",
						"wait 30",
						"There sure is. Could you grab some wheat from the field at the back there? Gotta get these biscuits ready before the lunch-time rush. A bundle of 'bout 10 should do it."
					);
				} else if (stage == QuestStage.STEP_TWO) {
					if (user.getOnlinePlayer().getInventory().containsAtLeast(new ItemStack(Material.MILK_BUCKET), 1)) {
						setNextNpc(user, MEL_GIBSON);
						return setStageGetScript(user, QuestStage.STEP_THREE);
					}
					return Collections.singletonList(
						"Thank you! One last thing now, could you run over to Daisy the cow and fetch a bucket of milk? After that, we should be set!"
					);
				} else if (ordinal >= QuestStage.STEP_THREE.ordinal() && ordinal < QuestStage.STEPS_DONE.ordinal()) {
					if (!user.isReceivedBrikkies()) {
						user.setReceivedBrikkies(true);
						new BearFair21UserService().save(user);
						PlayerUtils.giveItem(user.getOnlinePlayer(), BRIKKIES.build());
					}
					return List.of(
						"Thanks again <player>! As appreciation, please help yourself to some of my famous Anzac Bikkies.",
						"wait 60",
						"<self> Thank you! By the way, would you happen to have heard anything about the cause of the drought?",
						"wait 60",
						"Hmm... I heard Mr. Gibson muttering on about a... snake?... and water? Oh... and also how the townsfolk have no respect for the elders... " +
							"I think he's just grumpy though. But maybe you should head on over to his house and have a chat - he might have a snake problem or something.",
						"wait 100",
						"<self> Alright, and thank you again for the cookies- I mean, biscuits!",
						"No worries!"
					);
				} else if (stage == QuestStage.STEPS_DONE) {
					return List.of(
						"Oh wow, I had no idea... I wish my grandparents had've told me about my history a bit more...",
						"wait 60",
						"Thank you for sharing this with me <player>! I will pass it on too!"
					);
				} else {
					return List.of(
						"No way! Daisy, we’re saved!",
						"<name:Daisy> Moo!"
					);
				}
			}
		},
		MEL_GIBSON(BearFair21NPC.MEL_GIBSON) {
			@Override
			public List<String> getScript(BearFair21User user) {
				QuestStage stage = user.getQuestStage_SDU();
				int ordinal = stage.ordinal();
				if (ordinal < QuestStage.STEP_THREE.ordinal()) {
					return greeting;
				} else if (ordinal <= QuestStage.STEPS_DONE.ordinal()) {
					if (stage == QuestStage.STEP_THREE) {
						setNextNpc(user, MILO);
						setStage(user, QuestStage.STEP_FOUR);
					}
					return List.of(
						"G'day. I take it you're <player>? The one Bruce has sent out to get to the bottom of the issue we're facin' here?",
						"wait 60",
						"<self> I sure am!",
						"wait 20",
						"Good on ya. I'm too old to be dealin' with it myself.",
						"wait 40",
						"If only I had the foresight to see where we'd be now...",
						"wait 40",
						"<self> What? You knew this would happen?",
						"wait 40",
						"Huh? What? Oh no... not at all... No idea...",
						"wait 40",
						"<self> For an actor, you're not too good at lying...",
						"wait 40",
						"Okay well... I do know... but I can't be the one to tell ya. This has to be your own adventure.",
						"wait 80",
						"For now, visit my son Milo. I believe his RV has something that might be useful to you.",
						"wait 70",
						"<self> Okay, I'll head on over there.",
						"wait 30",
						"Good luck out there, <player>."
					);
				} else if (stage == QuestStage.STEPS_DONE) {
					return Collections.singletonList("Ah yes, I recall it clearly now. Thank you for your effort. We couldn't have done it without ya.");
				} else {
					return Collections.singletonList("Ha! This is all thanks to you <player>! Good on ya!");
				}
			}
		},
		MILO(BearFair21NPC.MILO) {
			@Override
			public List<String> getScript(BearFair21User user) {
				QuestStage stage = user.getQuestStage_SDU();
				int ordinal = stage.ordinal();
				if (ordinal < QuestStage.STEP_FOUR.ordinal())
					return greeting;
				if (stage == QuestStage.STEP_FOUR) {
					if (false) { // todo: items
						return setStageGetScript(user, QuestStage.STEP_FIVE);
					}
					return List.of(
						"Oh hey <player>! My dad just called to tell me you'd be over here soon.",
						"wait 60",
						"My van is absolutely filthy! I haven't taken 'er out in 'bout 2 years now.",
						"wait 40",
						"Would ya mind helping me clean up a bit? Just pick up all the junk lying 'round in there and bring it back to me. I'll throw it away for ya.",
						"wait 80",
						"<self> No problem, I'll be back soon!"
					);
				} else if (ordinal >= QuestStage.STEP_FIVE.ordinal() && ordinal < QuestStage.STEPS_DONE.ordinal()) {
					return List.of(
						"Sweet! Thanks a bunch, mate.",
						"wait 20",
						"Oh uh... hold on to that feather though. I've seen a few more of those lyin' around lately. Collect seven more of them and bring them to Bruce, he'll be able to make them into something useful!",
						"wait 130",
						"<self> Alright, see you later!"
					);
				} else if (stage == QuestStage.STEPS_DONE) {
					return Collections.singletonList("Woah, that’s why there was no rain? She was sad we had forgotten ‘er? I feel kinda bad now… I won’t forget about this.");
				} else {
					return Collections.singletonList("Sick! Any more of that drought and we’d be goners! Thanks!");
				}
			}
		}
		;

		private final BearFair21NPC npc;
		private final List<String> script;
		static final List<String> greeting = Collections.singletonList("G'day!");

		@Override
		public List<String> getScript(BearFair21User user) {
			return this.script;
		}

		@Override
		public String getName() {
			return this.npc.getNpcName();
		}

		@Override
		public int getNpcId() {
			return this.npc.getId();
		}

		void setNextNpc(BearFair21User user, SummerDownUnderNPCs npc) {
			Set<Integer> nextNpcs = user.getNextStepNPCs();
			nextNpcs.remove(getNpcId());
			nextNpcs.add(npc.getNpcId());
			new BearFair21UserService().save(user);
		}

		void setStage(BearFair21User user, QuestStage stage) {
			user.setQuestStage_SDU(stage);
			new BearFair21UserService().save(user);
		}

		List<String> setStageGetScript(BearFair21User user, QuestStage stage) {
			user.setQuestStage_SDU(stage);
			new BearFair21UserService().save(user);
			return getScript(user);
		}

		SummerDownUnderNPCs(BearFair21NPC npc) {
			this.npc = npc;
			this.script = Collections.emptyList();
		}
	}
}
