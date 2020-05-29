package me.pugabyte.bncore.features.dailyrewards;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.dailyreward.DailyReward;
import me.pugabyte.bncore.models.dailyreward.DailyRewardService;
import me.pugabyte.bncore.models.dailyreward.R3ward;
import me.pugabyte.bncore.models.dailyreward.Reward;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;

public class DailyRewardsFeature {
	private static List<Reward> rewards = new ArrayList<>();
	private static List<R3ward> rewards1 = new ArrayList<>();
	private static List<R3ward> rewards2 = new ArrayList<>();
	private static List<R3ward> rewards3 = new ArrayList<>();

	public DailyRewardsFeature() {
		setupDailyRewards();
		scheduler();
		BNCore.getCron().schedule("00 00 * * *", DailyRewardsFeature::dailyReset);
	}

	private void scheduler() {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(5), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				try {
					if (((Hours) new HoursService().get(player)).getDaily() < Time.MINUTE.x(15) / 20) continue;

					DailyRewardService service = new DailyRewardService();
					DailyReward dailyReward = service.get(player);
					if (dailyReward.isEarnedToday()) continue;

					Tasks.sync(() -> {
						dailyReward.increaseStreak();
						service.save(dailyReward);
					});
				} catch (Exception ex) {
					BNCore.warn("Error in DailyRewards scheduler: " + ex.getMessage());
				}
			}
		});
	}

	static void dailyReset() {
		DailyRewardService service = new DailyRewardService();
		List<DailyReward> dailyRewards = service.getAll();
		for (DailyReward dailyReward : dailyRewards) {
			if (!dailyReward.isEarnedToday()) {
				dailyReward.setActive(false);
				service.save(dailyReward);
				dailyReward = new DailyReward(dailyReward.getUuid());
			}

			dailyReward.setEarnedToday(false);
			if (dailyReward.getPlayer().isOnline())
				dailyReward.increaseStreak();

			service.save(dailyReward);
		}
	}

	public static int getMaxDays() {
		return rewards.size();
	}

	public static void menu(Player player, DailyReward dailyReward) {
		SmartInventory inv = SmartInventory.builder()
				.provider(new DailyRewardsMenu(dailyReward))
				.size(3, 9)
				.title(ChatColor.DARK_AQUA + "Daily Rewards")
				.build();

		inv.open(player);
	}

	public static Reward getReward(int day) {
		return rewards.get(day - 1);
	}

	public static R3ward getReward1(int day) {
		return rewards1.get(day - 1);
	}

	public static R3ward getReward2(int day) {
		return rewards2.get(day - 1);
	}

	public static R3ward getReward3(int day) {
		return rewards3.get(day - 1);
	}

	// @formatter:off
	@SuppressWarnings("DuplicatedCode")
	private void setupDailyRewards() {
		/*  1 */ rewards.add(new Reward("$100", 100));
		/*  2 */ rewards.add(new Reward("32 bread", new ItemStack(BREAD, 32)));
		/*  3 */ rewards.add(new Reward("3 iron blocks", new ItemStack(IRON_BLOCK, 3)));
		/*  4 */ rewards.add(new Reward("1 anvil", new ItemStack(ANVIL, 1)));
		/*  5 */ rewards.add(new Reward("64 steak", new ItemStack(COOKED_BEEF, 64)));
		/*  6 */ rewards.add(new Reward("32 glass", new ItemStack(GLASS, 32)));
		/*  7 */ rewards.add(new Reward("a saddle", new ItemStack(SADDLE)));
		/*  8 */ rewards.add(new Reward("$1,000", 1000));
		/*  9 */ rewards.add(new Reward("10 experience bottles", new ItemStack(EXPERIENCE_BOTTLE, 10)));
		/* 10 */ rewards.add(new Reward("32 leather", new ItemStack(LEATHER, 32)));
		/* 11 */ rewards.add(new Reward("$2,000", 2000));
		/* 12 */ rewards.add(new Reward("32 apples", new ItemStack(APPLE, 32)));
		/* 13 */ rewards.add(new Reward("5 diamonds", new ItemStack(DIAMOND, 5)));
		/* 14 */ rewards.add(new Reward("32 coal blocks", new ItemStack(COAL_BLOCK, 32)));
		/* 15 */ rewards.add(new Reward("a silk touch book", new ItemBuilder(ENCHANTED_BOOK).enchant(SILK_TOUCH).build()));
		/* 16 */ rewards.add(new Reward("3 golden apples", new ItemStack(GOLDEN_APPLE, 3)));
		/* 17 */ rewards.add(new Reward("8 healing potions", new ItemBuilder(POTION).amount(8).effect(PotionEffectType.HEAL).build()));
		/* 18 */ rewards.add(new Reward("$2,500", 2500));
		/* 19 */ rewards.add(new Reward("32 blaze rods", new ItemStack(BLAZE_ROD, 32)));
		/* 20 */ rewards.add(new Reward("8 ghast tears", new ItemStack(GHAST_TEAR, 8)));
		/* 21 */ rewards.add(new Reward("diamond boots", new ItemStack(DIAMOND_BOOTS)));
		/* 22 */ rewards.add(new Reward("16 powered rails and 128 normal rails", new ItemStack(POWERED_RAIL, 16), new ItemStack(RAIL, 128)));
		/* 23 */ rewards.add(new Reward("$3,000", 3000));
		/* 24 */ rewards.add(new Reward("8 of every dye", MaterialTag.DYES.getValues().stream().map(dye -> new ItemStack(dye, 8)).collect(Collectors.toList())));
		/* 25 */ rewards.add(new Reward("a bow with Power 2, Punch 1 and Unbreaking 2", new ItemBuilder(BOW).enchant(ARROW_DAMAGE, 2).enchant(ARROW_KNOCKBACK).enchant(DURABILITY, 2).build()));
		/* 26 */ rewards.add(new Reward("5 gold blocks, 10 iron blocks and 3 emerald blocks", new ItemStack(GOLD_BLOCK, 5), new ItemStack(IRON_BLOCK, 10), new ItemStack(EMERALD_BLOCK, 3)));
		/* 27 */ rewards.add(new Reward("50 experience levels", "exp give %player% 50L"));
		/* 28 */ rewards.add(new Reward("$3,500", 3500));
		/* 29 */ rewards.add(new Reward("8 emerald blocks", new ItemStack(EMERALD_BLOCK, 8)));
		/* 30 */ rewards.add(new Reward("diamond boots with Frost Walker, Protection 1 and Unbreaking 2", new ItemBuilder(DIAMOND_BOOTS).enchant(FROST_WALKER).enchant(PROTECTION_ENVIRONMENTAL).enchant(DURABILITY, 2).build()));
		/* 31 */ rewards.add(new Reward("$4,000", 4000));
		/* 32 */ rewards.add(new Reward("32 slime balls", new ItemStack(SLIME_BALL, 32)));
		/* 33 */ rewards.add(new Reward("4 redstone blocks", new ItemStack(REDSTONE_BLOCK, 4)));
		/* 34 */ rewards.add(new Reward("an enchanting table", new ItemStack(ENCHANTING_TABLE)));
		/* 35 */ rewards.add(new Reward("10 cakes", new ItemStack(CAKE, 10)));
		/* 36 */ rewards.add(new Reward("1 clock", new ItemStack(CLOCK)));
		/* 37 */ rewards.add(new Reward("diamond horse armor", new ItemStack(DIAMOND_HORSE_ARMOR)));
		/* 38 */ rewards.add(new Reward("$4,500", 4500));
		/* 39 */ rewards.add(new Reward("a totem of undying", new ItemStack(TOTEM_OF_UNDYING)));
		/* 40 */ rewards.add(new Reward("18 books", new ItemStack(BOOK, 18)));
		/* 41 */ rewards.add(new Reward("$5,000", 5000));
		/* 42 */ rewards.add(new Reward("3 notch apples", new ItemStack(ENCHANTED_GOLDEN_APPLE, 3)));
		/* 43 */ rewards.add(new Reward("6 empty maps and 1 book and quill", new ItemStack(MAP, 6), new ItemStack(WRITABLE_BOOK)));
		/* 44 */ rewards.add(new Reward("32 sea lanterns", new ItemStack(SEA_LANTERN, 32)));
		/* 45 */ rewards.add(new Reward("a mending book", new ItemBuilder(ENCHANTED_BOOK).enchant(MENDING).build()));
		/* 46 */ rewards.add(new Reward("3 brewing stands", new ItemStack(BREWING_STAND, 3)));
		/* 47 */ rewards.add(new Reward("64 blaze rods", new ItemStack(BLAZE_ROD, 64)));
		/* 48 */ rewards.add(new Reward("$5,500", 5500));
		/* 49 */ rewards.add(new Reward("6 armor stands", new ItemStack(ARMOR_STAND, 6)));
		/* 50 */ rewards.add(new Reward("a full set of chainmail armour", new ItemStack(CHAINMAIL_HELMET), new ItemStack(CHAINMAIL_CHESTPLATE), new ItemStack(CHAINMAIL_LEGGINGS), new ItemStack(CHAINMAIL_BOOTS)));
		/* 51 */ rewards.add(new Reward("diamond leggings", new ItemStack(DIAMOND_LEGGINGS)));
		/* 52 */ rewards.add(new Reward("4 water breathing potions", new ItemBuilder(POTION).amount(4).effect(PotionEffectType.WATER_BREATHING, 8 * 60).build()));
		/* 53 */ rewards.add(new Reward("$6,000", 6000));
		/* 54 */ rewards.add(new Reward("a zombie skull and a creeper skull", new ItemStack(ZOMBIE_HEAD), new ItemStack(CREEPER_HEAD)));
		/* 55 */ rewards.add(new Reward("a fishing rod with Lure 5, Luck 3, and Unbreaking 4", new ItemBuilder(FISHING_ROD).enchant(LURE, 5).enchant(LUCK, 3).enchant(DURABILITY, 4).build()));
		/* 56 */ rewards.add(new Reward("2 villager spawn eggs", new ItemStack(VILLAGER_SPAWN_EGG, 2)));
		/* 57 */ rewards.add(new Reward("75 enchanting levels", "exp give %player% 75L"));
		/* 58 */ rewards.add(new Reward("$6,500", 6500));
		/* 59 */ rewards.add(new Reward("1 diamond helmet with Respiration 3, Aqua Affinity 1 and Unbreaking 2", new ItemBuilder(DIAMOND_HELMET).enchant(OXYGEN, 3).enchant(WATER_WORKER).enchant(DURABILITY, 2).build()));
		/* 60 */ rewards.add(new Reward("1 shulker box", new ItemStack(PURPLE_SHULKER_BOX)));

//		rewards1 = new ArrayList<R3ward>() {{
//			/*   1 */ add(new R3ward("5 Cooked Chicken")					.item(COOKED_CHICKEN, 5));
//			/*   2 */ add(new R3ward("5 Steak")								.item(COOKED_BEEF, 5));
//			/*   3 */ add(new R3ward("10 Leather")							.item(LEATHER, 10));
//			/*   4 */ add(new R3ward("10 Bread")							.item(BREAD, 10));
//			/*   5 */ add(new R3ward("2 Golden Apples")						.item(GOLDEN_APPLE, 2));
//			/*   6 */ add(new R3ward("Set of iron tools")					.item(MaterialTag.TOOLS_IRON));
//			/*   7 */ add(new R3ward("16 books")							.item(BOOK, 16));
//			/*   8 */ add(new R3ward("carrot on a stick")					.item(CARROT_ON_A_STICK));
//			/*   9 */ add(new R3ward("12 fire charges")						.item(FIRE_CHARGE, 12));
//			/*  10 */ add(new R3ward("$1,000")								.money(1000));
//			/*  11 */ add(new R3ward("5 Cooked Chicken")					.item(COOKED_CHICKEN, 5));
//			/*  12 */ add(new R3ward("5 Steak")								.item(COOKED_BEEF, 5));
//			/*  13 */ add(new R3ward("10 Leather")							.item(LEATHER, 10));
//			/*  14 */ add(new R3ward("10 Bread")							.item(BREAD, 10));
//			/*  15 */ add(new R3ward("3 Golden Apples")						.item(GOLDEN_APPLE, 3));
//			/*  16 */ add(new R3ward("1 Diamond pickaxe")					.item(DIAMOND_PICKAXE, 1));
//			/*  17 */ add(new R3ward("2 lava, water, milk, empty buckets")	.item(LAVA_BUCKET, 2).item(WATER_BUCKET, 2).item(MILK_BUCKET, 2).item(BUCKET, 2));
//			/*  18 */ add(new R3ward("4 regen II pot")						.item(regen II pot, 4));
//			/*  19 */ add(new R3ward("4 health II pots")					.item(health II pots, 4));
//			/*  20 */ add(new R3ward("$2,000")								.money(2000));
//			/*  21 */ add(new R3ward("10 Cooked Chicken")					.item(COOKED_CHICKEN, 10));
//			/*  22 */ add(new R3ward("10 Steak")							.item(COOKED_BEEF, 10));
//			/*  23 */ add(new R3ward("20 Leather")							.item(LEATHER, 20));
//			/*  24 */ add(new R3ward("20 Bread")							.item(BREAD, 20));
//			/*  25 */ add(new R3ward("2 Golden Apple")						.item(GOLDEN_APPLE, 2));
//			/*  26 */ add(new R3ward("Diamond sword and shield")			.item(DIAMOND_SWORD).item(SHIELD));
//			/*  27 */ add(new R3ward("5 of each Sapling")					.item(MaterialTag.SAPLINGS, 5));
//			/*  28 */ add(new R3ward("32 Apples")							.item(APPLE, 32));
//			/*  29 */ add(new R3ward("1 Saddle")							.item(SADDLE, 1));
//			/*  30 */ add(new R3ward("$4,000")								.money(4000));
//			/*  31 */ add(new R3ward("10 Cooked Chicken")					.item(COOKED_CHICKEN, 10));
//			/*  32 */ add(new R3ward("10 Steak")							.item(COOKED_BEEF, 10));
//			/*  33 */ add(new R3ward("20 Leather")							.item(LEATHER, 20));
//			/*  34 */ add(new R3ward("20 Bread")							.item(BREAD, 20));
//			/*  35 */ add(new R3ward("4 Golden Apples")						.item(GOLDEN_APPLE, 4));
//			/*  36 */ add(new R3ward("1 Silk Touch")						.item(new ItemBuilder(ENCHANTED_BOOK).enchant(SILK_TOUCH)));
//			/*  37 */ add(new R3ward("1 Mending")							.item(new ItemBuilder(ENCHANTED_BOOK).enchant(MENDING)));
//			/*  38 */ add(new R3ward("1 Unbreaking 3")						.item(new ItemBuilder(ENCHANTED_BOOK).enchant(DURABILITY, 3)));
//			/*  39 */ add(new R3ward("1 Efficiency 5")						.item(new ItemBuilder(ENCHANTED_BOOK).enchant(DIG_SPEED, 5)));
//			/*  40 */ add(new R3ward("$4,000")								.money(4000));
//			/*  41 */ add(new R3ward("15 Cooked Chicken")					.item(COOKED_CHICKEN, 15));
//			/*  42 */ add(new R3ward("15 Steak")							.item(COOKED_BEEF, 15));
//			/*  43 */ add(new R3ward("30 Leather")							.item(LEATHER, 30));
//			/*  44 */ add(new R3ward("30 Bread")							.item(BREAD, 30));
//			/*  45 */ add(new R3ward("6 Golden Apples")						.item(GOLDEN_APPLE, 6));
//			/*  46 */ add(new R3ward("16 eyes of ender")					.item(ENDER_EYE, 16));
//			/*  47 */ add(new R3ward("32 Slime Balls")						.item(SLIME_BALL, 32));
//			/*  48 */ add(new R3ward("15 VPS")								.votePoints(15));
//			/*  49 */ add(new R3ward("Totem Of Undying")					.item(TOTEM_OF_UNDYING));
//			/*  50 */ add(new R3ward("$6,000")								.money(6000));
//			/*  51 */ add(new R3ward("15 Cooked Chicken")					.item(COOKED_CHICKEN, 15));
//			/*  52 */ add(new R3ward("15 Steak")							.item(COOKED_BEEF, 15));
//			/*  53 */ add(new R3ward("30 Leather")							.item(LEATHER, 30));
//			/*  54 */ add(new R3ward("30 Bread")							.item(BREAD, 30));
//			/*  55 */ add(new R3ward("6 Golden Apples")						.item(GOLDEN_APPLE, 6));
//			/*  56 */ add(new R3ward("32 Leads")							.item(LEAD, 32));
//			/*  57 */ add(new R3ward("Skeleton skull")						.item(SKELETON_SKULL));
//			/*  58 */ add(new R3ward("1 Elytra")							.item(ELYTRA, 1));
//			/*  59 */ add(new R3ward("8 Enchanted Golden Apples")			.item(ENCHANTED_GOLDEN_APPLE, 8));
//			/*  60 */ add(new R3ward("$6,000")								.money(6000));
//			/*  61 */ add(new R3ward("20 Cooked Chicken")					.item(COOKED_CHICKEN, 20));
//			/*  62 */ add(new R3ward("20 Steak")							.item(COOKED_BEEF, 20));
//			/*  63 */ add(new R3ward("40 Leather")							.item(LEATHER, 40));
//			/*  64 */ add(new R3ward("40 Bread")							.item(BREAD, 40));
//			/*  65 */ add(new R3ward("8 Golden Apples")						.item(GOLDEN_APPLE, 8));
//			/*  66 */ add(new R3ward("set of chain mail armour")			.item(MaterialTag.ARMOR_CHAINMAIL));
//			/*  67 */ add(new R3ward("64 spectral arrows")					.item(SPECTRAL_ARROW, 64));
//			/*  68 */ add(new R3ward("Power 5 book")						.item(new ItemBuilder(ENCHANTED_BOOK).enchant(ARROW_DAMAGE, 5)));
//			/*  69 */ add(new R3ward("16 name tags")						.item(NAME_TAG, 16));
//			/*  70 */ add(new R3ward("$8,000")								.money(8000));
//			/*  71 */ add(new R3ward("25 Cooked Chicken")					.item(COOKED_CHICKEN, 25));
//			/*  72 */ add(new R3ward("25 Steak")							.item(COOKED_BEEF, 25));
//			/*  73 */ add(new R3ward("45 Leather")							.item(LEATHER, 45));
//			/*  74 */ add(new R3ward("45 Bread")							.item(BREAD, 45));
//			/*  75 */ add(new R3ward("1 Notch Apple")						.item(ENCHANTED_GOLDEN_APPLE, 1));
//			/*  76 */ add(new R3ward("64 Pumpkin Pie")						.item(PUMPKIN_PIE, 64));
//			/*  77 */ add(new R3ward("16 firework stars")					.item(FIREWORK_STAR, 16));
//			/*  78 */ add(new R3ward("2 Villager Spawn Eggs")				.item(VILLAGER_SPAWN_EGG, 2));
//			/*  79 */ add(new R3ward("1 Wither Skeleton Skull")				.item(WITHER_SKELETON_SKULL, 1));
//			/*  80 */ add(new R3ward("$10,000")								.money(10000));
//			/*  81 */ add(new R3ward("35 Cooked Chicken")					.item(COOKED_CHICKEN, 35));
//			/*  82 */ add(new R3ward("35 Steak")							.item(COOKED_BEEF, 35));
//			/*  83 */ add(new R3ward("70 Leather")							.item(LEATHER, 70));
//			/*  84 */ add(new R3ward("McMMo level coupon, 2 level")			.item(COUPON));
//			/*  85 */ add(new R3ward("2 Horse Spawn Eggs")					.item(HORSE_SPAWN_EGG, 2));
//			/*  86 */ add(new R3ward("3 Notch Apples")						.item(ENCHANTED_GOLDEN_APPLE, 3));
//			/*  87 */ add(new R3ward("iron, gold, diamond horse armour")	.item(IRON_HORSE_ARMOR).item(GOLDEN_HORSE_ARMOR).item(DIAMOND_HORSE_ARMOR));
//			/*  88 */ add(new R3ward("64 Golden Carrots")					.item(GOLDEN_CARROT, 64));
//			/*  89 */ add(new R3ward("16 End Crystals")						.item(END_CRYSTAL, 16));
//			/*  90 */ add(new R3ward("$15,000")								.money(15000));
//			/*  91 */ add(new R3ward("100 Cooked Chicken")					.item(COOKED_CHICKEN, 100));
//			/*  92 */ add(new R3ward("100 Steak")							.item(COOKED_BEEF, 100));
//			/*  93 */ add(new R3ward("200 Leather")							.item(LEATHER, 200));
//			/*  94 */ add(new R3ward("McMMO level coupon 5 levels")			.item(COUPON));
//			/*  95 */ add(new R3ward("5 Notch Apples")						.item(ENCHANTED_GOLDEN_APPLE, 5));
//			/*  96 */ add(new R3ward("That Super Fishing Pole")				.item(new ItemBuilder(FISHING_ROD).enchant(LURE, 5).enchant(LUCK, 3).enchant(DURABILITY, 4)));
//			/*  97 */ add(new R3ward("Maxed Diamond Sword")					.item(new ItemBuilder(DIAMOND_SWORD).enchantMax(DAMAGE_ALL).enchantMax(MENDING).enchantMax(FIRE_ASPECT).enchantMax(KNOCKBACK).enchant(DURABILITY, 4)));
//			/*  98 */ add(new R3ward("Full set of Diamond gear and tools")	.item(MaterialTag.ARMOR_DIAMOND).item(MaterialTag.TOOLS_DIAMOND));
//			/*  99 */ add(new R3ward("Maxed Diamond Pickaxe")				.item(new ItemBuilder(DIAMOND_PICKAXE).enchantMax(DIG_SPEED).enchant(MENDING).enchant(DURABILITY, 4)));
//			/* 100 */ add(new R3ward("$20,000")								.money(20000));
//		}};
//
//		rewards2 = new ArrayList<R3ward>() {{
//			/*   1 */ add(new R3ward("1 Coal Block")						.item(COAL_BLOCK, 1));
//			/*   2 */ add(new R3ward("1 Redstone Block")					.item(REDSTONE_BLOCK, 1));
//			/*   3 */ add(new R3ward("1 Iron Block")						.item(IRON_BLOCK, 1));
//			/*   4 */ add(new R3ward("1 Lapis Block")						.item(LAPIS_BLOCK, 1));
//			/*   5 */ add(new R3ward("1 Gold Block")						.item(GOLD_BLOCK, 1));
//			/*   6 */ add(new R3ward("5 Glowstone")							.item(GLOWSTONE, 5));
//			/*   7 */ add(new R3ward("5 Obsidian")							.item(OBSIDIAN, 5));
//			/*   8 */ add(new R3ward("5 Quartz Blocks")						.item(QUARTZ_BLOCK, 5));
//			/*   9 */ add(new R3ward("5 Emeralds")							.item(EMERALD, 5));
//			/*  10 */ add(new R3ward("5 Diamonds")							.item(DIAMOND, 5));
//			/*  11 */ add(new R3ward("1 Coal Block")						.item(COAL_BLOCK, 1));
//			/*  12 */ add(new R3ward("2 Redstone Block")					.item(REDSTONE_BLOCK, 2));
//			/*  13 */ add(new R3ward("1 Iron Block")						.item(IRON_BLOCK, 1));
//			/*  14 */ add(new R3ward("1 Lapis Block")						.item(LAPIS_BLOCK, 1));
//			/*  15 */ add(new R3ward("1 Gold Block")						.item(GOLD_BLOCK, 1));
//			/*  16 */ add(new R3ward("5 Glowstone")							.item(GLOWSTONE, 5));
//			/*  17 */ add(new R3ward("5 Obsidian")							.item(OBSIDIAN, 5));
//			/*  18 */ add(new R3ward("5 Quartz Blocks")						.item(QUARTZ_BLOCK, 5));
//			/*  19 */ add(new R3ward("1 Emerald Block")						.item(EMERALD_BLOCK, 1));
//			/*  20 */ add(new R3ward("1 Diamond Block")						.item(DIAMOND_BLOCK, 1));
//			/*  21 */ add(new R3ward("2 Coal Block")						.item(COAL_BLOCK, 2));
//			/*  22 */ add(new R3ward("2 Redstone Block")					.item(REDSTONE_BLOCK, 2));
//			/*  23 */ add(new R3ward("2 Iron Block")						.item(IRON_BLOCK, 2));
//			/*  24 */ add(new R3ward("2 Lapis Block")						.item(LAPIS_BLOCK, 2));
//			/*  25 */ add(new R3ward("2 Gold Block")						.item(GOLD_BLOCK, 2));
//			/*  26 */ add(new R3ward("10 Glowstone")						.item(GLOWSTONE, 10));
//			/*  27 */ add(new R3ward("10 Obsidian")							.item(OBSIDIAN, 10));
//			/*  28 */ add(new R3ward("10 Quartz Blocks")					.item(QUARTZ_BLOCK, 10));
//			/*  29 */ add(new R3ward("2 Emerald Block")						.item(EMERALD_BLOCK, 2));
//			/*  30 */ add(new R3ward("2 Diamond Block")						.item(DIAMOND_BLOCK, 2));
//			/*  31 */ add(new R3ward("2 Coal Block")						.item(COAL_BLOCK, 2));
//			/*  32 */ add(new R3ward("3 Redstone Block")					.item(REDSTONE_BLOCK, 3));
//			/*  33 */ add(new R3ward("2 Iron Block")						.item(IRON_BLOCK, 2));
//			/*  34 */ add(new R3ward("2 Lapis Block")						.item(LAPIS_BLOCK, 2));
//			/*  35 */ add(new R3ward("2 Gold Block")						.item(GOLD_BLOCK, 2));
//			/*  36 */ add(new R3ward("10 Glowstone")						.item(GLOWSTONE, 10));
//			/*  37 */ add(new R3ward("10 Obsidian")							.item(OBSIDIAN, 10));
//			/*  38 */ add(new R3ward("10 Quartz Blocks")					.item(QUARTZ_BLOCK, 10));
//			/*  39 */ add(new R3ward("2 Emerald Block")						.item(EMERALD_BLOCK, 2));
//			/*  40 */ add(new R3ward("2 Diamond Block")						.item(DIAMOND_BLOCK, 2));
//			/*  41 */ add(new R3ward("3 Coal Block")						.item(COAL_BLOCK, 3));
//			/*  42 */ add(new R3ward("3 Redstone Block")					.item(REDSTONE_BLOCK, 3));
//			/*  43 */ add(new R3ward("3 Iron Block")						.item(IRON_BLOCK, 3));
//			/*  44 */ add(new R3ward("3 Lapis Block")						.item(LAPIS_BLOCK, 3));
//			/*  45 */ add(new R3ward("3 Gold Block")						.item(GOLD_BLOCK, 3));
//			/*  46 */ add(new R3ward("15 Glowstone")						.item(GLOWSTONE, 15));
//			/*  47 */ add(new R3ward("15 Obsidian")							.item(OBSIDIAN, 15));
//			/*  48 */ add(new R3ward("15 Quartz Blocks")					.item(QUARTZ_BLOCK, 15));
//			/*  49 */ add(new R3ward("3 Emerald Block")						.item(EMERALD_BLOCK, 3));
//			/*  50 */ add(new R3ward("3 Diamond Block")						.item(DIAMOND_BLOCK, 3));
//			/*  51 */ add(new R3ward("3 Coal Block")						.item(COAL_BLOCK, 3));
//			/*  52 */ add(new R3ward("4 Redstone Block")					.item(REDSTONE_BLOCK, 4));
//			/*  53 */ add(new R3ward("3 Iron Block")						.item(IRON_BLOCK, 3));
//			/*  54 */ add(new R3ward("3 Lapis Block")						.item(LAPIS_BLOCK, 3));
//			/*  55 */ add(new R3ward("3 Gold Block")						.item(GOLD_BLOCK, 3));
//			/*  56 */ add(new R3ward("15 Glowstone")						.item(GLOWSTONE, 15));
//			/*  57 */ add(new R3ward("15 Obsidian")							.item(OBSIDIAN, 15));
//			/*  58 */ add(new R3ward("15 Quartz Blocks")					.item(QUARTZ_BLOCK, 15));
//			/*  59 */ add(new R3ward("3 Emerald Block")						.item(EMERALD_BLOCK, 3));
//			/*  60 */ add(new R3ward("3 Diamond Block")						.item(DIAMOND_BLOCK, 3));
//			/*  61 */ add(new R3ward("4 Coal Block")						.item(COAL_BLOCK, 4));
//			/*  62 */ add(new R3ward("5 Redstone Block")					.item(REDSTONE_BLOCK, 5));
//			/*  63 */ add(new R3ward("4 Iron Block")						.item(IRON_BLOCK, 4));
//			/*  64 */ add(new R3ward("4 Lapis Block")						.item(LAPIS_BLOCK, 4));
//			/*  65 */ add(new R3ward("4 Gold Block")						.item(GOLD_BLOCK, 4));
//			/*  66 */ add(new R3ward("20 Shroomlight")						.item(SHROOMLIGHT, 20));
//			/*  67 */ add(new R3ward("20 Obsidian")							.item(OBSIDIAN, 20));
//			/*  68 */ add(new R3ward("20 Quartz Blocks")					.item(QUARTZ_BLOCK, 20));
//			/*  69 */ add(new R3ward("4 Emerald Block")						.item(EMERALD_BLOCK, 4));
//			/*  70 */ add(new R3ward("4 Diamond Block")						.item(DIAMOND_BLOCK, 4));
//			/*  71 */ add(new R3ward("5 Coal Block")						.item(COAL_BLOCK, 5));
//			/*  72 */ add(new R3ward("7 Redstone Block")					.item(REDSTONE_BLOCK, 7));
//			/*  73 */ add(new R3ward("5 Iron Block")						.item(IRON_BLOCK, 5));
//			/*  74 */ add(new R3ward("5 Lapis Block")						.item(LAPIS_BLOCK, 5));
//			/*  75 */ add(new R3ward("5 Gold Block")						.item(GOLD_BLOCK, 5));
//			/*  76 */ add(new R3ward("25 Glowstone")						.item(GLOWSTONE, 25));
//			/*  77 */ add(new R3ward("25 Obsidian")							.item(OBSIDIAN, 25));
//			/*  78 */ add(new R3ward("25 Quartz Blocks")					.item(QUARTZ_BLOCK, 25));
//			/*  79 */ add(new R3ward("5 Emerald Block")						.item(EMERALD_BLOCK, 5));
//			/*  80 */ add(new R3ward("5 Diamond Block")						.item(DIAMOND_BLOCK, 5));
//			/*  81 */ add(new R3ward("7 Coal Block")						.item(COAL_BLOCK, 7));
//			/*  82 */ add(new R3ward("10 Redstone Block")					.item(REDSTONE_BLOCK, 10));
//			/*  83 */ add(new R3ward("7 Iron Block")						.item(IRON_BLOCK, 7));
//			/*  84 */ add(new R3ward("7 Lapis Block")						.item(LAPIS_BLOCK, 7));
//			/*  85 */ add(new R3ward("7 Gold Block")						.item(GOLD_BLOCK, 7));
//			/*  86 */ add(new R3ward("35 Shroomlight")						.item(SHROOMLIGHT, 35));
//			/*  87 */ add(new R3ward("35 Obsidian")							.item(OBSIDIAN, 35));
//			/*  88 */ add(new R3ward("35 Quartz Blocks")					.item(QUARTZ_BLOCK, 35));
//			/*  89 */ add(new R3ward("7 Emerald Block")						.item(EMERALD_BLOCK, 7));
//			/*  90 */ add(new R3ward("7 Diamond Block")						.item(DIAMOND_BLOCK, 7));
//			/*  91 */ add(new R3ward("10 Coal Block")						.item(COAL_BLOCK, 10));
//			/*  92 */ add(new R3ward("10 Restone Blocks")					.item(REDSTONE_BLOCK, 10));
//			/*  93 */ add(new R3ward("10 Iron Block")						.item(IRON_BLOCK, 10));
//			/*  94 */ add(new R3ward("10 Lapis Block")						.item(LAPIS_BLOCK, 10));
//			/*  95 */ add(new R3ward("10 Gold Blocks")						.item(GOLD_BLOCK, 10));
//			/*  96 */ add(new R3ward("100 Glowstone")						.item(GLOWSTONE, 100));
//			/*  97 */ add(new R3ward("100 Obsidian")						.item(OBSIDIAN, 100));
//			/*  98 */ add(new R3ward("100 Quartz Blocks")					.item(QUARTZ_BLOCK, 100));
//			/*  99 */ add(new R3ward("10 Emerald Blocks")					.item(EMERALD_BLOCK, 10));
//			/* 100 */ add(new R3ward("10 Diamond Blocks")					.item(DIAMOND_BLOCK, 10));
//		}};
//
//		rewards3 = new ArrayList<R3ward>() {{
//			/*   1 */ add(new R3ward("1 Workbench")							.item(CRAFTING_TABLE, 1));
//			/*   2 */ add(new R3ward("1 Chest")								.item(CHEST, 1));
//			/*   3 */ add(new R3ward("1 Furnace")							.item(FURNACE, 1));
//			/*   4 */ add(new R3ward("1 Hopper")							.item(HOPPER, 1));
//			/*   5 */ add(new R3ward("1 Bed")								.item(BED, 1));
//			/*   6 */ add(new R3ward("1 Blast Furnace")						.item(BLAST_FURNACE, 1));
//			/*   7 */ add(new R3ward("1 Enchanting Table")					.item(ENCHANTING_TABLE, 1));
//			/*   8 */ add(new R3ward("1 Ender Chest")						.item(ENDER_CHEST, 1));
//			/*   9 */ add(new R3ward("8 Armour Stands")						.item(ARMOR_STAND, 8));
//			/*  10 */ add(new R3ward("1 Anvil")								.item(ANVIL, 1));
//			/*  11 */ add(new R3ward("2 Workbenches")						.item(CRAFTING_TABLE, 2));
//			/*  12 */ add(new R3ward("2 Chests")							.item(CHEST, 2));
//			/*  13 */ add(new R3ward("2 Furnaces")							.item(FURNACE, 2));
//			/*  14 */ add(new R3ward("2 Hoppers")							.item(HOPPER, 2));
//			/*  15 */ add(new R3ward("2 Beds")								.item(BED, 2));
//			/*  16 */ add(new R3ward("12 chorus fruit")						.item(CHORUS_FRUIT, 12));
//			/*  17 */ add(new R3ward("16 magma cream")						.item(MAGMA_CREAM, 16));
//			/*  18 */ add(new R3ward("32 repeaters, comparators, torches")	.item(REPEATER, 32).item(COMPARATOR, 32).item(REDSTONE_TORCH, 32));
//			/*  19 */ add(new R3ward("32 bricks")							.item(BRICKS, 32));
//			/*  20 */ add(new R3ward("1 of each villager workblock")		.item(MaterialTag.VILLAGER_WORKBLOCKS, 1));
//			/*  21 */ add(new R3ward("10 Workbenches")						.item(CRAFTING_TABLE, 10));
//			/*  22 */ add(new R3ward("10 Chests")							.item(CHEST, 10));
//			/*  23 */ add(new R3ward("10 Furnaces")							.item(FURNACE, 10));
//			/*  24 */ add(new R3ward("10 Hoppers")							.item(HOPPER, 10));
//			/*  25 */ add(new R3ward("10 Beds")								.item(BED, 10));
//			/*  26 */ add(new R3ward("16 Cobwebs")							.item(COBWEB, 16));
//			/*  27 */ add(new R3ward("16 Minecarts")						.item(MINECART, 16));
//			/*  28 */ add(new R3ward("8 of each glazed terracotta")			.item(MaterialTag.GLAZED_TERRACOTTAS, 8));
//			/*  29 */ add(new R3ward("32 packed ice")						.item(PACKED_ICE, 32));
//			/*  30 */ add(new R3ward("8 pistons, 8 sticky pistons")			.item(PISTON, 8).item(STICKY_PISTON, 8));
//			/*  31 */ add(new R3ward("10 Workbenches")						.item(CRAFTING_TABLE, 10));
//			/*  32 */ add(new R3ward("10 Chests")							.item(CHEST, 10));
//			/*  33 */ add(new R3ward("10 Furnaces")							.item(FURNACE, 10));
//			/*  34 */ add(new R3ward("10 Hoppers")							.item(HOPPER, 10));
//			/*  35 */ add(new R3ward("10 Beds")								.item(BED, 10));
//			/*  36 */ add(new R3ward("32 Item frames")						.item(ITEM_FRAME, 32));
//			/*  37 */ add(new R3ward("32 Prismarine blocks")				.item(PRISMARINE, 32));
//			/*  38 */ add(new R3ward("16 Cauldron")							.item(CAULDRON, 16));
//			/*  39 */ add(new R3ward("32 Nether bricks")					.item(NETHER_BRICK, 32));
//			/*  40 */ add(new R3ward("32 Endstone Blocks")					.item(END_STONE, 32));
//			/*  41 */ add(new R3ward("15 Workbenches")						.item(CRAFTING_TABLE, 15));
//			/*  42 */ add(new R3ward("15 Chests")							.item(CHEST, 15));
//			/*  43 */ add(new R3ward("15 Furnaces")							.item(FURNACE, 15));
//			/*  44 */ add(new R3ward("15 Hoppers")							.item(HOPPER, 15));
//			/*  45 */ add(new R3ward("15 Beds")								.item(BED, 15));
//			/*  46 */ add(new R3ward("32 Redstone lamps")					.item(REDSTONE_LAMP, 32));
//			/*  47 */ add(new R3ward("32 Sea lanterns")						.item(SEA_LANTERN, 32));
//			/*  48 */ add(new R3ward("12 note blocks")						.item(NOTE_BLOCK, 12));
//			/*  49 */ add(new R3ward("50 Enchanting levels")				.levels(50));
//			/*  50 */ add(new R3ward("32 end rods")							.item(END_ROD, 32));
//			/*  51 */ add(new R3ward("15 Workbenches")						.item(CRAFTING_TABLE, 15));
//			/*  52 */ add(new R3ward("15 Chests")							.item(CHEST, 15));
//			/*  53 */ add(new R3ward("15 Furnaces")							.item(FURNACE, 15));
//			/*  54 */ add(new R3ward("15 Hoppers")							.item(HOPPER, 15));
//			/*  55 */ add(new R3ward("15 Beds")								.item(BED, 15));
//			/*  56 */ add(new R3ward("32 Bookshelves")						.item(BOOKSHELF, 32));
//			/*  57 */ add(new R3ward("32 Coarse Dirt")						.item(COARSE_DIRT, 32));
//			/*  58 */ add(new R3ward("75 enchanting levels")				.levels(75));
//			/*  59 */ add(new R3ward("32 Brewing stands")					.item(BREWING_STAND, 32));
//			/*  60 */ add(new R3ward("2 parrot spawn eggs")					.item(PARROT_SPAWN_EGG, 2));
//			/*  61 */ add(new R3ward("20 Bookshelves")						.item(BOOKSHELF, 20));
//			/*  62 */ add(new R3ward("20 Chests")							.item(CHEST, 20));
//			/*  63 */ add(new R3ward("20 soul lanterns")					.item(SOUL_LANTERN, 20));
//			/*  64 */ add(new R3ward("20 Hoppers")							.item(HOPPER, 20));
//			/*  65 */ add(new R3ward("20 Noteblocks")						.item(NOTE_BLOCK, 20));
//			/*  66 */ add(new R3ward("4 ender chests")						.item(ENDER_CHEST, 4));
//			/*  67 */ add(new R3ward("64 Lily Pads")						.item(LILY_PAD, 64));
//			/*  68 */ add(new R3ward("64 Magma blocks")						.item(MAGMA_BLOCK, 64));
//			/*  69 */ add(new R3ward("3 anvils")							.item(ANVIL, 3));
//			/*  70 */ add(new R3ward("64 Slime blocks")						.item(SLIME_BLOCK, 64));
//			/*  71 */ add(new R3ward("25 Bookshelves")						.item(BOOKSHELF, 25));
//			/*  72 */ add(new R3ward("25 Chests")							.item(CHEST, 25));
//			/*  73 */ add(new R3ward("25 vote points")						.votePoints(25));
//			/*  74 */ add(new R3ward("25 Hoppers")							.item(HOPPER, 25));
//			/*  75 */ add(new R3ward("25 Noteblocks")						.item(NOTE_BLOCK, 25));
//			/*  76 */ add(new R3ward("64 Vines")							.item(VINE, 64));
//			/*  77 */ add(new R3ward("64 Paintings")						.item(PAINTING, 64));
//			/*  78 */ add(new R3ward("64 cocoa beans")						.item(COCOA_BEANS, 64));
//			/*  79 */ add(new R3ward("64 Mycelium")							.item(MYCELIUM, 64));
//			/*  80 */ add(new R3ward("5 Sponges")							.item(SPONGE, 5));
//			/*  81 */ add(new R3ward("35 Bookshelves")						.item(BOOKSHELF, 35));
//			/*  82 */ add(new R3ward("35 Chests")							.item(CHEST, 35));
//			/*  83 */ add(new R3ward("2 Netherite Ingots")					.item(NETHERITE_INGOT, 2));
//			/*  84 */ add(new R3ward("35 Hoppers")							.item(HOPPER, 35));
//			/*  85 */ add(new R3ward("35 Noteblocks")						.item(NOTE_BLOCK, 35));
//			/*  86 */ add(new R3ward("12 white banners")					.item(WHITE_BANNER, 12));
//			/*  87 */ add(new R3ward("100 enchanting levels")				.levels(100));
//			/*  88 */ add(new R3ward("2 ocelot spawn eggs")					.item(OCELOT_SPAWN_EGG, 2));
//			/*  89 */ add(new R3ward("16 Dragon's Breath")					.item(DRAGON_BREATH, 16));
//			/*  90 */ add(new R3ward("<player head>")						.item(PLAYERS_HEAD, 1));
//			/*  91 */ add(new R3ward("50 Bookshelves")						.item(BOOKSHELF, 50));
//			/*  92 */ add(new R3ward("50 Chests")							.item(CHEST, 50));
//			/*  93 */ add(new R3ward("5 Netherite Ingots")					.item(NETHERITE_INGOT, 5));
//			/*  94 */ add(new R3ward("Maxed Bow")							.item(new ItemBuilder(BOW).enchantMax(ARROW_DAMAGE).enchantMax(ARROW_KNOCKBACK).enchantMax(ARROW_INFINITE).enchant(MENDING)));
//			/*  95 */ add(new R3ward("5 crying obsidian")					.item(CRYING_OBSIDIAN, 5));
//			/*  96 */ add(new R3ward("16 of each color wool")				.item(MaterialTag.WOOL, 16));
//			/*  97 */ add(new R3ward("150 Enchanting Levels")				.levels(150));
//			/*  98 */ add(new R3ward("64 podzol")							.item(PODZOL, 64));
//			/*  99 */ add(new R3ward("1 Shulker Box")						.item(SHULKER_BOX, 1));
//			/* 100 */ add(new R3ward("64 sea lanterns")						.item(SEA_LANTERN, 64));
//		}};

	}
	// @formatter:on

}
