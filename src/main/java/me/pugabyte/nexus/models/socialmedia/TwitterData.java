package me.pugabyte.nexus.models.socialmedia;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.LocalDateTimeConverter;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.features.discord.ReactionVoter;
import me.pugabyte.nexus.features.socialmedia.SocialMedia;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

import java.io.IOException;
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
@Builder
@Entity("twitter_data")
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
			TwitterData data = service.get(Nexus.getUUID0());

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
			TextChannel.STAFF_SOCIAL_MEDIA.get().retrieveMessageById(messageId).queue(message -> {
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

					Status status = SocialMedia.getTwitter().tweets().updateStatus(statusUpdate);

					message.addReaction("twitter:829474002586173460").queue();
					message.reply("Tweeted successfully: " + SocialMedia.getUrl(status)).queue();

					TwitterService service = new TwitterService();
					TwitterData data = service.get();
					data.getPendingTweets().remove(this);
					service.save(data);
				} catch (TwitterException | IOException ex) {
					ex.printStackTrace();
				}
			});
		}
	}

}
