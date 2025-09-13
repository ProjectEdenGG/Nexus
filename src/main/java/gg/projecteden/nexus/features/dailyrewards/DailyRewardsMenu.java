package gg.projecteden.nexus.features.dailyrewards;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.menus.ColorSelectMenu;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUserService;
import gg.projecteden.nexus.models.dailyreward.Reward;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import gg.projecteden.nexus.utils.DialogUtils.MultiActionDialogBuilder;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Title("&3Daily Rewards")
@RequiredArgsConstructor
public class DailyRewardsMenu {
	private static final String PREFIX = StringUtils.getPrefix("DailyRewards");
	public static final int MAX_DAY = DailyRewardsFeature.getMaxDays();

	public void open(Player player) {
		MultiActionDialogBuilder builder = new DialogBuilder()
			.title("&6Daily Rewards")
			.multiAction();

		var user = new DailyRewardUserService().get(player.getPlayer());

		for (int i = 1; i <= Math.max(MAX_DAY, user.getCurrentStreak().getStreak()); i++) {
			final int day = i;
			final int width = 26;
			if (user.getCurrentStreak().canClaim(day))
				if (user.getCurrentStreak().hasClaimed(day))
					builder.button("&7" + day, "&7Claimed", width, response -> {});
				else
					builder.button(
						new JsonBuilder("&a" + day),
						new JsonBuilder("&aUnclaimed").newline().next("&3Click to select reward."),
						width,
						response -> new SelectItemMenu(user, day).open(response.getPlayer())
					);
			else
				builder.button("&c" + day, "&cLocked", width);
		}

		builder
			.exitButton("Close")
			.columns(14)
			.open(player);
	}

	@Rows(3)
	@Title("&3Daily Rewards")
	public static class SelectItemMenu extends InventoryProvider {
		private final DailyRewardUser user;
		private final int day;

		public SelectItemMenu(DailyRewardUser user, int day) {
			this.user = user;
			this.day = day;
		}

		@Override
		public void init() {
			addBackItem(e -> {
				e.getPlayer().closeInventory();
				Tasks.wait(1, () -> new DailyRewardsMenu().open(user.getOnlinePlayer()));
			});

			List<Reward> rewards = DailyRewardsFeature.getRewards(day);

			for (int i = 0; i < 3; i++) {
				int option = i;
				Reward currentReward = rewards.get(i);
				String rewardDescription = "&e" + StringUtils.camelCase(currentReward.getDescription());

				ItemBuilder item;
				if (!Nullables.isNullOrEmpty(currentReward.getItems()))
					item = new ItemBuilder(currentReward.getItems().getFirst().clone()).name(rewardDescription).lore("&3Click to claim");
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

			if (user.getCurrentStreak().hasClaimed(day))
				return;

			if (!Nullables.isNullOrEmpty(items)) {
				for (ItemStack item : items) {
					ItemStack clone = item.clone();
					if (Reward.RequiredSubmenu.COLOR.contains(clone.getType())) {
						new ColorSelectMenu(clone.getType(), itemClickData -> {
							player.closeInventory();
							PlayerUtils.giveItem(player, new ItemStack(itemClickData.getItem().getType(), clone.getAmount()));
							Tasks.wait(1, () -> saveAndReturn(day));
						}).open(player);
					} else if (Reward.RequiredSubmenu.NAME.contains(clone.getType())) {
						showPlayerHeadMenu(this);
					} else {
						player.closeInventory();
						PlayerUtils.giveItem(player, clone);
						Tasks.wait(1, () -> saveAndReturn(day));
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
			new DailyRewardsMenu().open(user.getOnlinePlayer());
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

		public void showPlayerHeadMenu(SelectItemMenu menu) {
			showPlayerHeadMenu(menu, null);
		}

		public void showPlayerHeadMenu(SelectItemMenu menu, String errorMessage) {
			new DialogBuilder()
				.title("Daily Rewards Player Head")
				.bodyText("Enter a player's name to receive their head")
				.inputText("username", errorMessage)
				.confirmation()
				.onCancel(menu::open)
				.onSubmit(response -> {
					try {
						Nerd nerd = PlayerUtils.getPlayer(response.getText("username"));
						ItemStack head = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(nerd).build();

						new DialogBuilder()
							.title("Daily Rewards Player Head")
							.bodyText("Claim " + nerd.getNickname() + "'s head?")
							.bodyItem(new ItemBuilder(head).model(ItemModelType.GUI_PLAYER_HEAD).build())
							.bodyBlankLines(5)
							.confirmation()
							.submitText("Yes!")
							.cancelText("Go back")
							.onCancel(player -> showPlayerHeadMenu(menu))
							.onSubmit(response2 -> {
								PlayerUtils.giveItem(viewer, head);
								saveAndReturn(menu.day);
							})
							.open(response.getPlayer());
					} catch (EdenException ex) {
						showPlayerHeadMenu(menu, "&c" + ex.getMessage());
					} catch (Exception ex) {
						MenuUtils.handleException(response.getPlayer(), DailyRewardsMenu.PREFIX, ex);
					}
				})
				.open(viewer);
		}

	}

}
