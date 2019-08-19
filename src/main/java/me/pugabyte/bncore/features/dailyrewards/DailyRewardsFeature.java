package me.pugabyte.bncore.features.dailyrewards;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.models.dailyrewards.DailyReward;
import me.pugabyte.bncore.models.dailyrewards.DailyRewards;
import me.pugabyte.bncore.models.dailyrewards.DailyRewardsService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DailyRewardsFeature implements Listener {
	private List<DailyReward> dailyRewards = new ArrayList<>();

	public DailyRewardsFeature() {
		setupDailyRewards();
	}

	public static void menu(Player player, DailyRewards dailyRewards) {
		SmartInventory inv = SmartInventory.builder()
				.provider(new DailyRewardsMenu(dailyRewards))
				.size(3, 9)
				.title(ChatColor.DARK_AQUA + "Daily Rewards")
				.build();

		inv.open(player);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		DailyRewardsService service = new DailyRewardsService();
		DailyRewards dailyReward = (DailyRewards) service.get(event.getPlayer());
		if (!dailyReward.isEarnedToday()) {
			dailyReward.setEarnedToday(true);
			dailyReward.increaseStreak();
			service.save(dailyReward);
		}
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
		/*  6 */ dailyRewards.add(new DailyReward("$100", 100));
		/*  7 */ dailyRewards.add(new DailyReward("$100", 100));
		/*  8 */ dailyRewards.add(new DailyReward("$100", 100));
		/*  9 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 10 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 11 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 12 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 13 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 14 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 15 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 16 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 17 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 18 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 19 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 20 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 21 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 22 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 23 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 24 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 25 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 26 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 27 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 28 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 29 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 30 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 31 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 32 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 33 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 34 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 35 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 36 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 37 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 38 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 39 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 40 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 41 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 42 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 43 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 44 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 45 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 46 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 47 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 48 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 49 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 50 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 51 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 52 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 53 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 54 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 55 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 56 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 57 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 58 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 59 */ dailyRewards.add(new DailyReward("$100", 100));
		/* 60 */ dailyRewards.add(new DailyReward("$100", 100));
	}



}
