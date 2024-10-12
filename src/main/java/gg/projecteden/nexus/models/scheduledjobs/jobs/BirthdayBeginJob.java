package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.BirthdaysCommand;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Async
@Schedule("*/30 * * * *")
public class BirthdayBeginJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		if (Nexus.getEnv() != Env.PROD)
			return completed();

		final LocalDateTime now = getTimestamp();

		for (Nerd nerd : new NerdService().getNerdsWithBirthdays())
			if (isBirthday(now, nerd)) {
				BirthdaysCommand.announcement(nerd);
				Costume partyHat = Costume.of("hat/misc/party_hat");
				if (partyHat != null) {
					CostumeUserService userService = new CostumeUserService();
					CostumeUser costumeUser = userService.get(nerd);
					costumeUser.getBirthdayCostumes().add(partyHat.getId());
					userService.save(costumeUser);
				}
			}
		return completed();
	}

	public static boolean isBirthday(LocalDateTime now, Nerd nerd) {
		DiscordUser discordUser = new DiscordUserService().get(nerd);

		if (discordUser.getUserId() == null)
			return false;

		LocalDate birthday = nerd.getBirthday().withYear(now.getYear());

		ZoneId zone = ZoneId.systemDefault();
		final GeoIP geoip = new GeoIPService().get(discordUser);
		if (geoip.getTimezone() != null && !isNullOrEmpty(geoip.getTimezone().getId()))
			zone = ZoneId.of(geoip.getTimezone().getId());

		final ZonedDateTime zonedNow = now.atZone(ZoneId.systemDefault());
		if (!zonedNow.isEqual(birthday.atStartOfDay(zone)))
			return false;

		return true;
	}

}

