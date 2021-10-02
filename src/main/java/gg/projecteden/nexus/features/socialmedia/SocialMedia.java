package gg.projecteden.nexus.features.socialmedia;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@NoArgsConstructor
public class SocialMedia implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("SocialMedia");

	public enum SocialMediaSite {
		TWITTER("Twitter", ChatColor.of("#1da1f2"), "", "https://twitter.com", "https://twitter.com/%s"),
		INSTAGRAM("Instagram", ChatColor.of("#e1306c"), "", "https://instgram.com", "https://instgram.com/%s"),
		SNAPCHAT("Snapchat", ChatColor.of("#fffc00"), "", "https://snapchat.com", "https://snapchat.com/add/%s"),
		YOUTUBE("YouTube", ChatColor.of("#ff0000"), "", "https://youtube.com", "https://youtube.com/channel/%s"),
		TWITCH("Twitch", ChatColor.of("#6441a5"), "", "https://twitch.tv", "https://twitch.tv/%s"),
		TIKTOK("TikTok", ChatColor.of("#69C9D0"), "", "https://tiktok.com", "https://tiktok.com/@%s"),
		DISCORD("Discord", ChatColor.of("#7289da"), "", "https://discord.com", "%s"),
		STEAM("Steam", ChatColor.of("#356d92"), "", "https://store.steampowered.com", "https://steamcommunity.com/id/%s"),
		SPOTIFY("Spotify", ChatColor.of("#1ed760"), "", "https://spotify.com", "https://open.spotify.com/user/%s"),
		REDDIT("Reddit", ChatColor.of("#ff5700"), "", "https://reddit.com", "https://reddit.com/u/%s"),
		GITHUB("GitHub", ChatColor.of("#777777"), "", "https://github.com", "https://github.com/%s"),
//		QUEUP("QueUp", ChatColor.of("#d42f8a"), "https://queup.net", "https://queup.net/user/%s"), // TODO QueUp
		;

		@Getter
		private final String name;
		@Getter
		private final ChatColor color;
		@Getter
		private final String emoji;
		@Getter
		private final String url;
		@Getter
		private final String profileUrl;
		@Getter
		@Setter
		private ItemStack head = new ItemStack(Material.PLAYER_HEAD);

		SocialMediaSite(String name, ChatColor color, String emoji, String url, String profileUrl) {
			this.name = name;
			this.color = color;
			this.emoji = emoji;
			this.url = url;
			this.profileUrl = profileUrl;
		}

		public String getLabel() {
			return color + name;
		}

	}

	public enum EdenSocialMediaSite {
		WEBSITE("https://" + Nexus.DOMAIN),
		DISCORD(null) {
			@Override
			@NotNull
			public String getUrl() {
				return Discord.getInvite();
			}
		},
		YOUTUBE("https://youtube." + Nexus.DOMAIN),
		TWITTER("https://twitter.com/ProjectEdenGG"),
		INSTAGRAM("https://instagram.com/ProjectEdenGG"),
		REDDIT("https://reddit.com/u/ProjectEdenGG"),
		STEAM("https://steamcommunity.com/groups/ProjectEdenGG"),
//		QUEUP("https://queup.net/join/projectedengg"), // TODO QueUp
		;

		@Getter
		private String name = "&3" + camelCase(name());
		private final String url;

		EdenSocialMediaSite(String url) {
			try {
				this.name = SocialMediaSite.valueOf(name()).getLabel();
			} catch (IllegalArgumentException ignore) {}

			this.url = url;
		}

		@NonNull
		public String getUrl() {
			return url;
		}
	}

}
