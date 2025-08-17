package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.discord.DiscordCaptcha;
import gg.projecteden.nexus.models.discord.DiscordCaptcha.CaptchaResult;
import gg.projecteden.nexus.models.discord.DiscordCaptchaService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Async
public class DiscordCaptchaKickJob extends AbstractJob {
	private String memberId;

	@Override
	protected CompletableFuture<JobStatus> run() {
		String name = Discord.getName(memberId);

		DiscordCaptcha verification = new DiscordCaptchaService().get();
		CaptchaResult result = verification.check(memberId);
		if (result != CaptchaResult.CONFIRMED) {
			Member member = Discord.getGuild().retrieveMemberById(memberId).complete();

			if (member != null) {
				Discord.staffLog("**[Captcha]** " + name + " - Kicking");
				member.kick().queue();
			} else
				Nexus.log("[Captcha] Kick scheduled for " + name + " cancelled, member not found");
		}

		return completed();
	}

}
