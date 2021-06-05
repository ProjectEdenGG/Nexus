package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2021.bearfair21.Quests;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.MainIsland.MainNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.clientside.ClientsideContentManager;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content.ContentCategory;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	static List<BearFair21NPC> invitees = Arrays.asList(BearFair21NPC.ARCHITECT, BearFair21NPC.ARTIST, BearFair21NPC.BAKER,
			BearFair21NPC.BARTENDER, BearFair21NPC.BLACKSMITH, BearFair21NPC.BOTANIST, BearFair21NPC.CARPENTER, BearFair21NPC.COLLECTOR,
			BearFair21NPC.FISHERMAN1, BearFair21NPC.INVENTOR, BearFair21NPC.PASTRY_CHEF, BearFair21NPC.SORCERER, BearFair21NPC.LUMBERJACK,
			BearFair21NPC.BEEKEEPER, BearFair21NPC.FISHERMAN2, BearFair21NPC.AERONAUT, BearFair21NPC.ADMIRAL);

	public enum MainNPCs implements BearFair21TalkingNPC {
		WAKKAFLOCKA(BearFair21NPC.ORGANIZER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				script.add("Welcome to Bear Fair, Project Eden's anniversary event!");
				script.add("wait 80");
				script.add("This year features several islands to explore, find easter eggs, and do quests!");
				script.add("wait 80");
				script.add("At the carnival, you can play daily minigames in which you can play to gain Event Points.");
				script.add("wait 80");
				script.add("At any point you can buy unique prizes and perks using those points.");
				script.add("wait 80");
				script.add("There are many ways to acquire the currency here, you should talk to the local merchants.");
				script.add("wait 80");
				script.add("And if you need help figuring out where you are, check out this map to my side.");

				return script;
			}
		},
		ADMIRAL(BearFair21NPC.ADMIRAL) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
				} else {
					script.add("TODO");
				}

				return script;
			}
		},
		// Side Quests
		BEEKEEPER(BearFair21NPC.BEEKEEPER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
				} else {
					script.add("TODO");
				}

				return script;
			}
		},
		FISHERMAN2(BearFair21NPC.FISHERMAN2) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
				} else {
					script.add("TODO");
					script.add("wait 20");
					script.add("You can get useful materials from recycling");
					script.add("wait 20");
					script.add("The more trash you recycle, the less trash you will catch");
					script.add("wait 20");
					script.add("You've recycled: " + user.getRecycledItems() + " trash");
				}


				return script;
			}
		},
		LUMBERJACK(BearFair21NPC.LUMBERJACK) {
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
				} else {
					script.add("TODO");
				}

				return script;
			}
		},
		// Main Quest
		MAYOR(BearFair21NPC.MAYOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("Welcome to our village!");
					script.add("I would love to give you a tour, but I’m swamped in preparation work for the upcoming anniversary event.");
					script.add("Do you have any time to spare? I’d greatly appreciate some help, if you do.");
				} else {
					switch (user.getQuestStage_Main()) {
						case NOT_STARTED -> {
							script.add("Oh splendid, I could really use some help gathering the necessary decorations, I've got a few tasks for you.");
							script.add("For your first task, could you gather the materials, and craft me 4 cyan & 4 yellow banners? I've seem to forgotten the recipe.");
							user.setQuestStage_Main(QuestStage.STARTED);
							return script;
						}
						case STARTED -> {
							List<ItemBuilder> required = Arrays.asList(new ItemBuilder(Material.CYAN_BANNER).amount(4), new ItemBuilder(Material.YELLOW_BANNER).amount(4));
							if (!Quests.hasItemsLikeFrom(user, required)) {
								script.add("For your first task, could you gather the materials, and craft me 4 cyan & 4 yellow banners? I've seem to forgotten the recipe.");
								return script;
							}

							Quests.removeItems(user.getPlayer(), required);
							script.add("TODO - Thanks");
							script.add("TODO - While im placing these around the town, could you gather me 16 cyan & 16 yellow balloons? Last I heard, you could get some from Skye, the Aeronaut.");

							ClientsideContentManager.addCategory(user, ContentCategory.BANNER);
							user.setQuestStage_Main(QuestStage.STEP_ONE);
							userService.save(user);
							return script;
						}
						case STEP_ONE -> {
							List<ItemBuilder> required = Arrays.asList(balloon_cyan.clone().amount(16), balloon_yellow.clone().amount(16));
							if (!Quests.hasItemsLikeFrom(user, required)) {

								script.add("For your next task, could you gather me 16 cyan & 16 yellow balloons? Last I heard, you could get some from Skye, the Aeronaut.");
								return script;
							}

							Quests.removeItems(user.getPlayer(), required);
							script.add("TODO - Thanks");
							script.add("TODO - While im placing these around the town, could you gather me 32 White Wool and 8 of each red, green, and blue dyes?");

							ClientsideContentManager.addCategory(user, ContentCategory.BALLOON);
							user.setQuestStage_Main(QuestStage.STEP_TWO);
							userService.save(user);
							return script;
						}
						case STEP_TWO -> {
							List<ItemBuilder> required = Arrays.asList(new ItemBuilder(Material.WHITE_WOOL).amount(32),
									new ItemBuilder(Material.RED_DYE).amount(8),
									new ItemBuilder(Material.GREEN_DYE).amount(8),
									new ItemBuilder(Material.BLUE_DYE).amount(8));

							if (!Quests.hasItemsLikeFrom(user, required)) {
								script.add("For your next task, could you gather me 32 White Wool and 8 of each red, green, and blue dyes?");
								return script;
							}

							Quests.removeItems(user.getPlayer(), required);
							script.add("TODO - Thanks");
							script.add("TODO - While im placing these around the town, could you follow up with Maple the Pastry Chef about my cake order?");

							ClientsideContentManager.addCategory(user, ContentCategory.FESTOON);
							user.setQuestStage_Main(QuestStage.STEP_THREE);
							userService.save(user);
							return script;
						}
						case STEP_THREE -> {
							List<ItemBuilder> required = Collections.singletonList(bf_cake);
							if (!Quests.hasItemsLikeFrom(user, required)) {
								script.add("For your next task, could you follow up with Maple the Pastry Chef about my cake order?");
								return script;
							}

							Quests.removeItems(user.getPlayer(), required);
							script.add("TODO - Thanks");
							script.add("TODO - That's almost everything, there's just one last task I need you to do.");
							script.add("TODO - I had these invitations custom made, could you go around the island and give one to each of the townspeople?");
							PlayerUtils.giveItem(user.getOnlinePlayer(), invitation.clone().amount(invitees.size()).build());

							ClientsideContentManager.addCategory(user, ContentCategory.FOOD);
							user.setQuestStage_Main(QuestStage.STEP_FOUR);
							userService.save(user);
							return script;
						}
						case STEP_FOUR -> {
							script.add("TODO - I had those invitations custom made, could you go around the island and give one to each of the townspeople?");
							return script;
						}
						case STEP_FIVE -> {
							script.add("TODO - Thanks! And as a token of my gratitude, have this key to the village *gives BF21 Key*");

							user.setQuestStage_Main(QuestStage.COMPLETE);
							userService.save(user);
							return script;
						}
						case COMPLETE -> {
							script.add("TODO - Thanks for all the hard work!");
							return script;
						}
					}
				}

				return script;
			}

		},
		AERONAUT(BearFair21NPC.AERONAUT) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					invite(user, this.getNpcId(), tool);
				} else {
					script.add("TODO");
				}

				return script;
			}
		},
		// Merchants
		ARCHITECT(BearFair21NPC.ARCHITECT) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");

					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		ARTIST(BearFair21NPC.ARTIST) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");
					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		BAKER(BearFair21NPC.BAKER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");
					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		BARTENDER(BearFair21NPC.BARTENDER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");

					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		BLACKSMITH(BearFair21NPC.BLACKSMITH) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");
					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		BOTANIST(BearFair21NPC.BOTANIST) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");
					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		CARPENTER(BearFair21NPC.CARPENTER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");

					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		COLLECTOR(BearFair21NPC.COLLECTOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");
					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		FISHERMAN1(BearFair21NPC.FISHERMAN1) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");
					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		INVENTOR(BearFair21NPC.INVENTOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");
					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		PASTRY_CHEF(BearFair21NPC.PASTRY_CHEF) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (user.getQuestStage_Main() == QuestStage.STEP_THREE) {
					ItemStack item = Quests.getItemLikeFrom(user, new ItemBuilder(Material.CAKE));
					if (!ItemUtils.isNullOrAir(item))
						return script;

					script.add("Oh my goodness, I totally forgot! Nor do I have the necessary supplies for a cake that size.");
					script.add("Would you mind baking the cake and gathering the cocoa beans for the frosting, and I'll decorate the cake to perfection?");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");
					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		SORCERER(BearFair21NPC.SORCERER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				ItemStack tool = ItemUtils.getTool(user.getOnlinePlayer());

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				} else if (isInviting(user, this.getNpcId(), tool)) {
					script.add("TODO - Thanks!");
					script.add("wait 40");
					invite(user, this.getNpcId(), tool);
				}

				return script;
			}
		},
		;

		private static void invite(BearFair21User user, int npcId, ItemStack tool) {
			tool.setAmount(tool.getAmount() - 1);
			user.getInvitees().add(npcId);
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
