package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.models.scheduledjobs.common.AbstractJob;
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
				member.kick("Please complete the verification process in your DMs with KodaBear").queue();
			} else
				Nexus.log("[Captcha] Kick scheduled for " + name + " cancelled, member not found");
		}

		return completed();
	}

}
