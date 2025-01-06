package gg.projecteden.nexus.features.test;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@HideFromWiki
@Permission(Group.ADMIN)
public class CurveTestCommand extends CustomCommand {

	public CurveTestCommand(CommandEvent event) {
		super(event);
	}

	private static final Map<UUID, BezierCurve> curves = new HashMap<>();

	@NotNull
	private BezierCurve curve() {
		return curves.computeIfAbsent(uuid(), $ -> new BezierCurve());
	}

	private void dot(Location control, ColorType lightGreen) {
		DotEffect.builder().player(player()).location(control).speed(0.1).ticks(TickTime.SECOND.x(10)).color(lightGreen.getBukkitColor()).start();
	}

	@Path("reset")
	public void reset() {
		curves.remove(uuid());
		send(PREFIX + "Reset");
	}

	@Path("add")
	public void add() {
		curve().add(location());
		dot(location(), ColorType.LIGHT_GREEN);
		send(PREFIX + "Point added");
	}

	@Path("draw [--segments]")
	void draw(@Arg("100") @Switch int segments) {
		final BezierCurve curve = curve();
		for (Location control : curve.controls)
			dot(control, ColorType.LIGHT_GREEN);

		final List<Location> points = curve.build(segments);

		for (Location point : points)
			dot(point, ColorType.PURPLE);
	}

	public static class BezierCurve {
		private final List<Location> controls = new ArrayList<>();

		public static BezierCurve builder() {
			return new BezierCurve();
		}

		public BezierCurve add(Location location) {
			controls.add(location);
			return this;
		}

		public BezierCurve add(List<Location> locations) {
			this.controls.addAll(locations);
			return this;
		}

		public List<Location> build(int segments) {
			return new ArrayList<>() {{
				for (int i = 1; i < segments; i++)
					add(bezier(i / (float) segments));
			}};
		}

		private float binomial(float n, float k) {
			var coeff = 1;
			for (var i = n - k + 1; i <= n; i++) coeff *= i;
			for (var i = 1; i <= k; i++) coeff /= i;
			return coeff;
		}

		private Location bezier(float step) {
			var size = controls.size() - 1;

			final Location location = new Location(controls.get(0).getWorld(), 0, 0, 0);

			for (int i = 0; i <= size; i++)
				location.add(controls.get(i).clone().multiply(binomial(size, i) * Math.pow((1 - step), (size - i)) * Math.pow(step, i)));

			return location;
		}

	}

}
