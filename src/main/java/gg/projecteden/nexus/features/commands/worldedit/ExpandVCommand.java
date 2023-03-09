package gg.projecteden.nexus.features.commands.worldedit;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldEditUtils;
import org.bukkit.entity.Player;

@DoubleSlash
@Permission("worldedit.wand")
public class ExpandVCommand extends CustomCommand {

	public ExpandVCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	@Description("Expand your selection vertically")
	void expandV(@Arg("1") int amount) {
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

