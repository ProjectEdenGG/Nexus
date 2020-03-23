package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.World;
import org.bukkit.entity.Bat;

import java.util.ArrayList;
import java.util.List;

@Permission("vanish.vanish")
public class WhoDaresSummonMeCommand extends CustomCommand {

	public WhoDaresSummonMeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("vanish off");
		World world = player().getWorld();
		world.strikeLightning(player().getLocation().clone().add(0, 5, 0));
		world.strikeLightningEffect(player().getLocation().clone().add(5, 0, 0));
		world.strikeLightningEffect(player().getLocation().clone().add(0, 0, 5));
		world.strikeLightningEffect(player().getLocation().clone().subtract(5, 0, 0));
		world.strikeLightningEffect(player().getLocation().clone().subtract(0, 0, 5));
		List<Bat> bats = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			bats.add(world.spawn(player().getLocation(), Bat.class));
		}
		Tasks.wait(Time.SECOND.x(5), () -> bats.forEach(Bat::remove));
	}


}
