package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.profile.ProfileUser;
import gg.projecteden.nexus.models.profile.ProfileUser.ProfileTextureType;
import gg.projecteden.nexus.models.profile.ProfileUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Title("Select a profile texture")
public class ProfileTextureProvider extends InventoryProvider {
	private static final ProfileUserService service = new ProfileUserService();
	private final ProfileUser user;
	private final InventoryProvider previousMenu;
	private final Set<ProfileTextureType> unlockedTextures;

	public ProfileTextureProvider(ProfileUser user, @Nullable InventoryProvider previousMenu) {
		this.user = user;
		this.previousMenu = previousMenu;
		this.unlockedTextures = new HashSet<>(user.getUnlockedTextureTypes());
		this.unlockedTextures.add(ProfileTextureType.NONE);
	}

	@Override
	public void init() {
		addBackItem(previousMenu);

		List<ClickableItem> items = new ArrayList<>();

		for (ProfileTextureType type : this.unlockedTextures.stream().sorted().toList()) {
			if (type.isInternal())
				continue;

			ItemBuilder displayItem = new ItemBuilder(type.getCouponModel()).name("&3" + StringUtils.camelCase(type));

			if (type.isDyeable())
				displayItem.lore("&eColorable");

			items.add(ClickableItem.of(displayItem, e -> {
				user.setTextureType(type);
				service.save(user);
				previousMenu.open(viewer);
			}));
		}

		paginate(items);
	}
}
