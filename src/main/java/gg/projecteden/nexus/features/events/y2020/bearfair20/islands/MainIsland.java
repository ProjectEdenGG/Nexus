package gg.projecteden.nexus.features.events.y2020.bearfair20.islands;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.annotations.Region;
import gg.projecteden.nexus.features.events.models.BearFairIsland;
import gg.projecteden.nexus.features.events.models.BearFairIsland.NPCClass;
import gg.projecteden.nexus.features.events.models.BearFairTalker;
import gg.projecteden.nexus.features.events.models.Talker.TalkingNPC;
import gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20;
import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.MainIsland.MainNPCs;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests;
import gg.projecteden.nexus.models.bearfair20.BearFair20User;
import gg.projecteden.nexus.models.bearfair20.BearFair20UserService;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.*;

@Region("main")
@NPCClass(MainNPCs.class)
public class MainIsland implements Listener, BearFairIsland {

	@Override
	public String getEventRegion() {
		return BearFair20.getRegion();
	}

	public static ItemStack honeyStroopWafel = new ItemBuilder(Material.COOKIE).lore(BFQuests.itemLore).name("Honey Stroopwafel").amount(1).glow().build();
	public static ItemStack stroofWafel = new ItemBuilder(Material.COOKIE).lore(BFQuests.itemLore).name("Stoopwafel").amount(1).build();
	public static ItemStack blessedHoneyBottle = new ItemBuilder(Material.HONEY_BOTTLE).lore(BFQuests.itemLore, " ", "Blessed by the Queen herself").name("Blessed Bottle Of Honey").amount(1).build();
	public static ItemStack unpurifiedMarble = new ItemBuilder(Material.DIORITE).lore(BFQuests.itemLore).name("Unpurified Marble").amount(1).build();
	public static ItemStack relic_arms = new ItemBuilder(Material.BLAZE_ROD).lore(BFQuests.itemLore).name("Ancient Relic Arms").amount(2).build();
	public static ItemStack relic_base = new ItemBuilder(Material.LIGHT_WEIGHTED_PRESSURE_PLATE).lore(BFQuests.itemLore).name("Ancient Relic Base").amount(1).build();
	public static ItemStack relic_body = new ItemBuilder(Material.GOLDEN_HORSE_ARMOR).lore(BFQuests.itemLore).name("Ancient Relic Body").amount(1).build();
	public static ItemStack relic_eyes = new ItemBuilder(Material.EMERALD).lore(BFQuests.itemLore).name("Ancient Relic Eyes").amount(2).build();
	public static ItemStack relic = new ItemBuilder(Material.TOTEM_OF_UNDYING).lore(BFQuests.itemLore).name("Ancient Relic").amount(1).build();
	public static ItemStack ancientPickaxe = new ItemBuilder(Material.STONE_PICKAXE).lore(BFQuests.itemLore).name("Ancient Pickaxe").amount(1).build();
	public static ItemStack rareFlower = new ItemBuilder(Material.BLUE_ORCHID).lore(BFQuests.itemLore).name("Rare Flower").amount(1).build();
	private static String witchDwellingRg = "bearfair2020_witchdwelling";
	private static Location specialPrizeLoc = new Location(BearFair20.getWorld(), -1016, 120, -1605);
	public static ItemStack specialPrize;
	//

	public MainIsland() {
		Nexus.registerListener(this);

		List<ItemStack> drops = new ArrayList<>(specialPrizeLoc.getBlock().getDrops());
		if (!drops.isEmpty())
			specialPrize = new ItemBuilder(drops.get(0)).clone().lore(BFQuests.itemLore, "&f", "RClick while holding to open").name("Special Prize").glow().build();
	}

	public enum MainNPCs implements TalkingNPC {
		Excavator1(2969) {
			@Override
			public List<String> getScript(Player player) {
				List<String> script = new ArrayList<>();
				switch (LocalDateTime.now().getDayOfMonth()) {
					case 1:
						script.add("We've got some digging to do. You should speak with my supervisor.");
						break;
					case 2:
						script.add("I'm not too sure about this dig anymore...");
						break;
					case 3:
						script.add("I was mining earlier and it sounded metallic. I hope we are close.");
						break;
					case 4:
					case 5:
						script.add("The safe has some special word on it, 'SafeCracker.'");
						break;
					default:
						return null;
				}
				return script;
			}
		},
		Excavator2(2968) {
			@Override
			public List<String> getScript(Player player) {
				List<String> script = new ArrayList<>();
				switch (LocalDateTime.now().getDayOfMonth()) {
					case 1:
						script.add("I'm not supposed to tell you what we're looking for, but my supervisor may have some information for you.");
						break;
					case 2:
						script.add("I hope I get to keep a part of whatever is in the evil safe");
						break;
					case 3:
						script.add("I'm so ready for this dig to be over. I thought this was a one day job!");
						break;
					case 4:
					case 5:
						script.add("I'm so glad this dig is finally over. I can go home and get some rest.");
						break;
					default:
						return null;
				}
				return script;
			}
		},
		Supervisor(2970) {
			@Override
			public List<String> getScript(Player player) {
				List<String> script = new ArrayList<>();
				switch (LocalDateTime.now().getDayOfMonth()) {
					case 1:
						script.add("We've heard about a lost safe under these fair grounds.");
						script.add("wait 80");
						script.add("We've tracked it to this location and are working hard to uncover it.");
						script.add("wait 80");
						script.add("Check back with us over the next few days and we should make some progress!");
						break;
					case 2:
						script.add("We can feel a strong energy radiating from this site.");
						script.add("wait 80");
						script.add("I'm not sure what we're dealing with, but it's definitely special.");
						script.add("wait 80");
						script.add("We are getting closer to the safe, I can tell");
						break;
					case 3:
					case 4:
						script.add("I've had to call in reinforcements for this dig, the stone around the safe is very tough.");
						script.add("wait 80");
						script.add("One of the excavators said he thought he heard a clink while mining, so we might be close.");
						script.add("wait 80");
						script.add("It should only be one more day before we find what we are looking for.");
						break;
					case 5:
						BearFair20UserService service = new BearFair20UserService();
						BearFair20User user = service.get(player);
						int wait = 0;

						if (!user.isSafeCracker_talkedWith_Supervisor()) {
							user.setSafeCracker_talkedWith_Supervisor(true);
							service.save(user);

							script.add("We found the safe, but it's protected by a magical spell. We can't seem to break it no matter what we try.");
							script.add("wait " + (wait += 80));
							script.add("However, I've wandered around the island at night, and last night a whole bunch of strangers showed up.");
							script.add("wait " + (wait += 80));
							script.add("They were muttering strange questions at me, and I think they might have something to do with this safe.");
							script.add("wait " + (wait += 80));
							script.add("Search around the island for these strangers, and see if they can give you a clue on how to open this safe.");
							script.add("wait " + (wait += 80));
							script.add("When you think you know the code, go inspect that safe and try and crack the spell.");
							wait += 100;
						}

						Tasks.wait(wait, () -> PlayerUtils.runCommand(player, "safecracker start"));
						break;
					default:
						return null;
				}
				return script;
			}
		},
		Miner(2743) {
			@Override
			public List<String> getScript(Player player) {
				BearFair20UserService service = new BearFair20UserService();
				BearFair20User user = service.get(player);
				if (user.isQuest_talkedWith_Miner())
					return null;
				user.setQuest_talkedWith_Miner(true);
				service.save(user);

				List<String> script = new ArrayList<>();
				script.add("We find all kinds'a stuff in this here marble quarry if yer interested. 'fact we recently dug up an old pickaxe.");
				script.add("wait 80");
				script.add("Can't quite figure how old but I'm sure you could put it in a museum or sumthin.");
				return script;
			}
		},
		Collector(2750) {
			@Override
			public List<String> getScript(Player player) {
				BearFair20UserService service = new BearFair20UserService();
				BearFair20User user = service.get(player);
				if (user.isQuest_talkedWith_Collector())
					return null;
				user.setQuest_talkedWith_Collector(true);
				service.save(user);

				List<String> script = new ArrayList<>();
				script.add("Ah hello there. I don't believe we've met. You can call me the Collector.");
				script.add("wait 80");
				script.add("I sell only the finest and rarest of items, but I don't dawdle in any one place for long so if you don't have the coin, don't waste my time.");
				return script;
			}
		},
		WakkaFlocka(2962) {
			@Override
			public List<String> getScript(Player player) {
				List<String> script = new ArrayList<>();
				script.add("Welcome to Bear Fair, Project Eden's anniversary event!");
				script.add("wait 80");
				script.add("This year features several islands to explore, find easter eggs, and do quests!");
				script.add("wait 80");
				script.add("At the carnival, you can play daily minigames in which you can play to gain Bear Fair Points");
				script.add("wait 80");
				script.add("And at the end of Bear Fair, you can buy unique prizes and perks using those points.");
				script.add("wait 80");
				script.add("I'd recommend having an empty inventory while you're here. All quests require only Bear Fair items/tools. To acquire the currency here, you can farm, fish, or mine, and then sell it.");
				script.add("wait 120");
				script.add("To get started with the quests, you must find the evil wicked Witch. Last I heard, she was brewing up something crooked in the forest.");
				script.add("wait 120");
				script.add("And if you need help figuring out where you are, check out this map to my side.");
				return script;
			}
		},
		Witch(2670) {
			@Override
			public List<String> getScript(Player player) {
				BearFair20UserService service = new BearFair20UserService();
				BearFair20User user = service.get(player);
				int step = user.getQuest_Main_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Welcome, welcome <player>, the roots of the island informed me of your soon arrival.");
				startQuest.add("wait 80");
				startQuest.add("Wait, shhh, &7&o... yes... no... ok... got it... ok!");
				startQuest.add("wait 120");
				startQuest.add("The roots have spoken. You are here to deliver my pizza.");
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
				String mainIslandHint = "- A Honey Stroopwafel, I believe the Pastry Chef at the carnival has a trade for that";
				String mgnIslandHint = "- An Arcade Token...&7&o Huh? Ax? ... Alex? ... Oh, ok.&f The roots tell me a boy named \"Axel\" is the one you should look for";
				String pugmasIslandHint = "- A Present, I believe someone on the Pugmas Island can help you with that";
				String halloweenIslandHint = "- A Halloween Candy Basket, last I heard, the mansion museum was giving out candy baskets, I'd ask the Tour Guide";
				String sduIslandHint = "- A Anzac Biscuit, Rolex is the one you seek";

				List<String> reminderAll = new ArrayList<>();
				List<String> acceptQuest = new ArrayList<>();
				List<String> howToCraft = new ArrayList<>();

				howToCraft.add("When you've collected all 5 ingredients, bring them here, and when the clock strikes midnight, a lightning bolt will strike the ingredients in your inventory and the special prize will be summoned.");

				reminderAll.add("The recipe takes 5 unique items, each gathered from one of the islands:");
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
				if (user.isQuest_Main_Finish()) {
					return null;

				} else if (step == 3
						&& user.isQuest_Halloween_Finish()
						&& user.isQuest_MGN_Finish()
						&& user.isQuest_Pugmas_Finish()
						&& user.isQuest_SDU_Finish()) {
					return Collections.singletonList("Come back at dusk and when the clock strikes midnight, a lightning bolt " +
							"will strike the ingredients in your inventory and the special prize will be summoned.");

				} else if (step == 1 && !user.isQuest_Main_Start()) {
					user.setQuest_Main_Start(true);
					return acceptQuest;
				} else if (user.isQuest_Main_Start()) {
					List<String> reminder = new ArrayList<>(Collections.singleton("I see you're missing some ingredients. The items you need are:"));
					boolean sendReminder = false;

					if (step != 3) {
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

				} else {
					Tasks.wait(640, () -> {
						JsonBuilder json = new JsonBuilder("&f[&aClick to accept quest&f]").command("bearfair quests npc accept_witch").hover("Accept the Witch's quest");
						json.send(player);
					});
					Tasks.wait(85, () -> {
						World world = BearFair20.getWorld();
						Location loc = new Location(BearFair20.getWorld(), -1015.5, 136.8, -1602.5);
						for (int i = 0; i < 8; i++) {
							Tasks.wait(i * 10, () -> {
								world.spawnParticle(Particle.BLOCK, loc, 40, 0.2, 0.2, 0.2, 0.000001, Material.OAK_LOG.createBlockData());
								world.spawnParticle(Particle.HAPPY_VILLAGER, loc, 5, 0.25, 0.25, 0.25, 0.01);
								world.playSound(loc, Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1F, 0.1F);
							});
						}
					});
					return startQuest;
				}
			}
		};

		@Getter
		private final int npcId;
		@Getter
		private final List<String> script;

		@Override
		public String getName() {
			return this.name();
		}

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
		BearFair20UserService service = new BearFair20UserService();
		BearFair20User user = service.get(player);
		if (user.getQuest_Main_Step() == 0) {
			nextStep(player); // 1
			BearFairTalker.startScript(player, 2670);
			nextStep(player); // 2
		}
	}

	public static void setStep(Player player, int step) {
		new BearFair20UserService().edit(player, user -> user.setQuest_Main_Step(step));
	}

	public static void nextStep(Player player) {
		BearFair20UserService service = new BearFair20UserService();
		BearFair20User user = service.get(player);
		int step = user.getQuest_Main_Step() + 1;
		user.setQuest_Main_Step(step);
		service.save(user);
	}

	public static void witchQuestCraft() {
		Collection<Player> players = BearFair20.worldguard().getPlayersInRegion(witchDwellingRg);
		for (Player player : players) {
			if (hasAllIngredients(player)) {
				endMainQuest(player);
			}
		}
	}

	private static boolean hasAllIngredients(Player player) {
		boolean honeyStroop = false;
		boolean halloweenBasket = false;
		boolean present = false;
		boolean arcadeToken = false;
		boolean anzacBiscuit = false;
		for (ItemStack itemStack : player.getInventory()) {
			if (itemStack == null) continue;
			if (itemStack.equals(MainIsland.honeyStroopWafel))
				honeyStroop = true;
			else if (itemStack.equals(HalloweenIsland.basketItem))
				halloweenBasket = true;
			else if (itemStack.equals(PugmasIsland.presentItem))
				present = true;
			else if (itemStack.equals(SummerDownUnderIsland.anzacBiscuit))
				anzacBiscuit = true;
			else if (itemStack.equals(MinigameNightIsland.arcadeToken))
				arcadeToken = true;
		}

		return honeyStroop && halloweenBasket && present && arcadeToken && anzacBiscuit;
	}

	private static void endMainQuest(Player player) {
		player.getInventory().remove(MainIsland.honeyStroopWafel);
		player.getInventory().remove(HalloweenIsland.basketItem);
		player.getInventory().remove(PugmasIsland.presentItem);
		player.getInventory().remove(SummerDownUnderIsland.anzacBiscuit);
		player.getInventory().remove(MinigameNightIsland.arcadeToken);

		Tasks.wait(20, () -> {
			PlayerUtils.giveItem(player, specialPrize);
			player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2F, 1F);
		});

		new BearFair20UserService().edit(player, user -> user.setQuest_Main_Finish(true));
	}

	@EventHandler
	public void onPrizeOpen(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;
		if (!BearFair20.isBFItem(event.getItem())) return;
		if (!event.getItem().equals(specialPrize)) return;

		event.setCancelled(true);
		event.getItem().setAmount(event.getItem().getAmount() - 1);

		Player player = event.getPlayer();
		new BearFair20UserService().edit(player, user -> user.givePoints(500));

		PermissionChange.set().player(player).permissions("powder.powder.OrientalDiscoBathtub").runAsync();

		String prefix = "&8&l[&eBearFair&8&l] &3";
		BearFair20.send("", player);
		BearFair20.send(prefix + "You Received: ", player);
		BearFair20.send("&e-&3 &e500 &3Bear Fair Points", player);
		BearFair20.send("&e-&3 Song: &eOrientalDiscoBathtub", player);
		BearFair20.send("&e-&3 Random Reward: &e" + getRandomReward(player), player);
		BearFair20.send("", player);
		BFQuests.chime(player);
	}

	private String getRandomReward(Player player) {
		int ndx = RandomUtils.randomInt(1, 3);
		String reward;
		switch (ndx) {
			case 1 -> {
				List<String> songs = Arrays.asList("AutumnVoyage", "ForestDance", "DrunkenSailor", "Astronomia", "OwenWasHer", "Queen-BohemianRhapsody");
				String songPerm = RandomUtils.randomElement(songs);
				reward = "Song Coupon for " + songPerm;
				ItemStack songCoupon = new ItemBuilder(Material.PAPER)
						.name("Coupon For: " + songPerm)
						.lore(BFQuests.itemLore, "&f", "&3Song: &e" + songPerm, "&3Redeem this with an admin", "&3to receive your song").amount(1).build();
				PlayerUtils.giveItem(player, songCoupon);
			}
			case 2 -> {
				reward = "50 Bear Fair Points";
				new BearFair20UserService().edit(player, user -> user.givePoints(50));
			}
			default -> {
				reward = "30 Vote Points";
				new VoterService().edit(player, voter -> voter.givePoints(30));
			}
		}
		return reward;
	}

}
