package me.pugabyte.nexus.features.dailyrewards;

import com.google.common.base.Strings;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.dailyreward.DailyReward;
import me.pugabyte.nexus.models.dailyreward.DailyRewardService;
import me.pugabyte.nexus.models.dailyreward.Reward;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.vote.Voter;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.features.menus.SignMenuFactory.ARROWS;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;

public class DailyRewardsMenu extends MenuUtils implements InventoryProvider {
	private final DailyReward dailyReward;

	private static final int MAX_DAY = DailyRewardsFeature.getMaxDays();

	private static final ItemStack claimed = new ItemStack(Material.WHITE_WOOL);
	private static final ItemStack unclaimed = new ItemStack(Material.WHITE_WOOL);
	private static final ItemStack locked = new ItemStack(Material.BLACK_WOOL);

	private static final String PREFIX = StringUtils.getPrefix("DailyRewards");

	DailyRewardsMenu(DailyReward dailyReward) {
		this.dailyReward = dailyReward;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.size(6, 9)
				.title(ChatColor.DARK_AQUA + "Daily Rewards")
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		boolean hasResourcePack = player.getResourcePackStatus() == Status.SUCCESSFULLY_LOADED;

		List<ClickableItem> items = new ArrayList<>();

		for (int i = 1; i <= MAX_DAY; i++) {
			final int day = i;
			if (hasResourcePack) {
				if (dailyReward.getStreak() >= day) {
					if (dailyReward.hasClaimed(day)) {
						items.add(ClickableItem.empty(new ItemBuilder(Material.ARROW)
								.name("&eDay " + day)
								.lore("&3Claimed")
								.customModelData(2000 + day).build()));
					} else {
						items.add(ClickableItem.from(new ItemBuilder(Material.ARROW)
								.name("&eDay " + day)
								.lore("&6&lUnclaimed", "&3Click to select reward.")
								.customModelData(1000 + day)
								.build(), e -> new SelectItemMenu(dailyReward, day, contents.pagination().getPage()).open(player)));
					}
				} else {
					items.add(ClickableItem.empty(new ItemBuilder(Material.ARROW)
							.name("&eDay " + day)
							.lore("&cLocked")
							.customModelData(3000 + day)
							.build()));
				}
			} else {
				if (dailyReward.getStreak() >= day) {
					if (dailyReward.hasClaimed(day)) {
						ItemStack item = nameItem(claimed.clone(), "&eDay " + day, "&3Claimed" + "", day);
						items.add(ClickableItem.empty(addGlowing(item)));
					} else {
						ItemStack item = nameItem(unclaimed.clone(), "&eDay " + day, "&6&lUnclaimed||" + "&3Click to select reward.", day);
						items.add(ClickableItem.from(item, e -> new SelectItemMenu(dailyReward, day, contents.pagination().getPage()).open(player)));
					}
				} else {
					ItemStack item = nameItem(locked.clone(), "&eDay " + day, "&cLocked" + "", day);
					items.add(ClickableItem.empty(item));
				}
			}
		}

		addPagination(player, contents, items);
	}

	private ItemStack nameItem(ItemStack item, String name, String lore, int day) {
		ItemStack itemStack = super.nameItem(item, name, lore);
		itemStack.setAmount(day);
		return itemStack;
	}

	public static class SelectItemMenu extends MenuUtils implements InventoryProvider {
		private final DailyReward dailyReward;
		private final int day;
		private final int page;

		public SelectItemMenu(DailyReward dailyReward, int day, int page) {
			this.dailyReward = dailyReward;
			this.day = day;
			this.page = page;
		}

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
					.provider(this)
					.size(3, 9)
					.title(ChatColor.DARK_AQUA + "Daily Rewards")
					.build()
					.open(viewer);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addBackItem(contents, e -> new DailyRewardsMenu(dailyReward).open(dailyReward.getPlayer().getPlayer(), page));

			List<Reward> rewards = DailyRewardsFeature.getRewards(day);

			for (int i = 0; i < 3; i++) {
				int option = i;
				Reward currentReward = rewards.get(i);
				String rewardDescription = camelCase("&e" + currentReward.getDescription());

				ItemStack item;
				if (currentReward.getItems() != null)
					item = nameItem(currentReward.getItems().get(0).clone(), rewardDescription, "&3Click to claim");
				else
					item = nameItem(addGlowing(new ItemStack(Material.PAPER)), rewardDescription, "&3Click to claim");

				contents.set(1, (2 + i * 2), ClickableItem.from(item, e -> applyReward(day, option)));
			}
		}

		private void applyReward(int day, int option) {
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
							saveAndReturn(day);
							player.closeInventory();
						});
					} else if (Reward.RequiredSubmenu.NAME.contains(item.getType())) {
						Nexus.getSignMenuFactory().lines("", ARROWS, "Enter a", "player's name").prefix(PREFIX).response(lines -> {
							PlayerUtils.giveItem(player, new ItemBuilder(Material.PLAYER_HEAD).skullOwner(lines[0]).amount(item.getAmount()).build());
							saveAndReturn(day);
						}).open(player);
					} else {
						PlayerUtils.giveItem(player, item);
						saveAndReturn(day);
					}
				}

			} else {

				if (money != null) {
					new BankerService().deposit(player, money, ShopGroup.get(player), TransactionCause.DAILY_REWARD);
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

				saveAndReturn(day);
			}
		}

		public void saveAndReturn(int day) {
			dailyReward.claim(day);
			new DailyRewardService().save(dailyReward);
			new DailyRewardsMenu(dailyReward).open(dailyReward.getPlayer().getPlayer(), page);
		}
	}

}
