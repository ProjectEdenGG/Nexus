package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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

	@Path("get [world]")
	@Description("Get the weather of the world")
	void get(@Arg("current") World world) {
		send(PREFIX + "The world weather of &e" + world.getName() + " &3is &e" + FixedWeatherType.of(world).name().toLowerCase());
	}

	@Path("set <type> [duration] [--world]")
	@Description("Set the weather of the specified world")
	void set(FixedWeatherType weatherType, int duration, @Switch @Arg("current") World world) {
		weatherType.apply(world);
		if (duration > 0)
			world.setWeatherDuration(duration);

		send(PREFIX + "Weather set to &e" + weatherType.name().toLowerCase() + (duration > 0 ? " &3for &e" + Timespan.ofSeconds(duration).format() : ""));
	}

	@Permission(Group.ADMIN)
	@Path("getDuration [world]")
	@Description("View current weather durations")
	void getDuration(@Arg("current") World world) {
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
