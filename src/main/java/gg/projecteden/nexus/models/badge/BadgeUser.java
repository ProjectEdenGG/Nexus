package gg.projecteden.nexus.models.badge;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser.Connection;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

import static gg.projecteden.utils.StringUtils.isNullOrEmpty;

@Data
@Entity(value = "badge_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class BadgeUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Badge active;
	private Set<Badge> owned = new HashSet<>();

	public JsonBuilder getBadgeJson(Chatter viewer) {
		final JsonBuilder json = new JsonBuilder();
		if (!hasBadge())
			return json;

		final String emoji = viewer != null && ResourcePack.isEnabledFor(viewer) ? active.getEmoji() : active.getAlt();

		if (isNullOrEmpty(emoji))
			return json;

		json.next("&f" + emoji).hover("&f" + active.getName() + " Badge");
		active.customize(this, json);

		return json.next(" ").group();
	}

	public boolean hasBadge() {
		return active != null;
	}

	public boolean owns(Badge badge) {
		return owned.contains(badge);
	}

	public void give(Badge badge) {
		owned.add(badge);
	}

	@Getter
	@AllArgsConstructor
	public enum Badge {
		BOT("Bot", "\uE002", "&bʙᴏᴛ"),
		SUPPORTER("Supporter", "\uD83D\uDC96", "&c❤"),
		TWITTER(SocialMediaSite.TWITTER),
		INSTAGRAM(SocialMediaSite.INSTAGRAM),
		SNAPCHAT(SocialMediaSite.SNAPCHAT),
		YOUTUBE(SocialMediaSite.YOUTUBE),
		TWITCH(SocialMediaSite.TWITCH),
		TIKTOK(SocialMediaSite.TIKTOK),
		DISCORD(SocialMediaSite.DISCORD),
		STEAM(SocialMediaSite.STEAM),
		SPOTIFY(SocialMediaSite.SPOTIFY),
		REDDIT(SocialMediaSite.REDDIT),
		GITHUB(SocialMediaSite.GITHUB),
		;

		Badge(SocialMediaSite site) {
			this(site.getName(), site.getEmoji(), "&4▶", (nerd, json) -> {
				final SocialMediaUser user = new SocialMediaUserService().get(nerd);
				final Connection connection = user.getConnection(site);
				if (connection != null) {
					final String url = connection.getUrl();
					if (site.getProfileUrl().equals("%s"))
						json.copy(url).hover("", "&e" + url, "", "&eClick to copy");
					else
						json.url(url).hover("", "&e" + url, "", "&eClick to open");

					if (user.isMature())
						json.hover("", "&4Warning: &c18+ only");
				} else {
					json.hover("", "&cNo account linked");
				}
			});
		}

		Badge(String name, String emoji, String alt) {
			this(name, emoji, alt, null);
		}

		private final String name;
		private final String emoji;
		private final String alt;
		private final BiConsumer<BadgeUser, JsonBuilder> consumer;

		public void customize(BadgeUser nerd, JsonBuilder json) {
			if (consumer == null)
				return;

			consumer.accept(nerd, json);
		}

	}

}
