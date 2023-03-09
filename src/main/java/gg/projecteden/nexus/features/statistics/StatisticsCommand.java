package gg.projecteden.nexus.features.statistics;

import gg.projecteden.nexus.features.statistics.StatisticsMenu.StatsMenus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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

	@Path("[player]")
	@Description("Open the statistics menu")
	void check(@Arg("self") OfflinePlayer player) {
		new StatisticsMenuProvider(StatsMenus.MAIN, player).open(player(), 0);
	}

}
