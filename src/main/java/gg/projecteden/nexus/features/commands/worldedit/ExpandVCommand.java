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
public class ExpandVCommand extends CustomCommand {

	public ExpandVCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Expand your selection vertically")
	void expandV(@Optional("1") int amount) {
		expandV(player(), amount);
	}

	public static void expandV(Player player, int amount) {
		new WorldEditUtils(player).changeSelection(
				player,
				WorldEditUtils.SelectionChangeType.EXPAND,
				WorldEditUtils.SelectionChangeDirectionType.VERTICAL,
				amount
		);
	}

}

