package me.pugabyte.nexus.features.dailyrewards;

import com.google.common.base.Strings;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.dailyreward.DailyReward;
import me.pugabyte.nexus.models.dailyreward.DailyRewardService;
import me.pugabyte.nexus.models.dailyreward.Reward;
import me.pugabyte.nexus.models.vote.Voter;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.pugabyte.nexus.features.menus.SignMenuFactory.ARROWS;

public class DailyRewardsMenu extends MenuUtils implements InventoryProvider {
	private final DailyRewardService service = new DailyRewardService();
	private final DailyReward dailyReward;

	private final ItemStack back = new ItemBuilder(Material.BARRIER).name("&cScroll back 1 day").build();
	private final ItemStack back7 = new ItemBuilder(Material.BARRIER).amount(7).name("&cScroll back 7 days").build();
	private final ItemStack forward = new ItemBuilder(Material.ARROW).name("&2Scroll forward 1 day").build();
	private final ItemStack forward7 = new ItemBuilder(Material.ARROW).amount(7).name("&2Scroll forward 7 days").build();

	private final int MAX_DAY = DailyRewardsFeature.getMaxDays();
	private final ItemStack claimed = new ItemStack(Material.WHITE_WOOL);
	private final ItemStack unclaimed = new ItemStack(Material.WHITE_WOOL);
	private final ItemStack locked = new ItemStack(Material.BLACK_WOOL);

	private static final String PREFIX = StringUtils.getPrefix("DailyRewards");

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

	private void clearScreen(InventoryContents contents) {
		for (SlotPos slotPos : contents.slots()) {
			contents.set(slotPos, ClickableItem.empty(new ItemStack(Material.AIR)));
		}
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

			if (dailyReward.getStreak() >= day) {
				if (dailyReward.hasClaimed(day)) {
					ItemStack item = nameItem(claimed.clone(), "&eDay " + day, "&3Claimed" + "", day);
					contents.set(new SlotPos(1, column), ClickableItem.empty(addGlowing(item)));
				} else {
					ItemStack item = nameItem(unclaimed.clone(), "&eDay " + day, "&6&lUnclaimed||" + "&3Click to select reward.", day);
					final int currentDay = day;
					contents.set(new SlotPos(1, column), ClickableItem.from(item, e ->
							selectItem(contents, currentDay, initialDay)));
				}
			} else {
				ItemStack item = nameItem(locked.clone(), "&eDay " + day, "&cLocked" + "", day);
				contents.set(new SlotPos(1, column), ClickableItem.empty(item));
			}

			++day;
			++column;
		}
	}

	private void selectItem(InventoryContents contents, int currentDay, int initialDay) {
		clearScreen(contents);
		contents.set(new SlotPos(0, 0), ClickableItem.from(backItem(), e -> scroll(contents, 0, initialDay)));

		Reward[] reward = new Reward[3];
		reward[0] = DailyRewardsFeature.getReward1(currentDay);
		reward[1] = DailyRewardsFeature.getReward2(currentDay);
		reward[2] = DailyRewardsFeature.getReward3(currentDay);

		for (int i = 0; i < 3; i++) {
			Reward currentReward = reward[i];
			int option = i;
			String rewardDescription = "&e" + currentReward.getDescription();
			ItemStack item = nameItem(currentReward.getItems() != null ? currentReward.getItems().get(0).clone() : addGlowing(new ItemStack(Material.PAPER)), StringUtils.camelCase(rewardDescription), "&3Click to claim");

			contents.set(1, (2 + i * 2), ClickableItem.from(item, e ->
					applyReward(currentDay, option, contents, initialDay)));
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

	public void saveAndReturn(InventoryContents contents, int day, int initialDay) {
		dailyReward.claim(day);
		service.save(dailyReward);
		scroll(contents, 0, initialDay);
	}

	private void applyReward(int day, int option, InventoryContents contents, int initialDay) {
		Player player = (Player) dailyReward.getPlayer();

		Reward reward = DailyRewardsFeature.getReward(day, option);
		List<ItemStack> items = reward.getItems();
		Integer money = reward.getMoney();
		Integer levels = reward.getLevels();
		Integer votePoints = reward.getVotePoints();
		String command = reward.getCommand();

		if (dailyReward.hasClaimed(day)) return;

		if (items != null) {
			for (ItemStack item : items) {
				if (Reward.RequiredSubmenu.COLOR.contains(item.getType())) {
					MenuUtils.colorSelectMenu(player, item.getType(), itemClickData -> {
						PlayerUtils.giveItem(player, new ItemStack(itemClickData.getItem().getType(), item.getAmount()));
						saveAndReturn(contents, day, initialDay);
						player.closeInventory();
					});
				} else if (Reward.RequiredSubmenu.NAME.contains(item.getType())) {
					Nexus.getSignMenuFactory().lines("", ARROWS, "Enter a", "player's name").prefix(PREFIX).response(lines -> {
						PlayerUtils.giveItem(player, new ItemBuilder(Material.PLAYER_HEAD).skullOwner(lines[0]).amount(item.getAmount()).build());
						saveAndReturn(contents, day, initialDay);
					}).open(player);
				} else {
					PlayerUtils.giveItem(player, item);
					saveAndReturn(contents, day, initialDay);
				}
			}

		} else {

			if (money != null) {
				new BankerService().deposit(player, money, TransactionCause.DAILY_REWARD);
				PlayerUtils.send(player, PREFIX + "&e" + money + " &3has been added to your balance");
			}

			if (levels != null) {
				player.giveExpLevels(levels);
				PlayerUtils.send(player, PREFIX + "You have been given &e" + levels + " XP Levels");
			}

			if (votePoints != null) {
				new Voter(player).givePoints(votePoints);
				PlayerUtils.send(player, PREFIX + "&e" + votePoints + " &3vote points has been added to your balance");
			}

			if (!Strings.isNullOrEmpty(command))
				PlayerUtils.runCommandAsConsole(command.replaceAll("%player%", player.getName()));

			saveAndReturn(contents, day, initialDay);
		}
	}

}
