package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.listeners.events.FirstWorldGroupVisitEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.DescriptionExtra;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.WeatherType;
import org.bukkit.event.EventHandler;

import java.util.Arrays;

@Aliases({"rain", "snow", "pweather"})
@Redirect(from = { "/rainoff", "/snowoff" }, to = "/rain off")
public class PlayerWeatherCommand extends CustomCommand {

	public PlayerWeatherCommand(@NonNull CommandEvent event) {
		super(event);
	}

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

	@EventHandler
	public void on(FirstWorldGroupVisitEvent event) {
		event.getPlayer().resetPlayerWeather();
	}

}
