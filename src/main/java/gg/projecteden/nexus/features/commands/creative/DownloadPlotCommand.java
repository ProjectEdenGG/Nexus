package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;

@Permission("essentials.gamemode.creative")
@WikiConfig(rank = "Guest", feature = "Creative")
public class DownloadPlotCommand extends CustomCommand {

	public DownloadPlotCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Download your plot as a schematic")
	void run() {
		Discord.adminLog(nickname() + " has downloaded their plot as a schematic at " + StringUtils.xyzw(location()));
		runCommandAsOp("plot download");
	}
}
