package gg.projecteden.nexus.features.socialmedia.integrations;

import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.socialmedia.TwitterData;
import gg.projecteden.nexus.models.socialmedia.TwitterService;
import gg.projecteden.nexus.utils.Tasks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import twitter4j.v1.Query;
import twitter4j.v1.Status;
import twitter4j.v1.TwitterV1;

import java.util.List;

public class Twitter {
	public static twitter4j.Twitter twitter;

	public static void connect() {
		Tasks.async(() -> {
			FileConfiguration config = Nexus.getInstance().getConfig();
			twitter = twitter4j.Twitter.newBuilder()
				.prettyDebugEnabled(true)
				.oAuthConsumer(config.getString("tokens.twitter.consumerKey"), config.getString("tokens.twitter.consumerSecret"))
				.oAuthAccessToken(config.getString("tokens.twitter.accessToken"), config.getString("tokens.twitter.accessTokenSecret"))
				.build();
		});
	}

	public static String getUrl(Status status) {
		return "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
	}

	public static TwitterV1 get() {
		return twitter.v1();
	}

	public static void lookForNewTweets() {
		try {
			TwitterService service = new TwitterService();
			TwitterData data = service.get0();

			List<Status> tweets = get().search().search(Query.of("from:ProjectEdenGG")).getTweets();
			for (Status tweet : tweets) {
				if (data.getKnownTweets().contains(tweet.getId()))
					continue;

				EmbedBuilder embed = new EmbedBuilder().setTitle("New tweet! <:twitter:829474002586173460>")
					.appendDescription(tweet.getText() + System.lineSeparator() + System.lineSeparator() + "[View on Twitter](" + Twitter.getUrl(tweet) + ")");

				MessageCreateBuilder content = new MessageCreateBuilder().setEmbeds(embed.build());

				Discord.send(content, TextChannel.GENERAL);
				data.getKnownTweets().add(tweet.getId());
				service.save(data);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
