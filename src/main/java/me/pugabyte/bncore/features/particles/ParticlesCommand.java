package me.pugabyte.bncore.features.particles;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.features.particles.effects.LineEffect;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.event.Listener;

@NoArgsConstructor
@Permission("group.admin")
public class ParticlesCommand extends CustomCommand implements Listener {

	public ParticlesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("line [distance] [density]")
	void run(@Arg("10") int distance, @Arg("0.1") double density) {
		new LineEffect(player(), distance, density);
	}
}
