package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.NPCClass;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.Region;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.MainIsland.MainNPCs;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;

@Region("main")
@NPCClass(MainNPCs.class)
public class MainIsland implements Listener, Island {

	public static ItemStack honeyStroopWafel = new ItemBuilder(Material.COOKIE).lore(itemLore).name("Honey Stroopwafel").amount(1).glow().build();
	public static ItemStack stroofWafel = new ItemBuilder(Material.COOKIE).lore(itemLore).name("Stoopwafel").amount(1).build();
	public static ItemStack blessedHoneyBottle = new ItemBuilder(Material.HONEY_BOTTLE).lore(itemLore).name("Blessed Bottle Of Honey").amount(1).build();
	public static ItemStack unpurifiedMarble = new ItemBuilder(Material.DIORITE).lore(itemLore).name("Unpurified Marble").amount(1).build();
	public static ItemStack relic_arms = new ItemBuilder(Material.BLAZE_ROD).lore(itemLore).name("Ancient Relic Arms").amount(2).build();
	public static ItemStack relic_base = new ItemBuilder(Material.LIGHT_WEIGHTED_PRESSURE_PLATE).lore(itemLore).name("Ancient Relic Base").amount(1).build();
	public static ItemStack relic_body = new ItemBuilder(Material.GOLDEN_HORSE_ARMOR).lore(itemLore).name("Ancient Relic Body").amount(1).build();
	public static ItemStack relic_eyes = new ItemBuilder(Material.EMERALD).lore(itemLore).name("Ancient Relic Eyes").amount(2).build();
	public static ItemStack relic = new ItemBuilder(Material.TOTEM_OF_UNDYING).lore(itemLore).name("Ancient Relic").amount(1).build();
	public static ItemStack ancientPickaxe = new ItemBuilder(Material.STONE_PICKAXE).lore(itemLore).name("Ancient Pickaxe").amount(1).build();
	public static ItemStack rareFlower = new ItemBuilder(Material.BLUE_ORCHID).lore(itemLore).name("Rare Flower").amount(1).build();
	private static String witchDwellingRg = "bearfair2020_witchdwelling";
	private static Location specialPrizeLoc = new Location(BearFair20.getWorld(), -1016, 120, -1605);
	private static ItemStack specialPrize;

	public MainIsland() {
		BNCore.registerListener(this);

		List<ItemStack> drops = new ArrayList<>(specialPrizeLoc.getBlock().getDrops());
		specialPrize = new ItemBuilder(drops.get(0)).clone().lore(itemLore, "&f", "RClick while holding to open").name("Special Prize").glow().build();
	}

	public enum MainNPCs implements TalkingNPC {
		Miner(2743) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
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
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
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
				if (user.isQuest_Main_Finish())
					return null;

				else if (step == 1 && !user.isQuest_Main_Start()) {
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
						JsonBuilder json = new JsonBuilder("&f[&aClick to accept quest&f]").command("bearfair quests accept_witch").hover("Accept the Witch's quest");
						json.send(player);
					});
					Tasks.wait(85, () -> {
						World world = BearFair20.getWorld();
						Location loc = new Location(BearFair20.getWorld(), -1015.5, 136.8, -1602.5);
						for (int i = 0; i < 8; i++) {
							Tasks.wait(i * 10, () -> {
								world.spawnParticle(Particle.BLOCK_CRACK, loc, 40, 0.2, 0.2, 0.2, 0.000001, Material.OAK_LOG.createBlockData());
								world.spawnParticle(Particle.VILLAGER_HAPPY, loc, 5, 0.25, 0.25, 0.25, 0.01);
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
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		if (user.getQuest_Main_Step() == 0) {
			nextStep(player); // 1
			Talkers.startScript(player, 2670);
			nextStep(player); // 2
		}
	}

	private static void nextStep(Player player) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		int step = user.getQuest_Main_Step() + 1;
		user.setQuest_Main_Step(step);
		service.save(user);
	}

	public static void witchQuestCraft() {
		Collection<Player> players = WGUtils.getPlayersInRegion(witchDwellingRg);
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
			Utils.giveItem(player, specialPrize);
			player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2F, 1F);
		});

		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		user.setQuest_Main_Finish(true);
		service.save(user);
	}

}
