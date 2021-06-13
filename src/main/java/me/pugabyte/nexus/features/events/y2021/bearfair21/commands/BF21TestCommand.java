package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.PugmasIsland;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PacketUtils;
import me.pugabyte.nexus.utils.Tasks;
import net.minecraft.server.v1_16_R3.EntityFallingBlock;
import org.bukkit.block.Block;

@Permission("group.admin")
public class BF21TestCommand extends CustomCommand {

	public BF21TestCommand(CommandEvent event) {
		super(event);
	}

	@Path("beacon")
	void beacon(){

	}
}
