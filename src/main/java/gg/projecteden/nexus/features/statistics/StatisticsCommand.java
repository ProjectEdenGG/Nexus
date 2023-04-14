package gg.projecteden.nexus.features.statistics;

import gg.projecteden.nexus.features.statistics.StatisticsMenu.StatsMenus;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

@Aliases("stats")
public class StatisticsCommand extends CustomCommand {

	public static List<Material> blockCache = new ArrayList<>();

	public StatisticsCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[player]")
	@Description("Open the statistics menu")
	void check(@Optional("self") OfflinePlayer player) {
		new StatisticsMenuProvider(StatsMenus.MAIN, player).open(player(), 0);
	}

}
