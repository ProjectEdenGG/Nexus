package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.trophy.TrophyHolder;
import gg.projecteden.nexus.models.trophy.TrophyHolderService;
import gg.projecteden.nexus.models.trophy.TrophyType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.menus.MenuUtils.handleException;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Aliases("trophies")
public class TrophyCommand extends CustomCommand {
	private static final List<WorldGroup> CAN_CLAIM_IN = List.of(
		WorldGroup.SURVIVAL,
		WorldGroup.CREATIVE,
		WorldGroup.SKYBLOCK
	);

	private final TrophyHolderService service = new TrophyHolderService();
	private TrophyHolder holder;

	public TrophyCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			holder = service.get(player());
	}

	@Path
	@Description("Open the trophy menu")
	void menu() {
		if (holder.getEarned().isEmpty())
			error("You have not earned any event trophies! Participate in server hosted events to earn them");

		if (!CAN_CLAIM_IN.contains(worldGroup()))
			error("You can't use this command in this world");

		new TrophyMenu().open(player());
	}

	@Permission(Group.ADMIN)
	@Path("reward <player> <trophy>")
	@Description("Give a player a trophy")
	void reward(TrophyHolder holder, TrophyType trophy) {
		if (holder.earn(trophy)) {
			send(PREFIX + "Rewarded " + camelCase(trophy) + " trophy to " + holder.getNickname());
			service.save(holder);
		} else
			error(holder.getNickname() + " has already earned that trophy");
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("remove <player> <trophy>")
	@Description("Remove a player's access to a trophy")
	void remove(TrophyHolder holder, TrophyType trophy) {
		holder.getEarned().remove(trophy);
		holder.getClaimed().remove(trophy);
		service.save(holder);
		send(PREFIX + "Removed trophy " + camelCase(trophy) + " from " + holder.getNickname() + "'s trophies");
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("reset <player>")
	@Description("Reset a player's trophies")
	void reward(TrophyHolder holder) {
		holder.getEarned().clear();
		holder.getClaimed().clear();
		service.save(holder);
		send(PREFIX + "Reset " + holder.getNickname() + "'s trophies");
	}

	@Permission(Group.ADMIN)
	@Path("get <trophy>")
	@Description("Spawn a trophy item")
	void get(TrophyType trophy) {
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
			final TrophyHolder holder = service.get(viewer);

			if (previousMenu == null)
				addCloseItem();
			else
				addBackItem(e -> previousMenu.open(viewer));

			List<ClickableItem> items = new ArrayList<>();

			if (isNullOrEmpty(event)) {
				for (String event : TrophyType.getEvents()) {
					if (TrophyType.getEarnedTrophies(holder, event).isEmpty())
						continue;

					ItemBuilder item = TrophyType.getDisplayItem(holder, event).name(StringUtils.camelCase(event));
					items.add(ClickableItem.of(item.build(), e -> new TrophyMenu(event, this).open(viewer)));
				}
			} else {
				for (TrophyType trophy : TrophyType.getEarnedTrophies(holder, event)) {
					ItemBuilder item = trophy.getItem();
					if (holder.hasClaimed(trophy)) {
						item.lore("", "&cClaimed");
						items.add(ClickableItem.empty(item.build()));
					} else {
						item.lore("", "&eClick to receive a copy");
						item.lore("", "&cYou can only claim one copy, so make", "&csure you are in the correct world");
						items.add(ClickableItem.of(item.build(), e -> {
							try {
								holder.claim(trophy);
								service.save(holder);
							} catch (InvalidInputException ex) {
								handleException(viewer, Commands.getPrefix(TrophyCommand.class), ex);
							} finally {
								open(viewer);
							}
						}));
					}
				}
			}

			paginate(items);
		}

	}

}
