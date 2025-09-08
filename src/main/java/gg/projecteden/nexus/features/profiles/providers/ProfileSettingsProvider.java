package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.profiles.ProfileSettingsUpdatedEvent;
import gg.projecteden.nexus.features.profiles.colorcreator.ColorCreatorProvider;
import gg.projecteden.nexus.features.profiles.colorcreator.ColorCreatorProvider.CreatedColor;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.profile.ProfileUser;
import gg.projecteden.nexus.models.profile.ProfileUser.PrivacySetting;
import gg.projecteden.nexus.models.profile.ProfileUser.ProfileTextureType;
import gg.projecteden.nexus.models.profile.ProfileUser.ProfileTitleFont;
import gg.projecteden.nexus.models.profile.ProfileUserService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

@Rows(3)
@Title("Profile Settings")
public class ProfileSettingsProvider extends InventoryProvider {
	private static final ProfileUserService service = new ProfileUserService();
	private final ProfileUser user;

	public ProfileSettingsProvider(Player viewer, ProfileUser user) {
		this.viewer = viewer;
		this.user = user;
	}

	@Override
	public void init() {
		addBackItem(e -> new ProfileProvider(viewer).open(viewer)); // ensures refreshing of data

		int col = 0;
		for (ProfileSetting setting : ProfileSetting.values()) {
			if (setting.setClickableItem(SlotPos.of(1, col), viewer, user, this, this, contents))
				col++;
		}
	}

	@Getter
	private enum ProfileSetting {
		BACKGROUND_COLOR(ItemModelType.DYE_STATION_BUTTON_DYE) {
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
					new ProfileSettingsUpdatedEvent(user).callEvent();
					new ProfileSettingsProvider(viewer, user).open(viewer);
				};

				Consumer<Color> saveColor = _color -> {
					user.getCreatedColors().add(new CreatedColor(_color));
					service.save(user);
				};

				Consumer<Color> deleteColor = _color -> {
					user.getCreatedColors().removeIf(createdColor -> createdColor.matches(_color));
					service.save(user);
				};

				Consumer<CreatedColor> renameColor = _createdColor -> {
					for (CreatedColor createdColor : user.getCreatedColors()) {
						if (createdColor.equals(_createdColor))
							createdColor.setName(_createdColor.getName());
					}
					service.save(user);
				};

				new ColorCreatorProvider(viewer, previousMenu, user.getBackgroundColor(), applyColor, user.getCreatedColors(), renameColor, saveColor, deleteColor).open(viewer);
			}

			private Color getUserBackgroundColor(ProfileUser user) {
				return user.getBukkitBackgroundColor();
			}
		},

		TEXTURE_COLOR(ItemModelType.DYE_STATION_BUTTON_DYE) {
			@Override
			public boolean shouldNotShow(Player viewer, ProfileUser user) {
				return user.getUnlockedTextureTypes().isEmpty() || !user.getTextureType().isDyeable();
			}

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
					new ProfileSettingsProvider(viewer, user).open(viewer);
				};

				Consumer<Color> saveColor = _color -> {
					user.getCreatedColors().add(new CreatedColor(_color));
					service.save(user);
				};

				Consumer<Color> deleteColor = _color -> {
					user.getCreatedColors().removeIf(createdColor -> createdColor.matches(_color));
					service.save(user);
				};

				Consumer<CreatedColor> renameColor = _createdColor -> {
					for (CreatedColor createdColor : user.getCreatedColors()) {
						if (createdColor.getColor().equals(_createdColor.getColor()))
							createdColor.setName(_createdColor.getName());
					}
					service.save(user);
				};

				new ColorCreatorProvider(viewer, previousMenu, user.getTextureColor(), applyColor, user.getCreatedColors(), renameColor, saveColor, deleteColor).open(viewer);
			}

			private Color getUserTextureColor(ProfileUser user) {
				return user.getBukkitTextureColor();
			}
		},

		TEXTURE_TYPE(ItemModelType.GUI_PROFILE_TEXTURE_ITEM_NONE) {
			@Override
			public boolean shouldNotShow(Player viewer, ProfileUser user) {
				return user.getUnlockedTextureTypes().isEmpty();
			}

			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				List<String> unlockedTypes = user.getUnlockedTextureTypes().stream()
					.map(type -> "&3 - &e" + StringUtils.camelCase(type))
					.toList();

				ProfileTextureType textureType = user.getTextureType();
				ItemModelType model = textureType.getCouponModel();
				if (model == null)
					model = ItemModelType.GUI_PROFILE_TEXTURE_ITEM_NONE;

				return super.getDisplayItem(viewer, user)
					.model(model)
					.lore("&3Selected: &e" + StringUtils.camelCase(textureType), "", "&3Unlocked:")
					.lore(unlockedTypes);
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				new ProfileTextureProvider(user, previousMenu).open(viewer);
			}
		},

		TITLE_FONT(ItemModelType.GUI_PROFILE_ICON_FONT) {
			@Override
			public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
				ProfileTitleFont current = user.getTitleFont();
				ProfileTitleFont previous = current.previousWithLoop();
				ProfileTitleFont next = current.nextWithLoop();

				return super.getDisplayItem(viewer, user)
					.lore("")
					.lore("&7⬇ " + previous.getName())
					.lore("&e⬇ " + current.getName())
					.lore("&7⬇ " + next.getName());

				/*
				return super.getDisplayItem(viewer, user)
					.lore("")
					.componentLore(new JsonBuilder("&7⬇ " + previous.getName()).font(previous.getFont()))
					.componentLore(new JsonBuilder("&e⬇ " + current.getName()).font(current.getFont()))
					.componentLore(new JsonBuilder("&7⬇ " + next.getName()).font(next.getFont()));
				*/
			}

			@Override
			public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
				user.setTitleFont(user.getTitleFont().nextWithLoop());
				service.save(user);

				refresh(viewer, user, previousMenu, provider, contents);
			}
		},

		FRIENDS_PRIVACY(ItemModelType.GUI_PROFILE_ICON_FRIENDS) {
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

		SOCIAL_MEDIA_PRIVACY(ItemModelType.GUI_PROFILE_ICON_SOCIAL_MEDIA) {
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
		STATUS(ItemModelType.GUI_PROFILE_ICON_STATUS) {
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
		ABOUT(ItemModelType.GUI_PROFILE_ICON_ABOUT) {
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

		private final Material material;
		private final String modelId;

		ProfileSetting(ItemModelType itemModelType) {
			this(itemModelType.getMaterial(), itemModelType.getModel());
		}

		ProfileSetting(Material material) {
			this(material, null);
		}

		ProfileSetting(Material material, String modelId) {
			this.material = material;
			this.modelId = modelId;
		}

		public String getDisplayName() {
			return "&eSet " + StringUtils.camelCase(this.name());
		}

		public ItemBuilder getDisplayItem(Player viewer, ProfileUser user) {
			return new ItemBuilder(getMaterial()).model(getModelId()).name(getDisplayName());
		}

		public boolean shouldNotShow(Player viewer, ProfileUser user) {
			return false;
		}

		public boolean setClickableItem(SlotPos slotPos, Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents) {
			ItemBuilder itemBuilder = getDisplayItem(viewer, user);
			if (itemBuilder == null || shouldNotShow(viewer, user))
				return false;

			contents.set(slotPos, ClickableItem.of(getDisplayItem(viewer, user), e -> onClick(viewer, user, previousMenu, provider, contents, e)));
			return true;
		}

		public void onClick(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents, ItemClickData e) {
		}

		public void refresh(Player viewer, ProfileUser user, InventoryProvider previousMenu, InventoryProvider provider, InventoryContents contents) {
			for (int i = 0; i < 9; i++) {
				contents.set(1, i, ClickableItem.AIR);
			}

			int col = 0;
			for (ProfileSetting setting : ProfileSetting.values()) {
				if (setting.setClickableItem(SlotPos.of(1, col), viewer, user, previousMenu, provider, contents))
					col++;
			}
		}
	}
}
