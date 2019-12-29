package me.pugabyte.bncore.features.tameables;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.tameables.models.TameablesAction;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

public class TameablesCommand extends CustomCommand {

	TameablesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		reply("Correct usage: &c/tameables <info|untame|transfer [player]>");
	}

	@Path("(info|view)")
	void info() {
		BNCore.tameables.addPendingAction(player(), TameablesAction.INFO);
		reply(PREFIX + "Punch the animal you wish to view information on");
	}

	@Path("untame")
	void untame() {
		BNCore.tameables.addPendingAction(player(), TameablesAction.UNTAME);
		reply(PREFIX + "Punch the animal you wish to remove ownership of");
	}

	@Path("transfer {offlineplayer}")
	void transfer(@Arg OfflinePlayer transfer) {
		if (player().getUniqueId().equals(transfer.getUniqueId()))
			error("You can't transfer an animal to yourself");
		BNCore.tameables.addPendingAction(player(), TameablesAction.TRANSFER.withPlayer(transfer));
		reply(PREFIX + "Punch the animal you wish to transfer to " + transfer.getName());
	}

}
