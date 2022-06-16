package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.api.common.utils.StringUtils;
import lombok.NonNull;

@Permission(Group.ADMIN)
public class DecorationCommand extends CustomCommand {

	public DecorationCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("get <type>")
	void get(DecorationType type) {
		giveItem(type.getItem());
		send("Given " + StringUtils.camelCase(type));
	}

	@Path("debug [enabled]")
	void debug(Boolean enabled) {
		if (enabled == null)
			enabled = !DecorationUtils.debug;

		DecorationUtils.debug = enabled;
		send("debug " + (enabled ? "&aEnabled" : "&cDisabled"));
	}
}
