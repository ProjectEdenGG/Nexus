package me.pugabyte.nexus.models.socialmedia;

import com.vdurmont.emoji.EmojiManager;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.Channel;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.socialmedia.SocialMedia;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@Builder
@Entity("twitter_data")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class TwitterData extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<PendingTweet> pendingTweets = new ArrayList<>();
	@Getter
	private static final Map<Role, Integer> requiredVotes = new HashMap<Role, Integer>() {{
		put(Role.ADMINS, 3);
		put(Role.MODERATORS, 3);
	}};

	public void addPendingTweet(Message message) {
		pendingTweets.add(new PendingTweet(message));
	}

	public void addPendingTweet(Message message, LocalDateTime timestamp) {
		pendingTweets.add(new PendingTweet(message, timestamp));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PendingTweet {
		private String messageId;
		private LocalDateTime timestamp;
		private boolean replied;

		public PendingTweet(Message message) {
			this(message, null);
		}

		public PendingTweet(Message message, LocalDateTime timestamp) {
			this.messageId = message.getId();
			this.timestamp = timestamp;
		}

		public void handle() {
			TwitterService service = new TwitterService();
			TwitterData data = service.get(Nexus.getUUID0());

			getChannel().retrieveMessageById(messageId).queue(message -> {
				MessageReaction white_check_mark = null;
				MessageReaction x = null;
				for (MessageReaction reaction : message.getReactions()) {
					String name = reaction.getReactionEmote().getName();

					String unicode_white_check_mark = EmojiManager.getForAlias("white_check_mark").getUnicode();
					String unicode_x = EmojiManager.getForAlias("x").getUnicode();

					if (unicode_x.equals(name))
						x = reaction;
					else if (unicode_white_check_mark.equals(name))
						white_check_mark = reaction;
				}

				if (x == null) {
					message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				} else if (x.getCount() > 1) {
					data.getPendingTweets().remove(this);
					message.reply("Tweet cancelled").queue();
					return;
				}

				if (white_check_mark == null) {
					message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
				} else {
					white_check_mark.retrieveUsers().queue(users -> {
						Map<Role, Integer> votesByRole = new HashMap<>();

						// TODO Better logic
						users.forEach(user -> {
							Member member = Discord.getGuild().getMember(user);
							if (member == null)
								throw new NexusException("Member from " + Discord.getName(user) + " not found");
							Role role = Role.of(member.getRoles().get(0));
							if (Role.OWNER.equals(role))
								role = Role.ADMINS;
							if (Role.OPERATORS.equals(role) || Role.BUILDERS.equals(role) || Role.ARCHITECTS.equals(role))
								role = Role.MODERATORS;

							votesByRole.put(role, votesByRole.getOrDefault(role, 0) + 1);
						});

						AtomicBoolean passed = new AtomicBoolean(true);
						TwitterData.getRequiredVotes().forEach((role, required) -> {
							if (!votesByRole.containsKey(role))
								passed.set(false);
							else if (votesByRole.get(role) < required)
								passed.set(false);
						});

						if (passed.get()) {
							if (timestamp == null) {
								tweet();
							} else {
								if (timestamp.isBefore(LocalDateTime.now()))
									tweet();
								else if (!replied) {
									message.reply("Tweet scheduled").queue();
									replied = true;
									service.save(data);
								}
							}
						}
					});
				}
			}, notFound -> {
				data.getPendingTweets().remove(this);
				service.save(data);
			});
		}

		public void tweet() {
			TextChannel channel = getChannel();

			channel.retrieveMessageById(messageId).queue(message -> {
				try {
					StatusUpdate statusUpdate = new StatusUpdate(message.getContentDisplay()
							.replaceFirst("/twitter tweet ", "")
							.replaceFirst("/twitter scheduleTweet " + (timestamp == null ? "" : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(timestamp) + " "), "")
							.trim());

					if (!message.getAttachments().isEmpty()) {
						OkHttpClient client = new OkHttpClient();
						List<Long> mediaIds = new ArrayList<>();

						for (Attachment attachment : message.getAttachments()) {
							Request request = new Request.Builder().url(attachment.getUrl()).build();
							Response response = client.newCall(request).execute();

							if (response.body() != null) {
								UploadedMedia uploadedMedia = SocialMedia.getTwitter().tweets().uploadMedia(attachment.getFileName(), response.body().byteStream());
								mediaIds.add(uploadedMedia.getMediaId());
							}
						}

						statusUpdate.setMediaIds(mediaIds.stream().mapToLong(l -> l).toArray());
					}

					SocialMedia.getTwitter().tweets().updateStatus(statusUpdate);

					message.addReaction("twitter:829474002586173460").queue();
					message.reply("Tweeted successfully").queue();

					TwitterService service = new TwitterService();
					TwitterData data = service.get();
					data.getPendingTweets().remove(this);
					service.save(data);
				} catch (TwitterException | IOException ex) {
					ex.printStackTrace();
				}
			});
		}

		@NotNull
		private TextChannel getChannel() {
			TextChannel channel = Discord.getGuild().getTextChannelById(Channel.STAFF_SOCIAL_MEDIA.getId());
			if (channel == null)
				throw new NexusException("Social media channel not found");
			return channel;
		}
	}

}
