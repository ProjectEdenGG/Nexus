package gg.projecteden.nexus.models.discord;

import com.vdurmont.emoji.EmojiManager;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.DiscordId.Role;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.scheduledjobs.jobs.DiscordCaptchaKickJob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.time.LocalDateTime.now;

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
	private Map<String, LocalDateTime> confirmed = new HashMap<>();
	private Map<String, LocalDateTime> unconfirmed = new HashMap<>();

	private static final String taskId = "discord-unconfirmed-kick";

	public void require(String id) {
		unconfirmed.put(id, now());

		User user = Bot.KODA.jda().retrieveUserById(id).complete();
		if (user == null) {
			Nexus.warn("[Captcha] Cannot send verification message to null user");
		} else {
			user.openPrivateChannel().complete()
					.sendMessage("Please react to verify your account").complete()
					.addReaction(EmojiManager.getForAlias("thumbsup").getUnicode()).queue();
		}

		new DiscordCaptchaKickJob(id).schedule(now().plusMinutes(9));
	}


	public void confirm(String id) {
		unconfirmed.remove(id);
		confirmed.put(id, now());
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
