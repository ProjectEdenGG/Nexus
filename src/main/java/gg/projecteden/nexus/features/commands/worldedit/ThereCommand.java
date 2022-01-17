package gg.projecteden.nexus.features.commands.worldedit;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldEditUtils;

@DoubleSlash
@Permission("worldedit.wand")
public class ThereCommand extends CustomCommand {

	public ThereCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void there(@Arg("0") int amount) {
		Player worldEditPlayer = WorldEditUtils.getPlugin().wrapPlayer(player());
		BlockVector3 pos1 = worldEditPlayer.getBlockTrace(300).toVector().toBlockPoint();
		BlockVector3 pos2 = worldEditPlayer.getBlockTrace(300).toVector().toBlockPoint();
		new WorldEditUtils(player()).setSelection(player(), pos1, pos2);
		ExpandAllCommand.expandAll(player(), amount);
	}
}

