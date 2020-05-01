package me.pugabyte.bncore.features.dailyrewards;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.dailyreward.DailyReward;
import me.pugabyte.bncore.models.dailyreward.DailyRewardService;
import me.pugabyte.bncore.models.dailyreward.Reward;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class DailyRewardsFeature {
	private static List<Reward> rewards = new ArrayList<>();

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

	private void setupDailyRewards() {
		/*  1 */ rewards.add(new Reward("$100", 100));
		/*  2 */ rewards.add(new Reward("32 bread", new ItemStack(Material.BREAD, 32)));
		/*  3 */ rewards.add(new Reward("3 iron blocks", new ItemStack(Material.IRON_BLOCK, 3)));
		/*  4 */ rewards.add(new Reward("1 anvil", new ItemStack(Material.ANVIL, 1)));
		/*  5 */ rewards.add(new Reward("64 steak", new ItemStack(Material.COOKED_BEEF, 64)));
		/*  6 */ rewards.add(new Reward("32 glass", new ItemStack(Material.GLASS, 32)));
		/*  7 */ rewards.add(new Reward("a saddle", new ItemStack(Material.SADDLE)));
		/*  8 */ rewards.add(new Reward("$1,000", 1000));
		/*  9 */ rewards.add(new Reward("10 experience bottles", new ItemStack(Material.EXP_BOTTLE, 10)));
		/* 10 */ rewards.add(new Reward("32 leather", new ItemStack(Material.LEATHER, 32)));
		/* 11 */ rewards.add(new Reward("$2,000", 2000));
		/* 12 */ rewards.add(new Reward("32 apples", new ItemStack(Material.APPLE, 32)));
		/* 13 */ rewards.add(new Reward("5 diamonds", new ItemStack(Material.DIAMOND, 5)));
		/* 14 */ rewards.add(new Reward("32 coal blocks", new ItemStack(Material.COAL_BLOCK, 32)));
		/* 15 */ rewards.add(new Reward("a silk touch book", new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.SILK_TOUCH).build()));
		/* 16 */ rewards.add(new Reward("3 golden apples", new ItemStack(Material.GOLDEN_APPLE, 3)));
		/* 17 */ rewards.add(new Reward("8 healing potions", new ItemBuilder(Material.POTION).amount(8).effect(PotionEffectType.HEAL).build()));
		/* 18 */ rewards.add(new Reward("$2,500", 2500));
		/* 19 */ rewards.add(new Reward("32 blaze rods", new ItemStack(Material.BLAZE_ROD, 32)));
		/* 20 */ rewards.add(new Reward("8 ghast tears", new ItemStack(Material.GHAST_TEAR, 8)));
		/* 21 */ rewards.add(new Reward("diamond boots", new ItemStack(Material.DIAMOND_BOOTS)));
		/* 22 */ rewards.add(new Reward("16 powered rails and 128 normal rails", new ItemStack(Material.POWERED_RAIL, 16), new ItemStack(Material.RAILS, 128)));
		/* 23 */ rewards.add(new Reward("$3,000", 3000));
		List<ItemStack> dyes = new ArrayList<>();
		for (int i = 0; i <= 15; ++i) dyes.add(new ItemStack(Material.INK_SACK, 8, (short) i));
		/* 24 */ rewards.add(new Reward("8 of every dye", dyes));
		/* 25 */ rewards.add(new Reward("a bow with Power 2, Punch 1 and Unbreaking 2", new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 2).enchant(Enchantment.ARROW_KNOCKBACK).enchant(Enchantment.DURABILITY, 2).build()));
		/* 26 */ rewards.add(new Reward("5 gold blocks, 10 iron blocks and 3 emerald blocks", new ItemStack(Material.GOLD_BLOCK, 5), new ItemStack(Material.IRON_BLOCK, 10), new ItemStack(Material.EMERALD_BLOCK, 3)));
		/* 27 */ rewards.add(new Reward("50 experience levels", "exp give %player% 50L"));
		/* 28 */ rewards.add(new Reward("$3,500", 3500));
		/* 29 */ rewards.add(new Reward("8 emerald blocks", new ItemStack(Material.EMERALD_BLOCK, 8)));
		/* 30 */ rewards.add(new Reward("diamond boots with Frost Walker, Protection 1 and Unbreaking 2", new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.FROST_WALKER).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).enchant(Enchantment.DURABILITY, 2).build()));
		/* 31 */ rewards.add(new Reward("$4,000", 4000));
		/* 32 */
		rewards.add(new Reward("32 slime balls", new ItemStack(Material.SLIME_BALL, 32)));
		/* 33 */
		rewards.add(new Reward("4 redstone blocks", new ItemStack(Material.REDSTONE_BLOCK, 4)));
		/* 34 */
		rewards.add(new Reward("an enchanting table", new ItemStack(Material.ENCHANTMENT_TABLE)));
		/* 35 */
		rewards.add(new Reward("10 cakes", new ItemStack(Material.CAKE, 10)));
		/* 36 */
		rewards.add(new Reward("1 clock", new ItemStack(Material.WATCH)));
		/* 37 */
		rewards.add(new Reward("diamond horse armor", new ItemStack(Material.DIAMOND_BARDING)));
		/* 38 */
		rewards.add(new Reward("$4,500", 4500));
		/* 39 */
		rewards.add(new Reward("a totem of undying", new ItemStack(Material.TOTEM)));
		/* 40 */
		rewards.add(new Reward("18 books", new ItemStack(Material.BOOK, 18)));
		/* 41 */
		rewards.add(new Reward("$5,000", 5000));
		/* 42 */
		rewards.add(new Reward("3 notch apples", new ItemStack(Material.GOLDEN_APPLE, 3, (short) 1)));
		/* 43 */
		rewards.add(new Reward("6 empty maps and 1 book and quill", new ItemStack(Material.EMPTY_MAP, 6), new ItemStack(Material.BOOK_AND_QUILL)));
		/* 44 */
		rewards.add(new Reward("32 sea lanterns", new ItemStack(Material.SEA_LANTERN, 32)));
		/* 45 */
		rewards.add(new Reward("a mending book", new ItemBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.MENDING).build()));
		/* 46 */
		rewards.add(new Reward("3 brewing stands", new ItemStack(Material.BREWING_STAND_ITEM, 3)));
		/* 47 */
		rewards.add(new Reward("64 blaze rods", new ItemStack(Material.BLAZE_ROD, 64)));
		/* 48 */
		rewards.add(new Reward("$5,500", 5500));
		/* 49 */
		rewards.add(new Reward("6 armor stands", new ItemStack(Material.ARMOR_STAND, 6)));
		/* 50 */
		rewards.add(new Reward("a full set of chainmail armour", new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_BOOTS)));
		/* 51 */
		rewards.add(new Reward("diamond leggings", new ItemStack(Material.DIAMOND_LEGGINGS)));
		/* 52 */
		rewards.add(new Reward("4 water breathing potions", new ItemBuilder(Material.POTION).amount(4).effect(PotionEffectType.WATER_BREATHING, 8 * 60).build()));
		/* 53 */ rewards.add(new Reward("$6,000", 6000));
		/* 54 */ rewards.add(new Reward("a zombie skull and a creeper skull", new ItemStack(Material.SKULL_ITEM, 1, (short) 2), new ItemStack(Material.SKULL, 1, (short) 4)));
		/* 55 */ rewards.add(new Reward("a fishing rod with Lure 5, Luck 3, and Unbreaking 4", new ItemBuilder(Material.FISHING_ROD).enchant(Enchantment.LURE, 5).enchant(Enchantment.LUCK, 3).enchant(Enchantment.DURABILITY, 4).build()));
		/* 56 */ rewards.add(new Reward("2 villager spawn eggs", new ItemStack(Material.MONSTER_EGG, 2, EntityType.VILLAGER.getTypeId())));
		/* 57 */ rewards.add(new Reward("75 enchanting levels", "exp give %player% 75L"));
		/* 58 */ rewards.add(new Reward("$6,500", 6500));
		/* 59 */ rewards.add(new Reward("1 diamond helmet with Respiration 3, Aqua Affinity 1 and Unbreaking 2", new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.OXYGEN, 3).enchant(Enchantment.WATER_WORKER).enchant(Enchantment.DURABILITY, 2).build()));
		/* 60 */ rewards.add(new Reward("1 shulker box", new ItemStack(Material.PURPLE_SHULKER_BOX)));
	}



}
