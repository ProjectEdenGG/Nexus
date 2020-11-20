package me.pugabyte.nexus.features.commands.worldedit;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldEditUtils;
import org.bukkit.entity.Player;

@DoubleSlash
@Permission("worldedit.wand")
public class ExpandHCommand extends CustomCommand {

	public ExpandHCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void expandH(@Arg("1") int amount) {
		expandH(player(), amount);
	}

	public static void expandH(Player player, int amount) {
		new WorldEditUtils(player).changeSelection(
				player,
				WorldEditUtils.SelectionChangeType.EXPAND,
				WorldEditUtils.SelectionChangeDirectionType.HORIZONTAL,
				amount
		);
	}

}

