package me.pugabyte.bncore.features.discord;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.features.discord.DiscordId.User;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.models.discord.DiscordCaptcha;
import me.pugabyte.bncore.models.discord.DiscordCaptcha.CaptchaResult;
import me.pugabyte.bncore.models.discord.DiscordCaptchaService;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.Tasks;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@NoArgsConstructor
public class DiscordListener extends ListenerAdapter {

	@Override
	public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
		Tasks.async(() -> {
			String name = Discord.getName(event.getMember());
			String channel = event.getChannelJoined().getName();

			BNCore.fileLog("discord", name + " joined " + channel);
		});
	}

	@Data
	private class RandomPugClubResponse {
		private String image;
		private String link;
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		Tasks.async(() -> {
			String name = Discord.getName(event.getMember());
			String channel = event.getChannel().getName();
			String message = event.getMessage().getContentRaw();

			if (Arrays.asList(Channel.STAFF_BRIDGE.getId(), Channel.BRIDGE.getId()).contains(event.getChannel().getId()))
				if (event.getMember().getUser().getId().equals(User.RELAY.getId()))
					return;

			for (Message.Attachment attachment : event.getMessage().getAttachments())
				message += " " + attachment.getUrl();

			BNCore.fileLog("discord", "[#" + channel + "] " + name + ": " + message.trim());

			if (Channel.BOT_COMMANDS.getId().equals(event.getChannel().getId())) {
				if (message.toLowerCase().startsWith(".pug")) {
					try {
						try (Response response = new OkHttpClient().newCall(new Request.Builder().url("http://randompug.club/loaf").build()).execute()) {
							RandomPugClubResponse result = new Gson().fromJson(response.body().string(), RandomPugClubResponse.class);
							Discord.koda(result.getImage(), Channel.BOT_COMMANDS);
						}
					} catch (Exception ex) {
						event.getChannel().sendMessage(stripColor(ex.getMessage())).queue();
						if (!(ex instanceof BNException))
							ex.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
		Tasks.async(() -> {
			final String id = event.getUser().getId();

			DiscordCaptchaService captchaService = new DiscordCaptchaService();
			DiscordCaptcha captcha = captchaService.get();
			CaptchaResult result = captcha.check(id);

			if (result == CaptchaResult.CONFIRMED) {
				Tasks.waitAsync(1, () -> {
					Discord.addRole(id, Role.NERD);
					DiscordUser user = new DiscordService().getFromUserId(id);
					if (user != null && !Strings.isNullOrEmpty(user.getUuid()))
						Discord.addRole(id, Role.VERIFIED);
				});

				return;
			}

			Discord.staffLog("**[Captcha]** " + Discord.getName(event.getMember()) + " - Requiring verification");
			captcha.require(id);
			captchaService.save(id);
		});
	}

	@Override
	public void onPrivateMessageReactionAdd(@Nonnull PrivateMessageReactionAddEvent event) {
		Tasks.async(() -> {
			if (event.getUser() == null) {
				BNCore.log("[Captcha] Received reaction from null user");
				return;
			}

			final String id = event.getUser().getId();
			DiscordCaptchaService captchaService = new DiscordCaptchaService();
			DiscordCaptcha captcha = captchaService.get();

			CaptchaResult result = captcha.check(id);
			if (result == CaptchaResult.UNCONFIRMED) {
				captcha.confirm(id);

				Member member = Discord.getGuild().getMemberById(id);
				PrivateChannel complete = event.getUser().openPrivateChannel().complete();

				if (member == null) {
					Message message = complete.getHistory().getMessageById(event.getMessageId());
					if (message == null)
						BNCore.warn("[Captcha] Could not find original message");
					else
						message.editMessage("Account confirmed. You may now join the server again: discord.gg/bearnation").queue();
				} else
					complete.sendMessage("Account confirmed, thank you!").queue();

				captchaService.save(captcha);
			}
		});
	}

}
