package me.pugabyte.bncore.features.dailyrewards;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.dailyrewards.DailyReward;
import me.pugabyte.bncore.models.dailyrewards.DailyRewards;
import me.pugabyte.bncore.models.dailyrewards.DailyRewardsService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DailyRewardsProvider extends MenuUtils implements InventoryProvider {
	private DailyRewardsService service = new DailyRewardsService();
	private DailyRewards dailyRewards;

	private ItemStack back = new ItemStack(Material.BARRIER);
	private ItemStack back7 = new ItemStack(Material.BARRIER, 7);
	private ItemStack forward = new ItemStack(Material.ARROW);
	private ItemStack forward7 = new ItemStack(Material.ARROW, 7);

	private ItemStack claimed = new ItemStack(Material.WOOL, 1);
	private ItemStack unclaimed = new ItemStack(Material.WOOL, 1);
	private ItemStack locked = new ItemStack(Material.WOOL, 1, (short) 15);
	private int maxDay = 60;

	public DailyRewardsProvider(Player player) {
		this.dailyRewards = (DailyRewards) service.get(player);
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
		if (day > maxDay - 6) day = maxDay - 6;

		int initialDay = day;
		contents.set(new SlotPos(0, 0), ClickableItem.of(back, e -> scroll(contents, -1, initialDay)));
		contents.set(new SlotPos(2, 0), ClickableItem.of(back7, e -> scroll(contents, -7, initialDay)));
		contents.set(new SlotPos(0, 8), ClickableItem.of(forward, e -> scroll(contents, 1, initialDay)));
		contents.set(new SlotPos(2, 8), ClickableItem.of(forward7, e -> scroll(contents, 7, initialDay)));

		int column = 1;
		for (int i = 0; i < 7; ++i) {
			DailyReward dailyReward = BNCore.dailyRewards.getDailyReward(day);

			String reward = "||&f||&6&lReward: &e" + dailyReward.getDescription();
			if (dailyRewards.getStreak() >= day) {
				if (dailyRewards.hasClaimed(day)) {
					ItemStack item = nameItem(claimed.clone(), "&eDay " + day, "&3Claimed" + reward, day);
					contents.set(new SlotPos(1, column), ClickableItem.empty(item));
				} else {
					ItemStack item = nameItem(unclaimed.clone(), "&eDay " + day, "&6&lClick to claim" + reward, day);
					int currentDay = day;
					contents.set(new SlotPos(1, column), ClickableItem.of(item, e -> {
						dailyRewards.claim(currentDay);
						service.save(dailyRewards);
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
