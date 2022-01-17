package gg.projecteden.nexus.models.socialmedia;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.discord.DiscordId.Role;
import gg.projecteden.discord.DiscordId.TextChannel;
import gg.projecteden.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.ReactionVoter;
import gg.projecteden.nexus.features.socialmedia.integrations.Twitter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import okhttp3.Response;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "twitter_data", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class TwitterData implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<PendingTweet> pendingTweets = new ArrayList<>();
	@Getter
	private static final Map<Role, Integer> requiredVotes = new HashMap<>() {{
		put(Role.ADMINS, 3);
		put(Role.MODERATORS, 3);
	}};

	private Set<Long> knownTweets = new HashSet<>();

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
			TwitterData data = service.get0();

			ReactionVoter.builder()
					.channelId(TextChannel.STAFF_SOCIAL_MEDIA.getId())
					.messageId(messageId)
					.requiredVotes(TwitterData.getRequiredVotes())
					.onDeny(message -> {
						data.getPendingTweets().remove(this);
						message.reply("Tweet cancelled").queue();
					})
					.onAccept(message -> {
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
					})
					.onError(error -> {
						data.getPendingTweets().remove(this);
						service.save(data);
					})
					.run();
		}

		public void tweet() {
			TextChannel.STAFF_SOCIAL_MEDIA.get(Bot.KODA.jda()).retrieveMessageById(messageId).queue(message -> {
				try {
					StatusUpdate statusUpdate = new StatusUpdate(message.getContentDisplay()
							.replaceFirst("/twitter tweet ", "")
							.replaceFirst("/twitter scheduleTweet " + (timestamp == null ? "" : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(timestamp) + " "), "")
							.trim());

					if (!message.getAttachments().isEmpty()) {
						List<Long> mediaIds = new ArrayList<>();

						for (Attachment attachment : message.getAttachments()) {
							Response response = HttpUtils.callUrl(attachment.getUrl());

							if (response.body() != null) {
								UploadedMedia uploadedMedia = Twitter.get().tweets().uploadMedia(attachment.getFileName(), response.body().byteStream());
								mediaIds.add(uploadedMedia.getMediaId());
							}
						}

						statusUpdate.setMediaIds(mediaIds.stream().mapToLong(l -> l).toArray());
					}

					Status status = Twitter.get().tweets().updateStatus(statusUpdate);

					message.addReaction("twitter:829474002586173460").queue();
					message.reply("Tweeted successfully: " + Twitter.getUrl(status)).queue();

					TwitterService service = new TwitterService();
					TwitterData data = service.get0();
					data.getPendingTweets().remove(this);
					service.save(data);
				} catch (TwitterException ex) {
					ex.printStackTrace();
				}
			});
		}
	}

}
