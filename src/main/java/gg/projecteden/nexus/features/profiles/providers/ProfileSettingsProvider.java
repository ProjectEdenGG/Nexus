package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.profile.ProfileUser;
import gg.projecteden.nexus.models.profile.ProfileUser.PrivacySetting;
import gg.projecteden.nexus.models.profile.ProfileUserService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
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
	private static final ProfileUserService service = new ProfileUserService();
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
		BACKGROUND_COLOR(CustomMaterial.DYE_STATION_BUTTON_DYE) {
			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				Color color = getUserBackgroundColor(user);

				return super.getDisplayItem(viewer, user)
					.dyeColor(user.getBukkitBackgroundColor())
					.itemFlags(ItemFlags.HIDE_ALL)
					.lore(List.of(
						"",
						"&cR: " + color.getRed(),
						"&aG: " + color.getGreen(),
						"&bB: " + color.getBlue(),
						"",
						"&3Hex: &e" + StringUtils.toHex(color)
					));
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				Consumer<Color> applyColor = _color -> {
					user.setBackgroundColor(ChatColor.of(ColorType.toJava(_color)));
					service.save(user);
					new ProfileSettingsProvider(viewer, previousMenu, user).open(viewer);
				};

				Consumer<Color> saveColor = _color -> {
					user.getSavedColors().add(_color);
					service.save(user);
				};

				Consumer<Color> unSaveColor = _color -> {
					user.getSavedColors().remove(_color);
					service.save(user);
				};

				new ColorCreatorProvider(viewer, previousMenu, user.getBackgroundColor(), applyColor, user.getSavedColors(), saveColor, unSaveColor).open(viewer);
			}

			private Color getUserBackgroundColor(ProfileUser user) {
				return user.getBukkitBackgroundColor();
			}
		},

		TEXTURE_COLOR(CustomMaterial.DYE_STATION_BUTTON_DYE) {
			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				Color color = getUserTextureColor(user);

				return super.getDisplayItem(viewer, user)
					.dyeColor(user.getBukkitTextureColor())
					.itemFlags(ItemFlags.HIDE_ALL)
					.lore(List.of(
						"",
						"&cR: " + color.getRed(),
						"&aG: " + color.getGreen(),
						"&bB: " + color.getBlue(),
						"",
						"&3Hex: &e" + StringUtils.toHex(color)
					));
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				Consumer<Color> applyColor = _color -> {
					user.setTextureColor(ChatColor.of(ColorType.toJava(_color)));
					service.save(user);
					new ProfileSettingsProvider(viewer, previousMenu, user).open(viewer);
				};

				Consumer<Color> saveColor = _color -> {
					user.getSavedColors().add(_color);
					service.save(user);
				};

				Consumer<Color> unSaveColor = _color -> {
					user.getSavedColors().remove(_color);
					service.save(user);
				};

				new ColorCreatorProvider(viewer, previousMenu, user.getTextureColor(), applyColor, user.getSavedColors(), saveColor, unSaveColor).open(viewer);
			}

			private Color getUserTextureColor(ProfileUser user) {
				return user.getBukkitTextureColor();
			}
		},

		FRIENDS_PRIVACY(CustomMaterial.GUI_PROFILE_ICON_FRIENDS) {
			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				ItemBuilder displayItem = super.getDisplayItem(viewer, user);
				PrivacySetting setting = getPrivacy(user);

				return displayItem
					.lore("")
					.lore("&7⬇ " + StringUtils.camelCase(setting.previousWithLoop()))
					.lore("&e⬇ " + StringUtils.camelCase(setting))
					.lore("&7⬇ " + StringUtils.camelCase(setting.nextWithLoop()));
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				user.setFriendsPrivacy(getPrivacy(user).nextWithLoop());
				service.save(user);

				refresh(viewer, user, previousMenu, provider, contents);
			}

			private PrivacySetting getPrivacy(ProfileUser user) {
				return user.getFriendsPrivacy();
			}
		},

		SOCIAL_MEDIA_PRIVACY(CustomMaterial.GUI_PROFILE_ICON_SOCIAL_MEDIA) {
			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				ItemBuilder displayItem = super.getDisplayItem(viewer, user);
				PrivacySetting setting = getPrivacy(user);

				return displayItem
					.lore("")
					.lore("&7⬇ " + StringUtils.camelCase(setting.previousWithLoop()))
					.lore("&e⬇ " + StringUtils.camelCase(setting))
					.lore("&7⬇ " + StringUtils.camelCase(setting.nextWithLoop()));
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				user.setSocialMediaPrivacy(getPrivacy(user).nextWithLoop());
				service.save(user);

				refresh(viewer, user, previousMenu, provider, contents);
			}

			private PrivacySetting getPrivacy(ProfileUser user) {
				return user.getSocialMediaPrivacy();
			}
		},
		STATUS(CustomMaterial.GUI_PROFILE_ICON_STATUS) {
			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				String status = user.getStatus();
				if (Nullables.isNullOrEmpty(status))
					status = "Not set yet";

				ItemBuilder itemBuilder = super.getDisplayItem(viewer, user);
				itemBuilder.lore("", "&3Current Status:", "&e" + status);
				return itemBuilder;
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				String status = "";
				if (Nullables.isNotNullOrEmpty(user.getStatus()))
					status = user.getStatus();

				PlayerUtils.sendLine(viewer);
				new JsonBuilder(StringUtils.getPrefix("Profile")).group()
					.next("&eShift+Click &3here to set profile status").hover("&eShift+Click &3to set your profile status")
					.insert("/profile setStatus " + status)
					.send(viewer);
				PlayerUtils.sendLine(viewer);

				viewer.closeInventory();
			}
		},
		ABOUT(CustomMaterial.GUI_PROFILE_ICON_ABOUT) {
			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				Nerd nerd = Nerd.of(user);
				String about = nerd.getAbout();
				if (Nullables.isNullOrEmpty(about))
					about = "Not set yet";

				ItemBuilder itemBuilder = super.getDisplayItem(viewer, user);
				itemBuilder.lore("", "&3Current About:", "&e" + about);
				return itemBuilder;
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				String about = "";
				if (Nullables.isNotNullOrEmpty(user.getNerd().getAbout()))
					about = user.getNerd().getAbout();

				PlayerUtils.sendLine(viewer);
				new JsonBuilder(StringUtils.getPrefix("Profile")).group()
					.next("&eShift+Click &3here to set profile about").hover("&eShift+Click &3to set your profile about")
					.insert("/profile setAbout " + about)
					.send(viewer);
				PlayerUtils.sendLine(viewer);

				viewer.closeInventory();
			}
		};

		private final CustomMaterial displayMaterial;

		public String getDisplayName() {
			return "&eSet " + StringUtils.camelCase(this.name());
		}

		public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
			return new ItemBuilder(getDisplayMaterial()).name(getDisplayName());
		}

		public SlotPos getSlotPos() {
			return SlotPos.of(1, this.ordinal());
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
