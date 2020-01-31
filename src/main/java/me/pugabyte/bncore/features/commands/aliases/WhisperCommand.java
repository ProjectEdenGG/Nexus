package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Aliases({"w", "m", "t", "tell", "emsg", "etell", "ewhisper", "epm", "pm", "esspm"})
public class WhisperCommand extends CustomCommand {

	public WhisperCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> [message...]")
	void player(Player player, String message) {
		runCommand("msg " + player.getName() + " " + ((message == null) ? "" : message));
	}
}
