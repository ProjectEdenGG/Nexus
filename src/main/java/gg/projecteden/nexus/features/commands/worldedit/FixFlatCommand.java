package gg.projecteden.nexus.features.commands.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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
	@Path
	@Description("Replace the terrain in your selection with flat world terrain")
	void fixFlat() {
		World world = new BukkitWorld(world());
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		LocalSession session = worldEditPlugin.getSession(player());
		new WorldEditUtils(player()).fixFlat(session, session.getSelection(world));
	}
}

