package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.striplogs.StripLogs;
import gg.projecteden.nexus.models.striplogs.StripLogs.Behavior;
import gg.projecteden.nexus.models.striplogs.StripLogsService;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

@NoArgsConstructor
public class StripLogsCommand extends CustomCommand implements Listener {
	private final StripLogsService service = new StripLogsService();
	private StripLogs stripLogs;

	public StripLogsCommand(CommandEvent event) {
		super(event);
		stripLogs = service.get(player());
	}

	@Path("behavior <behavior>")
	@Description("Change how you strip logs (default, require shift, or prevent)")
	void behavior(Behavior behavior) {
		stripLogs.setBehavior(behavior);
		service.save(stripLogs);

		send(PREFIX + "Behavior set to " + camelCase(behavior));
	}

	@EventHandler
	public void onStripLogs(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		Material before = event.getBlockReplacedState().getType();
		Material after = event.getBlockPlaced().getType();

		boolean stripping = false;
		if (MaterialTag.TREE_LOGS.isTagged(before) && MaterialTag.STRIPPED_LOGS.isTagged(after))
			stripping = true;
		if (MaterialTag.TREE_WOOD.isTagged(before) && MaterialTag.STRIPPED_WOOD.isTagged(after))
			stripping = true;

		if (!stripping)
			return;

		StripLogs stripLogs = new StripLogsService().get(player);
		switch (stripLogs.getBehavior()) {
			case REQUIRE_SHIFT:
				if (!event.getPlayer().isSneaking())
					event.setCancelled(true);
				break;
			case PREVENT:
				event.setCancelled(true);
				break;
		}
	}

}
