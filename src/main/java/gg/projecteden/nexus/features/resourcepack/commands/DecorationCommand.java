package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.nexus.features.resourcepack.decoration.Decorations;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.utils.StringUtils;
import lombok.NonNull;

@Permission("group.admin")
public class DecorationCommand extends CustomCommand {

	public DecorationCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("get <decoration>")
	void get(Decorations decorations) {
		giveItem(decorations.getItem());
		send("Given " + StringUtils.camelCase(decorations));
	}
}
