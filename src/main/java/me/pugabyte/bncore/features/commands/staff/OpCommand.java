package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.Nerd.StaffMember;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Set;

public class OpCommand extends CustomCommand {

	public OpCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	@Permission("group.admin")
	public void op(StaffMember staffMember) {
		OfflinePlayer player = staffMember.getOfflinePlayer();
		Nerd nerd = new Nerd(player);
		String name = nerd.getName();

		if (!nerd.getRank().isStaff())
			error(name + " is not staff");

		if (player.isOp())
			error(name + " is already a server operator");

		player.setOp(true);
		send(PREFIX + name + " is now a server operator");

		if (player.isOnline() && !player.equals(player()))
			send(player.getPlayer(), PREFIX + "You are now a server operator");
	}

	@Path("list")
	@Permission("group.admin")
	public void list() {
		Set<OfflinePlayer> ops = Bukkit.getOperators();
		if (ops.isEmpty())
			error("There are no server operators");

		send(PREFIX + "Ops:");
		for (OfflinePlayer operator : ops)
			send("&7- &3" + operator.getName());
	}
}
