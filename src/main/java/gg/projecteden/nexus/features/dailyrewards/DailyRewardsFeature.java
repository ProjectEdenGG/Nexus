package gg.projecteden.nexus.features.dailyrewards;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.staff.admin.CouponCommand;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser.DailyStreak;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUserService;
import gg.projecteden.nexus.models.dailyreward.Reward;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
			case 1 -> List.of(
				/*   1 */ new Reward("5 Cooked Chicken")						.item(Material.COOKED_CHICKEN, 5),
				/*   2 */ new Reward("5 Steak")								.item(Material.COOKED_BEEF, 5),
				/*   3 */ new Reward("10 Leather")							.item(Material.LEATHER, 10),
				/*   4 */ new Reward("10 Bread")								.item(Material.BREAD, 10),
				/*   5 */ new Reward("2 Golden Apples")						.item(Material.GOLDEN_APPLE, 2),
				/*   6 */ new Reward("Set of Iron tools")						.item(MaterialTag.TOOLS_IRON),
				/*   7 */ new Reward("16 books")								.item(Material.BOOK, 16),
				/*   8 */ new Reward("Carrot on a Stick")						.item(Material.CARROT_ON_A_STICK),
				/*   9 */ new Reward("12 Fire Charges")						.item(Material.FIRE_CHARGE, 12),
				/*  10 */ new Reward("$1,000")								.money(1000),
				/*  11 */ new Reward("5 Cooked Chicken")						.item(Material.COOKED_CHICKEN, 5),
				/*  12 */ new Reward("5 Steak")								.item(Material.COOKED_BEEF, 5),
				/*  13 */ new Reward("10 Leather")							.item(Material.LEATHER, 10),
				/*  14 */ new Reward("10 Bread")								.item(Material.BREAD, 10),
				/*  15 */ new Reward("3 Golden Apples")						.item(Material.GOLDEN_APPLE, 3),
				/*  16 */ new Reward("1 Diamond Pickaxe")						.item(Material.DIAMOND_PICKAXE, 1),
				/*  17 */ new Reward("2 Lava, Water, Milk, Empty buckets")	.item(Material.LAVA_BUCKET, 2).item(Material.WATER_BUCKET, 2).item(Material.MILK_BUCKET, 2).item(Material.BUCKET, 2),
				/*  18 */ new Reward("4 Regen 2 Potions")						.item(new ItemBuilder(Material.POTION).amount(4).potionType(PotionType.REGENERATION)),
				/*  19 */ new Reward("4 Health 2 Potions")					.item(new ItemBuilder(Material.POTION).amount(4).potionType(PotionType.STRONG_HEALING)),
				/*  20 */ new Reward("$2,000")								.money(2000),
				/*  21 */ new Reward("10 Cooked Chicken")						.item(Material.COOKED_CHICKEN, 10),
				/*  22 */ new Reward("10 Steak")								.item(Material.COOKED_BEEF, 10),
				/*  23 */ new Reward("20 Leather")							.item(Material.LEATHER, 20),
				/*  24 */ new Reward("20 Bread")								.item(Material.BREAD, 20),
				/*  25 */ new Reward("2 Golden Apple")						.item(Material.GOLDEN_APPLE, 2),
				/*  26 */ new Reward("Diamond sword and shield")				.item(Material.DIAMOND_SWORD).item(Material.SHIELD),
				/*  27 */ new Reward("5 of each Sapling")						.item(MaterialTag.ALL_SAPLINGS, 5),
				/*  28 */ new Reward("32 Apples")								.item(Material.APPLE, 32),
				/*  29 */ new Reward("1 Saddle")								.item(Material.SADDLE, 1),
				/*  30 */ new Reward("$4,000")								.money(4000),
				/*  31 */ new Reward("10 Cooked Chicken")						.item(Material.COOKED_CHICKEN, 10),
				/*  32 */ new Reward("10 Steak")								.item(Material.COOKED_BEEF, 10),
				/*  33 */ new Reward("20 Leather")							.item(Material.LEATHER, 20),
				/*  34 */ new Reward("20 Bread")								.item(Material.BREAD, 20),
				/*  35 */ new Reward("4 Golden Apples")						.item(Material.GOLDEN_APPLE, 4),
				/*  36 */ new Reward("1 Silk Touch")							.item(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchant.SILK_TOUCH)),
				/*  37 */ new Reward("1 Mending")								.item(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchant.MENDING)),
				/*  38 */ new Reward("1 Unbreaking 3")						.item(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchant.UNBREAKING, 3)),
				/*  39 */ new Reward("1 Efficiency 5")						.item(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchant.EFFICIENCY, 5)),
				/*  40 */ new Reward("$4,000")								.money(4000),
				/*  41 */ new Reward("15 Cooked Chicken")						.item(Material.COOKED_CHICKEN, 15),
				/*  42 */ new Reward("15 Steak")								.item(Material.COOKED_BEEF, 15),
				/*  43 */ new Reward("30 Leather")							.item(Material.LEATHER, 30),
				/*  44 */ new Reward("30 Bread")								.item(Material.BREAD, 30),
				/*  45 */ new Reward("6 Golden Apples")						.item(Material.GOLDEN_APPLE, 6),
				/*  46 */ new Reward("16 eyes of ender")						.item(Material.ENDER_EYE, 16),
				/*  47 */ new Reward("32 Slime Balls")						.item(Material.SLIME_BALL, 32),
				/*  48 */ new Reward("15 VPS")								.votePoints(15),
				/*  49 */ new Reward("Totem Of Undying")						.item(Material.TOTEM_OF_UNDYING),
				/*  50 */ new Reward("$6,000")								.money(6000),
				/*  51 */ new Reward("15 Cooked Chicken")						.item(Material.COOKED_CHICKEN, 15),
				/*  52 */ new Reward("15 Steak")								.item(Material.COOKED_BEEF, 15),
				/*  53 */ new Reward("30 Leather")							.item(Material.LEATHER, 30),
				/*  54 */ new Reward("30 Bread")								.item(Material.BREAD, 30),
				/*  55 */ new Reward("6 Golden Apples")						.item(Material.GOLDEN_APPLE, 6),
				/*  56 */ new Reward("32 Leads")								.item(Material.LEAD, 32),
				/*  57 */ new Reward("Skeleton Skull")						.item(Material.SKELETON_SKULL),
				/*  58 */ new Reward("1 Elytra")								.item(Material.ELYTRA, 1),
				/*  59 */ new Reward("8 Enchanted Golden Apples")				.item(Material.ENCHANTED_GOLDEN_APPLE, 8),
				/*  60 */ new Reward("$6,000")								.money(6000),
				/*  61 */ new Reward("20 Cooked Chicken")						.item(Material.COOKED_CHICKEN, 20),
				/*  62 */ new Reward("20 Steak")								.item(Material.COOKED_BEEF, 20),
				/*  63 */ new Reward("40 Leather")							.item(Material.LEATHER, 40),
				/*  64 */ new Reward("40 Bread")								.item(Material.BREAD, 40),
				/*  65 */ new Reward("8 Golden Apples")						.item(Material.GOLDEN_APPLE, 8),
				/*  66 */ new Reward("Set of Chainmail Armor")				.item(MaterialTag.ARMOR_CHAINMAIL),
				/*  67 */ new Reward("64 Spectral Arrows")					.item(Material.SPECTRAL_ARROW, 64),
				/*  68 */ new Reward("Power 5 Book")							.item(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchant.POWER, 5)),
				/*  69 */ new Reward("16 Name Tags")							.item(Material.NAME_TAG, 16),
				/*  70 */ new Reward("$8,000")								.money(8000),
				/*  71 */ new Reward("25 Cooked Chicken")						.item(Material.COOKED_CHICKEN, 25),
				/*  72 */ new Reward("25 Steak")								.item(Material.COOKED_BEEF, 25),
				/*  73 */ new Reward("45 Leather")							.item(Material.LEATHER, 45),
				/*  74 */ new Reward("45 Bread")								.item(Material.BREAD, 45),
				/*  75 */ new Reward("1 Enchanted Golden Apple")				.item(Material.ENCHANTED_GOLDEN_APPLE, 1),
				/*  76 */ new Reward("64 Pumpkin Pie")						.item(Material.PUMPKIN_PIE, 64),
				/*  77 */ new Reward("16 firework stars")						.item(Material.FIREWORK_STAR, 16),
				/*  78 */ new Reward("2 Villager Spawn Eggs")					.item(Material.VILLAGER_SPAWN_EGG, 2),
				/*  79 */ new Reward("1 Wither Skeleton Skull")				.item(Material.WITHER_SKELETON_SKULL, 1),
				/*  80 */ new Reward("$10,000")								.money(10000),
				/*  81 */ new Reward("35 Cooked Chicken")						.item(Material.COOKED_CHICKEN, 35),
				/*  82 */ new Reward("35 Steak")								.item(Material.COOKED_BEEF, 35),
				/*  83 */ new Reward("70 Leather")							.item(Material.LEATHER, 70),
				/*  84 */ new Reward("Coupon for 2 McMMO levels")				.item(CouponCommand.getGenericCoupon("mcmmo", 2)),
				/*  85 */ new Reward("2 Horse Spawn Eggs")					.item(Material.HORSE_SPAWN_EGG, 2),
				/*  86 */ new Reward("3 Enchanted Golden Apples")				.item(Material.ENCHANTED_GOLDEN_APPLE, 3),
				/*  87 */ new Reward("Iron, Gold, Diamond Horse Armor")		.item(Material.IRON_HORSE_ARMOR).item(Material.GOLDEN_HORSE_ARMOR).item(Material.DIAMOND_HORSE_ARMOR),
				/*  88 */ new Reward("64 Golden Carrots")						.item(Material.GOLDEN_CARROT, 64),
				/*  89 */ new Reward("16 End Crystals")						.item(Material.END_CRYSTAL, 16),
				/*  90 */ new Reward("$15,000")								.money(15000),
				/*  91 */ new Reward("100 Cooked Chicken")					.item(Material.COOKED_CHICKEN, 100),
				/*  92 */ new Reward("100 Steak")								.item(Material.COOKED_BEEF, 100),
				/*  93 */ new Reward("200 Leather")							.item(Material.LEATHER, 200),
				/*  94 */ new Reward("Coupon for 5 McMMO levels")				.item(CouponCommand.getGenericCoupon("mcmmo", 5)),
				/*  95 */ new Reward("5 Enchanted Golden Apples")				.item(Material.ENCHANTED_GOLDEN_APPLE, 5),
				/*  96 */ new Reward("Super Fishing Pole")					.item(new ItemBuilder(Material.FISHING_ROD).enchant(Enchant.LURE, 5).enchant(Enchant.LUCK_OF_THE_SEA, 3).enchant(Enchant.UNBREAKING, 4)),
				/*  97 */ new Reward("Maxed Diamond Sword")					.item(new ItemBuilder(Material.DIAMOND_SWORD).enchantMax(Enchant.SHARPNESS).enchantMax(Enchant.MENDING).enchantMax(Enchant.FIRE_ASPECT).enchantMax(Enchant.KNOCKBACK).enchant(Enchant.UNBREAKING, 4).enchant(Enchant.SWEEPING_EDGE, 3).enchant(Enchant.LOOTING, 3)),
				/*  98 */ new Reward("Full set of Diamond gear and tools")	.item(MaterialTag.ARMOR_DIAMOND).item(MaterialTag.TOOLS_DIAMOND),
				/*  99 */ new Reward("Maxed Diamond Pickaxe")					.item(new ItemBuilder(Material.DIAMOND_PICKAXE).enchantMax(Enchant.EFFICIENCY).enchant(Enchant.MENDING).enchant(Enchant.UNBREAKING, 4)),
				/* 100 */ new Reward("$20,000")								.money(20000)
			);

			case 2 -> List.of(
				/*   1 */ new Reward("1 Coal Block")							.item(Material.COAL_BLOCK, 1),
				/*   2 */ new Reward("1 Redstone Block")						.item(Material.REDSTONE_BLOCK, 1),
				/*   3 */ new Reward("1 Iron Block")							.item(Material.IRON_BLOCK, 1),
				/*   4 */ new Reward("1 Lapis Block")							.item(Material.LAPIS_BLOCK, 1),
				/*   5 */ new Reward("1 Gold Block")							.item(Material.GOLD_BLOCK, 1),
				/*   6 */ new Reward("5 Glowstone")							.item(Material.GLOWSTONE, 5),
				/*   7 */ new Reward("5 Obsidian")							.item(Material.OBSIDIAN, 5),
				/*   8 */ new Reward("5 Quartz Blocks")						.item(Material.QUARTZ_BLOCK, 5),
				/*   9 */ new Reward("5 Emeralds")							.item(Material.EMERALD, 5),
				/*  10 */ new Reward("5 Diamonds")							.item(Material.DIAMOND, 5),
				/*  11 */ new Reward("1 Coal Block")							.item(Material.COAL_BLOCK, 1),
				/*  12 */ new Reward("2 Redstone Block")						.item(Material.REDSTONE_BLOCK, 2),
				/*  13 */ new Reward("1 Iron Block")							.item(Material.IRON_BLOCK, 1),
				/*  14 */ new Reward("1 Lapis Block")							.item(Material.LAPIS_BLOCK, 1),
				/*  15 */ new Reward("1 Gold Block")							.item(Material.GOLD_BLOCK, 1),
				/*  16 */ new Reward("5 Glowstone")							.item(Material.GLOWSTONE, 5),
				/*  17 */ new Reward("5 Obsidian")							.item(Material.OBSIDIAN, 5),
				/*  18 */ new Reward("5 Quartz Blocks")						.item(Material.QUARTZ_BLOCK, 5),
				/*  19 */ new Reward("1 Emerald Block")						.item(Material.EMERALD_BLOCK, 1),
				/*  20 */ new Reward("1 Diamond Block")						.item(Material.DIAMOND_BLOCK, 1),
				/*  21 */ new Reward("2 Coal Block")							.item(Material.COAL_BLOCK, 2),
				/*  22 */ new Reward("2 Redstone Block")						.item(Material.REDSTONE_BLOCK, 2),
				/*  23 */ new Reward("2 Iron Block")							.item(Material.IRON_BLOCK, 2),
				/*  24 */ new Reward("2 Lapis Block")							.item(Material.LAPIS_BLOCK, 2),
				/*  25 */ new Reward("2 Gold Block")							.item(Material.GOLD_BLOCK, 2),
				/*  26 */ new Reward("10 Glowstone")							.item(Material.GLOWSTONE, 10),
				/*  27 */ new Reward("10 Obsidian")							.item(Material.OBSIDIAN, 10),
				/*  28 */ new Reward("10 Quartz Blocks")						.item(Material.QUARTZ_BLOCK, 10),
				/*  29 */ new Reward("2 Emerald Block")						.item(Material.EMERALD_BLOCK, 2),
				/*  30 */ new Reward("2 Diamond Block")						.item(Material.DIAMOND_BLOCK, 2),
				/*  31 */ new Reward("2 Coal Block")							.item(Material.COAL_BLOCK, 2),
				/*  32 */ new Reward("3 Redstone Block")						.item(Material.REDSTONE_BLOCK, 3),
				/*  33 */ new Reward("2 Iron Block")							.item(Material.IRON_BLOCK, 2),
				/*  34 */ new Reward("2 Lapis Block")							.item(Material.LAPIS_BLOCK, 2),
				/*  35 */ new Reward("2 Gold Block")							.item(Material.GOLD_BLOCK, 2),
				/*  36 */ new Reward("10 Glowstone")							.item(Material.GLOWSTONE, 10),
				/*  37 */ new Reward("10 Obsidian")							.item(Material.OBSIDIAN, 10),
				/*  38 */ new Reward("10 Quartz Blocks")						.item(Material.QUARTZ_BLOCK, 10),
				/*  39 */ new Reward("2 Emerald Block")						.item(Material.EMERALD_BLOCK, 2),
				/*  40 */ new Reward("2 Diamond Block")						.item(Material.DIAMOND_BLOCK, 2),
				/*  41 */ new Reward("3 Coal Block")							.item(Material.COAL_BLOCK, 3),
				/*  42 */ new Reward("3 Redstone Block")						.item(Material.REDSTONE_BLOCK, 3),
				/*  43 */ new Reward("3 Iron Block")							.item(Material.IRON_BLOCK, 3),
				/*  44 */ new Reward("3 Lapis Block")							.item(Material.LAPIS_BLOCK, 3),
				/*  45 */ new Reward("3 Gold Block")							.item(Material.GOLD_BLOCK, 3),
				/*  46 */ new Reward("15 Glowstone")							.item(Material.GLOWSTONE, 15),
				/*  47 */ new Reward("15 Obsidian")							.item(Material.OBSIDIAN, 15),
				/*  48 */ new Reward("15 Quartz Blocks")						.item(Material.QUARTZ_BLOCK, 15),
				/*  49 */ new Reward("3 Emerald Block")						.item(Material.EMERALD_BLOCK, 3),
				/*  50 */ new Reward("3 Diamond Block")						.item(Material.DIAMOND_BLOCK, 3),
				/*  51 */ new Reward("3 Coal Block")							.item(Material.COAL_BLOCK, 3),
				/*  52 */ new Reward("4 Redstone Block")						.item(Material.REDSTONE_BLOCK, 4),
				/*  53 */ new Reward("3 Iron Block")							.item(Material.IRON_BLOCK, 3),
				/*  54 */ new Reward("3 Lapis Block")							.item(Material.LAPIS_BLOCK, 3),
				/*  55 */ new Reward("3 Gold Block")							.item(Material.GOLD_BLOCK, 3),
				/*  56 */ new Reward("15 Glowstone")							.item(Material.GLOWSTONE, 15),
				/*  57 */ new Reward("15 Obsidian")							.item(Material.OBSIDIAN, 15),
				/*  58 */ new Reward("15 Quartz Blocks")						.item(Material.QUARTZ_BLOCK, 15),
				/*  59 */ new Reward("3 Emerald Block")						.item(Material.EMERALD_BLOCK, 3),
				/*  60 */ new Reward("3 Diamond Block")						.item(Material.DIAMOND_BLOCK, 3),
				/*  61 */ new Reward("4 Coal Block")							.item(Material.COAL_BLOCK, 4),
				/*  62 */ new Reward("5 Redstone Block")						.item(Material.REDSTONE_BLOCK, 5),
				/*  63 */ new Reward("4 Iron Block")							.item(Material.IRON_BLOCK, 4),
				/*  64 */ new Reward("4 Lapis Block")							.item(Material.LAPIS_BLOCK, 4),
				/*  65 */ new Reward("4 Gold Block")							.item(Material.GOLD_BLOCK, 4),
				/*  66 */ new Reward("20 Shroomlight")						.item(Material.SHROOMLIGHT, 20),
				/*  67 */ new Reward("20 Obsidian")							.item(Material.OBSIDIAN, 20),
				/*  68 */ new Reward("20 Quartz Blocks")						.item(Material.QUARTZ_BLOCK, 20),
				/*  69 */ new Reward("4 Emerald Block")						.item(Material.EMERALD_BLOCK, 4),
				/*  70 */ new Reward("4 Diamond Block")						.item(Material.DIAMOND_BLOCK, 4),
				/*  71 */ new Reward("5 Coal Block")							.item(Material.COAL_BLOCK, 5),
				/*  72 */ new Reward("7 Redstone Block")						.item(Material.REDSTONE_BLOCK, 7),
				/*  73 */ new Reward("5 Iron Block")							.item(Material.IRON_BLOCK, 5),
				/*  74 */ new Reward("5 Lapis Block")							.item(Material.LAPIS_BLOCK, 5),
				/*  75 */ new Reward("5 Gold Block")							.item(Material.GOLD_BLOCK, 5),
				/*  76 */ new Reward("25 Glowstone")							.item(Material.GLOWSTONE, 25),
				/*  77 */ new Reward("25 Obsidian")							.item(Material.OBSIDIAN, 25),
				/*  78 */ new Reward("25 Quartz Blocks")						.item(Material.QUARTZ_BLOCK, 25),
				/*  79 */ new Reward("5 Emerald Block")						.item(Material.EMERALD_BLOCK, 5),
				/*  80 */ new Reward("5 Diamond Block")						.item(Material.DIAMOND_BLOCK, 5),
				/*  81 */ new Reward("7 Coal Block")							.item(Material.COAL_BLOCK, 7),
				/*  82 */ new Reward("10 Redstone Block")						.item(Material.REDSTONE_BLOCK, 10),
				/*  83 */ new Reward("7 Iron Block")							.item(Material.IRON_BLOCK, 7),
				/*  84 */ new Reward("7 Lapis Block")							.item(Material.LAPIS_BLOCK, 7),
				/*  85 */ new Reward("7 Gold Block")							.item(Material.GOLD_BLOCK, 7),
				/*  86 */ new Reward("35 Shroomlight")						.item(Material.SHROOMLIGHT, 35),
				/*  87 */ new Reward("35 Obsidian")							.item(Material.OBSIDIAN, 35),
				/*  88 */ new Reward("35 Quartz Blocks")						.item(Material.QUARTZ_BLOCK, 35),
				/*  89 */ new Reward("7 Emerald Block")						.item(Material.EMERALD_BLOCK, 7),
				/*  90 */ new Reward("7 Diamond Block")						.item(Material.DIAMOND_BLOCK, 7),
				/*  91 */ new Reward("10 Coal Block")							.item(Material.COAL_BLOCK, 10),
				/*  92 */ new Reward("10 Restone Blocks")						.item(Material.REDSTONE_BLOCK, 10),
				/*  93 */ new Reward("10 Iron Block")							.item(Material.IRON_BLOCK, 10),
				/*  94 */ new Reward("10 Lapis Block")						.item(Material.LAPIS_BLOCK, 10),
				/*  95 */ new Reward("10 Gold Blocks")						.item(Material.GOLD_BLOCK, 10),
				/*  96 */ new Reward("100 Glowstone")							.item(Material.GLOWSTONE, 100),
				/*  97 */ new Reward("100 Obsidian")							.item(Material.OBSIDIAN, 100),
				/*  98 */ new Reward("100 Quartz Blocks")						.item(Material.QUARTZ_BLOCK, 100),
				/*  99 */ new Reward("10 Emerald Blocks")						.item(Material.EMERALD_BLOCK, 10),
				/* 100 */ new Reward("10 Diamond Blocks")						.item(Material.DIAMOND_BLOCK, 10)
			);

			case 3 -> List.of(
				/*   1 */ new Reward("1 Workbench")							.item(Material.CRAFTING_TABLE, 1),
				/*   2 */ new Reward("1 Chest")								.item(Material.CHEST, 1),
				/*   3 */ new Reward("1 Furnace")								.item(Material.FURNACE, 1),
				/*   4 */ new Reward("1 Hopper")								.item(Material.HOPPER, 1),
				/*   5 */ new Reward("1 Bed")									.item(Material.WHITE_BED, 1),
				/*   6 */ new Reward("1 Blast Furnace")						.item(Material.BLAST_FURNACE, 1),
				/*   7 */ new Reward("1 Enchanting Table")					.item(Material.ENCHANTING_TABLE, 1),
				/*   8 */ new Reward("1 Ender Chest")							.item(Material.ENDER_CHEST, 1),
				/*   9 */ new Reward("8 Armor Stands")						.item(Material.ARMOR_STAND, 8),
				/*  10 */ new Reward("1 Anvil")								.item(Material.ANVIL, 1),
				/*  11 */ new Reward("2 Workbenches")							.item(Material.CRAFTING_TABLE, 2),
				/*  12 */ new Reward("2 Chests")								.item(Material.CHEST, 2),
				/*  13 */ new Reward("2 Furnaces")							.item(Material.FURNACE, 2),
				/*  14 */ new Reward("2 Hoppers")								.item(Material.HOPPER, 2),
				/*  15 */ new Reward("2 Beds")								.item(Material.WHITE_BED, 2),
				/*  16 */ new Reward("12 Chorus Fruit")						.item(Material.CHORUS_FRUIT, 12),
				/*  17 */ new Reward("16 Magma Cream")						.item(Material.MAGMA_CREAM, 16),
				/*  18 */ new Reward("32 Repeaters, Comparators, Torches")	.item(Material.REPEATER, 32).item(Material.COMPARATOR, 32).item(Material.REDSTONE_TORCH, 32),
				/*  19 */ new Reward("32 bricks")								.item(Material.BRICKS, 32),
				/*  20 */ new Reward("1 of each Villager Workblock")			.item(MaterialTag.VILLAGER_WORKBLOCKS, 1),
				/*  21 */ new Reward("10 Workbenches")						.item(Material.CRAFTING_TABLE, 10),
				/*  22 */ new Reward("10 Chests")								.item(Material.CHEST, 10),
				/*  23 */ new Reward("10 Furnaces")							.item(Material.FURNACE, 10),
				/*  24 */ new Reward("10 Hoppers")							.item(Material.HOPPER, 10),
				/*  25 */ new Reward("10 Beds")								.item(Material.WHITE_BED, 10),
				/*  26 */ new Reward("16 Cobwebs")							.item(Material.COBWEB, 16),
				/*  27 */ new Reward("16 Minecarts")							.item(Material.MINECART, 16),
				/*  28 */ new Reward("8 of each Glazed Terracotta")			.item(MaterialTag.GLAZED_TERRACOTTAS, 8),
				/*  29 */ new Reward("32 Packed Ice")							.item(Material.PACKED_ICE, 32),
				/*  30 */ new Reward("8 Pistons, 8 Sticky Pistons")			.item(Material.PISTON, 8).item(Material.STICKY_PISTON, 8),
				/*  31 */ new Reward("10 Workbenches")						.item(Material.CRAFTING_TABLE, 10),
				/*  32 */ new Reward("10 Chests")								.item(Material.CHEST, 10),
				/*  33 */ new Reward("10 Furnaces")							.item(Material.FURNACE, 10),
				/*  34 */ new Reward("10 Hoppers")							.item(Material.HOPPER, 10),
				/*  35 */ new Reward("10 Beds")								.item(Material.WHITE_BED, 10),
				/*  36 */ new Reward("32 Item Frames")						.item(Material.ITEM_FRAME, 32),
				/*  37 */ new Reward("32 Prismarine Blocks")					.item(Material.PRISMARINE, 32),
				/*  38 */ new Reward("16 Cauldron")							.item(Material.CAULDRON, 16),
				/*  39 */ new Reward("32 Nether Bricks")						.item(Material.NETHER_BRICK, 32),
				/*  40 */ new Reward("32 Endstone Blocks")					.item(Material.END_STONE, 32),
				/*  41 */ new Reward("15 Workbenches")						.item(Material.CRAFTING_TABLE, 15),
				/*  42 */ new Reward("15 Chests")								.item(Material.CHEST, 15),
				/*  43 */ new Reward("15 Furnaces")							.item(Material.FURNACE, 15),
				/*  44 */ new Reward("15 Hoppers")							.item(Material.HOPPER, 15),
				/*  45 */ new Reward("15 Beds")								.item(Material.WHITE_BED, 15),
				/*  46 */ new Reward("32 Redstone Lamps")						.item(Material.REDSTONE_LAMP, 32),
				/*  47 */ new Reward("32 Sea Lanterns")						.item(Material.SEA_LANTERN, 32),
				/*  48 */ new Reward("12 Note Blocks")						.item(Material.NOTE_BLOCK, 12),
				/*  49 */ new Reward("50 Enchanting Levels")					.levels(50),
				/*  50 */ new Reward("32 End Rods")							.item(Material.END_ROD, 32),
				/*  51 */ new Reward("15 Workbenches")						.item(Material.CRAFTING_TABLE, 15),
				/*  52 */ new Reward("15 Chests")								.item(Material.CHEST, 15),
				/*  53 */ new Reward("15 Furnaces")							.item(Material.FURNACE, 15),
				/*  54 */ new Reward("15 Hoppers")							.item(Material.HOPPER, 15),
				/*  55 */ new Reward("15 Beds")								.item(Material.WHITE_BED, 15),
				/*  56 */ new Reward("32 Bookshelves")						.item(Material.BOOKSHELF, 32),
				/*  57 */ new Reward("32 Coarse Dirt")						.item(Material.COARSE_DIRT, 32),
				/*  58 */ new Reward("75 Enchanting Levels")					.levels(75),
				/*  59 */ new Reward("32 Brewing Stands")						.item(Material.BREWING_STAND, 32),
				/*  60 */ new Reward("2 Parrot Spawn Eggs")					.item(Material.PARROT_SPAWN_EGG, 2),
				/*  61 */ new Reward("20 Bookshelves")						.item(Material.BOOKSHELF, 20),
				/*  62 */ new Reward("20 Chests")								.item(Material.CHEST, 20),
				/*  63 */ new Reward("20 Soul Lanterns")						.item(Material.SOUL_LANTERN, 20),
				/*  64 */ new Reward("20 Hoppers")							.item(Material.HOPPER, 20),
				/*  65 */ new Reward("20 Note Blocks")						.item(Material.NOTE_BLOCK, 20),
				/*  66 */ new Reward("4 Ender Chests")						.item(Material.ENDER_CHEST, 4),
				/*  67 */ new Reward("64 Lily Pads")							.item(Material.LILY_PAD, 64),
				/*  68 */ new Reward("64 Magma Blocks")						.item(Material.MAGMA_BLOCK, 64),
				/*  69 */ new Reward("3 Anvils")								.item(Material.ANVIL, 3),
				/*  70 */ new Reward("64 Slime Blocks")						.item(Material.SLIME_BLOCK, 64),
				/*  71 */ new Reward("25 Bookshelves")						.item(Material.BOOKSHELF, 25),
				/*  72 */ new Reward("25 Chests")								.item(Material.CHEST, 25),
				/*  73 */ new Reward("25 Vote Points")						.votePoints(25),
				/*  74 */ new Reward("25 Hoppers")							.item(Material.HOPPER, 25),
				/*  75 */ new Reward("25 Note Blocks")						.item(Material.NOTE_BLOCK, 25),
				/*  76 */ new Reward("64 Vines")								.item(Material.VINE, 64),
				/*  77 */ new Reward("64 Paintings")							.item(Material.PAINTING, 64),
				/*  78 */ new Reward("64 Cocoa Beans")						.item(Material.COCOA_BEANS, 64),
				/*  79 */ new Reward("64 Mycelium")							.item(Material.MYCELIUM, 64),
				/*  80 */ new Reward("5 Sponges")								.item(Material.SPONGE, 5),
				/*  81 */ new Reward("35 Bookshelves")						.item(Material.BOOKSHELF, 35),
				/*  82 */ new Reward("35 Chests")								.item(Material.CHEST, 35),
				/*  83 */ new Reward("2 Netherite Ingots")					.item(Material.NETHERITE_INGOT, 2),
				/*  84 */ new Reward("35 Hoppers")							.item(Material.HOPPER, 35),
				/*  85 */ new Reward("35 Note Blocks")						.item(Material.NOTE_BLOCK, 35),
				/*  86 */ new Reward("12 White Banners")						.item(Material.WHITE_BANNER, 12),
				/*  87 */ new Reward("100 Enchanting Levels")					.levels(100),
				/*  88 */ new Reward("2 Cat Spawn Eggs")						.item(Material.CAT_SPAWN_EGG, 2),
				/*  89 */ new Reward("16 Dragon's Breath")					.item(Material.DRAGON_BREATH, 16),
				/*  90 */ new Reward("Custom Player Head")					.item(Material.PLAYER_HEAD, 1),
				/*  91 */ new Reward("50 Bookshelves")						.item(Material.BOOKSHELF, 50),
				/*  92 */ new Reward("50 Chests")								.item(Material.CHEST, 50),
				/*  93 */ new Reward("5 Netherite Ingots")					.item(Material.NETHERITE_INGOT, 5),
				/*  94 */ new Reward("Maxed Bow")								.item(new ItemBuilder(Material.BOW).enchantMax(Enchant.POWER).enchantMax(Enchant.PUNCH).enchantMax(Enchant.INFINITY).enchant(Enchant.MENDING)),
				/*  95 */ new Reward("5 Crying Obsidian")						.item(Material.CRYING_OBSIDIAN, 5),
				/*  96 */ new Reward("16 of each color Wool")					.item(MaterialTag.WOOL, 16),
				/*  97 */ new Reward("150 Enchanting Levels")					.levels(150),
				/*  98 */ new Reward("64 Podzol")								.item(Material.PODZOL, 64),
				/*  99 */ new Reward("1 Shulker Box")							.item(Material.CYAN_SHULKER_BOX, 1),
				/* 100 */ new Reward("64 Sea Lanterns")						.item(Material.SEA_LANTERN, 64)
			);

			default -> Collections.emptyList();
		};
	}
	// @formatter:on;

}
