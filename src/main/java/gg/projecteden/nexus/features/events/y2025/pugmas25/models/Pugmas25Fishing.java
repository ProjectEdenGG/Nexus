package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.operator.WeatherCommand.FixedWeatherType;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootTime;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.FishingLoot;
import gg.projecteden.nexus.features.events.models.PlayerEventFishingBiteEvent;
import gg.projecteden.nexus.features.events.models.PlayerEventFishingCaughtFishEvent;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25BiomeDistrict;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Sidebar.Pugmas25SidebarLine;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class Pugmas25Fishing implements Listener {

	private static final Pugmas25UserService userService = new Pugmas25UserService();
	private static final ItemModelType rodWooden = Pugmas25QuestItem.FISHING_ROD_WOOD.getItemModel();
	private static final ItemModelType rodReinforced = Pugmas25QuestItem.FISHING_ROD_REINFORCED.getItemModel();
	private static final ItemModelType rodGolden = Pugmas25QuestItem.FISHING_ROD_GOLDEN.getItemModel();
	private static final ItemModelType fishFinder = Pugmas25QuestItem.FISH_FINDER.getItemModel();
	private static final ItemModelType pda = Pugmas25QuestItem.PDA.getItemModel();
	private static final String anglerHat = Pugmas25QuestItem.ANGLER_HAT.getItemBuilder().model();
	private static final String anglerVest = Pugmas25QuestItem.ANGLER_VEST.getItemBuilder().model();
	private static final String anglerPants = Pugmas25QuestItem.ANGLER_PANTS.getItemBuilder().model();

	public Pugmas25Fishing() {
		Nexus.registerListener(this);
	}

	public static void giveRewards(Dialog dialog, Pugmas25User user, Quester quester) {
		// Generic reward
		dialog.npc("Thank you so much, here’s your reward!");
		new EventUserService().edit(quester, eventUser -> eventUser.giveTokens(5));

		int timesCompleted = user.getCompletedAnglerQuests();

		// Nth reward
		if (timesCompleted == 3) {
			dialog.npc("Have this fancy Gold Watch! Now you won’t have to squint at the sky and guess whether it’s day or night!");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.GOLD_WATCH.get());
		} else if (timesCompleted == 6) {
			dialog.npc("Take this Angler Hat, it'll help the fish like you more! ...Probably.");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.ANGLER_HAT.get());
		} else if (timesCompleted == 12) {
			dialog.npc("Here’s an Angler Vest to match your hat! Now you’re really starting to look like a master angler!");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.ANGLER_VEST.get());
		} else if (timesCompleted == 18) {
			dialog.npc("You're really committed! Here, have these Angler Pants! Now you’ve got the full Angler look!");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.ANGLER_PANTS.get());
		} else if (timesCompleted == 26) {
			dialog.npc("You’ve earned something special! This Reinforced Rod adds more luck AND lets you reel in Iron Crates!");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.FISHING_ROD_REINFORCED.get());
		} else if (timesCompleted == 38) {
			dialog.npc("You’re a legendary angler! Take this Golden Fishing Rod, it's super lucky, super shiny, AND it pulls up Diamond Crates!");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.FISHING_ROD_GOLDEN.get());
		}

		// Tool chance reward or nth
		tryGiveFishingTool(dialog, quester, timesCompleted, 8, Pugmas25SidebarLine.FISHING_LUCK); // FISHING_POCKET_GUIDE
		tryGiveFishingTool(dialog, quester, timesCompleted, 15, Pugmas25SidebarLine.HEIGHT); // SEXTANT
		tryGiveFishingTool(dialog, quester, timesCompleted, 22, Pugmas25SidebarLine.WEATHER); // WEATHER_RADIO
	}

	private static void tryGiveFishingTool(Dialog dialog, Quester quester, int timesCompleted, int guaranteed, Pugmas25SidebarLine sidebarLine) {
		if (sidebarLine.canRender(quester.getPlayer()))
			return;

		if (timesCompleted == guaranteed || RandomUtils.chanceOf(5)) {
			dialog.npc("Also, take this too! It should make fishing a little easier!");
			PlayerUtils.giveItem(quester, sidebarLine.getSpecificItem().get());
		}
	}

	public enum Pugmas25AnglerLoot {
		WIGGLY_STICK(Pugmas25District.RIVER, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_ELECTRIC_EEL, List.of(
			"You ever seen a fish that wiggles like a crazed twig? Go to the river and bring me one. I need it for... reasons!",
			"There’s this twitchy thing in the river, and I bet you can catch it. Mostly because I don’t want to touch it."
		)),
		DRIFTWOOD_CARP(Pugmas25District.RIVER, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_FOURHORN_SCULPIN, List.of(
			"I found a carp pretending to be driftwood in the river, but it only floats around when the sun's out. Go fetch it before someone turns it into firewood!",
			"At the river, during daytime, there's a 'log' that mysteriously swims. I need you to confirm I’m not losing my mind."
		)),
		CURRENTCLAW(Pugmas25District.RIVER, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_LINGCOD, List.of(
			"Something in the river keeps pinching my toes at night! Find whatever it is and bring it back so I can yell at it!",
			"The river gets handsy when it’s dark. I’m sending you instead of risking my toes again."
		)),
		PEBBLE_CHEWER_BASS(Pugmas25District.LAKE, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_SMALLMOUTH_BASS, List.of(
			"Something at the lake keeps crunching rocks like they're snacks. Go reel it in before it eats the entire shoreline.",
			"There’s something at the lake eating rocks. ROCKS. Bring me whatever’s doing it."
		)),
		GRASS_PICKEREL(Pugmas25District.LAKE, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_GRASS_PICKEREL, List.of(
			"The lake grass keeps moving suspiciously while the sun’s up. Check it out. Bring me the culprit.",
			"Something is lurking in the lake weeds when it's daylight. I need it for... research. Definitely research."
		)),
		MOONFIN(Pugmas25District.LAKE, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_MOTHER_MOON, List.of(
			"Something keeps shimmering on the lake after dark like it’s in a romance novel. Bring it.",
			"The lake has a sparkly night critter floating around. Fetch it before it stops being pretty."
		)),
		PENGFISH(Pugmas25District.FROZEN_LAKE, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_PENGFISH, List.of(
			"I swear something waddled past me on the frozen lake. It waddled away before I could pet it and I'm STILL mad.",
			"I saw something part-bird, part-fish, and all attitude, squawking at me from the frozen lake. Fetch it before it decides to migrate somewhere warmer!"
		)),
		SHIVERBACK_SALMON(Pugmas25District.FROZEN_LAKE, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_IRONFISH, List.of(
			"I spotted a fish in the frozen lake that’s shivering so hard I'm surprised it hasn’t burrowed a hole through the ice. Get it for me!",
			"There’s a blue swimmer at the frozen lake that keeps quivering like it’s cold. It lives in water. In the cold. What did it expect? Go catch it."
		)),
		FROSTBITE_MINNOW(Pugmas25District.FROZEN_LAKE, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_FROZEN_BONEMINNOW, List.of(
			"I heard tiny evil minnows come out on the frozen lake at night. Bring me one! Just thaw your fingers before handing it over.",
			"HINT2" // TODO
		)),
		SCALDING_SNAPPER(Pugmas25District.HOT_SPRINGS, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_RED_SNAPPER, List.of(
			"There's a creature bubbling in the hot springs that looks like it could boil someone alive. Bring it back, carefully. Or don't. I'm not responsible.",
			"Something in the hot springs looks like it wants to cook YOU. Catch it before it succeeds."
		)),
		THERMO_TROUT(Pugmas25District.HOT_SPRINGS, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_RAINBOW_TROUT, List.of(
			"A smug little creature is basking in the hot springs during the day. Ruin its spa session and bring it here.",
			"HINT2" // TODO
		)),
		OBSTER(Pugmas25District.HOT_SPRINGS, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_OBSTER, List.of(
			"Last time I went to the hot springs at night, something clicked at me from under the water. Bring me one so I know what was threatening me!",
			"I tried to grab something scuttling in the hot springs last night, but all I got was a burnt hand. Your turn!"
		)),
		CLOWNFISH(Pugmas25District.FAIRGROUNDS, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_TROPICAL_FISH, List.of(
			"There's a creature at the fairgrounds that keeps making honking noises at passersby. Bring it to me. I need to honk back.",
			"Something splashed me at the fairgrounds and then laughed. Fetch the comedian"
		)),
		COTTON_CANDY_GOLDFISH(Pugmas25District.FAIRGROUNDS, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_COTTON_CANDY_GOLDFISH, List.of(
			"Something fluffy-looking is bobbing around the fairgrounds ponds during the day. Fetch it before someone mistakes it for actual cotton candy.",
			"Something sweet-looking is drifting around at the fairgrounds while it's daylight. Bring me the snack imposter."
		)),
		TRICKSTER_KOI(Pugmas25District.FAIRGROUNDS, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_KOI, List.of(
			"That sneaky koi at the fairgrounds keeps pulling pranks once night falls. Go catch it so it stops tying knots in my fishing line!",
			"The fairgrounds get way too mischievous at night, something keeps pulling pranks on fishermen. Bring me the culprit!"
		)),
		SPLINTERTAIL(Pugmas25District.SAWMILL, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_SABLEFISH, List.of(
			"There’s something in the lumberyard waters that stabbed me with a SPLINTER. A SPLINTER! Go catch it so I can file a formal complaint.",
			"I found something swimming in the lumberyard runoff that’s sharper than the lumber. Go snag it before someone loses a finger!"
		)),
		SAWTOOTH_CARP(Pugmas25District.SAWMILL, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_SAWFISH, List.of(
			"Something in the sawmill waters during the day keeps chewing on woodchips like they're snacks. Bring me the sawdust junkie.",
			"I heard gnawing in the sawmill water while the sun was up. Bring me the beaver impersonator."
		)),
		WOODWORM_EEL(Pugmas25District.SAWMILL, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_EEL, List.of(
			"HINT1", // TODO
			"HINT2" // TODO
		)),
		ROCKTOPUS(Pugmas25District.CAVES, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_OCTOPUS, List.of(
			"Down in the caves, something with tentacles is pretending to be a boulder in the water. Go catch it before it learns how to roll.",
			"If you see an unusually lumpy ‘rock’ in the cave lakes, don’t kick it. Catch it and bring it to me!"
		)),
		CAVESHROOM(Pugmas25District.CAVES, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_SEASHROOM_RAINBOW, List.of(
			"Something jelly-like is pulsing rainbow colors in the cave water. I need it so I can use it as a nightlight!",
			"In the caves, during the day, something keeps floating around changing colors like it’s showing off. Bring it to me!"
		)),
		SKULLFIN(Pugmas25District.CAVES, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_SKULLFIN, List.of(
			"There’s a rattling noise in the cave pools during the day, and it’s not bones falling, it’s something swimming. Bring it before it rattles apart!",
			"There’s a bony weirdo swimming in broad daylight down in the caves. Haul it up before it tries to bite someone!"
		)),
		VAMPIRE_SQUID(Pugmas25District.CAVES, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_VAMPIRE_SQUID, List.of(
			"The caves get spooky at night, and something keeps swooshing behind me like it wants my neck. Go find it. Preferably without getting bitten.",
			"Just saw a squid creeping around the water in the caves at night. Pretty sure it hissed at me. Go grab it."
		)),
		ZOMBIEFISH(Pugmas25District.CAVES, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_ZOMBIEFISH, List.of(
			"After dark, something gross and half-rotted drags itself through the cave puddles. Go catch it before it decomposes.",
			"HINT2" // TODO
		)),
		ECHO_EEL(Pugmas25District.CAVES, Pugmas25BiomeDistrict.DRIPSTONE_CAVES, -44, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_LAMPREY, List.of(
			"The deep dripstone caves keep repeating everything I say. Something in there must be causing it. Bring it here so I can shush it personally.",
			"The deep dripstone caves echo weirdly. Something’s causing it. Bring it."
		)), // TODO: MAX Y VALUE
		SIPHONFIN(Pugmas25District.CAVES, Pugmas25BiomeDistrict.DRIPSTONE_CAVES, -44, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_LEECH, List.of(
			"There’s a creepy sucker hiding in the deep dripstone caves at night. Grab it before it grabs you!",
			"Something in the deep dripstone caves keeps drinking all the puddles at night. Bring the thirsty gremlin."
		)),
		SLIMEFISH(Pugmas25District.CAVES, Pugmas25BiomeDistrict.LUSH_CAVES, -25, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_SLIMEFISH, List.of(
			"I touched something in the lush caves and instantly regretted it. Fetch the goo.",
			"The lush caves have something squishy swimming around that tried to glue my feet together. Bring it before it sticks someone permanently."
		)),
		PIRANHA(Pugmas25District.CAVES, Pugmas25BiomeDistrict.LUSH_CAVES, -25, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_PIRANHA, List.of(
			"In the lush caves at night, something's biting anything that touches the water. Bring it here, preferably without losing fingers.",
			"The lush caves water gets full of hungry little monsters at night. Get one."
		)),
		;

		private final String modelId;
		@Getter
		private final String customName;
		private final EventFishingLootTime time;
		@Getter
		private final Pugmas25District district;
		;
		private Pugmas25BiomeDistrict biomeDistrict = null;
		private Integer maxY = null;
		private final List<String> hint;
		private final FishingLoot loot;

		Pugmas25AnglerLoot(Pugmas25District district, EventFishingLootTime time, ItemModelType model, List<String> hint) {
			this(district, (player) -> Pugmas25District.of(player.getLocation()) == district, model, time, hint);
		}

		Pugmas25AnglerLoot(Pugmas25District district, Pugmas25BiomeDistrict biomeDistrict, int maxY, EventFishingLootTime time, ItemModelType model, List<String> hint) {
			this(district, (player) ->
				Pugmas25.getPlayerWorldHeight(player) <= maxY && Pugmas25Districts.of(player) == district && Pugmas25BiomeDistrict.of(player) == biomeDistrict, model, time, hint);
			this.biomeDistrict = biomeDistrict;
			this.maxY = maxY;
			;
		}

		Pugmas25AnglerLoot(Pugmas25District district, Predicate<Player> predicate, ItemModelType model, EventFishingLootTime time, List<String> hint) {
			this.modelId = model.getModel();
			this.customName = StringUtils.camelCase(this);
			this.district = district;
			this.hint = hint;
			this.time = time;
			//
			this.loot = new FishingLoot(name(), EventFishingLootCategory.SPECIAL, null, Material.LEATHER_HORSE_ARMOR, modelId, 5,
				customName, "&7Quest Fish", time, null, predicate);
		}

		public ItemStack getItem() {
			return loot.getItem();
		}

		public String getRandomHint() {
			return RandomUtils.randomElement(hint);
		}

		public List<String> getExtraHelp() {
			return new ArrayList<>() {{
				add("&3Name: &e" + customName);
				add("&3Area: &e" + district.getName());
				if (biomeDistrict != null)
					add("&3Biome: &e" + biomeDistrict.getName());
				if (maxY != null)
					add("&3Max Height: &e" + maxY);

				String timeName = StringUtils.camelCase(time);
				if (time == EventFishingLootTime.BOTH)
					timeName = "Any";

				add("&3When: &e" + timeName + "time");
			}};
		}

		public boolean matches(ItemStack itemStack) {
			return new ItemBuilder(itemStack).model().equalsIgnoreCase(modelId);
		}
	}

	public static void getAnglerReaction(Dialog dialog, Pugmas25AnglerLoot questFish) {
		if (RandomUtils.chanceOf(50)) {
			dialog
				.npc("You did?! Let me see!")
				.npc("Wow! A " + questFish.getCustomName() + "!");
		} else {
			dialog
				.npc("NO WAY—YOU REALLY CAUGHT IT?!")
				.npc("That’s totally the " + questFish.getCustomName() + "!");
		}
	}

	public static int getLuck(Player player) {
		int luck = 0;

		// Weather
		if (FixedWeatherType.of(player.getWorld()) != FixedWeatherType.CLEAR)
			luck += 5;

		// Armor
		ItemStack helmet = player.getInventory().getHelmet();
		if (Nullables.isNotNullOrAir(helmet) && new ItemBuilder(helmet).model().equalsIgnoreCase(anglerHat))
			luck += 4;

		ItemStack chestplate = player.getInventory().getChestplate();
		if (Nullables.isNotNullOrAir(chestplate) && new ItemBuilder(helmet).model().equalsIgnoreCase(anglerVest))
			luck += 4;

		ItemStack leggings = player.getInventory().getLeggings();
		if (Nullables.isNotNullOrAir(leggings) && new ItemBuilder(helmet).model().equalsIgnoreCase(anglerPants))
			luck += 4;

		// Tools - In hand
		ItemStack tool = ItemUtils.getTool(player);
		if (Nullables.isNotNullOrAir(tool)) {
			ItemModelType toolModel = ItemModelType.of(tool);
			if (toolModel != null) {
				if (rodReinforced == toolModel)
					luck += 5;
				else if (rodGolden == toolModel)
					luck += 15;
			}
		}

		// Tools - In inventory
		boolean hasFishFinder = false;
		for (ItemStack content : player.getInventory().getContents()) {
			if (isNullOrAir(content))
				continue;

			ItemModelType itemModel = ItemModelType.of(content);
			if (!hasFishFinder) {
				if (fishFinder == itemModel || pda == itemModel) {
					hasFishFinder = true;
					luck += 5;
				}
			}
		}

		return luck;
	}

	@EventHandler
	public void onFishBite(PlayerEventFishingBiteEvent event) {
		Player player = event.getPlayer();

		if (!Pugmas25.get().shouldHandle(player))
			return;

		Pugmas25User user = userService.get(player);
		int luck = Pugmas25Fishing.getLuck(player);

		int anglerLuck = Math.max(luck, 5);
		if (user.isCaughtAnglerQuestLoot())
			anglerLuck = (int) Math.ceil(anglerLuck / 3.0);

		Pugmas25AnglerLoot anglerLoot = Pugmas25Config.get().getAnglerQuestFish();
		if (anglerLoot != null && !anglerLoot.loot.applies(player))
			anglerLoot = null;

		List<ItemStack> resultLoot = new ArrayList<>();
		for (ItemStack itemStack : event.getLoot()) {
			// Replace loot with angler quest fish
			if (anglerLoot != null && RandomUtils.chanceOf(anglerLuck)) {
				resultLoot.add(anglerLoot.getItem());
				continue;
			}

			// Replace loot with treasure
			if (RandomUtils.chanceOf(luck)) {
				ItemStack treasureLoot = getFishingTreasure(player, luck);
				resultLoot.add(treasureLoot);
				continue;
			}

			resultLoot.add(itemStack);
		}

		event.setLoot(resultLoot);
	}

	private @NonNull ItemStack getFishingTreasure(Player player, int luck) {
		List<ItemStack> possibleTreasure = new ArrayList<>();

		if (Pugmas25QuestItem.LUCKY_HORSESHOE.isInInventoryOf(player))
			luck += 10;
		else
			possibleTreasure.add(Pugmas25QuestItem.LUCKY_HORSESHOE.get());

		// Trunks
		ItemStack tool = ItemUtils.getTool(player);
		ItemModelType toolModel = ItemModelType.of(tool);
		if (Nullables.isNotNullOrAir(tool) && toolModel != null) {
			if (rodWooden == toolModel)
				possibleTreasure.add(Pugmas25QuestItem.TRUNK_IRON.get());
			else if (rodReinforced == toolModel)
				possibleTreasure.add(Pugmas25QuestItem.TRUNK_GOLD.get());
			else if (rodGolden == toolModel)
				possibleTreasure.add(Pugmas25QuestItem.TRUNK_DIAMOND.get());
		}

		// Ingots
		possibleTreasure.add(new ItemBuilder(Material.GOLD_NUGGET).amount(Pugmas25.getLuckyAmount(4, 10, luck)).build());
		possibleTreasure.add(new ItemBuilder(Material.GOLD_INGOT).amount(Pugmas25.getLuckyAmount(2, 8, luck)).build());
		possibleTreasure.add(new ItemBuilder(Material.IRON_NUGGET).amount(Pugmas25.getLuckyAmount(3, 7, luck)).build());
		possibleTreasure.add(new ItemBuilder(Material.IRON_INGOT).amount(Pugmas25.getLuckyAmount(2, 6, luck)).build());
		possibleTreasure.add(new ItemBuilder(Material.DIAMOND).amount(Pugmas25.getLuckyAmount(1, 6, luck)).build());
		possibleTreasure.add(new ItemBuilder(Material.EMERALD).amount(Pugmas25.getLuckyAmount(3, 8, luck)).build());

		// Tools
		possibleTreasure.add(new ItemBuilder(Material.DIAMOND_PICKAXE).build());
		possibleTreasure.add(new ItemBuilder(Material.IRON_PICKAXE).build());

		// Misc
		if (luck > 20) {
			if (!CommonQuestItem.DISCOUNT_CARD.isInInventoryOf(player))
				possibleTreasure.add(CommonQuestItem.DISCOUNT_CARD.get());

			possibleTreasure.add(Pugmas25QuestItem.SLOT_MACHINE_TOKEN.get());
			possibleTreasure.add(Pugmas25QuestItem.GIFT_INITIAL.get());
			possibleTreasure.add(new ItemBuilder(Material.NETHERITE_SCRAP).amount(Pugmas25.getLuckyAmount(1, 2, luck)).build());
		}

		return RandomUtils.randomElement(possibleTreasure);
	}

	@EventHandler
	public void on(PlayerEventFishingCaughtFishEvent event) {
		Player player = event.getPlayer();

		if (!Pugmas25.get().isAtEvent(player))
			return;

		Pugmas25AnglerLoot anglerLoot = Pugmas25Config.get().getAnglerQuestFish();
		if (anglerLoot == null)
			return;

		Pugmas25User user = userService.get(player);
		if (user.isCaughtAnglerQuestLoot())
			return;

		for (ItemStack item : event.getLoot()) {
			if (Nullables.isNullOrAir(item))
				continue;

			if (anglerLoot.matches(item)) {
				user.setCaughtAnglerQuestLoot(true);
				userService.save(user);
			}
		}
	}
}
