package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.World;

@Permission(Group.SENIOR_STAFF)
@Redirect(from = "/sun", to = "/weather sun")
@Redirect(from = "/storm", to = "/weather storm")
public class WeatherCommand extends CustomCommand {

	public WeatherCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Description("Get the weather of the world")
	void get(@Optional("current") World world) {
		send(PREFIX + "The world weather of &e" + world.getName() + " &3is &e" + FixedWeatherType.of(world).name().toLowerCase());
	}

	@Description("Set the weather of the specified world")
	void set(FixedWeatherType type, @Optional int duration, @Switch @Optional("current") World world) {
		type.apply(world);
		if (duration > 0)
			world.setWeatherDuration(duration);

		send(PREFIX + "Weather set to &e" + type.name().toLowerCase() + (duration > 0 ? " &3for &e" + Timespan.ofSeconds(duration).format() : ""));
	}

	@Permission(Group.ADMIN)
	@Description("View current weather durations")
	void getDuration(@Optional("current") World world) {
		send(PREFIX + "Durations for " + StringUtils.getWorldDisplayName(world));
		send(" &3Clear Weather: &e" + Timespan.ofSeconds(world.getClearWeatherDuration() / 20).format());
		send(" &3Weather: &e" + Timespan.ofSeconds(world.getWeatherDuration() / 20).format());
		send(" &3Thunder: &e" + Timespan.ofSeconds(world.getThunderDuration() / 20).format());
	}

	@Getter
	@AllArgsConstructor
	public enum FixedWeatherType {
		CLEAR(false, false),
		RAIN(true, false),
		STORM(true, true);

		private final boolean rain;
		private final boolean thunder;

		public void apply(World world) {
			world.setStorm(rain);
			world.setThundering(thunder);
		}

		public static FixedWeatherType of(World world) {
			return !world.hasStorm() ? CLEAR : !world.isThundering() ? RAIN : STORM;
		}
	}

}
