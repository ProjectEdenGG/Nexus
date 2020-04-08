package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;

@Aliases("fgb")
@NoArgsConstructor
public class FixGhostBlocksCommand extends CustomCommand implements Listener {

	public FixGhostBlocksCommand(CommandEvent event) {
		super(event);
	}

	@Path("[radius]")
	void run(@Arg("10") int radius) {
		if (radius > 15)
			error("Max allowed radius is 15");

		fixGhostBlocks(player(), player().getLocation(), radius);
		send("&eAll ghost blocks within " + radius + " blocks updated");
	}

	private void fixGhostBlocks(Player player, Location location, int radius) {
		Utils.getBlocksInRadius(location, radius).forEach(block ->
				player.sendBlockChange(block.getLocation(), block.getBlockData()));
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (Arrays.asList(WorldGroup.CREATIVE, WorldGroup.MINIGAMES).contains(WorldGroup.get(player)))
			return;

		Tasks.wait(1, () -> {
			fixGhostBlocks(player, event.getBlock().getLocation(), 3);
			if (Utils.getPing(player) > 100)
				Tasks.wait(3, () -> fixGhostBlocks(player, event.getBlock().getLocation(), 3));
		});
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (Utils.getPing(event.getPlayer()) > 100)
			Tasks.wait(3, () -> fixGhostBlocks(event.getPlayer(), event.getBlock().getLocation(), 2));
	}

}
