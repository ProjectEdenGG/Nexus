package me.pugabyte.nexus.features.commands.staff.moderator;

import lombok.NonNull;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Aliases({"sw", "swarn"})
@Permission("group.moderator")
public class SmiteWarnCommand extends CustomCommand {

	public SmiteWarnCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void smiteWarn(Player player) {
		if (!Minigames.isMinigameWorld(player.getWorld()))
			error("Target player is not in minigames");

		player.getWorld().strikeLightningEffect(player.getLocation());
		runCommand("warn " + player.getName() + " Please obey the rules of our minigames.");
	}


}
