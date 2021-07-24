package gg.projecteden.nexus.features.test;

import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.TimeUtils.Time;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Permission("group.admin")
public class CurveTestCommand extends CustomCommand {

	public CurveTestCommand(CommandEvent event) {
		super(event);
	}

	@Path("start")
	public void point1() {
		start = location();
		send("P1: " + StringUtils.getCoordinateString(start));
		location().getBlock().setType(Material.RED_STAINED_GLASS);
	}

	@Path("control1")
	public void point3() {
		startControl = location();
		send("P3: " + StringUtils.getCoordinateString(startControl));
		location().getBlock().setType(Material.PINK_STAINED_GLASS);
	}

	@Path("end")
	public void point2() {
		end = location();
		send("P2: " + StringUtils.getCoordinateString(end));
		location().getBlock().setType(Material.LIGHT_BLUE_STAINED_GLASS);
	}

	@Path("control2")
	public void point4() {
		endControl = location();
		send("P4: " + StringUtils.getCoordinateString(endControl));
		location().getBlock().setType(Material.BLUE_STAINED_GLASS);
	}

	@Path("showPoints")
	void showPoints() {
		start.getBlock().setType(Material.RED_STAINED_GLASS);
		startControl.getBlock().setType(Material.PINK_STAINED_GLASS);
		end.getBlock().setType(Material.LIGHT_BLUE_STAINED_GLASS);
		endControl.getBlock().setType(Material.BLUE_STAINED_GLASS);
	}

	@Path("display <segments>")
	public void display(int segments) {
		startControl.getBlock().setType(Material.AIR);
		endControl.getBlock().setType(Material.AIR);
		start.getBlock().setType(Material.AIR);
		end.getBlock().setType(Material.AIR);

		DotEffect.builder().player(player()).location(start).speed(0.1).ticks(Time.SECOND.x(10)).color(ColorType.LIGHT_RED.getBukkitColor()).start();
		DotEffect.builder().player(player()).location(startControl).speed(0.1).ticks(Time.SECOND.x(10)).color(ColorType.PINK.getBukkitColor()).start();
		DotEffect.builder().player(player()).location(end).speed(0.1).ticks(Time.SECOND.x(10)).color(ColorType.LIGHT_BLUE.getBukkitColor()).start();
		DotEffect.builder().player(player()).location(endControl).speed(0.1).ticks(Time.SECOND.x(10)).color(ColorType.BLUE.getBukkitColor()).start();

		List<Location> curve;
		if (endControl != null)
			curve = bezierCurve(segments, start, startControl, end, endControl);
		else
			curve = bezierCurve(segments, start, startControl, end);

		for (Location point : curve) {
			DotEffect.builder()
					.player(player())
					.location(point)
					.speed(0.1)
					.ticks(Time.SECOND.x(10))
					.color(ColorType.PURPLE.getBukkitColor())
					.start();
		}
	}
	static Location start, end, startControl, endControl;

	public static Location bezierPoint(float t, Location start, Location control, Location end) {
		float a = (1 - t) * (1 - t); // (1−t)^2
		float b = 2 * (1 - t) * t; // 2(1−t)*t
		float c = t * t; // t^2

		return end.clone().multiply(a).add(control.clone().multiply(b)).add(start.clone().multiply(c));
	}

	public static Location bezierPoint(float t, Location start, Location startControl, Location end, Location endControl) {
		float a = (1 - t) * (1 - t) * (1 - t); // (1−t)^3
		float b = 3 * (1 - t) * (1 - t) * t; // 3(1−t)^2*t
		float c = 3 * (1 - t) * t * t; // 3(1−t)*t^2
		float d = t * t * t; // t^3

		return start.clone().multiply(a).add(startControl.clone().multiply(b)).add(endControl.clone().multiply(c)).add(end.clone().multiply(d));

	}

	public static List<Location> bezierCurve(int segmentCount, Location start, Location control, Location end) {
		List<Location> points = new ArrayList<>();
		for (int i = 1; i < segmentCount; i++) {
			float t = i / (float) segmentCount;
			points.add(bezierPoint(t, start, control, end));
		}
		return points;
	}

	public static List<Location> bezierCurve(int segmentCount, Location start, Location startControl, Location end, Location endControl) {
		List<Location> points = new ArrayList<>();
		for (int i = 1; i < segmentCount; i++) {
			float t = i / (float) segmentCount;
			points.add(bezierPoint(t, start, startControl, end, endControl));
		}
		return points;
	}

}
