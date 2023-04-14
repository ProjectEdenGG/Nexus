package gg.projecteden.nexus.features.commands.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.block.BlockTypes;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.DoubleSlash;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

@DoubleSlash
@Permission(Group.STAFF)
public class DelVillageCommand extends CustomCommand {

	public DelVillageCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@SneakyThrows
	@Description("Delete a village and replace with flat world terrain")
	void run() {
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
