package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.profiles.ProfileCommand;
import gg.projecteden.nexus.features.profiles.providers.FriendRequestsProvider.FriendRequestType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendsProvider extends InventoryProvider {
	InventoryProvider previousMenu;
	private static final FriendsUserService userService = new FriendsUserService();
	private final FriendsUser user;
	private final int friendCount;
	private final boolean isSelf;

	public FriendsProvider(OfflinePlayer target, Player viewer, @Nullable InventoryProvider previousMenu) {
		this.isSelf = PlayerUtils.isSelf(target, viewer);
		this.user = userService.get(target);
		this.friendCount = user.getFriends().size();
		this.previousMenu = previousMenu;
	}

	@Override
	public String getTitle() {
		if (isSelf) {
			if (friendCount == 0)
				return "You have no friends ):";

			return "Your friends:";
		} else {
			if (friendCount == 0)
				return user.getNickname() + " has no friends ):";

			return user.getNickname() + "'s friends: ";
		}
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		if (isSelf) {
			if (previousMenu == null) {
				ItemBuilder skull = getBaseFriendSkull(Nerd.of(viewer)).name("&eClick &3to view your profile");
				contents.set(0, 8, ClickableItem.of(skull, e -> new ProfileProvider(viewer, this).open(viewer)));
			}

			// Requests Sent
			int sentCount = user.getRequests_sent().size();
			if (sentCount > 0) {
				ItemBuilder sent = new ItemBuilder(ItemModelType.GUI_PROFILE_ICON_FRIEND_REQUESTS_SENT).name("&3Requests sent: &a" + sentCount)
					.dyeColor(ColorType.CYAN).itemFlags(ItemFlags.HIDE_ALL);

				contents.set(0, 3, ClickableItem.of(sent,
					e -> new FriendRequestsProvider(FriendRequestType.SENT, this).open(viewer)));
			}

			// Requests Received
			int receivedCount = user.getRequests_received().size();

			if (receivedCount > 0) {
				int unreadCount = user.getUnreadReceived().size();
				ItemBuilder received = new ItemBuilder(ItemModelType.GUI_PROFILE_ICON_FRIEND_REQUESTS_RECEIVED).name("&3Requests received: &a" + receivedCount)
					.dyeColor(ColorType.LIGHT_GREEN).itemFlags(ItemFlags.HIDE_ALL);

				if (unreadCount > 0)
					received.lore("&e" + unreadCount + " &3Unread");

				contents.set(0, 5, ClickableItem.of(received,
					e -> {
						user.clearUnread();
						new FriendRequestsProvider(FriendRequestType.RECEIVED, this).open(viewer);
					}));
			}
		}

		List<ClickableItem> items = new ArrayList<>();

		for (UUID uuid : user.getFriends()) {
			Nerd targetNerd = Nerd.of(uuid);
			ItemBuilder skull = getFriendSkull(targetNerd, viewer);
			skull.lore("", "&eClick &3to view profile");

			items.add(ClickableItem.of(skull, e -> ProfileCommand.openProfile(targetNerd, viewer, this)));
		}

		paginate(items);
	}

	public static ItemBuilder getBaseFriendSkull(Nerd targetNerd) {
		return new ItemBuilder(Material.PLAYER_HEAD)
			.skullOwner(targetNerd)
			.name("&e" + targetNerd.getNickname());
	}

	public static ItemBuilder getFriendSkull(Nerd targetNerd, Player viewer) {
		ItemBuilder skull = getBaseFriendSkull(targetNerd);

		OfflinePlayer target = targetNerd.getPlayer();
		if (target != null) {
			if (target.isOnline() && PlayerUtils.canSee(viewer, target)) {
				LocalDateTime lastJoin = targetNerd.getLastJoin(viewer);
				skull.lore("&3Online for: &e" + Timespan.of(lastJoin).format());
			} else {
				LocalDateTime lastQuit = targetNerd.getLastQuit(viewer);
				skull.lore("&3Last seen: &e" + Timespan.of(lastQuit).format());
			}
		}

		return skull;
	}
}
