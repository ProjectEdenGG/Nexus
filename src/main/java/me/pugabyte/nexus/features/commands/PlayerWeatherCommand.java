package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.WeatherType;

import java.util.Arrays;

@Aliases({"rain", "snow", "pweather"})
@Redirect(from = { "/rainoff", "/snowoff" }, to = "/rain off")
public class PlayerWeatherCommand extends CustomCommand {

	public PlayerWeatherCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<weather>")
	void run(WeatherType type) {
		if (type == WeatherType.DOWNFALL && !hasPermission("group.staff"))
			error("Due to an exploit, setting your weather to rain has been disabled");
		player().setPlayerWeather(type);
		send(PREFIX + "Weather set to " + camelCase(type));
	}

	@Path("off")
	void off() {
		run(WeatherType.CLEAR);
	}

	@Path("reset")
	void reset() {
		player().resetPlayerWeather();
		send(PREFIX + "Weather synced with server");
	}

	@ConverterFor(WeatherType.class)
	WeatherType convertToWeatherType(String value) {
		if (Arrays.asList("storm", "rain").contains(value))
			value = WeatherType.DOWNFALL.name();
		if (Arrays.asList("sun", "none").contains(value))
			value = WeatherType.CLEAR.name();
		return (WeatherType) convertToEnum(value, WeatherType.class);
	}

}
