package gg.projecteden.nexus.features.discord;

import gg.projecteden.api.discord.DiscordId.Role;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.models.discord.DiscordCaptcha;
import gg.projecteden.nexus.models.discord.DiscordCaptcha.CaptchaResult;
import gg.projecteden.nexus.models.discord.DiscordCaptchaService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

@NoArgsConstructor
public class DiscordCaptchaListener extends ListenerAdapter {

	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
		Tasks.async(() -> {
			final String id = event.getUser().getId();

			DiscordCaptchaService captchaService = new DiscordCaptchaService();
			DiscordCaptcha captcha = captchaService.get0();
			CaptchaResult result = captcha.check(id);

			if (result == CaptchaResult.CONFIRMED) {
				Tasks.waitAsync(1, () -> {
					Discord.addRole(id, Role.NERD);
					DiscordUser user = new DiscordUserService().getFromUserId(id);
					if (user != null)
						Discord.addRole(id, Role.VERIFIED);
				});

				return;
			}

			Discord.staffLog("**[Captcha]** " + Discord.getName(event.getMember()) + " - Requiring verification");
			captcha.require(id);
			captchaService.save(captcha);
		});
	}

	@Override
	public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
		Tasks.async(() -> {
			if (event.getChannelType() != ChannelType.PRIVATE)
				return;

			if (event.getUser() == null) {
				Nexus.log("[Captcha] Received reaction from null user");
				return;
			}

			final String id = event.getUser().getId();
			DiscordCaptchaService captchaService = new DiscordCaptchaService();
			DiscordCaptcha captcha = captchaService.get0();

			CaptchaResult result = captcha.check(id);
			if (result == CaptchaResult.UNCONFIRMED) {
				captcha.confirm(id);

				Member member = Discord.getGuild().retrieveMemberById(id).complete();
				PrivateChannel complete = event.getUser().openPrivateChannel().complete();

				if (member == null) {
					Message message = complete.getHistory().getMessageById(event.getMessageId());
					if (message == null)
						Nexus.warn("[Captcha] Could not find original message");
					else
						message.editMessage("Account confirmed. You may now join the server again: " + EdenSocialMediaSite.DISCORD.getUrl()).queue();
				} else
					complete.sendMessage("Account confirmed, thank you!").queue();

				captchaService.save(captcha);
			}
		});
	}
}
