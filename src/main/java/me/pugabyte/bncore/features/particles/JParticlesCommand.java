package me.pugabyte.bncore.features.particles;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.features.particles.effects.DiscoEffect;
import me.pugabyte.bncore.features.particles.effects.DotEffect;
import me.pugabyte.bncore.features.particles.effects.LineEffect;
import me.pugabyte.bncore.features.particles.effects.StarEffect;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.List;

@NoArgsConstructor
@Permission("group.admin")
public class JParticlesCommand extends CustomCommand implements Listener {

	public JParticlesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<effectType>")
	void run(EffectType effectType) {
		effectType.run(player());
	}

	@Path("stop <effectType>")
	void stop(EffectType effectType) {
		ParticleUtils.cancelEffect(player(), effectType);
	}

	@Path("stopall")
	void stopAll() {
		ParticleUtils.cancelAllEffects(player());
	}

	@Path("line [distance] [density]")
	void line(@Arg("10") int distance, @Arg("0.1") double density) {
		LineEffect.builder().player(player()).distance(distance).density(density).rainbow(true).start();
	}

	@Path("dot")
	void dot() {
		Location loc = Utils.getCenteredLocation(player().getLocation()).add(0, 1, 0);
		DotEffect.builder().player(player()).loc(loc).ticks(10 * 20).rainbow(true).start();
	}

	@Path("disco slow")
	void discoslow() {
		Vector vector = new Vector(0, 4, 0);
		Location loc = player().getLocation().add(vector);
		DiscoEffect.builder().player(player()).location(loc).ticks(10 * 20).lineLength(5).maxLines(4).sphereRadius(0.5)
				.direction(DiscoEffect.Direction.BOTH).sphereColor(Color.WHITE).lineRainbow(true).rainbowOption(DiscoEffect.RainbowOption.SLOW).start();
	}

	@Path("disco fast")
	void discofast() {
		Vector vector = new Vector(0, 4, 0);
		Location loc = player().getLocation().add(vector);
		DiscoEffect.builder().player(player()).location(loc).ticks(10 * 20).lineLength(5).maxLines(4).sphereRadius(0.5)
				.direction(DiscoEffect.Direction.BOTH).sphereColor(Color.WHITE).lineRainbow(true).rainbowOption(DiscoEffect.RainbowOption.FAST).start();
	}

	@Path("disco line")
	void discoline() {
		Vector vector = new Vector(0, 4, 0);
		Location loc = player().getLocation().add(vector);
		DiscoEffect.builder().player(player()).location(loc).ticks(10 * 20).lineLength(5).maxLines(4).sphereRadius(0.5)
				.direction(DiscoEffect.Direction.BOTH).sphereColor(Color.WHITE).lineRainbow(true).rainbowOption(DiscoEffect.RainbowOption.LINE).start();
	}

	@Path("star")
	void star() {
		StarEffect.builder().player(player()).location(player().getLocation()).start();
	}

	@ConverterFor(EffectType.class)
	EffectType convertToEffectType(String value) {
		try {
			return EffectType.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ignore) {
			throw new InvalidInputException("EffectType from " + value + " not found");
		}
	}

	@TabCompleterFor(EffectType.class)
	List<String> tabCompleteEffectType(String filter) {
		return tabCompleteEnum(EffectType.class, filter);
	}
}
