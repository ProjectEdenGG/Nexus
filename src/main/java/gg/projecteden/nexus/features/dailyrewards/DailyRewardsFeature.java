package gg.projecteden.nexus.features.dailyrewards;

import gg.projecteden.nexus.features.commands.staff.CouponCommand;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser.DailyStreak;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUserService;
import gg.projecteden.nexus.models.dailyreward.Reward;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.ARROW_DAMAGE;
import static org.bukkit.enchantments.Enchantment.ARROW_INFINITE;
import static org.bukkit.enchantments.Enchantment.ARROW_KNOCKBACK;
import static org.bukkit.enchantments.Enchantment.DAMAGE_ALL;
import static org.bukkit.enchantments.Enchantment.DIG_SPEED;
import static org.bukkit.enchantments.Enchantment.DURABILITY;
import static org.bukkit.enchantments.Enchantment.FIRE_ASPECT;
import static org.bukkit.enchantments.Enchantment.KNOCKBACK;
import static org.bukkit.enchantments.Enchantment.LUCK;
import static org.bukkit.enchantments.Enchantment.LURE;
import static org.bukkit.enchantments.Enchantment.MENDING;
import static org.bukkit.enchantments.Enchantment.SILK_TOUCH;

public class DailyRewardsFeature extends Feature {
	private static final List<Reward> rewards1 = setupDailyRewards(1);
	private static final List<Reward> rewards2 = setupDailyRewards(2);
	private static final List<Reward> rewards3 = setupDailyRewards(3);

	@Override
	public void onStart() {
		scheduler();
	}

	@Getter
	private static LocalDateTime lastTaskTime;

	private void scheduler() {
		Tasks.repeatAsync(TickTime.SECOND, TickTime.SECOND.x(6), () -> {
			lastTaskTime = LocalDateTime.now();

			DailyRewardUserService service = new DailyRewardUserService();
			for (Player player : OnlinePlayers.getAll()) {
				try {
					if (new HoursService().get(player.getUniqueId()).getDaily() < TickTime.MINUTE.x(15) / 20)
						continue;

					DailyRewardUser user = service.get(player);
					if (user.getCurrentStreak().isEarnedToday())
						continue;

					Tasks.sync(() -> {
						user.getCurrentStreak().increaseStreak();
						service.save(user);
					});
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			Tasks.waitAsync(TickTime.SECOND.x(3), () -> {
				for (DailyRewardUser user : service.getAllNotEarnedToday()) {
					try {
						if (new HoursService().get(user.getUniqueId()).getDaily() < TickTime.MINUTE.x(15) / 20)
							continue;

						Tasks.sync(() -> {
							user.getCurrentStreak().increaseStreak();
							service.save(user);
						});
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		});
	}

	public static void dailyReset() {
		final DailyRewardUserService service = new DailyRewardUserService();
		for (DailyRewardUser user : service.getCache().values()) {
			final DailyStreak streak = user.getCurrentStreak();
			if (!streak.isEarnedToday()) {
				user.endStreak();
			} else {
				streak.setEarnedToday(false);
				if (user.isOnline())
					streak.increaseStreak();
			}
			service.save(user);
		}
	}

	public static int getMaxDays() {
		return rewards1.size();
	}

	public static void menu(Player player, DailyRewardUser user) {
		new DailyRewardsMenu(user).open(player);
	}

	public static int getRewardDay(int day) {
		if (day % getMaxDays() == 0)
			return getMaxDays();
		return day % getMaxDays();
	}

	public static Reward getReward(int day, int option) {
		return switch (option) {
			case 0 -> rewards1.get(getRewardDay(day) - 1);
			case 1 -> rewards2.get(getRewardDay(day) - 1);
			case 2 -> rewards3.get(getRewardDay(day) - 1);
			default -> null;
		};
	}

	public static List<Reward> getRewards(int day) {
		return Arrays.asList(
				getReward(getRewardDay(day), 0),
				getReward(getRewardDay(day), 1),
				getReward(getRewardDay(day), 2)
		);
	}

	// @formatter:off
	@SuppressWarnings("DuplicatedCode")
	private static List<Reward> setupDailyRewards(int i) {
		return switch (i) {
			case 1 -> new ArrayList<>() {{
				/*   1 */ add(new Reward("5 Cooked Chicken")						.item(COOKED_CHICKEN, 5));
				/*   2 */ add(new Reward("5 Steak")									.item(COOKED_BEEF, 5));
				/*   3 */ add(new Reward("10 Leather")								.item(LEATHER, 10));
				/*   4 */ add(new Reward("10 Bread")								.item(BREAD, 10));
				/*   5 */ add(new Reward("2 Golden Apples")							.item(GOLDEN_APPLE, 2));
				/*   6 */ add(new Reward("Set of Iron tools")						.item(MaterialTag.TOOLS_IRON));
				/*   7 */ add(new Reward("16 books")								.item(BOOK, 16));
				/*   8 */ add(new Reward("Carrot on a Stick")						.item(CARROT_ON_A_STICK));
				/*   9 */ add(new Reward("12 Fire Charges")							.item(FIRE_CHARGE, 12));
				/*  10 */ add(new Reward("$1,000")									.money(1000));
				/*  11 */ add(new Reward("5 Cooked Chicken")						.item(COOKED_CHICKEN, 5));
				/*  12 */ add(new Reward("5 Steak")									.item(COOKED_BEEF, 5));
				/*  13 */ add(new Reward("10 Leather")								.item(LEATHER, 10));
				/*  14 */ add(new Reward("10 Bread")								.item(BREAD, 10));
				/*  15 */ add(new Reward("3 Golden Apples")							.item(GOLDEN_APPLE, 3));
				/*  16 */ add(new Reward("1 Diamond Pickaxe")						.item(DIAMOND_PICKAXE, 1));
				/*  17 */ add(new Reward("2 Lava, Water, Milk, Empty buckets")		.item(LAVA_BUCKET, 2).item(WATER_BUCKET, 2).item(MILK_BUCKET, 2).item(BUCKET, 2));
				/*  18 */ add(new Reward("4 Regen 2 Potions")						.item(new ItemBuilder(POTION).amount(4).potionType(PotionType.REGEN, false, true)));
				/*  19 */ add(new Reward("4 Health 2 Potions")						.item(new ItemBuilder(POTION).amount(4).potionType(PotionType.INSTANT_HEAL, false, true)));
				/*  20 */ add(new Reward("$2,000")									.money(2000));
				/*  21 */ add(new Reward("10 Cooked Chicken")						.item(COOKED_CHICKEN, 10));
				/*  22 */ add(new Reward("10 Steak")								.item(COOKED_BEEF, 10));
				/*  23 */ add(new Reward("20 Leather")								.item(LEATHER, 20));
				/*  24 */ add(new Reward("20 Bread")								.item(BREAD, 20));
				/*  25 */ add(new Reward("2 Golden Apple")							.item(GOLDEN_APPLE, 2));
				/*  26 */ add(new Reward("Diamond sword and shield")				.item(DIAMOND_SWORD).item(SHIELD));
				/*  27 */ add(new Reward("5 of each Sapling")						.item(MaterialTag.SAPLINGS, 5));
				/*  28 */ add(new Reward("32 Apples")								.item(APPLE, 32));
				/*  29 */ add(new Reward("1 Saddle")								.item(SADDLE, 1));
				/*  30 */ add(new Reward("$4,000")									.money(4000));
				/*  31 */ add(new Reward("10 Cooked Chicken")						.item(COOKED_CHICKEN, 10));
				/*  32 */ add(new Reward("10 Steak")								.item(COOKED_BEEF, 10));
				/*  33 */ add(new Reward("20 Leather")								.item(LEATHER, 20));
				/*  34 */ add(new Reward("20 Bread")								.item(BREAD, 20));
				/*  35 */ add(new Reward("4 Golden Apples")							.item(GOLDEN_APPLE, 4));
				/*  36 */ add(new Reward("1 Silk Touch")							.item(new ItemBuilder(ENCHANTED_BOOK).enchant(SILK_TOUCH)));
				/*  37 */ add(new Reward("1 Mending")								.item(new ItemBuilder(ENCHANTED_BOOK).enchant(MENDING)));
				/*  38 */ add(new Reward("1 Unbreaking 3")							.item(new ItemBuilder(ENCHANTED_BOOK).enchant(DURABILITY, 3)));
				/*  39 */ add(new Reward("1 Efficiency 5")							.item(new ItemBuilder(ENCHANTED_BOOK).enchant(DIG_SPEED, 5)));
				/*  40 */ add(new Reward("$4,000")									.money(4000));
				/*  41 */ add(new Reward("15 Cooked Chicken")						.item(COOKED_CHICKEN, 15));
				/*  42 */ add(new Reward("15 Steak")								.item(COOKED_BEEF, 15));
				/*  43 */ add(new Reward("30 Leather")								.item(LEATHER, 30));
				/*  44 */ add(new Reward("30 Bread")								.item(BREAD, 30));
				/*  45 */ add(new Reward("6 Golden Apples")							.item(GOLDEN_APPLE, 6));
				/*  46 */ add(new Reward("16 eyes of ender")						.item(ENDER_EYE, 16));
				/*  47 */ add(new Reward("32 Slime Balls")							.item(SLIME_BALL, 32));
				/*  48 */ add(new Reward("15 VPS")									.votePoints(15));
				/*  49 */ add(new Reward("Totem Of Undying")						.item(TOTEM_OF_UNDYING));
				/*  50 */ add(new Reward("$6,000")									.money(6000));
				/*  51 */ add(new Reward("15 Cooked Chicken")						.item(COOKED_CHICKEN, 15));
				/*  52 */ add(new Reward("15 Steak")								.item(COOKED_BEEF, 15));
				/*  53 */ add(new Reward("30 Leather")								.item(LEATHER, 30));
				/*  54 */ add(new Reward("30 Bread")								.item(BREAD, 30));
				/*  55 */ add(new Reward("6 Golden Apples")							.item(GOLDEN_APPLE, 6));
				/*  56 */ add(new Reward("32 Leads")								.item(LEAD, 32));
				/*  57 */ add(new Reward("Skeleton Skull")							.item(SKELETON_SKULL));
				/*  58 */ add(new Reward("1 Elytra")								.item(ELYTRA, 1));
				/*  59 */ add(new Reward("8 Enchanted Golden Apples")				.item(ENCHANTED_GOLDEN_APPLE, 8));
				/*  60 */ add(new Reward("$6,000")									.money(6000));
				/*  61 */ add(new Reward("20 Cooked Chicken")						.item(COOKED_CHICKEN, 20));
				/*  62 */ add(new Reward("20 Steak")								.item(COOKED_BEEF, 20));
				/*  63 */ add(new Reward("40 Leather")								.item(LEATHER, 40));
				/*  64 */ add(new Reward("40 Bread")								.item(BREAD, 40));
				/*  65 */ add(new Reward("8 Golden Apples")							.item(GOLDEN_APPLE, 8));
				/*  66 */ add(new Reward("Set of Chainmail Armor")					.item(MaterialTag.ARMOR_CHAINMAIL));
				/*  67 */ add(new Reward("64 Spectral Arrows")						.item(SPECTRAL_ARROW, 64));
				/*  68 */ add(new Reward("Power 5 Book")							.item(new ItemBuilder(ENCHANTED_BOOK).enchant(ARROW_DAMAGE, 5)));
				/*  69 */ add(new Reward("16 Name Tags")							.item(NAME_TAG, 16));
				/*  70 */ add(new Reward("$8,000")									.money(8000));
				/*  71 */ add(new Reward("25 Cooked Chicken")						.item(COOKED_CHICKEN, 25));
				/*  72 */ add(new Reward("25 Steak")								.item(COOKED_BEEF, 25));
				/*  73 */ add(new Reward("45 Leather")								.item(LEATHER, 45));
				/*  74 */ add(new Reward("45 Bread")								.item(BREAD, 45));
				/*  75 */ add(new Reward("1 Enchanted Golden Apple")				.item(ENCHANTED_GOLDEN_APPLE, 1));
				/*  76 */ add(new Reward("64 Pumpkin Pie")							.item(PUMPKIN_PIE, 64));
				/*  77 */ add(new Reward("16 firework stars")						.item(FIREWORK_STAR, 16));
				/*  78 */ add(new Reward("2 Villager Spawn Eggs")					.item(VILLAGER_SPAWN_EGG, 2));
				/*  79 */ add(new Reward("1 Wither Skeleton Skull")					.item(WITHER_SKELETON_SKULL, 1));
				/*  80 */ add(new Reward("$10,000")									.money(10000));
				/*  81 */ add(new Reward("35 Cooked Chicken")						.item(COOKED_CHICKEN, 35));
				/*  82 */ add(new Reward("35 Steak")								.item(COOKED_BEEF, 35));
				/*  83 */ add(new Reward("70 Leather")								.item(LEATHER, 70));
				/*  84 */ add(new Reward("Coupon for 2 McMMO levels")				.item(CouponCommand.getGenericCoupon("mcmmo", 2)));
				/*  85 */ add(new Reward("2 Horse Spawn Eggs")						.item(HORSE_SPAWN_EGG, 2));
				/*  86 */ add(new Reward("3 Enchanted Golden Apples")				.item(ENCHANTED_GOLDEN_APPLE, 3));
				/*  87 */ add(new Reward("Iron, Gold, Diamond Horse Armor")			.item(IRON_HORSE_ARMOR).item(GOLDEN_HORSE_ARMOR).item(DIAMOND_HORSE_ARMOR));
				/*  88 */ add(new Reward("64 Golden Carrots")						.item(GOLDEN_CARROT, 64));
				/*  89 */ add(new Reward("16 End Crystals")							.item(END_CRYSTAL, 16));
				/*  90 */ add(new Reward("$15,000")									.money(15000));
				/*  91 */ add(new Reward("100 Cooked Chicken")						.item(COOKED_CHICKEN, 100));
				/*  92 */ add(new Reward("100 Steak")								.item(COOKED_BEEF, 100));
				/*  93 */ add(new Reward("200 Leather")								.item(LEATHER, 200));
				/*  94 */ add(new Reward("Coupon for 5 McMMO levels")				.item(CouponCommand.getGenericCoupon("mcmmo", 5)));
				/*  95 */ add(new Reward("5 Enchanted Golden Apples")				.item(ENCHANTED_GOLDEN_APPLE, 5));
				/*  96 */ add(new Reward("Super Fishing Pole")						.item(new ItemBuilder(FISHING_ROD).enchant(LURE, 5).enchant(LUCK, 3).enchant(DURABILITY, 4)));
				/*  97 */ add(new Reward("Maxed Diamond Sword")						.item(new ItemBuilder(DIAMOND_SWORD).enchantMax(DAMAGE_ALL).enchantMax(MENDING).enchantMax(FIRE_ASPECT).enchantMax(KNOCKBACK).enchant(DURABILITY, 4)));
				/*  98 */ add(new Reward("Full set of Diamond gear and tools")		.item(MaterialTag.ARMOR_DIAMOND).item(MaterialTag.TOOLS_DIAMOND));
				/*  99 */ add(new Reward("Maxed Diamond Pickaxe")					.item(new ItemBuilder(DIAMOND_PICKAXE).enchantMax(DIG_SPEED).enchant(MENDING).enchant(DURABILITY, 4)));
				/* 100 */ add(new Reward("$20,000")									.money(20000));
			}};

			case 2 -> new ArrayList<>() {{
				/*   1 */ add(new Reward("1 Coal Block")							.item(COAL_BLOCK, 1));
				/*   2 */ add(new Reward("1 Redstone Block")						.item(REDSTONE_BLOCK, 1));
				/*   3 */ add(new Reward("1 Iron Block")							.item(IRON_BLOCK, 1));
				/*   4 */ add(new Reward("1 Lapis Block")							.item(LAPIS_BLOCK, 1));
				/*   5 */ add(new Reward("1 Gold Block")							.item(GOLD_BLOCK, 1));
				/*   6 */ add(new Reward("5 Glowstone")								.item(GLOWSTONE, 5));
				/*   7 */ add(new Reward("5 Obsidian")								.item(OBSIDIAN, 5));
				/*   8 */ add(new Reward("5 Quartz Blocks")							.item(QUARTZ_BLOCK, 5));
				/*   9 */ add(new Reward("5 Emeralds")								.item(EMERALD, 5));
				/*  10 */ add(new Reward("5 Diamonds")								.item(DIAMOND, 5));
				/*  11 */ add(new Reward("1 Coal Block")							.item(COAL_BLOCK, 1));
				/*  12 */ add(new Reward("2 Redstone Block")						.item(REDSTONE_BLOCK, 2));
				/*  13 */ add(new Reward("1 Iron Block")							.item(IRON_BLOCK, 1));
				/*  14 */ add(new Reward("1 Lapis Block")							.item(LAPIS_BLOCK, 1));
				/*  15 */ add(new Reward("1 Gold Block")							.item(GOLD_BLOCK, 1));
				/*  16 */ add(new Reward("5 Glowstone")								.item(GLOWSTONE, 5));
				/*  17 */ add(new Reward("5 Obsidian")								.item(OBSIDIAN, 5));
				/*  18 */ add(new Reward("5 Quartz Blocks")							.item(QUARTZ_BLOCK, 5));
				/*  19 */ add(new Reward("1 Emerald Block")							.item(EMERALD_BLOCK, 1));
				/*  20 */ add(new Reward("1 Diamond Block")							.item(DIAMOND_BLOCK, 1));
				/*  21 */ add(new Reward("2 Coal Block")							.item(COAL_BLOCK, 2));
				/*  22 */ add(new Reward("2 Redstone Block")						.item(REDSTONE_BLOCK, 2));
				/*  23 */ add(new Reward("2 Iron Block")							.item(IRON_BLOCK, 2));
				/*  24 */ add(new Reward("2 Lapis Block")							.item(LAPIS_BLOCK, 2));
				/*  25 */ add(new Reward("2 Gold Block")							.item(GOLD_BLOCK, 2));
				/*  26 */ add(new Reward("10 Glowstone")							.item(GLOWSTONE, 10));
				/*  27 */ add(new Reward("10 Obsidian")								.item(OBSIDIAN, 10));
				/*  28 */ add(new Reward("10 Quartz Blocks")						.item(QUARTZ_BLOCK, 10));
				/*  29 */ add(new Reward("2 Emerald Block")							.item(EMERALD_BLOCK, 2));
				/*  30 */ add(new Reward("2 Diamond Block")							.item(DIAMOND_BLOCK, 2));
				/*  31 */ add(new Reward("2 Coal Block")							.item(COAL_BLOCK, 2));
				/*  32 */ add(new Reward("3 Redstone Block")						.item(REDSTONE_BLOCK, 3));
				/*  33 */ add(new Reward("2 Iron Block")							.item(IRON_BLOCK, 2));
				/*  34 */ add(new Reward("2 Lapis Block")							.item(LAPIS_BLOCK, 2));
				/*  35 */ add(new Reward("2 Gold Block")							.item(GOLD_BLOCK, 2));
				/*  36 */ add(new Reward("10 Glowstone")							.item(GLOWSTONE, 10));
				/*  37 */ add(new Reward("10 Obsidian")								.item(OBSIDIAN, 10));
				/*  38 */ add(new Reward("10 Quartz Blocks")						.item(QUARTZ_BLOCK, 10));
				/*  39 */ add(new Reward("2 Emerald Block")							.item(EMERALD_BLOCK, 2));
				/*  40 */ add(new Reward("2 Diamond Block")							.item(DIAMOND_BLOCK, 2));
				/*  41 */ add(new Reward("3 Coal Block")							.item(COAL_BLOCK, 3));
				/*  42 */ add(new Reward("3 Redstone Block")						.item(REDSTONE_BLOCK, 3));
				/*  43 */ add(new Reward("3 Iron Block")							.item(IRON_BLOCK, 3));
				/*  44 */ add(new Reward("3 Lapis Block")							.item(LAPIS_BLOCK, 3));
				/*  45 */ add(new Reward("3 Gold Block")							.item(GOLD_BLOCK, 3));
				/*  46 */ add(new Reward("15 Glowstone")							.item(GLOWSTONE, 15));
				/*  47 */ add(new Reward("15 Obsidian")								.item(OBSIDIAN, 15));
				/*  48 */ add(new Reward("15 Quartz Blocks")						.item(QUARTZ_BLOCK, 15));
				/*  49 */ add(new Reward("3 Emerald Block")							.item(EMERALD_BLOCK, 3));
				/*  50 */ add(new Reward("3 Diamond Block")							.item(DIAMOND_BLOCK, 3));
				/*  51 */ add(new Reward("3 Coal Block")							.item(COAL_BLOCK, 3));
				/*  52 */ add(new Reward("4 Redstone Block")						.item(REDSTONE_BLOCK, 4));
				/*  53 */ add(new Reward("3 Iron Block")							.item(IRON_BLOCK, 3));
				/*  54 */ add(new Reward("3 Lapis Block")							.item(LAPIS_BLOCK, 3));
				/*  55 */ add(new Reward("3 Gold Block")							.item(GOLD_BLOCK, 3));
				/*  56 */ add(new Reward("15 Glowstone")							.item(GLOWSTONE, 15));
				/*  57 */ add(new Reward("15 Obsidian")								.item(OBSIDIAN, 15));
				/*  58 */ add(new Reward("15 Quartz Blocks")						.item(QUARTZ_BLOCK, 15));
				/*  59 */ add(new Reward("3 Emerald Block")							.item(EMERALD_BLOCK, 3));
				/*  60 */ add(new Reward("3 Diamond Block")							.item(DIAMOND_BLOCK, 3));
				/*  61 */ add(new Reward("4 Coal Block")							.item(COAL_BLOCK, 4));
				/*  62 */ add(new Reward("5 Redstone Block")						.item(REDSTONE_BLOCK, 5));
				/*  63 */ add(new Reward("4 Iron Block")							.item(IRON_BLOCK, 4));
				/*  64 */ add(new Reward("4 Lapis Block")							.item(LAPIS_BLOCK, 4));
				/*  65 */ add(new Reward("4 Gold Block")							.item(GOLD_BLOCK, 4));
				/*  66 */ add(new Reward("20 Shroomlight")							.item(SHROOMLIGHT, 20));
				/*  67 */ add(new Reward("20 Obsidian")								.item(OBSIDIAN, 20));
				/*  68 */ add(new Reward("20 Quartz Blocks")						.item(QUARTZ_BLOCK, 20));
				/*  69 */ add(new Reward("4 Emerald Block")							.item(EMERALD_BLOCK, 4));
				/*  70 */ add(new Reward("4 Diamond Block")							.item(DIAMOND_BLOCK, 4));
				/*  71 */ add(new Reward("5 Coal Block")							.item(COAL_BLOCK, 5));
				/*  72 */ add(new Reward("7 Redstone Block")						.item(REDSTONE_BLOCK, 7));
				/*  73 */ add(new Reward("5 Iron Block")							.item(IRON_BLOCK, 5));
				/*  74 */ add(new Reward("5 Lapis Block")							.item(LAPIS_BLOCK, 5));
				/*  75 */ add(new Reward("5 Gold Block")							.item(GOLD_BLOCK, 5));
				/*  76 */ add(new Reward("25 Glowstone")							.item(GLOWSTONE, 25));
				/*  77 */ add(new Reward("25 Obsidian")								.item(OBSIDIAN, 25));
				/*  78 */ add(new Reward("25 Quartz Blocks")						.item(QUARTZ_BLOCK, 25));
				/*  79 */ add(new Reward("5 Emerald Block")							.item(EMERALD_BLOCK, 5));
				/*  80 */ add(new Reward("5 Diamond Block")							.item(DIAMOND_BLOCK, 5));
				/*  81 */ add(new Reward("7 Coal Block")							.item(COAL_BLOCK, 7));
				/*  82 */ add(new Reward("10 Redstone Block")						.item(REDSTONE_BLOCK, 10));
				/*  83 */ add(new Reward("7 Iron Block")							.item(IRON_BLOCK, 7));
				/*  84 */ add(new Reward("7 Lapis Block")							.item(LAPIS_BLOCK, 7));
				/*  85 */ add(new Reward("7 Gold Block")							.item(GOLD_BLOCK, 7));
				/*  86 */ add(new Reward("35 Shroomlight")							.item(SHROOMLIGHT, 35));
				/*  87 */ add(new Reward("35 Obsidian")								.item(OBSIDIAN, 35));
				/*  88 */ add(new Reward("35 Quartz Blocks")						.item(QUARTZ_BLOCK, 35));
				/*  89 */ add(new Reward("7 Emerald Block")							.item(EMERALD_BLOCK, 7));
				/*  90 */ add(new Reward("7 Diamond Block")							.item(DIAMOND_BLOCK, 7));
				/*  91 */ add(new Reward("10 Coal Block")							.item(COAL_BLOCK, 10));
				/*  92 */ add(new Reward("10 Restone Blocks")						.item(REDSTONE_BLOCK, 10));
				/*  93 */ add(new Reward("10 Iron Block")							.item(IRON_BLOCK, 10));
				/*  94 */ add(new Reward("10 Lapis Block")							.item(LAPIS_BLOCK, 10));
				/*  95 */ add(new Reward("10 Gold Blocks")							.item(GOLD_BLOCK, 10));
				/*  96 */ add(new Reward("100 Glowstone")							.item(GLOWSTONE, 100));
				/*  97 */ add(new Reward("100 Obsidian")							.item(OBSIDIAN, 100));
				/*  98 */ add(new Reward("100 Quartz Blocks")						.item(QUARTZ_BLOCK, 100));
				/*  99 */ add(new Reward("10 Emerald Blocks")						.item(EMERALD_BLOCK, 10));
				/* 100 */ add(new Reward("10 Diamond Blocks")						.item(DIAMOND_BLOCK, 10));
			}};

			case 3 -> new ArrayList<>() {{
				/*   1 */ add(new Reward("1 Workbench")								.item(CRAFTING_TABLE, 1));
				/*   2 */ add(new Reward("1 Chest")									.item(CHEST, 1));
				/*   3 */ add(new Reward("1 Furnace")								.item(FURNACE, 1));
				/*   4 */ add(new Reward("1 Hopper")								.item(HOPPER, 1));
				/*   5 */ add(new Reward("1 Bed")									.item(WHITE_BED, 1));
				/*   6 */ add(new Reward("1 Blast Furnace")							.item(BLAST_FURNACE, 1));
				/*   7 */ add(new Reward("1 Enchanting Table")						.item(ENCHANTING_TABLE, 1));
				/*   8 */ add(new Reward("1 Ender Chest")							.item(ENDER_CHEST, 1));
				/*   9 */ add(new Reward("8 Armor Stands")							.item(ARMOR_STAND, 8));
				/*  10 */ add(new Reward("1 Anvil")									.item(ANVIL, 1));
				/*  11 */ add(new Reward("2 Workbenches")							.item(CRAFTING_TABLE, 2));
				/*  12 */ add(new Reward("2 Chests")								.item(CHEST, 2));
				/*  13 */ add(new Reward("2 Furnaces")								.item(FURNACE, 2));
				/*  14 */ add(new Reward("2 Hoppers")								.item(HOPPER, 2));
				/*  15 */ add(new Reward("2 Beds")									.item(WHITE_BED, 2));
				/*  16 */ add(new Reward("12 Chorus Fruit")							.item(CHORUS_FRUIT, 12));
				/*  17 */ add(new Reward("16 Magma Cream")							.item(MAGMA_CREAM, 16));
				/*  18 */ add(new Reward("32 Repeaters, Comparators, Torches")		.item(REPEATER, 32).item(COMPARATOR, 32).item(REDSTONE_TORCH, 32));
				/*  19 */ add(new Reward("32 bricks")								.item(BRICKS, 32));
				/*  20 */ add(new Reward("1 of each Villager Workblock")			.item(MaterialTag.VILLAGER_WORKBLOCKS, 1));
				/*  21 */ add(new Reward("10 Workbenches")							.item(CRAFTING_TABLE, 10));
				/*  22 */ add(new Reward("10 Chests")								.item(CHEST, 10));
				/*  23 */ add(new Reward("10 Furnaces")								.item(FURNACE, 10));
				/*  24 */ add(new Reward("10 Hoppers")								.item(HOPPER, 10));
				/*  25 */ add(new Reward("10 Beds")									.item(WHITE_BED, 10));
				/*  26 */ add(new Reward("16 Cobwebs")								.item(COBWEB, 16));
				/*  27 */ add(new Reward("16 Minecarts")							.item(MINECART, 16));
				/*  28 */ add(new Reward("8 of each Glazed Terracotta")				.item(MaterialTag.GLAZED_TERRACOTTAS, 8));
				/*  29 */ add(new Reward("32 Packed Ice")							.item(PACKED_ICE, 32));
				/*  30 */ add(new Reward("8 Pistons, 8 Sticky Pistons")				.item(PISTON, 8).item(STICKY_PISTON, 8));
				/*  31 */ add(new Reward("10 Workbenches")							.item(CRAFTING_TABLE, 10));
				/*  32 */ add(new Reward("10 Chests")								.item(CHEST, 10));
				/*  33 */ add(new Reward("10 Furnaces")								.item(FURNACE, 10));
				/*  34 */ add(new Reward("10 Hoppers")								.item(HOPPER, 10));
				/*  35 */ add(new Reward("10 Beds")									.item(WHITE_BED, 10));
				/*  36 */ add(new Reward("32 Item Frames")							.item(ITEM_FRAME, 32));
				/*  37 */ add(new Reward("32 Prismarine Blocks")					.item(PRISMARINE, 32));
				/*  38 */ add(new Reward("16 Cauldron")								.item(CAULDRON, 16));
				/*  39 */ add(new Reward("32 Nether Bricks")						.item(NETHER_BRICK, 32));
				/*  40 */ add(new Reward("32 Endstone Blocks")						.item(END_STONE, 32));
				/*  41 */ add(new Reward("15 Workbenches")							.item(CRAFTING_TABLE, 15));
				/*  42 */ add(new Reward("15 Chests")								.item(CHEST, 15));
				/*  43 */ add(new Reward("15 Furnaces")								.item(FURNACE, 15));
				/*  44 */ add(new Reward("15 Hoppers")								.item(HOPPER, 15));
				/*  45 */ add(new Reward("15 Beds")									.item(WHITE_BED, 15));
				/*  46 */ add(new Reward("32 Redstone Lamps")						.item(REDSTONE_LAMP, 32));
				/*  47 */ add(new Reward("32 Sea Lanterns")							.item(SEA_LANTERN, 32));
				/*  48 */ add(new Reward("12 Note Blocks")							.item(NOTE_BLOCK, 12));
				/*  49 */ add(new Reward("50 Enchanting Levels")					.levels(50));
				/*  50 */ add(new Reward("32 End Rods")								.item(END_ROD, 32));
				/*  51 */ add(new Reward("15 Workbenches")							.item(CRAFTING_TABLE, 15));
				/*  52 */ add(new Reward("15 Chests")								.item(CHEST, 15));
				/*  53 */ add(new Reward("15 Furnaces")								.item(FURNACE, 15));
				/*  54 */ add(new Reward("15 Hoppers")								.item(HOPPER, 15));
				/*  55 */ add(new Reward("15 Beds")									.item(WHITE_BED, 15));
				/*  56 */ add(new Reward("32 Bookshelves")							.item(BOOKSHELF, 32));
				/*  57 */ add(new Reward("32 Coarse Dirt")							.item(COARSE_DIRT, 32));
				/*  58 */ add(new Reward("75 Enchanting Levels")					.levels(75));
				/*  59 */ add(new Reward("32 Brewing Stands")						.item(BREWING_STAND, 32));
				/*  60 */ add(new Reward("2 Parrot Spawn Eggs")						.item(PARROT_SPAWN_EGG, 2));
				/*  61 */ add(new Reward("20 Bookshelves")							.item(BOOKSHELF, 20));
				/*  62 */ add(new Reward("20 Chests")								.item(CHEST, 20));
				/*  63 */ add(new Reward("20 Soul Lanterns")						.item(SOUL_LANTERN, 20));
				/*  64 */ add(new Reward("20 Hoppers")								.item(HOPPER, 20));
				/*  65 */ add(new Reward("20 Note Blocks")							.item(NOTE_BLOCK, 20));
				/*  66 */ add(new Reward("4 Ender Chests")							.item(ENDER_CHEST, 4));
				/*  67 */ add(new Reward("64 Lily Pads")							.item(LILY_PAD, 64));
				/*  68 */ add(new Reward("64 Magma Blocks")							.item(MAGMA_BLOCK, 64));
				/*  69 */ add(new Reward("3 Anvils")								.item(ANVIL, 3));
				/*  70 */ add(new Reward("64 Slime Blocks")							.item(SLIME_BLOCK, 64));
				/*  71 */ add(new Reward("25 Bookshelves")							.item(BOOKSHELF, 25));
				/*  72 */ add(new Reward("25 Chests")								.item(CHEST, 25));
				/*  73 */ add(new Reward("25 Vote Points")							.votePoints(25));
				/*  74 */ add(new Reward("25 Hoppers")								.item(HOPPER, 25));
				/*  75 */ add(new Reward("25 Note Blocks")							.item(NOTE_BLOCK, 25));
				/*  76 */ add(new Reward("64 Vines")								.item(VINE, 64));
				/*  77 */ add(new Reward("64 Paintings")							.item(PAINTING, 64));
				/*  78 */ add(new Reward("64 Cocoa Beans")							.item(COCOA_BEANS, 64));
				/*  79 */ add(new Reward("64 Mycelium")								.item(MYCELIUM, 64));
				/*  80 */ add(new Reward("5 Sponges")								.item(SPONGE, 5));
				/*  81 */ add(new Reward("35 Bookshelves")							.item(BOOKSHELF, 35));
				/*  82 */ add(new Reward("35 Chests")								.item(CHEST, 35));
				/*  83 */ add(new Reward("2 Netherite Ingots")						.item(NETHERITE_INGOT, 2));
				/*  84 */ add(new Reward("35 Hoppers")								.item(HOPPER, 35));
				/*  85 */ add(new Reward("35 Note Blocks")							.item(NOTE_BLOCK, 35));
				/*  86 */ add(new Reward("12 White Banners")						.item(WHITE_BANNER, 12));
				/*  87 */ add(new Reward("100 Enchanting Levels")					.levels(100));
				/*  88 */ add(new Reward("2 Ocelot Spawn Eggs")						.item(OCELOT_SPAWN_EGG, 2));
				/*  89 */ add(new Reward("16 Dragon's Breath")						.item(DRAGON_BREATH, 16));
				/*  90 */ add(new Reward("Custom Player Head")						.item(PLAYER_HEAD, 1));
				/*  91 */ add(new Reward("50 Bookshelves")							.item(BOOKSHELF, 50));
				/*  92 */ add(new Reward("50 Chests")								.item(CHEST, 50));
				/*  93 */ add(new Reward("5 Netherite Ingots")						.item(NETHERITE_INGOT, 5));
				/*  94 */ add(new Reward("Maxed Bow")								.item(new ItemBuilder(BOW).enchantMax(ARROW_DAMAGE).enchantMax(ARROW_KNOCKBACK).enchantMax(ARROW_INFINITE).enchant(MENDING)));
				/*  95 */ add(new Reward("5 Crying Obsidian")						.item(CRYING_OBSIDIAN, 5));
				/*  96 */ add(new Reward("16 of each color Wool")					.item(MaterialTag.WOOL, 16));
				/*  97 */ add(new Reward("150 Enchanting Levels")					.levels(150));
				/*  98 */ add(new Reward("64 Podzol")								.item(PODZOL, 64));
				/*  99 */ add(new Reward("1 Shulker Box")							.item(CYAN_SHULKER_BOX, 1));
				/* 100 */ add(new Reward("64 Sea Lanterns")							.item(SEA_LANTERN, 64));
			}};

			default -> new ArrayList<>();
		};
	}
	// @formatter:on;

}
