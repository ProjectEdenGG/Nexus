package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIP.TimeFormat;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.Player;

@Aliases("timefor")
@Description("Check what time it is for another player")
public class CurrentTimeCommand extends CustomCommand {
	GeoIPService service = new GeoIPService();

	public CurrentTimeCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Timezones");
	}

	@Path("format <12/24> [player]")
	void format(int format, @Arg(value = "self", permission = Group.SENIOR_STAFF) GeoIP user) {
		if (format != 12 && format != 24)
			send(json()
				.line()
				.next(" &eClick &3 the time format you prefer:  ")
				.next("&e12 hour")
				.command("/currenttime format 12 " + user.getNickname())
				.hover("&eClick &3to set the display format to &e12 hour")
				.group()
				.next("  &3||  &3")
				.next("&e24 hour")
				.command("/currenttime format 24 " + user.getNickname())
					.hover("&eClick &3to set the display format to &e24 hour")
					.group());
		else {
			user.setTimeFormat(format == 12 ? TimeFormat.TWELVE : TimeFormat.TWENTY_FOUR);
			service.save(user);
			send(PREFIX + "Time format set to &e" + format + " hour");
		}
	}

	@Path("update [player]")
	@Cooldown(value = TickTime.HOUR, bypass = Group.SENIOR_STAFF)
	void update(@Arg(value = "self", permission = Group.SENIOR_STAFF) Player player) {
		final String name = isSelf(player) ? "your" : Nickname.of(player) + "'s";
		send(PREFIX + "Updating " + name + " timezone information...");
		GeoIP geoip = service.request(player.getUniqueId(), player.getAddress().getHostString());
		if (geoip == null || geoip.getIp() == null)
			error("There was an error while updating " + name + " timezone. Please try again later.");

		service.save(geoip);
		send(PREFIX + "Updated " + name + " timezone to &3" + geoip.getTimezone());
	}

	@Path("<player>")
	void timeFor(GeoIP geoip) {
		if (!GeoIP.exists(geoip))
			error(geoip == null ? "That player" : geoip.getNickname() + "'s timezone is not set.");

		send(PREFIX + "The current time for &e" + geoip.getNickname() + " &3is &e" + geoip.getCurrentTimeShort());
	}

	@Path
	@Override
	public void help() {
		send(PREFIX + "This command shows you what time it is for other players.");
		send(json("&eClick here &3to change the time format.")
				.hover("&3The valid formats are &e12 &3and &e24 &3hours. It defaults to &e12 &3hours.")
				.command("/currenttime format"));
		line();
		send("&3Usage: &c/currenttime <player>");
	}

}
