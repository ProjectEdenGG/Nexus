package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.models.badge.BadgeUser;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.profile.ProfileUser;
import gg.projecteden.nexus.models.profile.ProfileUserService;
import gg.projecteden.nexus.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Title("Badge Choices:")
public class BadgesProvider extends InventoryProvider {
	private static final ProfileUserService profileUserService = new ProfileUserService();

	InventoryProvider previousMenu;
	private final ProfileUser profileUser;
	private final BadgeUser badgeUser;
	private final int column;

	public BadgesProvider(InventoryProvider previousMenu, BadgeUser targetUser, ProfileUser profileUser, int column) {
		this.previousMenu = previousMenu;
		this.badgeUser = targetUser;
		this.profileUser = profileUser;
		this.column = column;
	}

	@Override
	public void init() {
		addBackItem(previousMenu);

		Set<Badge> badgeChoices = new HashSet<>(badgeUser.getOwned());
		badgeChoices.removeAll(profileUser.getBadges().keySet());

		List<ClickableItem> items = new ArrayList<>();
		for (Badge badge : badgeChoices) {
			ItemBuilder item = new ItemBuilder(badge.getModelType())
				.name(badge.getName() + " Badge")
				.lore(badge.getLore(badgeUser));

			items.add(ClickableItem.of(item, e -> {
				profileUser.getBadges().put(badge, column);
				profileUserService.save(profileUser);
				previousMenu.open(viewer);
			}));
		}

		paginate(items);
	}
}
