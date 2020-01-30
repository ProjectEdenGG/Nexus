package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class StaffRulesCommand extends CustomCommand {

	public StaffRulesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send("&b                 Staff Rules");
		line();
		send("&4> &3No swearing in global chat. You are a role model.");
		send("&4> &3Never lash out at players because you are not in a good mood. If you feel you are not in the right state of mind at the moment, ask another staff member to handle it.");
		send("&4> &3Do not abuse your powers.");
		send("&4> &3If a player asks a stupid question, be nice about it. Minecraft players are often young.");
		send("&4> &3Players are your first priority.");
		send("&4> &3Use &c/afk &c<message> &3when you go afk.");
		send("&4> &3Never answer a question with \"I don't know\"). If you don't know, find out.");
		send("&4> &3Don't ruin survival for other players by doing things such as building in fly for them, or giving them tons of stuff that you got because you have powers such as fly and godmode.");
	}

}
