package me.pugabyte.bncore.features.tameables;

import me.pugabyte.bncore.features.tameables.models.TameablesAction;
import me.pugabyte.bncore.features.tameables.models.TameablesActionType;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

public class TameablesCommand extends CustomCommand {

	TameablesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		send("Correct usage: &c/tameables <info|untame|transfer [player]>");
	}

	@Path("(info|view)")
	void info() {
		Tameables.addPendingAction(player(), new TameablesAction(TameablesActionType.INFO));
		send(PREFIX + "Punch the animal you wish to view information on");
	}

	@Path("untame")
	void untame() {
		Tameables.addPendingAction(player(), new TameablesAction(TameablesActionType.UNTAME));
		send(PREFIX + "Punch the animal you wish to remove ownership of");
	}

	@Path("transfer <player>")
	void transfer(OfflinePlayer transfer) {
		if (player().equals(transfer))
			error("You can't transfer an animal to yourself");
		Tameables.addPendingAction(player(), new TameablesAction(TameablesActionType.TRANSFER, transfer));
		send(PREFIX + "Punch the animal you wish to transfer to " + transfer.getName());
	}

}
