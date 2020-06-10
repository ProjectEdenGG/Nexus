package me.pugabyte.bncore.features.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
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
			player().getWorld().strikeLightning(player().getTargetBlockExact(600).getLocation());
		else {
			player.getWorld().strikeLightningEffect(player.getLocation());
			send(PREFIX + "Smiting " + player.getName());
			send(player, "&cThou hast been smitten!");
		}
	}

}
