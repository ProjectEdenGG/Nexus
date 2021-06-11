package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2021.bearfair21.Quests;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.MainIsland.MainNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.clientside.ClientsideContentManager;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.FishingLootCategory;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content.ContentCategory;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC.*;

// TODO BF21: Quest + Dialog
@Region("main")
@NPCClass(MainNPCs.class)
public class MainIsland implements Listener, BearFair21Island {

	static BearFair21UserService userService = new BearFair21UserService();
	//
	static ItemBuilder balloon_cyan = new ItemBuilder(Material.STICK).customModelData(21);
	static ItemBuilder balloon_yellow = new ItemBuilder(Material.STICK).customModelData(18);
	public static ItemBuilder bf_cake = new ItemBuilder(Material.CAKE).name("Bear Fair Cake");
	static ItemBuilder invitation = new ItemBuilder(Material.PAPER).name("Anniversary Event Invitation");
	static List<BearFair21NPC> invitees = Arrays.asList(ARCHITECT, ARTIST, BAKER, BARTENDER, BLACKSMITH, BOTANIST, CARPENTER, COLLECTOR,
			FISHERMAN1, INVENTOR, PASTRY_CHEF, SORCERER, LUMBERJACK, BEEKEEPER, FISHERMAN2, AERONAUT, ADMIRAL, ORGANIZER);

	public enum MainNPCs implements BearFair21TalkingNPC {
		WAKKAFLOCKA(BearFair21NPC.ORGANIZER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Hello there, my name is WakkaFlocka, and I am an admin on Project Eden and the organizer for this event. And...");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				script.add("Welcome to Bear Fair, Project Eden's anniversary event!");
				script.add("wait 80");
				script.add("There are several islands to explore, find easter eggs, and quests to complete!");
				script.add("wait 80");
				script.add("At the carnival, you can play daily minigames in which you can play to gain Event Points.");
				script.add("wait 80");
				script.add("At any point you can buy unique prizes and perks using those points.");
				script.add("wait 80");
				script.add("There are many ways to acquire the currency here, you should talk to the local merchants.");
				script.add("wait 80");
				script.add("And if you need help figuring out where you are, check out this map to my side.");

				user.getNextStepNPCs().remove(this.getNpcId());
				userService.save(user);
				return script;
			}
		},
		ADMIRAL(BearFair21NPC.ADMIRAL) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("The name is Phoenix, Admiral Phoenix and my job is to keep all yall people here safe.");
					script.add("If you see anything suspicious simply let me know. I am more than capable of handling things myself.");
					script.add("Now you just continue to have fun, got it?");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				script.add("TODO - Hello");
				return script;
			}
		},
		ARCHITECT(BearFair21NPC.ARCHITECT) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Hm, are you admiring the scenery as well?");
					script.add("There is honestly nothing more stunning than an area bustling with life.");
					script.add("Nice to meet you, my name is Zach and I love architecture with all my heart.");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				script.add("TODO - Hello");
				return script;
			}
		},
		CARPENTER(BearFair21NPC.CARPENTER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("I swear, there is nothing like the smell of sawdust to wake you up in the morning.");
					script.add("How do you do friend, the name is Ron and as this town's carpenter, my job is to create masterpieces everyday!");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				script.add("TODO - Hello");
				return script;
			}
		},
		// Side Quests
		BEEKEEPER(BearFair21NPC.BEEKEEPER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Hm did you say something? Apologies, my hearing isn't quite what it used to be.");
					script.add("Feel free to call me Harold, I'm the Beekeeper around these parts and I have been for many years.");
					script.add("I honestly wouldn't have it any other way.");
					script.add("Are you by chance a fan of honey?");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				script.add("TODO - Hello");
				return script;
			}
		},
		FISHERMAN2(BearFair21NPC.FISHERMAN2) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("I swear there is nothing more beautiful than the open water.");
					script.add("It's somehow both a symbol of dangerous adventure and complete calm.");
					script.add("My name is Nate by the way and honestly I can see why the fish would be happy to live out there.");
					script.add("However I suppose I can’t put too much thought into it, as a fisherman it is my job to pull them out of that paradise, isn't it?");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
					return script;
				} else if (user.getQuestStage_Recycle() == QuestStage.NOT_STARTED) {
					script.add("TODO - better dialog");
					script.add("You can get useful materials from recycling");
					script.add("wait 20");
					script.add("The more trash you recycle, the less trash you will catch");
					script.add("wait 20");
					script.add("You've recycled: " + user.getRecycledItems() + " trash");

					user.setQuestStage_Recycle(QuestStage.STARTED);
					userService.save(user);
					return script;
				} else if (user.getQuestStage_Recycle() == QuestStage.STARTED) {
					script.add("You've recycled: " + user.getRecycledItems() + " trash");
					return script;
				}


				script.add("TODO - Hello");
				return script;
			}
		},
		// Main Quest
		MAYOR(BearFair21NPC.MAYOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("Welcome to the village!");
					script.add("wait 40");
					script.add("I would love to give you a tour, but I’m swamped in preparation work for the upcoming anniversary event.");
					script.add("wait 80");
					script.add("You know, if you want to give a good first impression, helping me out would certainly do the trick.");
					script.add("wait 80");
					script.add("When you get some spare time, come back and talk to me. I'd greatly appreciate the help.");

					return script;
				} else {
					switch (user.getQuestStage_Main()) {
						case NOT_STARTED -> {
							script.add("Oh splendid, I could really use some help gathering the necessary decorations, I've got a few tasks for you.");
							script.add("wait 80");
							script.add("For your first task, could you gather the materials, and craft me 4 cyan & 4 yellow banners? I've seem to forgotten the recipe.");
							user.setQuestStage_Main(QuestStage.STARTED);
							userService.save(user);
							return script;
						}
						case STARTED -> {
							List<ItemBuilder> required = Arrays.asList(new ItemBuilder(Material.CYAN_BANNER).amount(4), new ItemBuilder(Material.YELLOW_BANNER).amount(4));
							if (!Quests.hasItemsLikeFrom(user, required)) {
								script.add("For your first task, could you gather the materials, and craft me 4 cyan & 4 yellow banners? I've seem to forgotten the recipe.");
								return script;
							}

							Quests.removeItemBuilders(user.getPlayer(), required);
							script.add("TODO - Thanks");
							script.add("wait 20");
							script.add("TODO - While im placing these around the town, could you gather me 16 cyan & 16 yellow balloons? Last I heard, you could get some from Skye, the Aeronaut.");

							ClientsideContentManager.addCategory(user, ContentCategory.BANNER);
							user.setQuestStage_Main(QuestStage.STEP_ONE);
							userService.save(user);
							return script;
						}
						case STEP_ONE, STEP_TWO -> {
							List<ItemBuilder> required = Arrays.asList(balloon_cyan.clone().amount(16), balloon_yellow.clone().amount(16));
							if (!Quests.hasItemsLikeFrom(user, required)) {

								script.add("For your next task, could you gather me 16 cyan & 16 yellow balloons? Last I heard, you could get some from Skye, the Aeronaut.");
								return script;
							}

							Quests.removeItemBuilders(user.getPlayer(), required);
							script.add("TODO - Thanks");
							script.add("wait 20");
							script.add("TODO - While im placing these around the town, could you gather me 32 White Wool and 8 of each red, green, and blue dyes?");

							ClientsideContentManager.addCategory(user, ContentCategory.BALLOON);
							user.setQuestStage_Main(QuestStage.STEP_THREE);
							userService.save(user);
							return script;
						}
						case STEP_THREE -> {
							// Require all items, not just some
							List<ItemBuilder> required = Arrays.asList(new ItemBuilder(Material.WHITE_WOOL).amount(32),
									new ItemBuilder(Material.RED_DYE).amount(8),
									new ItemBuilder(Material.GREEN_DYE).amount(8),
									new ItemBuilder(Material.BLUE_DYE).amount(8));

							if (!Quests.hasItemsLikeFrom(user, required)) {
								script.add("For your next task, could you gather me 32 White Wool and 8 of each red, green, and blue dyes?");
								return script;
							}

							Quests.removeItemBuilders(user.getPlayer(), required);
							script.add("TODO - Thanks");
							script.add("wait 20");
							script.add("TODO - While im placing these around the town, could you follow up with Maple the Pastry Chef about my cake order?");

							ClientsideContentManager.addCategory(user, ContentCategory.FESTOON);
							user.setQuestStage_Main(QuestStage.STEP_FOUR);
							userService.save(user);
							return script;
						}
						case STEP_FOUR -> {
							List<ItemBuilder> required = Collections.singletonList(bf_cake);
							if (!Quests.hasItemsLikeFrom(user, required)) {
								script.add("For your next task, could you follow up with Maple the Pastry Chef about my cake order?");
								return script;
							}

							Quests.removeItemBuilders(user.getPlayer(), required);
							script.add("TODO - Thanks");
							script.add("wait 20");
							script.add("That's almost everything, there's just one last task I need you to do, while I'm finishing up.");
							script.add("wait 40");
							script.add("I had these invitations custom made, could you go around the island and give one to each of the townspeople?");
							Tasks.wait(80, () -> Quests.giveItem(user, invitation.clone().amount(invitees.size()).build()));

							ClientsideContentManager.addCategory(user, ContentCategory.FOOD);
							user.setQuestStage_Main(QuestStage.STEP_FIVE);
							userService.save(user);
							return script;
						}
						case STEP_FIVE -> {
							script.add("I had those invitations custom made, could you go around the island and give one to each of the townspeople?");
							return script;
						}
						case STEP_SIX -> {
							script.add("You're a life saver, thank you! And as a token of my gratitude, have this...");
							Tasks.wait(40, () -> Quests.giveItem(user, Quests.getCrateKey()));

							user.setQuestStage_Main(QuestStage.COMPLETE);
							userService.save(user);
							return script;
						}
						case COMPLETE -> {
							script.add("Thanks for all the hard work!");
							return script;
						}
					}
				}

				script.add("TODO - Hello");
				return script;
			}

		},
		AERONAUT(BearFair21NPC.AERONAUT) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Hey there land dweller, how do you feel about exploring above the clouds?");
					script.add("The names Skye and I'm an explorer of well... the skies! They call me an aeronaut, pretty cool right?");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;

				} else if (user.getQuestStage_Main() == QuestStage.STEP_ONE) {
					List<ItemBuilder> required = new ArrayList<>();
					Arrays.stream(FishingLoot.values())
							.filter(fishingLoot -> fishingLoot.getCategory().equals(FishingLootCategory.FISH))
							.toList().forEach(fishingLoot -> required.add(fishingLoot.getItemBuilder()));

					List<ItemStack> items = Quests.getItemsListFrom(user, required);
					if (Utils.isNullOrEmpty(items)) {
						script.add("TODO - gib any fish to get balloons, ok?");
						return script;
					} else {
						Quests.removeItem(user, RandomUtils.randomElement(items));
						script.add("TODO - here ye are");

						Quests.giveItem(user, balloon_cyan.clone().amount(16).build());
						Quests.giveItem(user, balloon_yellow.clone().amount(16).build());

						user.setQuestStage_Main(QuestStage.STEP_TWO);
						userService.save(user);
						return script;
					}
				}

				script.add("TODO - Hello");
				return script;
			}
		},
		// Merchants
		ARTIST(BearFair21NPC.ARTIST) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Ah, just the inspiration I was looking for. The name’s Sage and I'm your local artist!");
					script.add("Feel free to browse as long as you’d like. I would love to get to paint- I mean know you.");
					script.add("wait 20");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		BAKER(BearFair21NPC.BAKER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Oh hi, my name is Rye and you may know me as the baker guy.");
					script.add("I just took this loaf of bread out of the oven, if you are interested.");
					script.add("wait 20");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		BARTENDER(BearFair21NPC.BARTENDER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Hey friend, can I interest you in a nice refreshing beverage.");
					script.add("My name is Cosmo and drinks are my specialty. It's quite hot out there, and I think I've got just what you need.");
					script.add("wait 20");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		BLACKSMITH(BearFair21NPC.BLACKSMITH) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Can I help you with something?");
					script.add("Oh you must be new around here, I'm Alvor and I'm a blacksmith that's pretty much all you need to know.");
					script.add("If it needs a forge, I'm probably selling it, so take a look around I suppose.");
					script.add("wait 20");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		BOTANIST(BearFair21NPC.BOTANIST) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Oh hi there! I'm Fern and I absolutely love plants, probably why I decided to become a botanist in the first place.");
					script.add("I have got a ton of beautiful plants for purchase and a story for each and every one of them.");
					script.add("wait 20");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		COLLECTOR(BearFair21NPC.COLLECTOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("I'm Mercury, I'm a collector of many wondrous goods, old and new.");
					script.add("Anything can be a treasure if your heart decides to give it value.");
					script.add("Trade with me whenever you like, but be aware, I tend to wander.");
					script.add("I may be here now, but I certainly won't be much longer.");
					script.add("wait 20");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		FISHERMAN1(BearFair21NPC.FISHERMAN1) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Have you ever been fishing before? There is nothing quite as peaceful as sitting by the water and waiting for a fish to bite.");
					script.add("I'm Gage and I've been fishing for as long as I can remember. You should join me some time.");
					script.add("wait 20");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		INVENTOR(BearFair21NPC.INVENTOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("...AH! Apologies I didn't see you there.");
					script.add("Guess I was lost in my own world again. I'm Joshua and I'm always trying to come up with awesome new gadgets for people to use.");
					script.add("I suppose that's why the people around here call me an inventor.");
					script.add("What can I help you with?");
					script.add("wait 20");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		PASTRY_CHEF(BearFair21NPC.PASTRY_CHEF) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("I'd say a little sugar never hurt anyone. A lot of sugar you say? Well I'd certainly be willing to risk it.");
					script.add("I'm Maple and sweet treats are my passion! Cookies, cakes, pies, you name it, I've probably made it at some point.");
					script.add("wait 20");
					return script;
				} else if (user.getQuestStage_Main() == QuestStage.STEP_THREE) {
					ItemStack item = Quests.getItemLikeFrom(user, new ItemBuilder(Material.CAKE));
					if (!ItemUtils.isNullOrAir(item))
						return script;

					script.add("Oh my goodness, I totally forgot! Nor do I have the necessary supplies for a cake that size.");
					script.add("Would you mind baking the cake and gathering the cocoa beans for the frosting, and I'll decorate the cake to perfection?");
					script.add("wait 80");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		LUMBERJACK(BearFair21NPC.LUMBERJACK) {
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("I promise I really do love my job however chopping wood all day can be extremely tiring.");
					script.add("This poor guy needs a break at some point too.");
					script.add("Oh wait I don’t think we’ve met before, sorry about that. My name is Flint and I'm a lumberjack.");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		SORCERER(BearFair21NPC.SORCERER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = getTool(user.getPlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("Hmm I can tell you come here seeking knowledge of the arcane.");
					script.add("How do I know? Well that is simply not important right now, what is important is that I, as the great sorcerer Lucian, have knowledge of magic that far surpasses that of anyone else!");
					script.add("So don’t just stand there, take a look around.");
					script.add("wait 20");
					return script;
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("<exit>");
					invite(user, this.getNpcId(), tool);
					return script;
				}

				return script;
			}
		},
		;

		private static void invite(BearFair21User user, int npcId, ItemStack tool) {
			tool.setAmount(tool.getAmount() - 1);
			user.getInvitees().add(npcId);
			if (user.getInvitees().size() == invitees.size()) {
				user.sendMessage("TODO - You've completed the task, return to Mayor John");
				user.setQuestStage_Main(QuestStage.STEP_SIX);
			}
			userService.save(user);
		}

		private static boolean isInviting(BearFair21User user, int npcId, ItemStack tool) {
			if (user.getInvitees().contains(npcId))
				return false;

			return !ItemUtils.isNullOrAir(tool) && ItemUtils.isFuzzyMatch(tool, invitation.build());
		}

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

		MainNPCs(BearFair21NPC npc) {
			this.npc = npc;
			this.script = new ArrayList<>();
		}
	}
}
