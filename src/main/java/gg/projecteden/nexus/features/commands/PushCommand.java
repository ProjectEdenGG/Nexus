package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import lombok.Getter;
import lombok.SneakyThrows;

public class PushCommand extends CustomCommand {
	@Getter
	private static String perm = "stoppushing.allow";

	public PushCommand(CommandEvent event) {
		super(event);
	}

	@Path("[enable]")
	void toggle(Boolean enable) {
		if (enable == null) enable = !player().hasPermission(perm);
		push(enable);
	}

	@SneakyThrows
	void push(boolean enable) {
		if (enable) {
			PermissionChange.set().player(player()).permission(perm).run();
			send("&ePushing will be turned &aon&e shortly.");
		} else {
			PermissionChange.set().player(player()).permission(perm).value(false).run();
			send("&ePushing will be turned &coff&e shortly.");
		}
	}

}
