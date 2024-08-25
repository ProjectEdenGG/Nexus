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
import gg.projecteden.parchment.HasPlayer;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.Block;

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

	public static void play(HasPlayer player, Block block) {
		play(player.getPlayer(), block.getLocation().toCenterLocation());
	}

	public static void play(HasPlayer player, Location location) {
		play(player.getPlayer(), location, ColorType.RED);
	}

	public static void play(HasPlayer player, Block location, ColorType color) {
		play(player.getPlayer(), location.getLocation().toCenterLocation(), color);
	}

	public static void play(HasPlayer player, Location location, ColorType color) {
		play(player.getPlayer(), location, color, TickTime.SECOND.x(3));
	}

	public static void play(HasPlayer player, Location location, ColorType color, long ticks) {
		if (player == null)
			return;

		DotEffect.builder()
			.player(player.getPlayer())
			.location(location)
			.color(color.getBukkitColor())
			.clientSide(true)
			.speed(.1)
			.ticks(ticks)
			.start();
	}
}
