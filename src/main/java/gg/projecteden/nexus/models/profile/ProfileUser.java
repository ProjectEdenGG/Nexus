package gg.projecteden.nexus.models.profile;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.profiles.colorcreator.ColorCreatorProvider.CreatedColor;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomTexture;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

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
	public enum ProfileTextureType {
		NONE(null),
		//
		DOTS(CustomTexture.GUI_PROFILE_TEXTURE_DOTS),
		SHINE(CustomTexture.GUI_PROFILE_TEXTURE_SHINE),
		VERTICAL_STRIPES(CustomTexture.GUI_PROFILE_TEXTURE_STRIPES_VERTICAL),
		SPLIT(CustomTexture.GUI_PROFILE_TEXTURE_SPLIT),

		// Overlays
		TEST(59, 2, CustomTexture.GUI_PROFILE_IMAGE_TEST),
		BIRTHDAY(59, 2, CustomTexture.GUI_PROFILE_IMAGE_BIRTHDAY),
		CATS(59, 2, CustomTexture.GUI_PROFILE_IMAGE_CATS),
		BEES(59, 2, CustomTexture.GUI_PROFILE_IMAGE_BEES),
		;

		@Getter
		private final boolean image;
		private final int imageMinus;
		private final int shiftPlayerName;
		private final CustomTexture texture;

		ProfileTextureType(CustomTexture texture) {
			this(false, 59, 0, texture);
		}

		ProfileTextureType(int imageMinus, int shiftPlayerName, CustomTexture texture) {
			this(true, imageMinus, shiftPlayerName, texture);
		}

		public String getShiftedTitleName(ProfileUser user) {
			String shiftedTitle = "";
			if (this.isImage())
				shiftedTitle = " ꈃ".repeat(shiftPlayerName);

			return shiftedTitle + user.getNickname().toLowerCase().chars()
				.mapToObj(c -> (char) c + "ꈃ")
				.collect(Collectors.joining());
		}

		public String getTexture(ChatColor color, int rows) {
			if (this == NONE)
				return "";

			if (this.isImage())
				return CustomTexture.getMenuTexture(this.imageMinus, this.texture.getFontChar(), ChatColor.WHITE, rows);

			return this.texture.getNextMenuTexture(color, rows);
		}
	}

}
