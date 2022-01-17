package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;

@Aliases("fgb")
@NoArgsConstructor
@Description("Request block updates from the server to fix nearby \"ghost\" blocks")
public class FixGhostBlocksCommand extends CustomCommand implements Listener {

	public FixGhostBlocksCommand(CommandEvent event) {
		super(event);
	}

	@Path("[radius]")
	void run(@Arg("10") int radius) {
		if (radius > 15)
			error("Max allowed radius is 15");

		fixGhostBlocks(player(), location(), radius);
		send("&eAll ghost blocks within " + radius + " blocks updated");
	}

	private void fixGhostBlocks(Player player, Location location, int radius) {
		BlockUtils.getBlocksInRadius(location, radius).forEach(block ->
				player.sendBlockChange(block.getLocation(), block.getBlockData()));
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (Arrays.asList(WorldGroup.CREATIVE, WorldGroup.MINIGAMES).contains(WorldGroup.of(player)))
			return;

		Tasks.waitAsync(1, () -> {
			fixGhostBlocks(player, event.getBlock().getLocation(), 3);
			if (player.getPing() > 100)
				Tasks.waitAsync(3, () -> fixGhostBlocks(player, event.getBlock().getLocation(), 3));
		});
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getPing() > 100)
			Tasks.waitAsync(3, () -> fixGhostBlocks(event.getPlayer(), event.getBlock().getLocation(), 2));
	}

}
