package me.pugabyte.bncore.features.dailyrewards;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.dailyreward.DailyReward;
import me.pugabyte.bncore.models.dailyreward.DailyRewardService;
import me.pugabyte.bncore.models.dailyreward.Reward;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.utils.Utils.loreize;

public class DailyRewardsMenu extends MenuUtils implements InventoryProvider {
	private DailyRewardService service = new DailyRewardService();
	private DailyReward dailyReward;

	private ItemStack back = new ItemStackBuilder(Material.BARRIER).name("&cScroll back 1 day").build();
	private ItemStack back7 = new ItemStackBuilder(Material.BARRIER).amount(7).name("&cScroll back 7 days").build();
	private ItemStack forward = new ItemStackBuilder(Material.ARROW).name("&2Scroll forward 1 day").build();
	private ItemStack forward7 = new ItemStackBuilder(Material.ARROW).amount(7).name("&2Scroll forward 7 days").build();

	private final int MAX_DAY = DailyRewardsFeature.getMaxDays();
	private ItemStack claimed = new ItemStack(Material.WOOL, 1);
	private ItemStack unclaimed = new ItemStack(Material.WOOL, 1);
	private ItemStack locked = new ItemStack(Material.WOOL, 1, (short) 15);

	DailyRewardsMenu(DailyReward dailyReward) {
		this.dailyReward = dailyReward;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		scroll(contents, 0, 1);
	}

	private ItemStack nameItem(ItemStack item, String name, String lore, int day) {
		ItemStack itemStack = super.nameItem(item, name, lore);
		itemStack.setAmount(day);
		return itemStack;
	}

	private void scroll(InventoryContents contents, int change, int day) {
		day += change;
		if (day < 1) day = 1;
		if (day > MAX_DAY - 6) day = MAX_DAY - 6;

		final int initialDay = day;
		contents.set(new SlotPos(0, 0), ClickableItem.from(back, e -> scroll(contents, -1, initialDay)));
		contents.set(new SlotPos(2, 0), ClickableItem.from(back7, e -> scroll(contents, -7, initialDay)));
		contents.set(new SlotPos(0, 8), ClickableItem.from(forward, e -> scroll(contents, 1, initialDay)));
		contents.set(new SlotPos(2, 8), ClickableItem.from(forward7, e -> scroll(contents, 7, initialDay)));

		int column = 1;
		for (int i = 0; i < 7; ++i) {
			Reward dailyReward = DailyRewardsFeature.getReward(day);

			String reward = "||&f||&6&lReward: &e" + loreize(dailyReward.getDescription(), ChatColor.YELLOW);
			if (this.dailyReward.getStreak() >= day) {
				if (this.dailyReward.hasClaimed(day)) {
					ItemStack item = nameItem(claimed.clone(), "&eDay " + day, "&3Claimed" + reward, day);
					contents.set(new SlotPos(1, column), ClickableItem.empty(addGlowing(item)));
				} else {
					ItemStack item = nameItem(unclaimed.clone(), "&eDay " + day, "&6&lClick to claim" + reward, day);
					final int currentDay = day;
					contents.set(new SlotPos(1, column), ClickableItem.from(item, e -> {
						this.dailyReward.claim(currentDay);
						service.save(this.dailyReward);
						scroll(contents, 0, initialDay);
					}));
				}
			} else {
				ItemStack item = nameItem(locked.clone(), "&eDay " + day, "&cLocked" + reward, day);
				contents.set(new SlotPos(1, column), ClickableItem.empty(item));
			}

			++day;
			++column;
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {}

}
