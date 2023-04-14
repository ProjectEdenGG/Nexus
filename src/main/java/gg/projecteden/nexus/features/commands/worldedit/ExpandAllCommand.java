package gg.projecteden.nexus.features.commands.worldedit;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.DoubleSlash;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.WorldEditUtils;
import org.bukkit.entity.Player;

@DoubleSlash
@Permission("worldedit.wand")
public class ExpandAllCommand extends CustomCommand {

	public ExpandAllCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Expand your selection in every direction")
	void expandAll(@Optional("1") int amount) {
		expandAll(player(), amount);
	}

	public static void expandAll(Player player, int amount) {
		new WorldEditUtils(player).changeSelection(
				player,
				WorldEditUtils.SelectionChangeType.EXPAND,
				WorldEditUtils.SelectionChangeDirectionType.ALL,
				amount
		);
	}
}

