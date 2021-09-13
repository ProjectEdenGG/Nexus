package gg.projecteden.nexus.models.emblem;

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

import static gg.projecteden.utils.StringUtils.camelCase;

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

	public String getBadge() {
		if (!hasBadge())
			return "";

		return "&f" + active.getEmoji() + " ";
	}

	public JsonBuilder getBadgeJson(Chatter viewer) {
		if (!hasBadge())
			return new JsonBuilder();

		final String emoji = viewer != null && ResourcePack.isEnabledFor(viewer) ? active.getEmoji() : active.getAlt();

		final JsonBuilder json = new JsonBuilder("&f" + emoji).hover("&f" + camelCase(active.name()) + " Badge");
		active.customize(this, json);

		return json.group().next(" ").group();
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
		BOT("\uE002", "&bʙᴏᴛ"),
		SUPPORTER("\uD83D\uDC96", "&c❤"),
		TWITCH("\uE001", "&4▶", (nerd, json) -> {
			final SocialMediaUser user = new SocialMediaUserService().get(nerd);
			final Connection twitch = user.getConnection(SocialMediaSite.TWITCH);
			if (twitch != null) {
				final String url = twitch.getUrl();
				json.hover(url).url(url);
				if (user.isMature())
					json.hover("&4Warning: &c18+ only");
			}
		}),

		;

		Badge(String emoji, String alt) {
			this(emoji, alt, null);
		}

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
