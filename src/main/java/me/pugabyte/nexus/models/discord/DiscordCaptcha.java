package me.pugabyte.nexus.models.discord;

import com.vdurmont.emoji.EmojiManager;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.LocalDateTimeConverter;
import eden.mongodb.serializers.UUIDConverter;
import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.task.Task;
import me.pugabyte.nexus.models.task.TaskService;
import me.pugabyte.nexus.utils.Tasks;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity("discord_captcha")
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class DiscordCaptcha implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, LocalDateTime> confirmed = new HashMap<>();
	private Map<String, LocalDateTime> unconfirmed = new HashMap<>();

	private static final String taskId = "discord-unconfirmed-kick";

	public void require(String id) {
		unconfirmed.put(id, LocalDateTime.now());

		User user = Bot.KODA.jda().retrieveUserById(id).complete();
		if (user == null) {
			Nexus.warn("[Captcha] Cannot send verification message to null user");
		} else {
			user.openPrivateChannel().complete()
					.sendMessage("Please react to verify your account").complete()
					.addReaction(EmojiManager.getForAlias("thumbsup").getUnicode()).queue();
		}

		new TaskService().save(new Task(taskId, new HashMap<>() {{
			put("id", id);
		}}, LocalDateTime.now().plusMinutes(9)));
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

	static {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(15), () -> {
			TaskService service = new TaskService();
			service.process(taskId).forEach(task -> {
				try {
					Map<String, Object> data = task.getJson();
					String id = (String) data.get("id");
					String name = Discord.getName(id);

					DiscordCaptcha verification = new DiscordCaptchaService().get();
					CaptchaResult result = verification.check(id);
					if (result != CaptchaResult.CONFIRMED) {
						Member member = Discord.getGuild().retrieveMemberById(id).complete();

						if (member != null) {
							Discord.staffLog("**[Captcha]** " + name + " - Kicking");
							member.kick("Please complete the verification process in your DMs with KodaBear").queue();
						} else
							Nexus.log("[Captcha] Kick scheduled for " + name + " cancelled, member not found");
					}
				} catch (Exception ex) {
					try {
						Discord.staffLog("**[Captcha]** Error in kick processor: " + ex.getMessage());
						ex.printStackTrace();
					} catch (Exception ex2) {
						ex2.printStackTrace();
					}
				}
				service.complete(task);
			});
		});
	}

}
