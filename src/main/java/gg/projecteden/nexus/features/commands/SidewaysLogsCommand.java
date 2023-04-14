package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.blockorientation.BlockOrientationUser;
import gg.projecteden.nexus.models.blockorientation.BlockOrientationUser.Logs;
import gg.projecteden.nexus.models.blockorientation.BlockOrientationUserService;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.NoArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

@Aliases("swl")
@NoArgsConstructor
public class SidewaysLogsCommand extends CustomCommand implements Listener {
	private static final BlockOrientationUserService service = new BlockOrientationUserService();
	private BlockOrientationUser user;

	SidewaysLogsCommand(CommandEvent event) {
		super(event);
		user = service.get(player());
	}

	private Logs logs() {
		return user.getLogs();
	}

	@NoLiterals
	@Path("[state]")
	@Description("Toggle locking log placement to the vertical orientation")
	void toggle(Boolean normal) {
		if (normal == null)
			normal = !logs().isNormal();

		logs().setNormal(normal);
		service.save(user);
		send(PREFIX + "Now placing logs " + (normal ? "normally" : "vertically only"));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlockPlaced();

		if (!MaterialTag.LOGS.isTagged(block.getType()))
			return;

		if (new BlockOrientationUserService().get(player).getLogs().isNormal())
			return;

		BlockUtils.updateBlockProperty(block, "axis", "y");
	}
}
