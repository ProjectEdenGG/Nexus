package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.protection.ProtectionCompatibility;
import gg.projecteden.nexus.utils.protection.ProtectionUtils;

@Permission(Group.STAFF)
public class ProtectionUtilsCommand extends CustomCommand {

	public ProtectionUtilsCommand(CommandEvent event) {
		super(event);
	}

	@Path("listCompatibilities")
	@Description("Lists the loaded protection compatibilities")
	void listCompats() {
		var compats = ProtectionUtils.getCompatibilities();
		if (compats.isEmpty())
			error("No compatibilities found");

		send(PREFIX + "Loaded compatibilities:");
		for (ProtectionCompatibility compat : compats) {
			send(" &3- &e" + compat.getPlugin().getName());
		}
	}

	@Path("canBuild")
	@Description("Tests if you can build at your location")
	void canBuild() {
		var result = ProtectionUtils._canBuild(player(), location());
		var reason = result.getSecond();
		String pluginName = reason == null ? "" : " &3from &e" + reason;
		send(PREFIX + "canBuild = " + StringUtils.bool(result.getFirst()) + pluginName);
	}

	@Path("canBreak")
	@Description("Tests if you can break at your location")
	void canBreak() {
		var result = ProtectionUtils._canBreak(player(), location());
		var reason = result.getSecond();
		String pluginName = reason == null ? "" : " &3from &e" + reason;
		send(PREFIX + "canBreak = " + StringUtils.bool(result.getFirst()) + pluginName);
	}

	@Path("canInteract")
	@Description("Tests if you can interact at your location")
	void canInteract() {
		var result = ProtectionUtils._canInteract(player(), location());
		var reason = result.getSecond();
		String pluginName = reason == null ? "" : " &3from &e" + reason;
		send(PREFIX + "canInteract = " + StringUtils.bool(result.getFirst()) + pluginName);
	}

	@Path("canUse")
	@Description("Tests if you can use at your location")
	void canUse() {
		var result = ProtectionUtils._canUse(player(), location());
		var reason = result.getSecond();
		String pluginName = reason == null ? "" : " &3from &e" + reason;
		send(PREFIX + "canUse = " + StringUtils.bool(result.getFirst()) + pluginName);
	}
}
