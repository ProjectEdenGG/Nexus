package gg.projecteden.nexus.features.dailyrewards;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.ColorSelectMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.ResourcePack.ResourcePackNumber;
import gg.projecteden.nexus.features.votes.vps.VPSMenu;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUserService;
import gg.projecteden.nexus.models.dailyreward.Reward;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.features.menus.api.SignMenuFactory.ARROWS;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Title("&3Daily Rewards")
@RequiredArgsConstructor
public class DailyRewardsMenu extends InventoryProvider {
	private static final String PREFIX = StringUtils.getPrefix("DailyRewards");
	private static final int MAX_DAY = DailyRewardsFeature.getMaxDays();

	private final DailyRewardUser user;

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();

		for (int i = 1; i <= Math.max(MAX_DAY, user.getCurrentStreak().getStreak()); i++) {
			final int day = i;
			if (user.getCurrentStreak().canClaim(day))
				if (user.getCurrentStreak().hasClaimed(day))
					items.add(ClickableItem.empty(ResourcePackNumber.of(day, ColorType.GRAY).get()
						.name("&eDay " + day)
						.lore("&3Claimed")
						.build()));
				else
					items.add(ClickableItem.of(ResourcePackNumber.of(day, ColorType.GREEN).get()
						.name("&eDay " + day)
						.lore("&6&lUnclaimed", "&3Click to select reward.")
						.build(), e -> new SelectItemMenu(user, day, contents.pagination().getPage()).open(viewer)));
			else
				items.add(ClickableItem.empty(ResourcePackNumber.of(day, ColorType.RED).get()
					.name("&eDay " + day)
					.lore("&cLocked")
					.build()));
		}

		paginate(items);
	}

	@Rows(3)
	@Title("&3Daily Rewards")
	public static class SelectItemMenu extends InventoryProvider {
		private final DailyRewardUser user;
		private final int day;
		private final int page;

		public SelectItemMenu(DailyRewardUser user, int day, int page) {
			this.user = user;
			this.day = day;
			this.page = page;
		}

		@Override
		public void init() {
			addBackItem(e -> new DailyRewardsMenu(user).open(user.getOnlinePlayer(), page));

			List<Reward> rewards = DailyRewardsFeature.getRewards(day);

			for (int i = 0; i < 3; i++) {
				int option = i;
				Reward currentReward = rewards.get(i);
				String rewardDescription = "&e" + camelCase(currentReward.getDescription());

				ItemBuilder item;
				if (!isNullOrEmpty(currentReward.getItems()))
					item = new ItemBuilder(currentReward.getItems().get(0).clone()).name(rewardDescription).lore("&3Click to claim");
				else
					item = new ItemBuilder(Material.PAPER).name(rewardDescription).lore("&3Click to claim").glow();

				contents.set(1, (2 + i * 2), ClickableItem.of(item, e -> applyReward(day, option)));
			}
		}

		private void applyReward(int day, int option) {
			Player player = user.getOnlinePlayer();

			Reward reward = DailyRewardsFeature.getReward(day, option);
			List<ItemStack> items = reward.getItems();
			Integer money = reward.getMoney();
			Integer levels = reward.getLevels();
			Integer votePoints = reward.getVotePoints();
			String command = reward.getCommand();

			if (user.getCurrentStreak().hasClaimed(day)) return;

			if (!isNullOrEmpty(items)) {
				for (ItemStack item : items) {
					ItemStack clone = item.clone();
					if (Reward.RequiredSubmenu.COLOR.contains(clone.getType())) {
						new ColorSelectMenu(clone.getType(), itemClickData -> {
											PlayerUtils.giveItem(player, new ItemStack(itemClickData.getItem().getType(), clone.getAmount()));
											saveAndReturn(day);
											player.closeInventory();
										}).open(player);
					} else if (Reward.RequiredSubmenu.NAME.contains(clone.getType())) {
						Nexus.getSignMenuFactory().lines("", ARROWS, "Enter a", "player's name").prefix(PREFIX).response(lines -> {
							PlayerUtils.giveItem(player, new ItemBuilder(Material.PLAYER_HEAD).skullOwner(lines[0]).amount(clone.getAmount()).build());
							saveAndReturn(day);
						}).open(player);
					} else {
						PlayerUtils.giveItem(player, clone);
						saveAndReturn(day);
					}
				}

			} else {

				if (money != null) {
					new BankerService().deposit(player, money, ShopGroup.of(player), TransactionCause.DAILY_REWARD);
					PlayerUtils.send(player, PREFIX + "&e" + money + " &3has been added to your balance");
				}

				if (levels != null) {
					player.giveExpLevels(levels);
					PlayerUtils.send(player, PREFIX + "You have been given &e" + levels + " XP Levels");
				}

				if (votePoints != null) {
					new VoterService().edit(player, voter -> voter.givePoints(votePoints));
					PlayerUtils.send(player, PREFIX + "&e" + votePoints + " &3vote points has been added to your balance");
				}

				if (!Nullables.isNullOrEmpty(command))
					PlayerUtils.runCommandAsConsole(command.replaceAll("%player%", player.getName()));

				saveAndReturn(day);
			}
			log(option);
		}

		public void saveAndReturn(int day) {
			user.getCurrentStreak().claim(day);
			new DailyRewardUserService().save(user);
			new DailyRewardsMenu(user).open(user.getOnlinePlayer(), page);
		}

		public void log(int choice) {
			List<String> columns = new ArrayList<>(Arrays.asList(
				DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
				viewer.getUniqueId().toString(),
				viewer.getName(),
				String.valueOf(day),
				String.valueOf(choice)
			));

			IOUtils.csvAppend("daily-rewards", String.join(",", columns));
		}

	}

}
