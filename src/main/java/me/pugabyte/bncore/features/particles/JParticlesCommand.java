package me.pugabyte.bncore.features.particles;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.features.particles.effects.BandEffect;
import me.pugabyte.bncore.features.particles.effects.CircleEffect;
import me.pugabyte.bncore.features.particles.effects.DiscoEffect;
import me.pugabyte.bncore.features.particles.effects.DotEffect;
import me.pugabyte.bncore.features.particles.effects.LineEffect;
import me.pugabyte.bncore.features.particles.effects.NyanCatEffect;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

@NoArgsConstructor
@Permission("group.admin")
public class JParticlesCommand extends CustomCommand implements Listener {

	public JParticlesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("line [distance] [density]")
	void line(@Arg("10") int distance, @Arg("0.1") double density) {
		LineEffect.builder().player(player()).distance(distance).density(density).rainbow(true).start();
	}

	@Path("circles")
	void circles() {
		CircleEffect.builder().player(player()).location(player().getLocation()).density(10).radius(0.333).ticks(20 * 20).whole(true).updateLoc(true).rainbow(true).start();
		Tasks.wait(20, () -> CircleEffect.builder().player(player()).location(player().getLocation()).density(20).radius(0.666).ticks(20 * 20).whole(true).updateLoc(true).rainbow(true).start());
		Tasks.wait(40, () -> CircleEffect.builder().player(player()).location(player().getLocation()).density(40).radius(0.999).ticks(20 * 20).whole(true).updateLoc(true).rainbow(true).start());
		Tasks.wait(60, () -> CircleEffect.builder().player(player()).location(player().getLocation()).density(60).radius(1.333).ticks(20 * 20).whole(true).updateLoc(true).rainbow(true).start());
	}

	@Path("circle")
	void circle() {
		Vector vector = new Vector(0, 1, 0);
		Location loc = player().getLocation().add(vector);
		CircleEffect.builder().player(player()).location(loc).updateVector(vector).density(100).radius(1.5).ticks(20 * 20).rainbow(true).start();
	}

	@Path("halo")
	void halo() {
		Vector vector = new Vector(0, 2.1, 0);
		Location loc = player().getLocation().add(vector);
		CircleEffect.builder().player(player()).location(loc).updateVector(vector).density(20).radius(0.5).ticks(20 * 20).rainbow(true).start();
	}

	@Path("slowsphere")
	void slowsphere() {
		Vector vector = new Vector(0, 1.5, 0);
		Location loc = player().getLocation().add(vector);
		CircleEffect.builder().player(player()).location(loc).updateVector(vector).density(100).radius(1.5).ticks(20 * 20).randomRotation(true).rainbow(true).start();
	}

	@Path("fastsphere")
	void fastsphere() {
		Vector vector = new Vector(0, 1.5, 0);
		Location loc = player().getLocation().add(vector);
		CircleEffect.builder().player(player()).location(loc).updateVector(vector).density(100).radius(1.5).ticks(20 * 20).randomRotation(true).rainbow(true).fast(true).start();
	}

	@Path("BNRings")
	void bnrings() {
		Vector vector = new Vector(0, 1.5, 0);
		Location loc = player().getLocation().add(vector);
		CircleEffect.builder().player(player()).location(loc).updateVector(vector).density(100).radius(1.5).ticks(20 * 20).randomRotation(true).color(Color.TEAL).fast(true).start();
		Tasks.wait(20, () ->
				CircleEffect.builder().player(player()).location(loc).updateVector(vector).density(100).radius(1.5).ticks(20 * 20).randomRotation(true).color(Color.YELLOW).fast(true).start());
	}

	@Path("dot")
	void dot() {
		Location loc = Utils.getCenteredLocation(player().getLocation()).add(0, 1, 0);
		DotEffect.builder().player(player()).loc(loc).ticks(10 * 20).rainbow(true).start();
	}

	@Path("band")
	void band() {
		BandEffect.builder().player(player()).ticks(10 * 20).rainbow(true).start();
	}

	@Path("nyancat")
	void nyancat() {
		NyanCatEffect.builder().player(player()).ticks(10 * 20).start();
	}

	@Path("disco")
	void disco() {
		Vector vector = new Vector(0, 4, 0);
		Location loc = player().getLocation().add(vector);
		DiscoEffect.builder().player(player()).location(loc).ticks(10 * 20).lineLength(5).maxLines(4).sphereRadius(0.5)
				.direction(DiscoEffect.Direction.BOTH).sphereColor(Color.WHITE).lineRainbow(true).start();
	}
}
