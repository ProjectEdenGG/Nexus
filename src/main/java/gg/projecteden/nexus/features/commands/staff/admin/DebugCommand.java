package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Debug.DebugType;
import lombok.NonNull;

public class DebugCommand extends CustomCommand {

	public DebugCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[state]")
	void toggle(Boolean state) {
		if (state == null)
			state = !Debug.isEnabled();

		Debug.setDebug(state);
		send(PREFIX + "Debug " + (Debug.isEnabled() ? "&aenabled" : "&cdisabled"));
	}

	@Path("type <type> [state]")
	void type(DebugType type, Boolean state) {
		if (state == null)
			state = !Debug.isEnabled(type);

		if (state)
			Debug.enable(type);
		else
			Debug.disable(type);

		send(PREFIX + "Debug " + camelCase(type.name()) + " " + (state ? "&aenabled" : "&cdisabled"));
	}
}
