package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.WeatherType;
import org.bukkit.World;

import static me.pugabyte.bncore.utils.StringUtils.timespanFormat;

@Permission("group.seniorstaff")
@Redirect(from = "/sun", to = "/weather sun")
@Redirect(from = "/storm", to = "/weather storm")
public class WeatherCommand extends CustomCommand {

	public WeatherCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<type> [duration]")
	void run(WeatherType weatherType, int duration) {
		run(player().getWorld(), weatherType, duration);
	}

	@Path("<world> <type> [duration]")
	void run(World world, WeatherType weatherType, int duration) {
		world.setStorm(weatherType == WeatherType.DOWNFALL);
		if (duration > 0)
			world.setWeatherDuration(duration);

		send(PREFIX + "Weather set to &e" + camelCase(weatherType) + (duration > 0 ? " &3for &e" + timespanFormat(duration) : ""));
	}

}
