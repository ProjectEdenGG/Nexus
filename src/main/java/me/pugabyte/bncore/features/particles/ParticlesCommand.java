package me.pugabyte.bncore.features.particles;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.features.particles.effects.CircleEffect;
import me.pugabyte.bncore.features.particles.effects.DotEffect;
import me.pugabyte.bncore.features.particles.effects.LineEffect;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.Listener;

@NoArgsConstructor
@Permission("group.admin")
public class ParticlesCommand extends CustomCommand implements Listener {

	public ParticlesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("line [distance] [density]")
	void line(@Arg("10") int distance, @Arg("0.1") double density) {
		LineEffect.builder().player(player()).distance(distance).density(density).rainbow(true).build();
	}

	@Path("circle [radius] [density]")
	void circle(@Arg("1.0") double radius, @Arg("20") int density) {
		CircleEffect.builder().player(player()).location(player().getLocation()).density(10).radius(0.333).ticks(20 * 20).whole(true).rainbow(true).build();
		Tasks.wait(20, () -> CircleEffect.builder().player(player()).location(player().getLocation()).density(20).radius(0.666).ticks(20 * 20).whole(true).rainbow(true).build());
		Tasks.wait(40, () -> CircleEffect.builder().player(player()).location(player().getLocation()).density(40).radius(0.999).ticks(20 * 20).whole(true).rainbow(true).build());
		Tasks.wait(60, () -> CircleEffect.builder().player(player()).location(player().getLocation()).density(60).radius(1.333).ticks(20 * 20).whole(true).rainbow(true).build());
	}

	@Path("dot")
	void dot() {
		Location loc = Utils.getCenteredLocation(player().getLocation()).add(0, 1.5, 0);
		DotEffect.builder().player(player()).loc(loc).particle(Particle.REDSTONE).ticks(10 * 20).rainbow(true).build();
	}
}
