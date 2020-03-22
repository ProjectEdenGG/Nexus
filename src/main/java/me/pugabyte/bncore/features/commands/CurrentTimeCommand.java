package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.geoip.GeoIP;
import me.pugabyte.bncore.models.geoip.GeoIPService;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Aliases("timefor")
public class CurrentTimeCommand extends CustomCommand {

	public CurrentTimeCommand(CommandEvent event) {
		super(event);
	}

	GeoIPService geoIPService = new GeoIPService();
	SettingService settingService = new SettingService();

	String TIMEZONE = StringUtils.getPrefix("Timezones");

	@Path("format <12/24>")
	void format(int format) {
		if (format != 12 && format != 24) {
			line();
			send(json(" &eClick &3 the time format you prefer:  ")
					.next("&e12 hour")
					.command("currenttime format 12")
					.hover("&eClick &3to set the display format to &e12 hour")
					.group()
					.next("  &3||  ")
					.next("&e24 hour")
					.command("currenttime format 24")
					.hover("&eClick &3to set the display format to &e24 hour")
					.group());
		}
		Setting setting = settingService.get(player(), "timezoneFormat");
		setting.setValue(String.valueOf(format));
		settingService.save(setting);
		send(TIMEZONE + "Time format set to &e" + format + " hour");
	}

	@Path("update")
	void update() {
		send(TIMEZONE + "Updating your timezone information...");
		GeoIP geoIP = geoIPService.request(player());
		if (geoIP != null && geoIP.getIp() != null)
			error("There was an error while updating your timezone. Please try again later.");
		geoIPService.save(geoIP);
		send(TIMEZONE + "Updated your timezone to &3" + geoIP.getTimezone());
	}

	@Path("<player>")
	void timeFor(Player player) {
		GeoIP geoIP = geoIPService.get(player);
		if (geoIP != null && geoIP.getIp() != null)
			error("That player's timezone is not set.");
		Setting setting = settingService.get(player(), "timezoneFormat");
		DateFormat format;
		if (setting.getValue().equalsIgnoreCase("24")) format = new SimpleDateFormat("HH:mm");
		else format = new SimpleDateFormat("hh:mm aa");
		format.setTimeZone(TimeZone.getTimeZone(geoIP.getTimezone().getId()));
		send(TIMEZONE + "The current time for &e" + player.getName() + " &3is &e" + format.format(new Date()));
	}

	@Path()
	void help() {
		send(TIMEZONE + "This command shows you what time it is for other players.");
		send(json("&eClick here &3to change the time format.").command("/currenttime format").hover("&3The valid formats are &e12 &3and &e24 &3hours. It defaults to &e12 &3hours."));
		line();
		send("&3Usage: &c/currenttime <player>");
	}

}
