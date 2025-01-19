package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.profiles.ProfileCommand;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FriendRequestsProvider extends InventoryProvider {
	InventoryProvider previousMenu = null;
	private static final FriendsUserService userService = new FriendsUserService();
	private final FriendRequestType type;
	private FriendsUser viewerFriend;

	public FriendRequestsProvider(FriendRequestType type, @Nullable InventoryProvider previousMenu) {
		this.type = type;
		this.previousMenu = previousMenu;
	}

	public enum FriendRequestType {
		SENT,
		RECEIVED,
		;
	}

	@Override
	public String getTitle() {
		return switch (type) {
			case SENT -> "Sent Requests";
			case RECEIVED -> "Received Requests";
		};
	}

	@Override
	public void init() {
		viewerFriend = userService.get(viewer);

		List<ClickableItem> items = new ArrayList<>();
		Set<UUID> requests = getRequests(type);

		addBackItem(e -> new FriendsProvider(viewer, viewer, previousMenu).open(viewer));

		for (UUID uuid : requests) {
			Nerd targetNerd = Nerd.of(uuid);
			ItemBuilder skull = FriendsProvider.getFriendSkull(targetNerd, viewer);

			if (this.type == FriendRequestType.RECEIVED)
				skull.lore("&eClick &3to &caccept request", "&eShift Click &3to &cdeny request");
			else
				skull.lore("&eShift Click &3to &ccancel request");

			FriendsUser targetFriend = userService.get(uuid);

			items.add(ClickableItem.of(skull, e -> {
				if (this.type == FriendRequestType.RECEIVED) {
					if (e.isShiftClick())
						viewerFriend.denyRequest(targetFriend);
					else
						viewerFriend.sendRequest(targetFriend);
				} else {
					if (e.isShiftClick())
						viewerFriend.cancelSent(targetFriend);
					else
						ProfileCommand.openProfile(targetNerd, viewer, this);
				}
			}));
		}

		paginate(items);
	}

	private Set<UUID> getRequests(FriendRequestType type) {
		return switch (type) {
			case SENT -> viewerFriend.getRequests_sent();
			case RECEIVED -> viewerFriend.getRequests_received().keySet();
		};
	}

}
