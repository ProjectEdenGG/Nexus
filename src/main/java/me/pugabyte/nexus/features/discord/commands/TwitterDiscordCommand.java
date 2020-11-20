package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Bot.HandledBy;
import me.pugabyte.nexus.features.socialmedia.SocialMedia;
import me.pugabyte.nexus.framework.exceptions.BNException;
import me.pugabyte.nexus.utils.Tasks;
import twitter4j.Query;
import twitter4j.Status;

import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class TwitterDiscordCommand extends Command {

	public TwitterDiscordCommand() {
		this.name = "twitter";
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				Query query = new Query("from:BearNationSMP");

				List<Status> tweets = SocialMedia.getTwitter().search().search(query).getTweets();
				StringBuilder reply = new StringBuilder("Tweets from past 7 days: " + (tweets.isEmpty() ? "None" : ""));
				for (Status tweet : tweets)
					reply.append(System.lineSeparator()).append(SocialMedia.getUrl(tweet));

				event.reply(reply.toString());
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}

}
