package gg.projecteden.nexus.models.discord;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.discord.DiscordId.Role;
import gg.projecteden.api.discord.EmojiUtils;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.scheduledjobs.jobs.DiscordCaptchaKickJob;
import lombok.*;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(value = "discord_captcha", noClassnameStored = true)
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class DiscordCaptcha implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, LocalDateTime> confirmed = new ConcurrentHashMap<>();
	private Map<String, LocalDateTime> unconfirmed = new ConcurrentHashMap<>();

	private static final String taskId = "discord-unconfirmed-kick";

	public void require(String id) {
		unconfirmed.put(id, LocalDateTime.now());

		User user = Bot.KODA.jda().retrieveUserById(id).complete();
		if (user == null) {
			Nexus.warn("[Captcha] Cannot send verification message to null user");
		} else {
			user.openPrivateChannel().complete()
					.sendMessage("Please react to verify your account").complete()
					.addReaction(EmojiUtils.THUMBSUP).queue();
		}

		new DiscordCaptchaKickJob(id).schedule(LocalDateTime.now().plusMinutes(9));
	}

	public void confirm(String id) {
		unconfirmed.remove(id);
		confirmed.put(id, LocalDateTime.now());
		Discord.addRole(id, Role.NERD);

		User user = Bot.KODA.jda().retrieveUserById(id).complete();
		String name = id;
		if (user != null)
			name = user.getName();

		Discord.staffLog("**[Captcha]** " + name + " - Completed verification");
	}

	public CaptchaResult check(String id) {
		if (confirmed.containsKey(id))
			return CaptchaResult.CONFIRMED;
		else if (unconfirmed.containsKey(id))
			return CaptchaResult.UNCONFIRMED;

		return CaptchaResult.NEW;
	}

	public enum CaptchaResult {
		CONFIRMED,
		UNCONFIRMED,
		NEW
	}

}
