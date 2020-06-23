package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.NPCClass;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.Region;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.SummerDownUnderIsland.SummerDownUnderNPCs;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;

@Region("summerdownunder")
@NPCClass(SummerDownUnderNPCs.class)
public class SummerDownUnderIsland implements Listener, Island {

	static ItemStack greatNortherns = new ItemBuilder(Material.BARREL).name("&aGreat Northerns").amount(1).lore(itemLore).build();
	static ItemStack foolsGold = new ItemBuilder(Material.GOLD_NUGGET).name("&6Fools Gold").lore(itemLore).amount(1).build();
	static ItemStack anzacBiscuit = new ItemBuilder(Material.COOKIE).name("Anzac Biscuit").lore(itemLore).amount(1).build();
	//
	static ItemStack goldenSyrup = new ItemBuilder(Material.HONEY_BOTTLE).name("Golden Syrup").lore(itemLore).amount(1).build();
	static ItemStack peanuts = new ItemBuilder(Material.BEETROOT_SEEDS).name("Peanuts").lore(itemLore).amount(1).build();
	static ItemStack wheat = new ItemBuilder(Material.WHEAT).lore(itemLore).amount(1).build();
	static ItemStack sugar = new ItemBuilder(Material.SUGAR).lore(itemLore).amount(1).build();

	// TODO:
	//	- gold sifting

	public SummerDownUnderIsland() {
		BNCore.registerListener(this);
	}

	public enum SummerDownUnderNPCs implements TalkingNPC {
		// Quest NPCs
		ROLEX(2907) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_SDU_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Hey bro welcome to Queen’s Island!");
				startQuest.add("<self> Thanks, Friend! I was hoping you could tell me about this thing called an ANZAC Biscuit?");
				startQuest.add("So you’re looking for information about the ANZAC Biscuit hey? " +
						"Don’t know too much about it bro but you should hit up the ANZAC Statue in the centre of town. " +
						"There’s bound to be someone there you can talk to!");
				startQuest.add("Oh and don’t forget to say G’day to everyone around town.");

				if (!user.isQuest_Main_Start())
					return Collections.singletonList("TODO: GENERIC GREETING");

				if (step >= 1)
					return Collections.singletonList("TODO: REMINDER");

				user.setQuest_SDU_Start(true);
				nextStep(user); // 1
				return startQuest;
			}
		},
		SIR_JACK(2908) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_SDU_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Ah, you must be the one Rolex messaged me about. What can an old man such as myself do for ya?");
				startQuest.add("<self> I was hoping you could tell me how to get whatever an ANZAC Biscuit is?");
				startQuest.add("Well mate, it’s a biscuit the blokes and I used to get in the trenches back in the First World War. " +
						"It was sent all the bloody way from Australia to the Western Front. " +
						"It used to be a pretty common commodity in these parts and a damn good bikkie if I don’t say so myself.");
				startQuest.add("Unfortunately we don’t have any on hand, outta supply for months! " +
						"But I can tell ya what you’re gonna need to make one yourself.");
				startQuest.add("You’ll need the following ingredients; Wheat, Sugar, Peanuts & Golden Syrup.");
				startQuest.add("I highly recommend you visit the Queen’s Island Pie Shop once you have what you need, I’m sure they’d be happy to help.");
				startQuest.add("<self> Thank you for your service. Any ideas where I should start?");
				startQuest.add("There’ll be a cheeky farm across the bridge you can have a gawk at. " +
						"Afterwards, I’d recommend talking to my grandson Lachlan at the Pub. " +
						"He should be able to sort you out with something.");

				if (!user.isQuest_SDU_Start() || step < 1)
					return Collections.singletonList("TODO: GENERIC GREETING");

				if (step >= 2)
					return Collections.singletonList("TODO: REMINDER");

				nextStep(user); // 2
				return startQuest;
			}
		},
		FARMER(2752, new ArrayList<String>() {{
			add("G’day g’day, the crops are fresh and lookin’ real mean");
			add("Oh you need it for the ANZAC Biscuit? Crikey, we haven’t had those around here for months! " +
					"Take all the wheat you need chief, it’s for a bloody great cause.");
			add("You’ll need Sugar too. I’m pretty sure the Main Island has some sugar cane you could abscond with mate. Good luck!");
		}}),
		LACHLAN(2747) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_SDU_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Hey mate, my grandad told me you were headed here. Look, here’s the situation. " +
						"I’d love to help out; but I need you to do me a favour first.");
				startQuest.add("Somehow, my delivery is missing a whole case of Great Northerns. One that I need to sell at the " +
						"gatho tonight with the town. I suspect it’s been stolen. If you could find it for me and bring it back, " +
						"I’ll give ya all the peanuts you need.");
				startQuest.add("Good luck mate! Happy hunting.");

				List<String> endQuest = new ArrayList<>();
				endQuest.add("No way! You’re an absolute legend mate. When the gatho starts, we’d love to have you over. " +
						"Once the boys find out you rescued the bevvies they’ll love ya!");
				endQuest.add("Here’s the Peanuts, they’re pretty high quality stuff. I can’t just give you anything for that ANZAC Biscuit, hey?");
				endQuest.add("<self> What about the Golden Syrup?");
				endQuest.add("Yeah nah unfortunately I don’t have any on hand, but if you knock on some doors around town there’ll " +
						"definitely be some available. Thanks again mate.");

				if (!user.isQuest_SDU_Start() || step < 2)
					return Collections.singletonList("TODO: GENERIC GREETING");

				if (player.getInventory().contains(greatNortherns) && step == 3) {
					player.getInventory().remove(greatNortherns);
					nextStep(user); // 4
					return endQuest;
				}

				if (step >= 4)
					return Collections.singletonList("TODO: REMINDER 4");

				if (step >= 3)
					return Collections.singletonList("TODO: REMINDER 3");

				nextStep(user); // 3
				return startQuest;
			}
		},
		ROBERT_MENZIES(2911) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_SDU_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Hello mate. Golden Syrup hey? Hmm well. I think I should have some for you.");
				startQuest.add("Aha! Here it is. Have this mate. Fresh from a place called Landfall. Not really sure where that is on " +
						"Bear Nation, but their syrup is to die for!");

				if (!user.isQuest_SDU_Start() || step < 4)
					return Collections.singletonList("TODO: GENERIC GREETING");

				if (step >= 5)
					return Collections.singletonList("TODO: REMINDER");

				nextStep(user); // 5
				Tasks.wait(Time.SECOND.x(2), () -> Utils.giveItem(player, goldenSyrup));
				return startQuest;
			}
		},
		BRI(2744) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_SDU_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Hi! What will it be for today?");
				startQuest.add("<self> Uh, hey! I was wondering if you guys could bake these ANZAC Biscuits for me? I have all the ingredients " +
						"but I can’t make it myself. I’m new here!");
				startQuest.add("If it were up to me, sure! But you’ll need to speak with my Manager first. He’s really picky about these things.");

				List<String> endQuest = new ArrayList<>();
				endQuest.add("<self> Here’s the gold your manager wanted. Can you bake it now please?");
				endQuest.add("I can see you have Fool’s Gold there, honestly, just give me that. He won’t notice!");
				endQuest.add("Thanks! Alright now lets see what we can do...");
				endQuest.add("Here’s your Biscuit! Like we said earlier, I’ll take the rest and give them out around town! Best of luck in " +
						"your future endeavours. Feel free to stick around town for a while. We love visitors!");

				if (!user.isQuest_SDU_Start() || step < 5 || user.isQuest_SDU_Finish())
					return Collections.singletonList("TODO: GENERIC GREETING");


				ItemStack gold = hasFoolsGold(player);
				if (step == 8 && gold != null && hasAnzacIngredients(player)) {
					gold.setAmount(gold.getAmount() - 1);
					removeAnzacIngredients(player);
					//
					Utils.giveItem(player, anzacBiscuit);
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
					//
					user.setQuest_SDU_Finish(true);
					nextStep(user); // 9
					return endQuest;
				}

				Utils.giveItems(player, Arrays.asList(peanuts, goldenSyrup, wheat, sugar, foolsGold));

				if (step >= 8)
					return Collections.singletonList("TODO: REMINDER 8");

				if (step >= 6)
					return Collections.singletonList("TODO: REMINDER 6");

				nextStep(user); // 6
				return startQuest;
			}
		},
		MANAGER(2745) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_SDU_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("What? Who are you, what do you want?");
				startQuest.add("<self> I was wondering if you could bake these ANZAC Biscuits for me? I just need one to take back with me and " +
						"Queen’s Island can keep the rest!");
				startQuest.add("Excuse me? You’ll need to pay for that mate! No pay, no bake.");
				startQuest.add("If you don’t have any gold, there’s a whole mineshaft nearby. " +
						"They usually mine coal but recently they’ve been sifting for gold.");

				if (!user.isQuest_SDU_Start() || step < 6)
					return Collections.singletonList("TODO: GENERIC GREETING");

				if (step >= 7)
					return Collections.singletonList("TODO: REMINDER");

				nextStep(user); // 7
				return startQuest;
			}
		},
		PROSPECTOR(2753) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_SDU_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Xin Chao friend! You want to sift?");
				startQuest.add("Tuyet Dieu! Here, take the bowl and get to work in the water. I need one gold for you to sift here so chop chop! " +
						"You can keep any other gold or fool’s gold you find.");

				if (!user.isQuest_SDU_Start() || step < 7)
					return Collections.singletonList("TODO: GENERIC GREETING");

				if (step >= 8)
					return Collections.singletonList("TODO: REMINDER");

				nextStep(user); // 8
				return startQuest;
			}
		},
		//
		// Clutter NPCs
		DYLAN(2915, Collections.singletonList("These snags are lookin’ real hot boys hope you’re ready")),
		MATT(2916, Collections.singletonList("OOOOOOH BOOOY IM READY FOR SOME SNAAAAAAAAAAGSSSS")),
		MAX(2917, Collections.singletonList("Alright gents, if the snags are ready let’s get Lach and the boys from the pub here ASAP!")),
		TALITHA(2933, Collections.singletonList("I’ll get the plates!")),
		DECLAN(2922, Collections.singletonList("Oiiii Lachlan ch..uck as anofer beer ya dawwgg!")),
		CAMERON(2921, Collections.singletonList("Shut Up Declan we need to meet the boys at the Ablett’s!")),
		JOSH(2918, Collections.singletonList("Uh, yeah, I’m the deso tonight so just some Soft Drink if you got any mate.")),
		NIKKI(2944, Collections.singletonList("This view is amazing.")),
		NICOLE(2945, Collections.singletonList("I know right… I love farming.")),
		GRIFFIN(2931, Collections.singletonList("Lest we Forget.")),
		TRINITY(2932, Collections.singletonList("We Will Remember Them.")),
		RYAN(2923, Collections.singletonList("Hey mate, wanna have a go in the ‘Lux?")),
		FOREMAN(2927, Collections.singletonList("Who the bloody hell are ya? I have work to do mate, get lost!")),
		DRIVER(2930, Collections.singletonList("Oh man… Lachlan’s gonna kill me. Where is that damn case?")),
		TALISHA(2939, Collections.singletonList("Can you have a squiz at the drinks and suggest anything good? I’m new in town!")),
		TAYLOR(2940, Collections.singletonList("Hey! You must be new here, hope you’re having a ball!")),
		LUCY(2941, Collections.singletonList("We should probably get going soon, I think the party’s almost started. Wanna tag along?")),
		CHRIS(2942, Collections.singletonList("MmmmMmm! Just as good as I remember. Hope I’m not late for the gatho!"));

		@Getter
		private final int npcId;
		@Getter
		private final List<String> script;

		SummerDownUnderNPCs(int npcId) {
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}

		SummerDownUnderNPCs(int npcId, List<String> script) {
			this.npcId = npcId;
			this.script = script;
		}
	}

	private static void nextStep(BearFairUser user) {
		int step = user.getQuest_SDU_Step() + 1;
		user.setQuest_SDU_Step(step);
	}

	private static ItemStack hasFoolsGold(Player player) {
		ItemStack[] contents = player.getInventory().getContents();
		for (ItemStack content : contents) {
			if (Utils.isNullOrAir(content)) continue;
			if (!BearFair20.isBFItem(content)) continue;

			if (!content.getType().equals(Material.GOLD_NUGGET)) continue;

			String foolsGoldName = StringUtils.stripColor(foolsGold.getItemMeta().getDisplayName());
			String contentName = StringUtils.stripColor(content.getItemMeta().getDisplayName());
			if (contentName.equals(foolsGoldName)) {
				return content;
			}
		}

		return null;
	}

	private static boolean hasAnzacIngredients(Player player) {
		boolean hasWheat = player.getInventory().containsAtLeast(wheat, 1);
		boolean hasSugar = player.getInventory().containsAtLeast(sugar, 1);
		boolean hasPeanuts = player.getInventory().containsAtLeast(peanuts, 1);
		boolean hasSyrup = player.getInventory().containsAtLeast(goldenSyrup, 1);

		return hasWheat && hasSugar && hasPeanuts && hasSyrup;
	}

	private static void removeAnzacIngredients(Player player) {
		boolean wheatBool = true;
		boolean sugarBool = true;
		boolean peanutsBool = true;
		boolean syrupBool = true;
		for (ItemStack content : player.getInventory().getContents()) {
			if (Utils.isNullOrAir(content)) continue;

			ItemStack item = content.clone();
			item.setAmount(1);
			if (item.equals(wheat) && wheatBool) {
				content.setAmount(content.getAmount() - 1);
				wheatBool = false;
			}

			if (item.equals(sugar) && sugarBool) {
				content.setAmount(content.getAmount() - 1);
				sugarBool = false;
			}

			if (item.equals(peanuts) && peanutsBool) {
				content.setAmount(content.getAmount() - 1);
				peanutsBool = false;
			}

			if (item.equals(goldenSyrup) && syrupBool) {
				content.setAmount(content.getAmount() - 1);
				syrupBool = false;
			}
		}
	}

	@EventHandler
	public void onClickBarrel(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		ProtectedRegion region = WGUtils.getProtectedRegion(getRegion());
		if (!WGUtils.getRegionsAt(event.getPlayer().getLocation()).contains(region)) return;

		Block clicked = event.getClickedBlock();
		if (clicked == null) return;

		Material material = clicked.getType();
		if (!material.equals(Material.BARREL)) return;

		Material under = clicked.getRelative(0, -1, 0).getType();
		if (!under.equals(Material.EMERALD_BLOCK)) return;

		Player player = event.getPlayer();
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		int step = user.getQuest_SDU_Step();

		if (step == 3 && !player.getInventory().contains(greatNortherns)) {
			Utils.giveItem(player, greatNortherns);
			player.playSound(clicked.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
		}
		event.setCancelled(true);
	}

}
