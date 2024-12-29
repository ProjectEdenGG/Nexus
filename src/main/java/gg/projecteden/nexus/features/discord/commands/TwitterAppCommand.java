package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.discord.DiscordId.Guild;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.ReactionVoter;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import gg.projecteden.nexus.features.socialmedia.integrations.Twitter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.socialmedia.TwitterData;
import gg.projecteden.nexus.models.socialmedia.TwitterData.PendingTweet;
import gg.projecteden.nexus.models.socialmedia.TwitterService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import twitter4j.v1.Query;
import twitter4j.v1.Status;

import java.time.LocalDateTime;
import java.util.List;

@RequiredRole("Staff")
@Command("Manage twitter")
public class TwitterAppCommand extends NexusAppCommand {
	private final TwitterService service = new TwitterService();
	private final TwitterData data = service.get0();

	public TwitterAppCommand(AppCommandEvent event) {
		super(event);

		if (!TextChannel.STAFF_SOCIAL_MEDIA.getId().equals(channel().getId()))
			throw new InvalidInputException("This command can only be used in #social-media");
	}

	@SneakyThrows
	@Command("View Project Eden tweets from past 7 days")
	void history() {
		Query query = Query.of("from:ProjectEdenGG");
		List<Status> tweets = Twitter.get().search().search(query).getTweets();
		StringBuilder reply = new StringBuilder("Tweets from past 7 days: " + (tweets.isEmpty() ? "None" : ""));
		for (Status tweet : tweets)
			reply.append(System.lineSeparator()).append(Twitter.getUrl(tweet));
		reply(reply.toString());
	}

	@Command("Delete pending tweets")
	void clearData() {
		data.getPendingTweets().clear();
		service.save(data);
		thumbsup();
	}

	@Command("Send a tweet")
	void tweet(
		@Desc("Message") String tweet,
		@Desc("Time to send") LocalDateTime time
	) {
		reply(tweet).thenAccept(reply -> reply.retrieveOriginal().submit().thenAccept(message -> {
			data.addPendingTweet(message, time);
			service.save(data);
			ReactionVoter.addButtons(message);
		}));
	}

	@Command("View pending tweets")
	void pending() {
		StringBuilder message = new StringBuilder();
		for (PendingTweet pendingTweet : data.getPendingTweets()) {
			String link = "https://discord.com/channels/%s/%s/%s".formatted(Guild.PROJECT_EDEN.getId(), TextChannel.STAFF_SOCIAL_MEDIA.getId(), pendingTweet.getMessageId());
			message.append(link).append(System.lineSeparator());
		}
		reply(message.toString());
	}

	@NoArgsConstructor
	public static class TweetApprovalListener extends ListenerAdapter {
		@Override
		public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
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
