package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.trophy.Trophy;
import gg.projecteden.nexus.models.trophy.TrophyHolder;
import gg.projecteden.nexus.models.trophy.TrophyHolderService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.menus.MenuUtils.handleException;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Aliases("trophies")
public class TrophyCommand extends CustomCommand {
	private final TrophyHolderService service = new TrophyHolderService();
	private TrophyHolder holder;

	public TrophyCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			holder = service.get(player());
	}

	@Path
	void menu() {
		if (holder.getEarned().isEmpty())
			error("You have not earned any event trophies! Participate in server hosted events to earn them");

		new TrophyMenu().open(player());
	}

	@Permission(Group.ADMIN)
	@Path("reward <player> <trophy>")
	void reward(TrophyHolder holder, Trophy trophy) {
		if (holder.earn(trophy)) {
			send(PREFIX + "Rewarded " + camelCase(trophy) + " trophy to " + holder.getNickname());
			service.save(holder);
		} else
			error(holder.getNickname() + " has already earned that trophy");
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("remove <player> <trophy>")
	void remove(TrophyHolder holder, Trophy trophy) {
		holder.getEarned().remove(trophy);
		holder.getClaimed().remove(trophy);
		service.save(holder);
		send(PREFIX + "Reset " + holder.getNickname() + "'s trophies");
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("reset <player>")
	void reward(TrophyHolder holder) {
		holder.getEarned().clear();
		holder.getClaimed().clear();
		service.save(holder);
		send(PREFIX + "Reset " + holder.getNickname() + "'s trophies");
	}

	@Permission(Group.ADMIN)
	@Path("get <trophy>")
	void get(Trophy trophy) {
		PlayerUtils.giveItem(player(), trophy.getItem().build());
		send(PREFIX + "Gave " + camelCase(trophy) + " trophy");
	}

	@Title("Trophies")
	@AllArgsConstructor
	private static class TrophyMenu extends InventoryProvider {
		private final String event;
		private final TrophyMenu previousMenu;

		public TrophyMenu() {
			this(null);
		}

		public TrophyMenu(String event) {
			this(event, null);
		}

		@Override
		public void init() {
			final TrophyHolderService service = new TrophyHolderService();
			final TrophyHolder holder = service.get(player);

			if (previousMenu == null)
				addCloseItem();
			else
				addBackItem(e -> previousMenu.open(player));

			List<ClickableItem> items = new ArrayList<>();

			if (isNullOrEmpty(event)) {
				for (String event : Trophy.getEvents()) {
					if (Trophy.getEarnedTrophies(holder, event).isEmpty())
						continue;

					ItemBuilder item = Trophy.getDisplayItem(holder, event).name(StringUtils.camelCase(event));
					items.add(ClickableItem.of(item.build(), e -> new TrophyMenu(event, this).open(player)));
				}
			} else {
				for (Trophy trophy : Trophy.getEarnedTrophies(holder, event)) {
					ItemBuilder item = trophy.getItem();
					if (holder.hasClaimed(trophy)) {
						item.lore("", "&cClaimed");
						items.add(ClickableItem.empty(item.build()));
					} else {
						item.lore("", "&eClick to receive a copy");
						items.add(ClickableItem.of(item.build(), e -> {
							try {
								holder.claim(trophy);
								service.save(holder);
							} catch (InvalidInputException ex) {
								handleException(player, Commands.getPrefix(TrophyCommand.class), ex);
							} finally {
								open(player);
							}
						}));
					}
				}
			}

			paginator().items(items).build();
		}

	}

}
