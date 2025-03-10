package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Debug.DebugType;
import lombok.NonNull;

@Permission(Group.ADMIN)
public class DebugCommand extends CustomCommand {

	public DebugCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[state]")
	void toggle(Boolean state) {
		if (state == null)
			state = !Debug.isEnabled();

		Debug.setEnabled(state);
		send(PREFIX + "Debug " + (Debug.isEnabled() ? "&aenabled" : "&cdisabled"));
	}

	@Path("type <type> [state]")
	void type(DebugType type, Boolean state) {
		if (state == null)
			state = !Debug.isEnabled(type);

		Debug.setEnabled(type, state);
		send(PREFIX + "Debug " + camelCase(type.name()) + " " + (state ? "&aenabled" : "&cdisabled"));
	}
}
