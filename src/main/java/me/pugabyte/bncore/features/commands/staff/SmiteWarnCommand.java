package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

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
