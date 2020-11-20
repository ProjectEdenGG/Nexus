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
public class ExpandVCommand extends CustomCommand {

	public ExpandVCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
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

