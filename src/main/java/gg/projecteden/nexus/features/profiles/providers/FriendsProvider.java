package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.profiles.ProfileCommand;
import gg.projecteden.nexus.features.profiles.providers.FriendRequestsProvider.FriendRequestType;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Title("Friends")
public class FriendsProvider extends InventoryProvider {
	private static final FriendsUserService userService = new FriendsUserService();
	private FriendsUser user;

	@Override
	public void init() {
		user = userService.get(player);
		addCloseItem();

		contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.STONE_BUTTON).name("Requests Sent"),
			e -> new FriendRequestsProvider(FriendRequestType.SENT).open(player)));

		contents.set(0, 7, ClickableItem.of(new ItemBuilder(Material.STONE_BUTTON).name("Requests Received"),
			e -> new FriendRequestsProvider(FriendRequestType.RECEIVED).open(player)));

		List<ClickableItem> items = new ArrayList<>();
		for (UUID uuid : user.getFriends()) {
			Nerd nerd = Nerd.of(uuid);
			ItemBuilder skull = new ItemBuilder(Material.PLAYER_HEAD)
				.skullOwner(nerd)
				.name(nerd.getNickname())
				.lore("View Profile");

			items.add(ClickableItem.of(skull, e -> ProfileCommand.openProfile(nerd, player, this)));
		}

		paginator().items(items).build();
	}
}
