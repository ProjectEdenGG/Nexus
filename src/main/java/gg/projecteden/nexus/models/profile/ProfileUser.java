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
	private PrivacySetting friendsPrivacy = PrivacySetting.FRIENDS_ONLY;
	private PrivacySetting socialMediaPrivacy = PrivacySetting.FRIENDS_ONLY;

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

	public boolean canView(PrivacySettingType settingType, Player viewer) {
		if (viewer.getUniqueId().equals(uuid))
			return true;

		if (Rank.of(viewer).isSeniorStaff())
			return true;

		PrivacySetting setting = switch (settingType) {
			case FRIENDS -> this.friendsPrivacy;
			case SOCIAL_MEDIA -> this.socialMediaPrivacy;
		};

		return switch (setting) {
			case PRIVATE -> false;
			case PUBLIC -> true;
			case FRIENDS_ONLY -> {
				FriendsUserService friendsUserService = new FriendsUserService();
				FriendsUser friendsUser = friendsUserService.get(this);
				if (friendsUser.getFriends().isEmpty())
					yield false;

				yield friendsUser.getFriends().contains(viewer.getUniqueId());

			}
			default -> false;
		};
	}

}
