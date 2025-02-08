package gg.projecteden.nexus.features.socialmedia;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomEmoji;
import gg.projecteden.nexus.features.socialmedia.integrations.Twitch;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser.Connection;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor
public class SocialMedia implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("SocialMedia");

	public static CompletableFuture<Boolean> checkStreaming(UUID uuid) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		Tasks.async(() -> {
			boolean streaming;

			final SocialMediaUserService service = new SocialMediaUserService();
			final SocialMediaUser user = service.get(uuid);
			final Connection connection = user.getConnection(SocialMediaSite.TWITCH);
			if (Twitch.get() == null || connection == null)
				streaming = false;
			else {
				try {
					streaming = !Twitch.get().getStreams(null, null, null, 1, null, null, null, List.of(connection.getUsername()))
						.execute()
						.getStreams()
						.isEmpty();
				} catch (Exception ex) {
					// TODO: FAILING FOR "HELIX API ERROR, BAD REQUEST - 400 - MALFORMED QUERY PARAMS --> Username not a valid twitch account"
					streaming = false;
					Nexus.warn("Twitch#getStreams() failed for user " + user.getNickname());
				}
			}

			user.setStreaming(streaming);
			service.save(user);

			future.complete(streaming);
		});

		return future;
	}

	public enum SocialMediaSite {
		TWITTER("Twitter", CustomMaterial.GUI_SOCIAL_MEDIA_TWITTER, ChatColor.of("#1da1f2"), CustomEmoji.SOCIAL_MEDIA_TWITTER, "https://twitter.com", "https://twitter.com/%s"),
		INSTAGRAM("Instagram", CustomMaterial.GUI_SOCIAL_MEDIA_INSTAGRAM, ChatColor.of("#e1306c"), CustomEmoji.SOCIAL_MEDIA_INSTAGRAM, "https://instgram.com", "https://instgram.com/%s"),
		SNAPCHAT("Snapchat", CustomMaterial.GUI_SOCIAL_MEDIA_SNAPCHAT, ChatColor.of("#fffc00"), CustomEmoji.SOCIAL_MEDIA_SNAPCHAT, "https://snapchat.com", "https://snapchat.com/add/%s"),
		YOUTUBE("YouTube", CustomMaterial.GUI_SOCIAL_MEDIA_YOUTUBE, ChatColor.of("#ff0000"), CustomEmoji.SOCIAL_MEDIA_YOUTUBE, "https://youtube.com", "https://youtube.com/@%s"),
		TWITCH("Twitch", CustomMaterial.GUI_SOCIAL_MEDIA_TWITCH, ChatColor.of("#6441a5"), CustomEmoji.SOCIAL_MEDIA_TWITCH, "https://twitch.tv", "https://twitch.tv/%s"),
		TIKTOK("TikTok", CustomMaterial.GUI_SOCIAL_MEDIA_TIKTOK, ChatColor.of("#7f7f7f"), CustomEmoji.SOCIAL_MEDIA_TIKTOK, "https://tiktok.com", "https://tiktok.com/@%s"),
		DISCORD("Discord", CustomMaterial.GUI_SOCIAL_MEDIA_DISCORD, ChatColor.of("#5865F2"), CustomEmoji.SOCIAL_MEDIA_DISCORD, "https://discord.com", "%s"),
		STEAM("Steam", CustomMaterial.GUI_SOCIAL_MEDIA_STEAM, ChatColor.of("#00adee"), CustomEmoji.SOCIAL_MEDIA_STEAM, "https://store.steampowered.com", "https://steamcommunity.com/profiles/%s"),
		SPOTIFY("Spotify", CustomMaterial.GUI_SOCIAL_MEDIA_SPOTIFY, ChatColor.of("#1ed760"), CustomEmoji.SOCIAL_MEDIA_SPOTIFY, "https://spotify.com", "https://open.spotify.com/user/%s"),
		QUEUP("QueUp", CustomMaterial.GUI_SOCIAL_MEDIA_QUEUP, ChatColor.of("#d42f8a"), CustomEmoji.SOCIAL_MEDIA_QUEUP, "https://queup.net", "https://queup.net/user/%s"),
		REDDIT("Reddit", CustomMaterial.GUI_SOCIAL_MEDIA_REDDIT, ChatColor.of("#ff5700"), CustomEmoji.SOCIAL_MEDIA_REDDIT, "https://reddit.com", "https://reddit.com/u/%s"),
		GITHUB("GitHub", CustomMaterial.GUI_SOCIAL_MEDIA_GITHUB, ChatColor.of("#777777"), CustomEmoji.SOCIAL_MEDIA_GITHUB, "https://github.com", "https://github.com/%s"),
		VENMO("Venmo", CustomMaterial.NULL, ChatColor.of("#008CFF"), CustomEmoji.SOCIAL_MEDIA_VENMO, "https://venmo.com", "https://account.venmo.com/u/%s"),
		PAYPAL("PayPal", CustomMaterial.NULL, ChatColor.of("#498ebe"), CustomEmoji.SOCIAL_MEDIA_PAYPAL, "https://paypal.com", "https://paypal.me/%s"),
		// XBOX
		// PLAYSTATION
		// BATTLE.NET
		;

		@Getter
		private final String name;
		@Getter
		private final CustomMaterial model;
		@Getter
		private final ChatColor color;
		@Getter
		private final String emoji;
		@Getter
		private final String url;
		@Getter
		private final String profileUrl;

		SocialMediaSite(String name, CustomMaterial model, ChatColor color, CustomEmoji emoji, String url, String profileUrl) {
			this.name = name;
			this.model = model;
			this.color = color;
			this.emoji = emoji.getChar();
			this.url = url;
			this.profileUrl = profileUrl;
		}

		public String getLabel() {
			return color + name;
		}

		public ItemStack getItem() {
			return new ItemBuilder(Material.PAPER).model(model).build();
		}

		public ItemStack getNamedItem() {
			return new ItemBuilder(getItem()).name(getLabel()).build();
		}

	}

	@Getter
	public enum EdenSocialMediaSite {
		WEBSITE("https://" + Nexus.DOMAIN),
		DISCORD(null, "29829") {
			@Override
			@NotNull
			public String getUrl() {
				return Discord.getInvite();
			}
		},
		YOUTUBE("https://youtube.com/@ProjectEdenGG", "25156"),
		TWITTER("https://twitter.com/ProjectEdenGG", "20490"),
		INSTAGRAM("https://instagram.com/ProjectEdenGG", "3943"),
		TIKTOK("https://tiktok.com/@projectedengg", "31380"),
		REDDIT("https://reddit.com/u/ProjectEdenGG"),
		STEAM("https://steamcommunity.com/groups/ProjectEdenGG", "12247"),
		QUEUP("https://queup.net/join/projectedengg"),
		GITHUB("https://github.com/ProjectEdenGG"),
		;

		private String name = "&3" + StringUtils.camelCase(name());
		private final String url;
		private final String headId;

		EdenSocialMediaSite(String url) {
			this(url, null);
		}

		EdenSocialMediaSite(String url, String headId) {
			try {
				this.name = SocialMediaSite.valueOf(name()).getLabel();
			} catch (IllegalArgumentException ignore) {}

			this.url = url;
			this.headId = headId;
		}

		public static EdenSocialMediaSite ofHeadId(String id) {
			for (EdenSocialMediaSite site : EdenSocialMediaSite.values())
				if (id.equals(site.getHeadId()))
					return site;
			return null;
		}

		public SocialMediaSite getConfig() {
			return SocialMediaSite.valueOf(name());
		}
	}

}
