package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.profile.ProfileUser;
import gg.projecteden.nexus.models.profile.ProfileUser.PrivacySetting;
import gg.projecteden.nexus.models.profile.ProfileUserService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@Rows(3)
@Title("Profile Settings")
public class ProfileSettingsProvider extends InventoryProvider {
	InventoryProvider previousMenu = null;
	ProfileUser user;

	public ProfileSettingsProvider(Player viewer, @Nullable InventoryProvider previousMenu, ProfileUser user) {
		this.viewer = viewer;
		this.previousMenu = previousMenu;
		this.user = user;
	}

	@Override
	public void init() {
		addBackItem(e -> new ProfileProvider(viewer, this).open(viewer));

		for (ProfileSetting setting : ProfileSetting.values()) {
			setting.setClickableItem(viewer, user, previousMenu, this, contents);
		}
	}

	@Getter
	@AllArgsConstructor
	private enum ProfileSetting {
		BACKGROUND_COLOR(1, 0, CustomMaterial.DYE_STATION_BUTTON_DYE) {
			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				Color backgroundColor = getUserBackgroundColor(user);

				return super.getDisplayItem(viewer, user)
					.dyeColor(user.getBukkitBackgroundColor())
					.itemFlags(ItemFlags.HIDE_ALL)
					.lore(List.of(
						"&cR: " + backgroundColor.getRed(),
						"&aG: " + backgroundColor.getGreen(),
						"&bB: " + backgroundColor.getBlue(),
						"",
						"&3Hex: &e" + StringUtils.toHex(backgroundColor)
					));
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				Consumer<Color> applyColor = _color -> {
					ProfileUserService userService = new ProfileUserService();
					user.setBackgroundColor(ChatColor.of(ColorType.toJava(_color)));
					userService.save(user);
					refresh(viewer, user, previousMenu, provider, contents);
				};

				Consumer<Color> saveColor = _color -> {
					ProfileUserService userService = new ProfileUserService();
					user.getSavedColors().add(_color);
					userService.save(user);
					refresh(viewer, user, previousMenu, provider, contents);
				};

				Consumer<Color> unSaveColor = _color -> {
					ProfileUserService userService = new ProfileUserService();
					user.getSavedColors().remove(_color);
					userService.save(user);
					refresh(viewer, user, previousMenu, provider, contents);
				};

				new ColorCreatorProvider(viewer, previousMenu, user.getBackgroundColor(), applyColor, user.getSavedColors(), saveColor, unSaveColor).open(viewer);
			}

			private Color getUserBackgroundColor(ProfileUser user) {
				return user.getBukkitBackgroundColor();
			}
		},

		FRIENDS_PRIVACY(1, 1, CustomMaterial.GUI_PROFILE_ICON_FRIENDS) {
			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				ItemBuilder displayItem = super.getDisplayItem(viewer, user);
				PrivacySetting setting = getPrivacy(user);

				return displayItem
					.lore("&7⬇ " + StringUtils.camelCase(setting.previousWithLoop()))
					.lore("&e⬇ " + StringUtils.camelCase(setting))
					.lore("&7⬇ " + StringUtils.camelCase(setting.nextWithLoop()));
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				ProfileUserService userService = new ProfileUserService();
				user.setFriendsPrivacy(getPrivacy(user).nextWithLoop());
				userService.save(user);

				refresh(viewer, user, previousMenu, provider, contents);
			}

			private PrivacySetting getPrivacy(ProfileUser user) {
				return user.getFriendsPrivacy();
			}
		},

		SOCIAL_MEDIA_PRIVACY(1, 2, CustomMaterial.GUI_PROFILE_ICON_SOCIAL_MEDIA) {
			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				ItemBuilder displayItem = super.getDisplayItem(viewer, user);
				PrivacySetting setting = getPrivacy(user);

				return displayItem
					.lore("&7⬇ " + StringUtils.camelCase(setting.previousWithLoop()))
					.lore("&e⬇ " + StringUtils.camelCase(setting))
					.lore("&7⬇ " + StringUtils.camelCase(setting.nextWithLoop()));
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				ProfileUserService userService = new ProfileUserService();
				user.setSocialMediaPrivacy(getPrivacy(user).nextWithLoop());
				userService.save(user);

				refresh(viewer, user, previousMenu, provider, contents);
			}

			private PrivacySetting getPrivacy(ProfileUser user) {
				return user.getSocialMediaPrivacy();
			}
		};

		private final int row;
		private final int col;
		private final CustomMaterial displayMaterial;

		public String getDisplayName() {
			return "&eSet " + StringUtils.camelCase(this.name());
		}

		public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
			return new ItemBuilder(getDisplayMaterial()).name(getDisplayName());
		}

		public SlotPos getSlotPos() {
			return SlotPos.of(this.row, this.col);
		}

		public void setClickableItem(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents) {
			contents.set(getSlotPos(), ClickableItem.of(getDisplayItem(viewer, user), e -> onClick(viewer, user, previousMenu, provider, contents, e)));
		}

		public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
		}

		public void refresh(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents) {
			this.setClickableItem(viewer, user, previousMenu, provider, contents);
		}
	}
}
