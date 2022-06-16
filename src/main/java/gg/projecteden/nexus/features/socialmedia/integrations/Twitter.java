package gg.projecteden.nexus.features.socialmedia.integrations;

import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.socialmedia.TwitterData;
import gg.projecteden.nexus.models.socialmedia.TwitterService;
import gg.projecteden.nexus.utils.Tasks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

public class Twitter {
	public static twitter4j.Twitter twitter;

	public static void connect() {
		Tasks.async(() -> {
			FileConfiguration config = Nexus.getInstance().getConfig();
			ConfigurationBuilder twitterConfig = new ConfigurationBuilder();

			twitterConfig.setDebugEnabled(true)
				.setOAuthConsumerKey(config.getString("tokens.twitter.consumerKey"))
				.setOAuthConsumerSecret(config.getString("tokens.twitter.consumerSecret"))
				.setOAuthAccessToken(config.getString("tokens.twitter.accessToken"))
				.setOAuthAccessTokenSecret(config.getString("tokens.twitter.accessTokenSecret"));

			twitter = new TwitterFactory(twitterConfig.build()).getInstance();
		});
	}

	public static String getUrl(Status status) {
		return "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
	}

	public static twitter4j.Twitter get() {
		return twitter;
	}

	public static void lookForNewTweets() {
		try {
			TwitterService service = new TwitterService();
			TwitterData data = service.get0();

			List<Status> tweets = Twitter.get().search().search(new Query("from:ProjectEdenGG")).getTweets();
			for (Status tweet : tweets) {
				if (data.getKnownTweets().contains(tweet.getId()))
					continue;

				EmbedBuilder embed = new EmbedBuilder().setTitle("New tweet! <:twitter:829474002586173460>")
					.appendDescription(tweet.getText() + System.lineSeparator() + System.lineSeparator() + "[View on Twitter](" + Twitter.getUrl(tweet) + ")");

				MessageBuilder content = new MessageBuilder().setEmbeds(embed.build());

				Discord.send(content, TextChannel.GENERAL);
				data.getKnownTweets().add(tweet.getId());
				service.save(data);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
