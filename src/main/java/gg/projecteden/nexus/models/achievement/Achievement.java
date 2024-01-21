package gg.projecteden.nexus.models.achievement;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.achievements.events.AchievementCompletedEvent;
import gg.projecteden.nexus.framework.interfaces.HasDescription;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public enum Achievement implements HasDescription {
	// New achievement: "You put the 'fun' in 'funeral'..." -> get every death message (PYROPEGUIN)
	KILL_DA_DRAGON(
			"Kill a dragon",
			AchievementGroup.COMBAT,
			new ItemStack(Material.DRAGON_EGG)),
	DRAGON_SLAYER(
			"Kill 10 dragons",
			AchievementGroup.COMBAT,
			new ItemStack(Material.DRAGON_EGG, 5),
			ProgressType.NUMBER, 10),
	WITHERED(
			"Kill a wither",
			AchievementGroup.COMBAT,
			new ItemStack(Material.SOUL_SAND)),
	WATCHA_GUARDIAN(
			"Kill an Elder Guardian",
			AchievementGroup.COMBAT,
			new ItemStack(Material.PRISMARINE_CRYSTALS)),
	GUARDIANS_OF_THE_DEEP(
			"Kill 5 Elder Guardians",
			AchievementGroup.COMBAT,
			new ItemStack(Material.PRISMARINE_CRYSTALS, 5),
			ProgressType.NUMBER, 5),
	EVOKING_A_MEMORY(
			"Kill an Evoker",
			AchievementGroup.COMBAT,
			new ItemStack(Material.EMERALD)),
	WRONG_WAY(
			"Shoot yourself with an arrow",
			AchievementGroup.COMBAT,
			new ItemStack(Material.BOW)),
	/* THE_ART_OF_COMBAT(
			"Kill a mob without being hit back",
			AchievementGroup.COMBAT), ,
			new ItemStack(Material.SOMETHING)*/
	SHOWING_DOMINANCE(
			"Kill someone in SurvivalPVP",
			AchievementGroup.COMBAT,
			new ItemStack(Material.DIAMOND_SWORD)),
	AVOIDING_DEATH(
			"Use a Totem of Undying",
			AchievementGroup.COMBAT,
			new ItemStack(Material.TOTEM_OF_UNDYING)),

	FOUND_A_FRIEND(
			"Poof to someone",
			AchievementGroup.SOCIAL,
			new ItemStack(Material.ENDER_PEARL)),
	MR_PERIOD_POPULAR(
			"Poof to 10 different people",
			AchievementGroup.SOCIAL,
			new ItemStack(Material.ENDER_PEARL, 10),
			ProgressType.LIST, 10),
	NOOB(
			"Attend a Minigame Night",
			AchievementGroup.SOCIAL,
			new ItemStack(Material.DIAMOND_SWORD)),
	PRO_GAMER(
			"Attend 10 Minigame Nights",
			AchievementGroup.SOCIAL,
			new ItemStack(Material.DIAMOND_SWORD, 10),
			ProgressType.LIST, 10),
	CREATE_THE_TALE_OF_TIME(
			"Play in-game for 10 days",
			AchievementGroup.SOCIAL,
			new ItemStack(Material.CLOCK, 10),
			ProgressType.HOURS, 24 * 10),
	MEMBER_OF_SOCIETY(
			"Become a Member",
			AchievementGroup.SOCIAL,
			new ItemStack(Material.WHITE_WOOL)),
	ONE_YEAR_STRONG(
			"Play for a year",
			AchievementGroup.SOCIAL,
			new ItemStack(Material.OBSIDIAN)),
	SLEEPLESS_NIGHT(
			"Stay logged in for 12 hours straight",
			AchievementGroup.SOCIAL,
			new ItemStack(Material.CLOCK, 12)),
	CHANNEL_ALIKE(
			"Switch chat channels",
			AchievementGroup.SOCIAL,
			new ItemStack(Material.OAK_SIGN)),
	ONE_OF_US(
			"Link your Minecraft and Discord accounts",
			AchievementGroup.SOCIAL,
			new ItemStack(Material.DIAMOND)),

	COMMONWEALTH(
			"Have a balance of $10,000",
			AchievementGroup.ECONOMY,
			new ItemStack(Material.GOLD_NUGGET),
			ProgressType.BALANCE, 10000),
	UPPER_CLASS(
			"Have a balance of $100,000",
			AchievementGroup.ECONOMY,
			new ItemStack(Material.GOLD_INGOT),
			ProgressType.BALANCE, 100000),
	BOUGHT_AND_PAID_FOR(
			"Have a balance of $1,000,000",
			AchievementGroup.ECONOMY,
			new ItemStack(Material.GOLD_BLOCK),
			ProgressType.BALANCE, 1000000),
	SPENDER(
			"Buy 10 things from the Market",
			AchievementGroup.ECONOMY,
			new ItemStack(Material.OAK_SIGN),
			ProgressType.NUMBER, 10),
	SHOPAHOLIC(
			"Buy 1,000 things from the Market",
			AchievementGroup.ECONOMY,
			new ItemStack(Material.OAK_SIGN, 2),
			ProgressType.NUMBER, 1000),
	PERFECT_TRADE(
			"Receive $5,000 from other players",
			AchievementGroup.ECONOMY,
			new ItemStack(Material.PAPER),
			ProgressType.NUMBER, 5000),
	STEADY_INCOME(
			"Receive money from other players 25 times",
			AchievementGroup.ECONOMY,
			new ItemStack(Material.PAPER, 25),
			ProgressType.NUMBER, 25),
	HIRED_HELP(
			"Send money to other players 50 times",
			AchievementGroup.ECONOMY,
			new ItemStack(Material.PAPER, 50),
			ProgressType.NUMBER, 50),
/*	IN_DEMAND(
		"Have an item sell out in your shop",
		AchievementGroup.ECONOMY,
		new ItemStack(Material.SOMETHING)),
	SPREADING_THE_WEALTH(
		"Perform 100 transactions in player shops",
		AchievementGroup.ECONOMY,
		ProgressType.NUMBER, 100),
	BIG_SPENDER(
		"Buy $10,000 worth of items in player shops",
		AchievementGroup.ECONOMY,
		ProgressType.NUMBER, 10000),*/

	MAN_OF_THE_SKY(
			"Start a Skyblock island",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.SHORT_GRASS)),
	CREATIVE_GENIUS(
			"Claim a Creative plot",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.QUARTZ_BLOCK)),
	JOURNEY_OF_A_THOUSAND_MILES(
			"Leave spawn for the first time",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.LEATHER_BOOTS)),
	THE_EXPLORER(
			"Travel by foot for 100km",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.CHAINMAIL_BOOTS)),
	THE_WELL_HEELD_TRAVELER(
			"Travel by foot for 1,000km",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.IRON_BOOTS)),
	RIDE_OR_DIE(
			"Travel by vehicle/mount for 100km",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.MINECART)),
	UP_COMMA_UP_COMMA_AND_AWAY(
			"Travel by elytra for 100km",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.ELYTRA)),
	A_LIGHT_OF_HOPE(
			"Visit the Staff Hall",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.STONE_BRICK_STAIRS)),
	BLAST_FROM_THE_PAST(
			"Visit the Hall of History",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.QUARTZ_STAIRS)),
	TAKING_A_SHORTCUT(
			"Warp 10 times",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.ENDER_PEARL),
			ProgressType.NUMBER, 10),
	FAST_TRAVEL(
			"Visit all of the survival warps",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.ENDER_PEARL, 2),
			ProgressType.LIST, 16),
	WORLD_TRAVELER(
			"Warp 1,000 times",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.ENDER_PEARL, 3),
			ProgressType.NUMBER, 1000),
	HIS_POINT_OF_VIEW(
			"Stand on Koda's head in spawn",
			AchievementGroup.TRAVEL,
			new ItemStack(Material.BONE)),

	SLEEPY(
			"Use a bed 100 times",
			AchievementGroup.MISC,
			new ItemStack(Material.WHITE_BED),
			ProgressType.NUMBER, 100),
	CRAFTING_KING(
			"Craft 1 million items",
			AchievementGroup.MISC,
			new ItemStack(Material.CRAFTING_TABLE),
			ProgressType.NUMBER, 1000000),
	THE_COLLECTOR(
			"Pick up each music disc",
			AchievementGroup.MISC,
			new ItemStack(Material.MUSIC_DISC_CAT),
			ProgressType.LIST, 12),
	/* WE_HAPPY_FEW(
			"Punch all the NPCs in spawn",
			AchievementGroup.MISC,
			new ItemStack(Material.SOMETHING),
			ProgressType.LIST, 10), */
	BEAM_OF_LIFE(
			"Activate 5 beacons",
			AchievementGroup.MISC,
			new ItemStack(Material.BEACON),
			ProgressType.NUMBER, 5),
	HIDDEN_WORDS(
			"Punch the Schmileyface sign",
			AchievementGroup.MISC,
			new ItemStack(Material.OAK_SIGN)),
	JOINING_THE_WAR(
			"Punch the Sign War sign",
			AchievementGroup.MISC,
			new ItemStack(Material.OAK_SIGN)),

	THE_WANDERER(
			"Visit every biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.DIAMOND_BOOTS),
			ProgressType.LIST, 20),
	BLACK_HORSE_AND_THE_BIRCH_TREE(
			"Visit a Birch Forest biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.BIRCH_LOG)),
	A_HORSE_WITH_NO_NAME(
			"Visit a Desert biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.SAND)),
	THE_MISTY_MOUNTAINS_COLD(
			"Visit an Extreme Hills biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.SNOW_BLOCK)),
	WHERE_THE_WILD_ROSES_GROW(
			"Visit a Flower Forest biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.POPPY)),
	FORREST_GUMP(
			"Visit a Forest biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.OAK_LOG)),
	ICE_ICE_BABY(
			"Visit an Ice Plains biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.SNOW_BLOCK)),
	HEART_OF_ICE(
			"Visit an Ice Spikes biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.PACKED_ICE)),
	WELCOME_TO_THE_JUNGLE(
			"Visit a Jungle biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.JUNGLE_LOG)),
	THE_PAINTED_DESERT(
			"Visit a Mesa biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.TERRACOTTA)),
	INFECTED_MUSHROOM(
			"Visit a Mushroom Island biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.MYCELIUM)),
	SAILING_FOR_ADVENTURE(
			"Swim in an Ocean",
			AchievementGroup.BIOMES,
			new ItemStack(Material.WATER_BUCKET)),
	THE_RAIN_IN_SPAIN_STAYS_MAINLY_ON_THE_PLAIN(
			"Visit a Plains biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.SHORT_GRASS)),
	HOTEL_CALIFORNIA(
			"Visit a Redwood Forest biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.DIRT, 1, (short) 2)),
	CRY_ME_A_RIVER(
			"Wade in a River",
			AchievementGroup.BIOMES,
			new ItemStack(Material.CLAY)),
	FIDDLER_ON_THE_ROOF(
			"Visit a Roofed Forest biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.DARK_OAK_LOG)),
	SUNDAY_IN_SAVANNAH(
			"Visit a Savanna biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.ACACIA_LOG)),
	BORN_ON_THE_BAYOU(
			"Visit a Swamp biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.LILY_PAD)),
	TAIGA_TAIGA_BURNING_BRIGHT(
			"Visit a Taiga biome",
			AchievementGroup.BIOMES,
			new ItemStack(Material.SPRUCE_LOG, 1)),
	HIGHWAY_TO_HELL(
			"Visit the Nether",
			AchievementGroup.BIOMES,
			new ItemStack(Material.NETHERRACK)),
	THE_END_OF_THE_WORLD_AS_WE_KNOW_IT(
			"Visit the End",
			AchievementGroup.BIOMES,
			new ItemStack(Material.END_STONE)),

	WELCOME_TO_THE_FAIR(
			"Walk into the fairgrounds",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.CYAN_WOOL)),
	LET_YOUR_COLORS_BURST(
			"Trigger the tripwire that sets off fireworks",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.FIREWORK_ROCKET)),
	KEEP_GOING_EXCL(
			"Earn Bear Fair points",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.GOLD_NUGGET)),
	HI_COMMA_MONOPOLY_BEAR(
			"Earn 500 Bear Fair points",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.GOLD_INGOT)),
	START_OF_AN_ADDICTION(
			"Win any fair game",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.EMERALD)),
	MAKING_BANK(
			"Complete all the fairgrounds games 5 times in one day",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.EMERALD, 5),
			ProgressType.LIST, 5),
	ADVENTUROUS_SPIRIT(
			"Visit all the stalls",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.OAK_FENCE),
			ProgressType.LIST, 8),
	THE_KID_IN_YOU(
			"Bounce around in the bouncy castle",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.SLIME_BLOCK)),
	DOWN_COMMA_UP_COMMA_OVER_COMMA_AND_UNDER(
			"Go on all 3 roller coasters",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.MINECART),
			ProgressType.LIST, 3),
	STREAKS_EXCL(
			"Visit the bear fair grounds for the entire week",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.DIAMOND, 7),
			ProgressType.LIST, 7),
	TO_THE_SKIES_EXCL(
			"Use the launchpads 20 times",
			AchievementGroup.BEAR_FAIR,
			new ItemStack(Material.TNT),
			ProgressType.NUMBER, 20);

	private String description;
	private AchievementGroup group;
	private ItemStack itemStack;
	private ProgressType progressType;
	private int required;

	Achievement(String description, AchievementGroup group, ItemStack itemStack) {
		this.description = description;
		this.group = group;
		this.itemStack = itemStack;
		this.progressType = ProgressType.SINGLE;
	}

	Achievement(String description, AchievementGroup group, ItemStack itemStack, ProgressType progressType, int required) {
		this.description = description;
		this.group = group;
		this.itemStack = itemStack;
		this.progressType = progressType;
		this.required = required;
	}

	public @NotNull String getDescription() {
		return description;
	}

	public AchievementGroup getGroup() {
		return group;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public ProgressType getProgressType() {
		return progressType;
	}

	public int getRequired() {
		return required;
	}

	@Override
	public String toString() {
		return EnumUtils.prettyName(name());
	}

	public boolean check(String uuid) {
		return check(Bukkit.getPlayer(UUID.fromString(uuid)), 1);
	}

	public boolean check(String uuid, Object newValue) {
		return check(Bukkit.getPlayer(UUID.fromString(uuid)), newValue);
	}

	public boolean check(Player player) {
		return check(player, 1);
	}

	public boolean check(Player player, Object newValue) {
		AchievementPlayer achievementPlayer = new AchievementService().get(player);
		if (achievementPlayer == null) return false;
		if (achievementPlayer.hasAchievement(this)) return false;

		ProgressType progressType = this.getProgressType();

		switch (progressType) {
			case NUMBER:
				increase(achievementPlayer, (int) newValue);
				break;
			case LIST:
				if (newValue != null) {
					increase(achievementPlayer, (String) newValue);
				}
				break;
		}

		new AchievementService().save(achievementPlayer);

		if (hasCompleted(achievementPlayer)) {
			Bukkit.getPluginManager().callEvent(new AchievementCompletedEvent(achievementPlayer, this));
			return true;
		}

		return false;
	}

	private int getProgress(AchievementPlayer achievementPlayer) {
		Object progress = achievementPlayer.getAchievementProgress(this);
		if (progress == null) return 0;

		switch (this.getProgressType()) {
			case LIST:
				return ((HashSet<String>) progress).size();
			case NUMBER:
				return (int) progress;
			case HOURS:
				Hours hours = new HoursService().get(achievementPlayer);
				return hours.getTotal() / 60 / 60;
			case BALANCE:
				return (int) new BankerService().getBalance(achievementPlayer.getOnlinePlayer(), ShopGroup.of(achievementPlayer.getOnlinePlayer()));
		}
		return 0;
	}

	private void increase(AchievementPlayer achievementPlayer, int increase) {
		int progress = getProgress(achievementPlayer);
		achievementPlayer.setAchievementProgress(this, progress + increase);
	}

	private void increase(AchievementPlayer achievementPlayer, String newValue) {
		Set<String> progress;

		try {
			progress = (HashSet<String>) achievementPlayer.getAchievementProgress(this);
			if (progress == null) {
				progress = new HashSet<>();
			}
		} catch (NullPointerException ex) {
			progress = new HashSet<>();
		}

		progress.add(newValue);
		achievementPlayer.setAchievementProgress(this, progress);
	}

	private boolean hasCompleted(AchievementPlayer achievementPlayer) {
		if (this.getProgressType() == ProgressType.SINGLE) return true;

		return getProgress(achievementPlayer) >= this.getRequired();
	}

	public enum ProgressType {
		SINGLE,
		NUMBER,
		LIST,
		HOURS,
		BALANCE

	}

}
