package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.striplogs.StripLogs;
import me.pugabyte.nexus.models.striplogs.StripLogs.Behavior;
import me.pugabyte.nexus.models.striplogs.StripLogsService;
import me.pugabyte.nexus.utils.MaterialTag;
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

	@Path("<behavior>")
	void behavior(Behavior behavior) {
		stripLogs.setBehavior(behavior);
		service.save(stripLogs);

		send(PREFIX + "Behavior set to " + camelCase(behavior));
	}

	@EventHandler
	public void onStripLogs(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		Material material = event.getBlockPlaced().getType();
		if (!MaterialTag.STRIPPED_LOGS.isTagged(material))
			return;

		StripLogs stripLogs = new StripLogsService().get(player);
		switch (stripLogs.getBehavior()) {
			case REQUIRE_SHIFT:
				if (!event.getPlayer().isSneaking())
					event.setCancelled(true);
				break;
			case CANCEL:
				event.setCancelled(true);
				break;
		}
	}

}
