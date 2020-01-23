package me.pugabyte.bncore.features.dailyrewards;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.models.dailyrewards.DailyReward;
import me.pugabyte.bncore.models.dailyrewards.DailyRewards;
import me.pugabyte.bncore.models.dailyrewards.DailyRewardsService;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class DailyRewardsFeature implements Listener {
	private List<DailyReward> dailyRewards = new ArrayList<>();

	public DailyRewardsFeature() {
		setupDailyRewards();
	}

	public int getMaxDays() {
		return dailyRewards.size();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		DailyRewardsService service = new DailyRewardsService();
		DailyRewards dailyReward = service.get(event.getPlayer());
		if (!dailyReward.isEarnedToday()) {
			dailyReward.setEarnedToday(true);
			dailyReward.increaseStreak();
			service.save(dailyReward);
		}
	}

	public static void menu(Player player, DailyRewards dailyRewards) {
		SmartInventory inv = SmartInventory.builder()
				.provider(new DailyRewardsMenu(dailyRewards))
				.size(3, 9)
				.title(ChatColor.DARK_AQUA + "Daily Rewards")
				.build();

		inv.open(player);
	}

	public DailyReward getDailyReward(int day) {
		return dailyRewards.get(day - 1);
	}

	private void setupDailyRewards() {
		/*  1 */ dailyRewards.add(new DailyReward("$100", 100));
		/*  2 */ dailyRewards.add(new DailyReward("32 bread", new ItemStack(Material.BREAD, 32)));
		/*  3 */ dailyRewards.add(new DailyReward("3 iron blocks", new ItemStack(Material.IRON_BLOCK, 3)));
		/*  4 */ dailyRewards.add(new DailyReward("1 anvil", new ItemStack(Material.ANVIL, 1)));
		/*  5 */ dailyRewards.add(new DailyReward("64 steak", new ItemStack(Material.COOKED_BEEF, 64)));
		/*  6 */ dailyRewards.add(new DailyReward("32 glass", new ItemStack(Material.GLASS, 32)));
		/*  7 */ dailyRewards.add(new DailyReward("a saddle", new ItemStack(Material.SADDLE)));
		/*  8 */ dailyRewards.add(new DailyReward("$1,000", 1000));
		/*  9 */ dailyRewards.add(new DailyReward("10 experience bottles", new ItemStack(Material.EXP_BOTTLE, 10)));
		/* 10 */ dailyRewards.add(new DailyReward("32 leather", new ItemStack(Material.LEATHER, 32)));
		/* 11 */ dailyRewards.add(new DailyReward("$2,000", 2000));
		/* 12 */ dailyRewards.add(new DailyReward("32 apples", new ItemStack(Material.APPLE, 32)));
		/* 13 */ dailyRewards.add(new DailyReward("5 diamonds", new ItemStack(Material.DIAMOND, 5)));
		/* 14 */ dailyRewards.add(new DailyReward("32 coal blocks", new ItemStack(Material.COAL_BLOCK, 32)));
		/* 15 */ dailyRewards.add(new DailyReward("a silk touch book", new ItemStackBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.SILK_TOUCH).build()));
		/* 16 */ dailyRewards.add(new DailyReward("3 golden apples", new ItemStack(Material.GOLDEN_APPLE, 3)));
		/* 17 */ dailyRewards.add(new DailyReward("8 healing potions", new ItemStackBuilder(Material.POTION).amount(8).effect(PotionEffectType.HEAL).build()));
		/* 18 */ dailyRewards.add(new DailyReward("$2,500", 2500));
		/* 19 */ dailyRewards.add(new DailyReward("32 blaze rods", new ItemStack(Material.BLAZE_ROD, 32)));
		/* 20 */ dailyRewards.add(new DailyReward("8 ghast tears", new ItemStack(Material.GHAST_TEAR, 8)));
		/* 21 */ dailyRewards.add(new DailyReward("diamond boots", new ItemStack(Material.DIAMOND_BOOTS)));
		/* 22 */ dailyRewards.add(new DailyReward("16 powered rails and 128 normal rails", new ItemStack(Material.POWERED_RAIL, 16), new ItemStack(Material.RAILS, 128)));
		/* 23 */ dailyRewards.add(new DailyReward("$3,000", 3000));
		List<ItemStack> dyes = new ArrayList<>();
		for (int i = 0; i <= 15; ++i) dyes.add(new ItemStack(Material.INK_SACK, 8, (short) i));
		/* 24 */ dailyRewards.add(new DailyReward("8 of every dye", dyes));
		/* 25 */ dailyRewards.add(new DailyReward("a bow with Power 2, Punch 1 and Unbreaking 2", new ItemStackBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 2).enchant(Enchantment.ARROW_KNOCKBACK).enchant(Enchantment.DURABILITY, 2).build()));
		/* 26 */ dailyRewards.add(new DailyReward("5 gold blocks, 10 iron blocks and 3 emerald blocks", new ItemStack(Material.GOLD_BLOCK, 5), new ItemStack(Material.IRON_BLOCK, 10), new ItemStack(Material.EMERALD_BLOCK, 3)));
		/* 27 */ dailyRewards.add(new DailyReward("50 experience levels", "exp give %player% 50L"));
		/* 28 */ dailyRewards.add(new DailyReward("$3,500", 3500));
		/* 29 */ dailyRewards.add(new DailyReward("8 emerald blocks", new ItemStack(Material.EMERALD_BLOCK, 8)));
		/* 30 */ dailyRewards.add(new DailyReward("diamond boots with Frost Walker, Protection 1 and Unbreaking 2", new ItemStackBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.FROST_WALKER).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).enchant(Enchantment.DURABILITY, 2).build()));
		/* 31 */ dailyRewards.add(new DailyReward("$4,000", 4000));
		/* 32 */ dailyRewards.add(new DailyReward("32 slime balls", new ItemStack(Material.SLIME_BALL, 32)));
		/* 33 */ dailyRewards.add(new DailyReward("4 redstone blocks", new ItemStack(Material.REDSTONE_BLOCK, 4)));
		/* 34 */ dailyRewards.add(new DailyReward("an enchanting table", new ItemStack(Material.ENCHANTMENT_TABLE)));
		/* 35 */ dailyRewards.add(new DailyReward("10 cakes", new ItemStack(Material.CAKE, 10)));
		/* 36 */ dailyRewards.add(new DailyReward("1 clock", new ItemStack(Material.WATCH)));
		/* 37 */ dailyRewards.add(new DailyReward("diamond horse armor", new ItemStack(Material.DIAMOND_BARDING)));
		/* 38 */ dailyRewards.add(new DailyReward("$4,500", 4500));
		/* 39 */ dailyRewards.add(new DailyReward("a totem of undying", new ItemStack(Material.TOTEM)));
		/* 40 */ dailyRewards.add(new DailyReward("18 books", new ItemStack(Material.BOOK, 18)));
		/* 41 */ dailyRewards.add(new DailyReward("$5,000", 5000));
		/* 42 */ dailyRewards.add(new DailyReward("3 notch apples", new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1)));
		/* 43 */ dailyRewards.add(new DailyReward("6 empty maps and 1 book and quill", new ItemStack(Material.EMPTY_MAP, 6), new ItemStack(Material.BOOK_AND_QUILL)));
		/* 44 */ dailyRewards.add(new DailyReward("32 sea lanterns", new ItemStack(Material.SEA_LANTERN, 32)));
		/* 45 */ dailyRewards.add(new DailyReward("a mending book", new ItemStackBuilder(Material.ENCHANTED_BOOK).enchant(Enchantment.MENDING).build()));
		/* 46 */ dailyRewards.add(new DailyReward("3 brewing stands", new ItemStack(Material.BREWING_STAND_ITEM, 3)));
		/* 47 */ dailyRewards.add(new DailyReward("64 blaze rods", new ItemStack(Material.BLAZE_ROD, 64)));
		/* 48 */ dailyRewards.add(new DailyReward("$5,500", 5500));
		/* 49 */ dailyRewards.add(new DailyReward("6 armor stands", new ItemStack(Material.ARMOR_STAND, 6)));
		/* 50 */ dailyRewards.add(new DailyReward("a full set of chainmail armour", new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_BOOTS)));
		/* 51 */ dailyRewards.add(new DailyReward("diamond leggings", new ItemStack(Material.DIAMOND_LEGGINGS)));
		/* 52 */ dailyRewards.add(new DailyReward("4 water breathing potions", new ItemStackBuilder(Material.POTION).amount(4).effect(PotionEffectType.WATER_BREATHING, 8 * 60).build()));
		/* 53 */ dailyRewards.add(new DailyReward("$6,000", 6000));
		/* 54 */ dailyRewards.add(new DailyReward("a zombie skull and a creeper skull", new ItemStack(Material.SKULL_ITEM, 1, (short) 2), new ItemStack(Material.SKULL, 1, (short) 4)));
		/* 55 */ dailyRewards.add(new DailyReward("a fishing rod with Lure 5, Luck 3, and Unbreaking 4", new ItemStackBuilder(Material.FISHING_ROD).enchant(Enchantment.LURE, 5).enchant(Enchantment.LUCK, 3).enchant(Enchantment.DURABILITY, 4).build()));
		/* 56 */ dailyRewards.add(new DailyReward("2 villager spawn eggs", new ItemStack(Material.MONSTER_EGG, 2, EntityType.VILLAGER.getTypeId())));
		/* 57 */ dailyRewards.add(new DailyReward("75 enchanting levels", "exp give %player% 75L"));
		/* 58 */ dailyRewards.add(new DailyReward("$6,500", 6500));
		/* 59 */ dailyRewards.add(new DailyReward("1 diamond helmet with Respiration 3, Aqua Affinity 1 and Unbreaking 2", new ItemStackBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.OXYGEN, 3).enchant(Enchantment.WATER_WORKER).enchant(Enchantment.DURABILITY, 2).build()));
		/* 60 */ dailyRewards.add(new DailyReward("1 shulker box", new ItemStack(Material.PURPLE_SHULKER_BOX)));
	}



}
