package me.pugabyte.bncore.features.clearinventory;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static me.pugabyte.bncore.features.clearinventory.ClearInventory.PREFIX;

// What the fuck essentials
@Aliases({"eclearinventorytoggle", "clearinventoryconfirmtoggle", "eclearinventoryconfirmtoggle",
		"clearinventoryconfirmoff", "eclearinventoryconfirmoff", "clearconfirmoff", "eclearconfirmoff",
		"clearconfirmon", "eclearconfirmon", "clearconfirm", "eclearconfirm"})
@NoArgsConstructor
public class ClearInventoryToggleCommand extends CustomCommand {

	ClearInventoryToggleCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void info() {
		reply(PREFIX + "Use &c/clear undo &3to revert an inventory clear");
	}

}
