package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.vdurmont.emoji.EmojiManager;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.DiscordId.Guild;
import gg.projecteden.nexus.features.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.socialmedia.SocialMedia;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.socialmedia.TwitterData;
import gg.projecteden.nexus.models.socialmedia.TwitterData.PendingTweet;
import gg.projecteden.nexus.models.socialmedia.TwitterService;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import twitter4j.Query;
import twitter4j.Status;

import java.util.List;

import static gg.projecteden.nexus.features.discord.ReactionVoter.addButtons;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.utils.TimeUtils.parseDateTime;

@HandledBy(Bot.KODA)
public class TwitterDiscordCommand extends Command {

	public TwitterDiscordCommand() {
		this.name = "twitter";
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			TwitterService service = new TwitterService();
			TwitterData data = service.get0();

			try {
				if (!event.getChannel().getId().equals(TextChannel.STAFF_SOCIAL_MEDIA.getId()))
					throw new InvalidInputException("This command can only be used in #social-media");

				String[] args = event.getArgs().split(" ");

				if (args.length >= 1)
					switch (args[0].toLowerCase()) {
						case "clearData" -> {
							data.getPendingTweets().clear();
							event.getMessage().addReaction(EmojiManager.getForAlias("thumbsup").getUnicode()).queue();
						}
						case "history" -> {
							Query query = new Query("from:ProjectEdenGG");
							List<Status> tweets = SocialMedia.getTwitter().search().search(query).getTweets();
							StringBuilder reply = new StringBuilder("Tweets from past 7 days: " + (tweets.isEmpty() ? "None" : ""));
							for (Status tweet : tweets)
								reply.append(System.lineSeparator()).append(SocialMedia.getUrl(tweet));
							event.reply(reply.toString());
						}
						case "tweet" -> {
							if (args.length < 2)
								throw new InvalidInputException("Not enough arguments");
							data.addPendingTweet(event.getMessage());
							addButtons(event.getMessage());
						}
						case "scheduleTweet" -> {
							if (args.length < 3)
								throw new InvalidInputException("Not enough arguments");
							data.addPendingTweet(event.getMessage(), parseDateTime(args[1]));
							addButtons(event.getMessage());
						}
						case "pending" -> {
							StringBuilder message = new StringBuilder();
							for (PendingTweet pendingTweet : data.getPendingTweets()) {
								String link = "https://discord.com/channels/" + Guild.PROJECT_EDEN.getId() + "/" + TextChannel.STAFF_SOCIAL_MEDIA.getId() + "/" + pendingTweet.getMessageId();
								message.append(link).append(System.lineSeparator());
							}
							event.reply(message.toString());
						}
					}
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			} finally {
				service.save(data);
			}
		});
	}

	@NoArgsConstructor
	public static class TweetApprovalListener extends ListenerAdapter {

		@Override
		public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
			Tasks.async(() -> new TwitterService().get0().getPendingTweets().stream()
					.filter(pendingTweet -> pendingTweet.getMessageId().equals(event.getMessageId()))
					.findFirst()
					.ifPresent(PendingTweet::handle));
		}

	}

	static {
		Tasks.repeat(TickTime.MINUTE, TickTime.MINUTE, () -> new TwitterService().get0().getPendingTweets().forEach(PendingTweet::handle));
	}

}
