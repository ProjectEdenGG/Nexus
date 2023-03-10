package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd.StaffMember;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Set;

@Permission(Group.ADMIN)
public class OpCommand extends CustomCommand {

	public OpCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	@Description("Op a staff member")
	public void op(StaffMember staffMember) {
		OfflinePlayer player = staffMember.getOfflinePlayer();

		String oper = name();
		String opee = staffMember.getName();

		if (!staffMember.getRank().isStaff())
			error(opee + " is not staff");

		if (player.isOp())
			error(opee + " is already op");

		player.setOp(true);
		if (player.equals(player()))
			Broadcast.adminIngame().message(PREFIX + oper + " opped themselves").send();
		else
			Broadcast.adminIngame().message(PREFIX + oper + " opped " + opee).send();

		send(player, PREFIX + "You are now op");

	}

	@Path("list")
	@Description("List opped players")
	public void list() {
		Set<OfflinePlayer> ops = Bukkit.getOperators();
		if (ops.isEmpty())
			error("There are no server operators");

		send(PREFIX + "Ops:");
		for (OfflinePlayer operator : ops)
			send(" &7- &3" + operator.getName());
	}
}
