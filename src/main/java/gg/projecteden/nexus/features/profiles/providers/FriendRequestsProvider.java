package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.profiles.ProfileCommand;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendRequestsProvider extends InventoryProvider {
	private static final FriendsUserService userService = new FriendsUserService();
	private final FriendRequestType type;
	private FriendsUser user;

	public FriendRequestsProvider(FriendRequestType type) {
		this.type = type;
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
		user = userService.get(player);

		List<ClickableItem> items = new ArrayList<>();
		List<UUID> requests = getRequests(type);

		addBackItem(e -> new FriendsProvider().open(player));

		for (UUID uuid : requests) {
			Nerd nerd = Nerd.of(uuid);
			ItemBuilder skull = new ItemBuilder(Material.PLAYER_HEAD)
				.skullOwner(nerd)
				.name(nerd.getNickname())
				.lore("View Profile", "Shift Click to cancel request");

			items.add(ClickableItem.of(skull, e -> {
				if (e.isShiftClick()) {
					user.clearRequests(userService.get(uuid));
					refresh();
				} else
					ProfileCommand.openProfile(nerd, player, this);
			}));
		}

		paginator().items(items).build();
	}

	private List<UUID> getRequests(FriendRequestType type) {
		return switch (type) {
			case SENT -> user.getRequests_sent();
			case RECEIVED -> user.getRequests_received();
		};
	}

}
