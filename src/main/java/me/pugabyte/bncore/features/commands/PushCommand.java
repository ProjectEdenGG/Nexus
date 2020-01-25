package me.pugabyte.bncore.features.commands;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PushCommand extends CustomCommand {
	private static String permission = "stoppushing.allow";

	static {
		PlaceholderAPI.registerPlaceholder(BNCore.getInstance(), "pushing", event ->
				String.valueOf(event.getPlayer().hasPermission(permission)));
	}

	public PushCommand(CommandEvent event) {
		super(event);
	}

	@Path("[enable]")
	void toggle(Boolean enable) {
		if (enable == null)
			push(!player().hasPermission(permission));
		else
			push(enable);
	}

	void push(boolean enable) {
		if (enable) {
			PermissionsEx.getUser(player()).addPermission(permission);
			send("&ePushing will be turned &aon&e shortly.");
		} else {
			PermissionsEx.getUser(player()).removePermission(permission);
			send("&ePushing will be turned &coff&e shortly.");
		}
	}

}
