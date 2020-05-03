package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.BlockUtils;
import me.pugabyte.bncore.utils.MaterialTag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.Set;

@Aliases("swl")
@NoArgsConstructor
public class SidewaysLogsCommand extends CustomCommand implements Listener {
	private static Set<Player> enabledPlayers = new HashSet<>();

	SidewaysLogsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void toggle() {
		toggle(enabledPlayers.contains(player()));
	}

	@Path("<true|false>")
	void toggle(boolean normal) {
		if (normal) {
			enabledPlayers.remove(player());
			send(PREFIX + "Now placing logs normally.");
		} else {
			enabledPlayers.add(player());
			send(PREFIX + "Now placing logs vertically only.");
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		Player player = event.getPlayer();

		if (!MaterialTag.LOGS.isTagged(block.getType())) return;
		if (!enabledPlayers.contains(player)) return;

		BlockUtils.updateBlockProperty(block, "axis", "y");

	}
}
