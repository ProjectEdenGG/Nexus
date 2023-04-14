package gg.projecteden.nexus.features.commands.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.DoubleSlash;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

@DoubleSlash
@Permission(Group.STAFF)
public class FixFlatCommand extends CustomCommand {

	public FixFlatCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@NoLiterals
	@Description("Replace the terrain in your selection with flat world terrain")
	void fixFlat() {
		World world = new BukkitWorld(world());
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		LocalSession session = worldEditPlugin.getSession(player());
		new WorldEditUtils(player()).fixFlat(session, session.getSelection(world));
	}
}

