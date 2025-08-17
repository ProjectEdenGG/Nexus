package gg.projecteden.nexus.features.socialmedia.integrations;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.feed.Reel;
import com.github.instagram4j.instagram4j.models.media.reel.ReelMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.socialmedia.InstagramData;
import gg.projecteden.nexus.models.socialmedia.InstagramService;
import gg.projecteden.nexus.utils.Tasks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class Instagram {
	public static IGClient instagram;

	public static void connect() {
		Tasks.async(() -> {
			try {
				instagram = IGClient.builder()
					.username("ProjectEdenGG")
					.password(Nexus.getInstance().getConfig().getString("tokens.instagram.password"))
					.login();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	public static IGClient get() {
		return instagram;
	}

	public static String getUrl(TimelineMedia media) {
		return "https://instagram.com/p/" + media.getCode();
	}

	public static String getUrl(ReelMedia media) {
		return "https://instagram.com/stories/" + media.getUser().getUsername() + "/" + media.getPk() + "/";
	}

	public static String getThumbnailUrl(TimelineMedia media) {
		return Instagram.getUrl(media) + "/media/?size=l";
	}

	public static void lookForNewPosts() {
		try {
			if (instagram == null)
				return;

			InstagramService service = new InstagramService();
			InstagramData data = service.get0();
			final FeedUserRequest request = new FeedUserRequest(instagram.getSelfProfile().getPk());
			for (TimelineMedia media : instagram.sendRequest(request).get().getItems()) {
				if (data.getKnownPosts().contains(media.getPk()))
					continue;

				final String text = media.getCaption().getText().split("\n\\.\n", 2)[0];
				EmbedBuilder embed = new EmbedBuilder()
					.setTitle("New instagram post! <:instagram:736255140335714385>")
					.setImage(getThumbnailUrl(media))
					.appendDescription(text + System.lineSeparator() + System.lineSeparator() + "[View on Instagram](" + getUrl(media) + ")");

				var content = new MessageCreateBuilder().setEmbeds(embed.build());

				Discord.send(content, TextChannel.TEST);
				data.getKnownPosts().add(media.getPk());
				service.save(data);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void lookForNewStories() {
		try {
			if (instagram == null)
				return;

			InstagramService service = new InstagramService();
			InstagramData data = service.get0();
			final Reel reel = instagram.actions().story().userStory(instagram.getSelfProfile().getPk()).get().getReel();
			if (reel == null)
				return;

			for (ReelMedia media : reel.getItems()) {
				if (data.getKnownPosts().contains(media.getPk()))
					continue;

				EmbedBuilder embed = new EmbedBuilder()
					.setTitle("New instagram story post! <:instagram:736255140335714385>")
					.appendDescription(media.getCaption().getText() + System.lineSeparator() + System.lineSeparator() + "[View on Instagram](" + getUrl(media) + ")");

				var content = new MessageCreateBuilder().setEmbeds(embed.build());

				Discord.send(content, TextChannel.SOCIAL_MEDIA);
				data.getKnownPosts().add(media.getPk());
				service.save(data);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
