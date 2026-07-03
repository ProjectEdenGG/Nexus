package gg.projecteden.nexus.models.badge;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomEmoji;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.monthlypodium.MonthlyPodiumUser;
import gg.projecteden.nexus.models.monthlypodium.MonthlyPodiumUser.MonthlyPodiumData;
import gg.projecteden.nexus.models.monthlypodium.MonthlyPodiumUserService;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser.Connection;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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

		final String emoji = active.getEmoji();

		if (Nullables.isNullOrEmpty(emoji))
			return json;

		json.next("&f" + emoji).hover("&f" + active.getName() + " Badge");
		active.customize(this, json);

		return json.next(" ").group();
	}

	public boolean hasBadge() {
		return active != null;
	}

	public boolean owns(Badge badge) {
		if (badge == Badge.BOT)
			return false;

		return owned.contains(badge);
	}

	public void give(Badge badge) {
		owned.add(badge);
	}

	public void take(Badge badge) {
		owned.remove(badge);
		if (active == badge)
			active = null;
	}

	private static final Function<SocialMediaSite, BiConsumer<BadgeUser, JsonBuilder>> SOCIAL_MEDIA_CONSUMER = site -> (nerd, json) -> {
		final SocialMediaUser user = new SocialMediaUserService().get(nerd);
		final Connection connection = user.getConnection(site);
		if (connection != null) {
			final String url = connection.getUrl();
			if ("%s".equals(site.getProfileUrl()))
				json.copy(url).hover("", "&e" + url, "", "&eClick to copy");
			else
				json.url(url).hover("", "&e" + url, "", "&eClick to open");

			if (user.isMature())
				json.hover("", "&4Warning: &c18+ only");
		} else {
			json.hover("", "&cNo account linked");
		}
	};

	private static final BiConsumer<BadgeUser, JsonBuilder> MONTHLY_PODIUM_CONSUMER = (nerd, json) -> {
		final MonthlyPodiumUser user = new MonthlyPodiumUserService().get(nerd);
		final List<MonthlyPodiumData> podiums = new ArrayList<>(user.getPodiums());
		podiums.sort(Comparator.comparingInt(data -> data.getSpot().ordinal()));
		podiums.forEach(podium -> json.hover("", "%s %s place &7| &e%s &7- %s".formatted(
			podium.getSpot().getBadge().getEmoji(),
			StringUtils.getNumberWithSuffix(podium.getSpot().ordinal() + 1),
			StringUtils.camelCase(podium.getType()),
			podium.getText())
		));
	};

	@Getter
	@AllArgsConstructor
	public enum Badge {
		BOT("Bot", CustomEmoji.BOT, ItemModelType.BADGE_BOT, null),
		SUPPORTER("Supporter", CustomEmoji.SUPPORTER, ItemModelType.BADGE_SUPPORTER, "Purchase any package on the /store"),
		BIRTHDAY("Birthday", CustomEmoji.BIRTHDAY, ItemModelType.BADGE_BIRTHDAY, "Given on your /birthday"),
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
		MONTHLY_PODIUMS_FIRST("Monthly Podiums - 1st place", CustomEmoji.PODIUM_FIRST.getChar(), MONTHLY_PODIUM_CONSUMER, ItemModelType.BADGE_MONTHLY_FIRST, "Given for being 1st place on any podium from the previous month"),
		MONTHLY_PODIUMS_SECOND("Monthly Podiums - 2nd place", CustomEmoji.PODIUM_SECOND.getChar(), MONTHLY_PODIUM_CONSUMER, ItemModelType.BADGE_MONTHLY_SECOND, "Given for being 2nd place on any podium from the previous month"),
		MONTHLY_PODIUMS_THIRD("Monthly Podiums - 3rd place", CustomEmoji.PODIUM_THIRD.getChar(), MONTHLY_PODIUM_CONSUMER, ItemModelType.BADGE_MONTHLY_THIRD, "Given for being 3rd place on any podium from the previous month"),
		CONNECT4_CHAMPION("Connect4 Champion", CustomEmoji.CONNECT4, ItemModelType.BADGE_CONNECT4, "Win a Connect4 Tournament"),

		;

		Badge(SocialMediaSite site) {
			this(site.getName(), site.getEmoji(), SOCIAL_MEDIA_CONSUMER.apply(site), ItemModelType.NULL, null);
		}

		Badge(String name, CustomEmoji emoji, ItemModelType modelType, String obtain) {
			this(name, emoji.getChar(), modelType, obtain);
		}

		Badge(String name, String emoji, ItemModelType modelType, String obtain) {
			this(name, emoji, null, modelType, obtain);
		}

		private final String name;
		private final String emoji;
		private final BiConsumer<BadgeUser, JsonBuilder> consumer;
		private final ItemModelType modelType;
		@Nullable
		private final String howToObtain;

		public void customize(BadgeUser nerd, JsonBuilder json) {
			if (consumer == null)
				return;

			consumer.accept(nerd, json);
		}

		public List<String> getLore(BadgeUser nerd) {
			JsonBuilder json = new JsonBuilder();
			customize(nerd, json);
			return json.getHoverLines().stream().map(line -> "&f" + line).collect(Collectors.toList());
		}
	}

}
