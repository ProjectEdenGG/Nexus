package me.pugabyte.bncore.features.particles;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.features.particles.effects.CircleEffect;
import me.pugabyte.bncore.features.particles.effects.DiscoEffect;
import me.pugabyte.bncore.features.particles.effects.DotEffect;
import me.pugabyte.bncore.features.particles.effects.LineEffect;
import me.pugabyte.bncore.features.particles.effects.PolygonEffect;
import me.pugabyte.bncore.features.particles.effects.StarEffect;
import me.pugabyte.bncore.features.particles.effects.WingsEffect;
import me.pugabyte.bncore.features.particles.menu.ParticleMenu;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.particle.ParticleOwner;
import me.pugabyte.bncore.models.particle.ParticleService;
import me.pugabyte.bncore.models.particle.ParticleType;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.List;

@NoArgsConstructor
@Permission("group.admin")
public class JParticlesCommand extends CustomCommand implements Listener {
	ParticleService service = new ParticleService();
	ParticleOwner particleOwner;

	public JParticlesCommand(@NonNull CommandEvent event) {
		super(event);
		particleOwner = service.get(player());
	}

	@Path()
	void menu() {
		ParticleMenu.openMain(player(), 0);
	}

	@Path("<effectType>")
	void run(ParticleType particleType) {
		particleType.run(player());
	}

	@Path("stop <effectType>")
	void stop(ParticleType particleType) {
		particleOwner.cancelTasks(particleType);
	}

	@Path("stopall")
	void stopAll() {
		particleOwner.cancelTasks();
	}

	@Path("line [distance] [density]")
	void line(@Arg("10") int distance, @Arg("0.1") double density) {
		LineEffect.builder().player(player()).distance(distance).density(density).rainbow(true).start();
	}

	@Path("dot")
	void dot() {
		Location loc = Utils.getCenteredLocation(player().getLocation()).add(0, 1, 0);
		DotEffect.builder().player(player()).location(loc).ticks(10 * 20).rainbow(true).start();
	}

	@Path("Wings [boolean]")
	void wings(@Arg("true") boolean flapMode) {
		WingsEffect.builder()
				.player(player())
				.flapMode(flapMode)
				.flapSpeed(1)
				.color1(Color.BLACK)
				.rainbow1(false)
				.color2(Color.BLACK)
				.rainbow2(true)
				.color3(Color.BLACK)
				.rainbow3(false)
				.ticks(10 * 20)
				.wingStyle(WingsEffect.WingStyle.FOURTEEN)
				.start();
	}

	@Path("disco line")
	void discoline() {
		Vector vector = new Vector(0, 4, 0);
		Location loc = player().getLocation().add(vector);
		DiscoEffect.builder().player(player()).location(loc).ticks(10 * 20).lineLength(5).maxLines(4).sphereRadius(0.5)
				.direction(DiscoEffect.Direction.BOTH).sphereColor(Color.WHITE).lineRainbow(true).rainbowOption(DiscoEffect.RainbowOption.LINE).start();
	}

	@Path("pentagram")
	void pentagram() {
		CircleEffect.builder().player(player()).density(50).radius(2).ticks(25 * 20).whole(true).updateLoc(true).color(Color.BLACK).start();
		StarEffect.builder().player(player()).radius(2).ticks(25 * 20).updateLoc(true).color(Color.RED).rotateSpeed(0.2).start();
	}

	@Path("polygon <number> [number]")
	void polygon(@Arg int points, @Arg("1.5") double radius) {
		PolygonEffect.Polygon polygon;
		switch (points) {
			case 4:
				polygon = PolygonEffect.Polygon.SQUARE;
				break;
			case 5:
				polygon = PolygonEffect.Polygon.PENTAGON;
				break;
			case 6:
				polygon = PolygonEffect.Polygon.HEXAGON;
				break;
			case 7:
				polygon = PolygonEffect.Polygon.HEPTAGON;
				break;
			case 8:
				polygon = PolygonEffect.Polygon.OCTAGON;
				break;
			default:
				polygon = PolygonEffect.Polygon.TRIANGLE;
				break;
		}
		PolygonEffect.builder()
				.player(particleOwner.getPlayer())
				.updateLoc(true)
				.whole(true)
				.polygon(polygon)
				.radius(radius)
				.ticks(5 * 20)
				.rainbow(true)
				.rotateSpeed(0.0)
				.start();
	}

	@ConverterFor(ParticleType.class)
	ParticleType convertToEffectType(String value) {
		try {
			return ParticleType.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ignore) {
			throw new InvalidInputException("EffectType from " + value + " not found");
		}
	}

	@TabCompleterFor(ParticleType.class)
	List<String> tabCompleteEffectType(String filter) {
		return tabCompleteEnum(ParticleType.class, filter);
	}
}
