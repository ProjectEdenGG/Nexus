package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.DescriptionExtra;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.WeatherType;

import java.util.Arrays;

@Aliases({"rain", "snow", "pweather"})
@Redirect(from = { "/rainoff", "/snowoff" }, to = "/rain off")
public class PlayerWeatherCommand extends CustomCommand {

	public PlayerWeatherCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<weather>")
	@Description("Change your client-side weather")
	@DescriptionExtra("Does not change on the server, therefore does not affect things like mob spawning/burning")
	void run(WeatherType type) {
		if (type == WeatherType.DOWNFALL && !hasPermission(Group.STAFF))
			error("Due to an exploit, setting your weather to rain has been disabled");
		player().setPlayerWeather(type);
		send(PREFIX + "Weather set to " + camelCase(type));
	}

	@Path("off")
	@Description("Set your client-side weather to clear")
	void off() {
		run(WeatherType.CLEAR);
	}

	@Path("reset")
	@Description("Sync your weather with the server")
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
