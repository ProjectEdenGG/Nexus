package gg.projecteden.nexus.features.commands.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.block.BlockTypes;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
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
@Description("Delete a village and replace with flat world terrain")
public class DelVillageCommand extends CustomCommand {

	public DelVillageCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path
	void delVillage() {
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		LocalSession session = worldEditPlugin.getSession(player());
		WorldEditUtils worldedit = new WorldEditUtils(player());
		worldedit.setSelection(player(), location());
		ExpandAllCommand.expandAll(player(), 75);
		worldedit.set(worldedit.getPlayerSelection(player()), BlockTypes.AIR);
		worldedit.fixFlat(session, worldedit.getPlayerSelection(player()));
		send("&3Successfully removed the village");
	}
}
