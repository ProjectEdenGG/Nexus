package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.entity.Player;

@Permission("group.staff")
public class YeetCommand extends CustomCommand {

	public YeetCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(Player player) {
		int wait = 0;
		for (int i = 0; i < 100; i++)
			Tasks.wait(wait += 3, () -> runCommand("/slap " + player.getName()));
	}

}
