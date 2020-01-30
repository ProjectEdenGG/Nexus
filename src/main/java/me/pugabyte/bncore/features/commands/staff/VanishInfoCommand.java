package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class VanishInfoCommand extends CustomCommand {

	public VanishInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		line();
		send("&c/vanish fakejoin &9- &3Unvanish and broadcast a &ajoin &3message");
		send("&c/vanish fakequit &9- &3Vanish and broadcast a &cquit &3message");
		send("&c/vanish t chests &9- &3Toggle silent container opening");
		send("&c/vanish t damage-out &9- &3Toggle hurting mobs");
		send("&c/vanish check &9- &3Check if you are vanished");
		send("&c/np &9- &3Toggle picking up items while vanished");
		send("&c/nf &9- &3Toggle mobs targeting you");
		send("&c/ni &9- &3Toggle blocking any interactions, such as redstoney things, blocks, and mobs");
	}
}
