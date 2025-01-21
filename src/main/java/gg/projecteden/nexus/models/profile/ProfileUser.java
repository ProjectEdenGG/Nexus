package gg.projecteden.nexus.models.profile;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
	private String status;
	private PrivacySetting friendsPrivacy = PrivacySetting.FRIENDS_ONLY;
	private PrivacySetting socialMediaPrivacy = PrivacySetting.FRIENDS_ONLY;
	private Set<Color> savedColors = new HashSet<>();

	public Color getBukkitBackgroundColor() {
		return ColorType.toBukkitColor(this.backgroundColor);
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

}
