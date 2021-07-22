package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.WeatherType;

import java.util.Arrays;

@Aliases({"rain", "snow", "pweather"})
@Redirect(from = { "/rainoff", "/snowoff" }, to = "/rain off")
@Description("Change the weather for yourself. Does not change on the server, therefore does not affect things like mob spawning/burning.")
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
