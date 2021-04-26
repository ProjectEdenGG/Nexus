package me.pugabyte.nexus.features.commands.staff;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.World;
import org.bukkit.entity.Bat;

import java.util.ArrayList;
import java.util.List;

@Permission("pv.see")
public class WhoDaresSummonMeCommand extends CustomCommand {

	public WhoDaresSummonMeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("vanish off");
		World world = world();
		world.strikeLightning(location().clone().add(0, 5, 0));
		world.strikeLightningEffect(location().clone().add(5, 0, 0));
		world.strikeLightningEffect(location().clone().add(0, 0, 5));
		world.strikeLightningEffect(location().clone().subtract(5, 0, 0));
		world.strikeLightningEffect(location().clone().subtract(0, 0, 5));
		List<Bat> bats = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			bats.add(world.spawn(location(), Bat.class));
		}
		Tasks.wait(Time.SECOND.x(5), () -> bats.forEach(Bat::remove));
	}


}
