package gg.projecteden.nexus.features.events;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ColorType;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
public class DebugDotCommand extends CustomCommand {

	public DebugDotCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<x> <y> <z> [--color] [--ticks]")
	void dot(double x, double y, double z, @Switch @Arg("red") ColorType color, @Switch @Arg("20") int ticks) {
		Location location = new Location(player().getWorld(), x, y, z);

		play(player(), location, color, ticks);
	}

	@Path("relative <x> <y> <z> [--color] [--ticks]")
	void relative(double x, double y, double z, @Switch @Arg("red") ColorType color, @Switch @Arg("20") int ticks) {
		Location location = location().clone().add(x, y, z);

		play(player(), location, color, ticks);
	}

	public static void play(Player player, Location location, ColorType color, int ticks) {
		DotEffect.debug(player, location, color.getBukkitColor(), TickTime.TICK.x(ticks));
	}
}
