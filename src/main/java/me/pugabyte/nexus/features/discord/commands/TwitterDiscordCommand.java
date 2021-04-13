package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.vdurmont.emoji.EmojiManager;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.DiscordId.Guild;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.features.socialmedia.SocialMedia;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.socialmedia.TwitterData;
import me.pugabyte.nexus.models.socialmedia.TwitterData.PendingTweet;
import me.pugabyte.nexus.models.socialmedia.TwitterService;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import twitter4j.Query;
import twitter4j.Status;

import java.util.List;

import static me.pugabyte.nexus.features.discord.ReactionVoter.addButtons;
import static me.pugabyte.nexus.utils.StringUtils.parseDateTime;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class TwitterDiscordCommand extends Command {

	public TwitterDiscordCommand() {
		this.name = "twitter";
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			TwitterService service = new TwitterService();
			TwitterData data = service.get();

			try {
				if (!event.getChannel().getId().equals(TextChannel.STAFF_SOCIAL_MEDIA.getId()))
					throw new InvalidInputException("This command can only be used in #social-media");

				String[] args = event.getArgs().split(" ");

				if (args.length >= 1)
					switch (args[0].toLowerCase()) {
						case "clearData":
							data.getPendingTweets().clear();
							event.getMessage().addReaction(EmojiManager.getForAlias("thumbsup").getUnicode()).queue();
							break;
						case "history":
							Query query = new Query("from:BearNationSMP");

							List<Status> tweets = SocialMedia.getTwitter().search().search(query).getTweets();
							StringBuilder reply = new StringBuilder("Tweets from past 7 days: " + (tweets.isEmpty() ? "None" : ""));
							for (Status tweet : tweets)
								reply.append(System.lineSeparator()).append(SocialMedia.getUrl(tweet));

							event.reply(reply.toString());
							break;
						case "tweet":
							if (args.length < 2)
								throw new InvalidInputException("Not enough arguments");
							data.addPendingTweet(event.getMessage());
							addButtons(event.getMessage());
							break;
						case "scheduleTweet":
							if (args.length < 3)
								throw new InvalidInputException("Not enough arguments");
							data.addPendingTweet(event.getMessage(), parseDateTime(args[1]));
							addButtons(event.getMessage());
							break;
						case "pending":
							StringBuilder message = new StringBuilder();
							for (PendingTweet pendingTweet : data.getPendingTweets()) {
								String link = "https://discord.com/channels/" + Guild.BEAR_NATION.getId() + "/" + TextChannel.STAFF_SOCIAL_MEDIA.getId() + "/" + pendingTweet.getMessageId();
								message.append(link).append(System.lineSeparator());
							}
							event.reply(message.toString());
							break;
					}
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof NexusException))
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
			Tasks.async(() -> new TwitterService().get().getPendingTweets().stream()
					.filter(pendingTweet -> pendingTweet.getMessageId().equals(event.getMessageId()))
					.findFirst()
					.ifPresent(PendingTweet::handle));
		}

	}

	static {
		Tasks.repeat(Time.MINUTE, Time.MINUTE, () -> new TwitterService().get().getPendingTweets().forEach(PendingTweet::handle));
	}

}
