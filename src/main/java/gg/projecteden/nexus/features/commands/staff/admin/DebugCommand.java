package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Debug.DebugType;
import lombok.NonNull;
import org.bukkit.entity.Player;

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

	@Path("type <type> [state] [player]")
	void type(DebugType type, Boolean state, Player player) {
		if (player == null) {
			if (state == null)
				state = !Debug.isEnabled(type);

			Debug.setEnabled(type, state);
			send(PREFIX + "Debug " + camelCase(type.name()) + " " + (state ? "&aenabled" : "&cdisabled"));
			return;
		}

		if (state == null)
			state = !Debug.isEnabled(player, type);

		Debug.setEnabled(player, type, state);
		send(PREFIX + "Debug " + camelCase(type.name()) + " " + (state ? "&aenabled" : "&cdisabled") + " &3for &e" + Nickname.of(player));
	}
}
