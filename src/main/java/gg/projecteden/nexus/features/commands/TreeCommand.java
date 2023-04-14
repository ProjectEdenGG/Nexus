package gg.projecteden.nexus.features.commands;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.TreeGenerator;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.NonNull;
import org.bukkit.Location;

@Permission(Group.STAFF)
public class TreeCommand extends CustomCommand {

	public TreeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[type]")
	@Description("Spawn a tree at the block you are looking at")
	void run(@Optional("tree") TreeGenerator.TreeType treeType) {
		final WorldEditUtils worldedit = new WorldEditUtils(player());
		final BukkitPlayer worldeditPlayer = worldedit.getPlayer(player());
		final LocalSession session = worldeditPlayer.getSession();

		try (EditSession editSession = session.createEditSession(worldeditPlayer)) {
			Location location = getTargetBlockRequired().getLocation().add(0, 1, 0);
			final BlockVector3 pos = Vector3.toBlockPoint(location.getX(), location.getY(), location.getZ());
			boolean successful = false;
			for (int i = 0; i < 10; i++) {
				if (treeType.generate(editSession, pos)) {
					successful = true;
					break;
				}
			}

			if (!successful)
				worldeditPlayer.print(Caption.of("worldedit.tool.tree.obstructed"));

			session.remember(editSession);
		}
	}

}
