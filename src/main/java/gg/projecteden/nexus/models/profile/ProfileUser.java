package gg.projecteden.nexus.models.profile;

import de.tr7zw.nbtapi.NBTItem;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.profiles.colorcreator.ColorCreatorProvider.CreatedColor;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomFont;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Entity(value = "profile_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class ProfileUser implements PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private ChatColor backgroundColor = ChatColor.WHITE;
	private ChatColor textureColor = ChatColor.WHITE;
	private ProfileTextureType textureType = ProfileTextureType.NONE;
	private String status;
	private PrivacySetting friendsPrivacy = PrivacySetting.FRIENDS_ONLY;
	private PrivacySetting socialMediaPrivacy = PrivacySetting.FRIENDS_ONLY;
	private Set<CreatedColor> createdColors = new HashSet<>();
	private Set<ProfileTextureType> unlockedTextureTypes = new HashSet<>();
	private ProfileTitleFont titleFont = ProfileTitleFont.BLOCKY;

	public Color getBukkitBackgroundColor() {
		return ColorType.toBukkitColor(this.backgroundColor);
	}

	public Color getBukkitTextureColor() {
		return ColorType.toBukkitColor(this.textureColor);
	}

	public String getTexture(ChatColor color, int rows) {
		return textureType.getTexture(color, rows);
	}

	public enum PrivacySetting implements IterableEnum {
		PUBLIC,
		FRIENDS_ONLY,
		PRIVATE,
		;
	}

	public enum PrivacySettingType {
		FRIENDS,
		SOCIAL_MEDIA,
		;
	}

	@Getter
	@AllArgsConstructor
	public enum ProfileTitleFont implements IterableEnum {
		BLOCKY(CustomFont.BLOCKY),
		DAYDREAM(CustomFont.DAYDREAM),
		;

		private final CustomFont font;
	}

	public boolean canNotView(PrivacySettingType settingType, Player viewer) {
		if (viewer.getUniqueId().equals(uuid))
			return false;

		if (Rank.of(viewer).isSeniorStaff())
			return false;

		PrivacySetting setting = switch (settingType) {
			case FRIENDS -> this.friendsPrivacy;
			case SOCIAL_MEDIA -> this.socialMediaPrivacy;
		};

		return switch (setting) {
			case PRIVATE -> true;
			case PUBLIC -> false;
			case FRIENDS_ONLY -> {
				FriendsUserService friendsUserService = new FriendsUserService();
				FriendsUser friendsUser = friendsUserService.get(this);
				if (friendsUser.getFriends().isEmpty())
					yield true;

				yield friendsUser.getFriends().contains(viewer.getUniqueId());

			}
			default -> true;
		};
	}

	@AllArgsConstructor
	@SuppressWarnings("deprecation")
	public enum ProfileTextureType {
		NONE(null, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_NONE),

		// Background
		DOTS(InventoryTexture.GUI_PROFILE_TEXTURE_DOTS, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_DOTS),
		SHINE(InventoryTexture.GUI_PROFILE_TEXTURE_SHINE, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_SHINE),
		VERTICAL_STRIPES(InventoryTexture.GUI_PROFILE_TEXTURE_STRIPES_VERTICAL, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_VERTICAL_STRIPES),
		SPLIT(InventoryTexture.GUI_PROFILE_TEXTURE_SPLIT, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_SPLIT),
		CIRCUIT(InventoryTexture.GUI_PROFILE_TEXTURE_CIRCUIT, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_CIRCUIT),
		GINGHAM(InventoryTexture.GUI_PROFILE_TEXTURE_GINGHAM, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_GINGHAM),
		ZEBRA(InventoryTexture.GUI_PROFILE_TEXTURE_ZEBRA, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_ZEBRA),
		HEARTS(InventoryTexture.GUI_PROFILE_TEXTURE_HEARTS, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_HEARTS),

		// Overlay
		CATS(59, 2, InventoryTexture.GUI_PROFILE_IMAGE_CATS, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_CATS),
		BEES(59, 2, InventoryTexture.GUI_PROFILE_IMAGE_BEES, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_BEES),

		// Special
		BIRTHDAY(59, 2, InventoryTexture.GUI_PROFILE_IMAGE_BIRTHDAY, ItemModelType.GUI_PROFILE_TEXTURE_ITEM_BIRTHDAY),

		// Internal
		TEST(InventoryTexture.GUI_PROFILE_TESTING, null),
		;

		private final boolean internal;
		@Getter
		private final boolean image;
		private final int imageMinus;
		private final int shiftPlayerName;
		private final InventoryTexture texture;
		@Getter
		private final ItemModelType couponModel;
		public static final String NBT_TEXTURE_TYPE = "ProfileTextureType";

		ProfileTextureType(InventoryTexture texture, ItemModelType couponModel) {
			this(false, false, 59, 0, texture, couponModel);
		}

		ProfileTextureType(int imageMinus, int shiftPlayerName, InventoryTexture texture, ItemModelType couponModel) {
			this(false, true, imageMinus, shiftPlayerName, texture, couponModel);
		}

		public boolean isInternal() {
			return this.internal || this.couponModel == null;
		}

		public boolean isDyeable() {
			return !this.image && this != NONE;
		}

		public String getShiftedTitleName(ProfileUser user) {
			String shiftedTitle = "";
			if (this.isImage())
				shiftedTitle = " ꈃ".repeat(this.shiftPlayerName);

			return shiftedTitle + user.getNickname().toLowerCase().chars()
				.mapToObj(c -> (char) c + "ꈃ")
				.collect(Collectors.joining());
		}

		public String getTexture(ChatColor color, int rows) {
			if (this == NONE)
				return "";

			if (this.isImage())
				return InventoryTexture.getMenuTexture(this.imageMinus, this.texture.getFontChar(), ChatColor.WHITE, rows);

			return this.texture.getNextMenuTexture(color, rows);
		}


		public static ProfileTextureType fromItem(ItemStack itemStack) {
			NBTItem nbtItem = new NBTItem(itemStack);
			if (!nbtItem.hasKey(NBT_TEXTURE_TYPE))
				return null;

			String type = nbtItem.getString(NBT_TEXTURE_TYPE);

			try {
				return ProfileTextureType.valueOf(type.trim().toUpperCase().replaceAll(" ", "_"));
			} catch (Exception e) {
				return null;
			}
		}

		public ItemStack getCouponItem() {
			String type = StringUtils.camelCase(this);

			if (this.couponModel == null)
				throw new InvalidInputException("ProfileTextureType &e" + type + " &cdoes not have a coupon item model");

			return new ItemBuilder(this.couponModel)
				.name("&3Profile Texture Coupon")
				.lore(
					"&3Type: &e" + type,
					"",
					"&eRight Click &3to claim"
				)
				.nbt(nbt -> nbt.setString(NBT_TEXTURE_TYPE, type))
				.setting(ItemSetting.RENAMEABLE, false)
				.build();
		}
	}

}
