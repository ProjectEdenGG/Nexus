package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.staff")
@Aliases({"lightning", "thor"})
public class SmiteCommand extends CustomCommand {

	public SmiteCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(Player player) {
		if (player == null)
			world().strikeLightning(getTargetBlockRequired().getLocation());
		else {
			player.getWorld().strikeLightningEffect(player.getLocation());
			send(PREFIX + "Smiting " + player.getName());
			send(player, "&cThou hast been smitten!");
		}
	}

}
