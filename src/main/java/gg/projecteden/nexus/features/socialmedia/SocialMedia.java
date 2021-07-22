package gg.projecteden.nexus.features.socialmedia;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.Env;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@NoArgsConstructor
@Environments(Env.PROD)
public class SocialMedia extends Feature implements Listener {

	@Getter
	private static Twitter twitter;

	@Override
	public void onStart() {
		try {
			FileConfiguration config = Nexus.getInstance().getConfig();
			ConfigurationBuilder twitterConfig = new ConfigurationBuilder();

			twitterConfig.setDebugEnabled(true)
					.setOAuthConsumerKey(config.getString("tokens.twitter.consumerKey"))
					.setOAuthConsumerSecret(config.getString("tokens.twitter.consumerSecret"))
					.setOAuthAccessToken(config.getString("tokens.twitter.accessToken"))
					.setOAuthAccessTokenSecret(config.getString("tokens.twitter.accessTokenSecret"));

			twitter = new TwitterFactory(twitterConfig.build()).getInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getUrl(Status status) {
		return "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
	}

	public enum SocialMediaSite {
		TWITTER("Twitter", ChatColor.of("#1da1f2"), "https://twitter.com", "https://twitter.com/{{USERNAME}}"),
		INSTAGRAM("Instagram", ChatColor.of("#e1306c"), "https://instgram.com", "https://instgram.com/{{USERNAME}}"),
		SNAPCHAT("Snapchat", ChatColor.of("#fffc00"), "https://snapchat.com", "https://snapchat.com/add/{{USERNAME}}"),
		YOUTUBE("YouTube", ChatColor.of("#ff0000"), "https://youtube.com", "{{USERNAME}}"),
		TWITCH("Twitch", ChatColor.of("#6441a5"), "https://twitch.tv", "https://twitch.tv/{{USERNAME}}"),
		DISCORD("Discord", ChatColor.of("#7289da"), "https://discord.com", "{{USERNAME}}"),
		STEAM("Steam", ChatColor.of("#356d92"), "https://store.steampowered.com", "https://steamcommunity.com/id/{{USERNAME}}"),
		REDDIT("Reddit", ChatColor.of("#ff5700"), "https://reddit.com", "https://reddit.com/u/{{USERNAME}}"),
		GITHUB("GitHub", ChatColor.of("#ffffff"), "https://github.com", "https://github.com/{{USERNAME}}"),
		QUEUP("QueUp", ChatColor.of("#d42f8a"), "https://queup.net", "https://queup.net/user/{{USERNAME}}");

		@Getter
		private final String name;
		@Getter
		private final ChatColor color;
		@Getter
		private final String url;
		@Getter
		private final String profileUrl;
		@Getter
		@Setter
		private ItemStack head = new ItemStack(Material.PLAYER_HEAD);

		SocialMediaSite(String name, ChatColor color, String url, String profileUrl) {
			this.name = name;
			this.color = color;
			this.url = url;
			this.profileUrl = profileUrl;
		}

		public String getLabel() {
			return color + name;
		}

		static {
			if (Nexus.getEnv() == Env.PROD)
				reload();
		}

		public static void reload() {
			try {
				World world = Bukkit.getWorld("survival");

				for (Block block : new WorldEditUtils(world).getBlocks(new WorldGuardUtils(world).getRegion("socialmedia"))) {
					try {
						if (!MaterialTag.SIGNS.isTagged(block.getType())) continue;
						Sign sign = (Sign) block.getState();
						String line = sign.getLine(0);
						try {
							SocialMediaSite site = SocialMediaSite.valueOf(line);
							Block head = block.getRelative(BlockFace.DOWN);
							if (head.getState() instanceof Skull)
								site.setHead(head.getDrops().iterator().next());
							else
								Nexus.warn("Head for " + camelCase(site.name()) + " not found");
						} catch (IllegalArgumentException ex) {
							Nexus.warn("Found unknown social media head: " + line);
						}
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

	public enum EdenSocialMediaSite {
		WEBSITE("https://projecteden.gg"),
		DISCORD(null) {
			@Override
			@NotNull
			public String getUrl() {
				String url = "https://discord.projecteden.gg";
				if (Discord.getGuild() != null && Discord.getGuild().getBoostTier().getKey() == 3)
					url = "https://discord.gg/ProjectEdenGG";
				return url;
			}
		},
		YOUTUBE("https://youtube.projecteden.gg"),
		TWITTER("https://twitter.com/ProjectEdenGG"),
		INSTAGRAM("https://instagram.com/ProjectEdenGG"),
		REDDIT("https://reddit.com/u/ProjectEdenGG"),
		STEAM("https://steamcommunity.com/groups/ProjectEdenGG"),
		QUEUP("https://queup.net/join/projectedengg");

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
