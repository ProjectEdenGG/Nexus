package me.pugabyte.nexus.features.socialmedia.commands;

import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.Channel;
import me.pugabyte.nexus.features.socialmedia.SocialMedia;
import me.pugabyte.nexus.features.socialmedia.SocialMedia.BNSocialMediaSite;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.socialmedia.TwitterData;
import me.pugabyte.nexus.models.socialmedia.TwitterService;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import twitter4j.Query;
import twitter4j.Status;

import java.util.List;

public class TwitterCommand extends CustomCommand {

	public TwitterCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().next("&e" + BNSocialMediaSite.TWITTER.getUrl()));
	}

	@Path("lookForNewTweets")
	void lookForNewTweets() {
		lookForNewTweets0();
	}

	static {
		Tasks.repeat(Time.MINUTE, Time.MINUTE.x(5), TwitterCommand::lookForNewTweets0);
	}

	private static void lookForNewTweets0() {
		try {
			TwitterService service = new TwitterService();
			TwitterData data = service.get();

			List<Status> tweets = SocialMedia.getTwitter().search().search(new Query("from:BearNationSMP")).getTweets();
			for (Status tweet : tweets) {
				if (data.getKnownTweets().contains(tweet.getId()))
					continue;

				EmbedBuilder embed = new EmbedBuilder().setTitle("New tweet! <:twitter:829474002586173460>")
						.appendDescription(tweet.getText() + System.lineSeparator() + System.lineSeparator() + "[View](" + SocialMedia.getUrl(tweet) + ")");

				MessageBuilder content = new MessageBuilder().setEmbed(embed.build());

				Discord.send(content, Channel.GENERAL);
				data.getKnownTweets().add(tweet.getId());
				service.save(data);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}