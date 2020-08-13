package me.pugabyte.bncore.features.commands.staff.admin;

import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.Chat.StaticChannel;
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

		String oper = player().getName();
		String opee = nerd.getName();

		if (!nerd.getRank().isStaff())
			error(opee + " is not staff");

		if (player.isOp())
			error(opee + " is already op");

		player.setOp(true);
		if (player.equals(player()))
			Chat.broadcastIngame(PREFIX + oper + " opped themselves", StaticChannel.ADMIN);
		else
			Chat.broadcastIngame(PREFIX + oper + " opped " + opee, StaticChannel.ADMIN);

		if (player.isOnline() && !player.equals(player()))
			send(player.getPlayer(), PREFIX + "You are now op");

	}

	@Path("list")
	@Permission("group.admin")
	public void list() {
		Set<OfflinePlayer> ops = Bukkit.getOperators();
		if (ops.isEmpty())
			error("There are no server operators");

		send(PREFIX + "Ops:");
		for (OfflinePlayer operator : ops)
			send(" &7- &3" + operator.getName());
	}
}
