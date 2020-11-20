package me.pugabyte.nexus.features.commands.worldedit;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldEditUtils;

@DoubleSlash
@Permission("worldedit.wand")
public class ThereVCommand extends CustomCommand {

	public ThereVCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void there(@Arg("0") int amount) {
		Player worldEditPlayer = WorldEditUtils.getPlugin().wrapPlayer(player());
		BlockVector3 pos1 = worldEditPlayer.getBlockTrace(300).toBlockPoint();
		BlockVector3 pos2 = worldEditPlayer.getBlockTrace(300).toBlockPoint();
		new WorldEditUtils(player()).setSelection(player(), pos1, pos2);
		ExpandVCommand.expandV(player(), amount);
	}
}

