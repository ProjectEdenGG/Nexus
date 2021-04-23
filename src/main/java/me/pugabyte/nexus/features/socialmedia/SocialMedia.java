package me.pugabyte.nexus.features.socialmedia;

import eden.annotations.Environments;
import eden.utils.Env;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
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
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

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
		QUEUP("Queup", ChatColor.of("#d42f8a"), "https://queup.net", "https://queup.net/user/{{USERNAME}}");

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

	public enum BNSocialMediaSite {
		WEBSITE("https://bnn.gg"),
		DISCORD(null) {
			@Override
			public String getUrl() {
				String url = "https://discord.bnn.gg";
				if (Discord.getGuild() != null && Discord.getGuild().getBoostTier().getKey() == 3)
					url = "https://discord.gg/bearnation";
				return url;
			}
		},
		YOUTUBE("https://youtube.bnn.gg"),
		TWITTER("https://twitter.bnn.gg"),
		INSTAGRAM("https://instagram.bnn.gg"),
		REDDIT("https://reddit.bnn.gg"),
		STEAM("https://steam.bnn.gg"),
		QUEUP("https://queup.bnn.gg");

		@Getter
		private String name = "&3" + camelCase(name());
		private final String url;

		BNSocialMediaSite(String url) {
			try {
				this.name = SocialMediaSite.valueOf(name()).getLabel();
			} catch (IllegalArgumentException ignore) {}

			this.url = url;
		}

		public String getUrl() {
			return url;
		}
	}

}
