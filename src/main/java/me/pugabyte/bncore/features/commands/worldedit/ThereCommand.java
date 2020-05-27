package me.pugabyte.bncore.features.commands.worldedit;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldEditUtils;

@DoubleSlash
@Permission("worldedit.wand")
public class ThereCommand extends CustomCommand {

	public ThereCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void there(@Arg("0") int amount) {
		Player worldEditPlayer = WorldEditUtils.getPlugin().wrapPlayer(player());
		BlockVector3 pos1 = worldEditPlayer.getBlockTrace(300).toBlockPoint();
		BlockVector3 pos2 = worldEditPlayer.getBlockTrace(300).toBlockPoint();
		new WorldEditUtils(player()).setSelection(player(), pos1, pos2);
		ExpandAllCommand.expandAll(player(), amount);
	}
}

