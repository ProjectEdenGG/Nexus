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
	GeoIPService geoIpService = new GeoIPService();
	SettingService settingService = new SettingService();

	public CurrentTimeCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Timezones");
	}

	@Path("format <12/24>")
	void format(int format) {
		if (format != 12 && format != 24) {
			line();
			send(json(" &eClick &3 the time format you prefer:  ")
					.next("&e12 hour")
					.command("currenttime format 12")
					.hover("&eClick &3to set the display format to &e12 hour")
					.group()
					.next("  &3||  &3")
					.next("&e24 hour")
					.command("currenttime format 24")
					.hover("&eClick &3to set the display format to &e24 hour")
					.group());
		}
		Setting setting = settingService.get(player(), "timezoneFormat");
		setting.setValue(String.valueOf(format));
		settingService.save(setting);
		send(PREFIX + "Time format set to &e" + format + " hour");
	}

	@Path("update")
	void update() {
		send(PREFIX + "Updating your timezone information...");
		GeoIP geoIp = geoIpService.request(player());
		if (geoIp == null || geoIp.getIp() == null)
			error("There was an error while updating your timezone. Please try again later.");
		geoIpService.save(geoIp);
		send(PREFIX + "Updated your timezone to &3" + geoIp.getTimezone());
	}

	@Path("<player>")
	void timeFor(Player player) {
		GeoIP geoIp = geoIpService.get(player);
		if (geoIp == null || geoIp.getIp() == null)
			error("That player's timezone is not set.");
		Setting setting = settingService.get(player(), "timezoneFormat");
		DateFormat format = new SimpleDateFormat("h:mm aa");
		if (setting.getValue() != null && setting.getValue().equalsIgnoreCase("24"))
			format = new SimpleDateFormat("HH:mm");
		format.setTimeZone(TimeZone.getTimeZone(geoIp.getTimezone().getId()));
		send(PREFIX + "The current time for &e" + player.getName() + " &3is &e" + format.format(new Date()));
	}

	@Path()
	void help() {
		send(PREFIX + "This command shows you what time it is for other players.");
		send(json("&eClick here &3to change the time format.").command("/currenttime format").hover("&3The valid formats are &e12 &3and &e24 &3hours. It defaults to &e12 &3hours."));
		line();
		send("&3Usage: &c/currenttime <player>");
	}

}
