package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.annotations.Async;
import gg.projecteden.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.features.commands.BirthdaysCommand;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.nerd.Nerd;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

import static gg.projecteden.utils.Nullables.isNullOrEmpty;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Async
@Schedule("*/30 * * * *")
public class BirthdayBeginJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		final LocalDateTime now = getTimestamp();

		for (DiscordUser discordUser : new DiscordUserService().getAll()) {
			if (discordUser.getUserId() == null)
				continue;

			final Nerd nerd = Nerd.of(discordUser);
			if (nerd.getBirthday() == null)
				continue;

			LocalDate birthday = nerd.getBirthday().withYear(now.getYear());

			ZoneId zone;
			final GeoIP geoip = new GeoIPService().get(discordUser);
			if (geoip.getTimezone() != null && !isNullOrEmpty(geoip.getTimezone().getId()))
				zone = ZoneId.of(geoip.getTimezone().getId());
			else
				zone = ZoneId.systemDefault();

			final ZonedDateTime zonedNow = now.atZone(ZoneId.systemDefault());
			if (!zonedNow.equals(birthday.atStartOfDay(zone)))
				continue;

			if (discordUser.getMember() == null)
				continue;

			BirthdaysCommand.announcement(discordUser);
		}
		return completed();
	}

}
