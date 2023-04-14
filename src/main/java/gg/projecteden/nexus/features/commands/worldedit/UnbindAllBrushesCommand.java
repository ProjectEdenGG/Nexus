package gg.projecteden.nexus.features.commands.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.DoubleSlash;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.NonNull;

@DoubleSlash
@Redirect(from = "//brush none all", to = "//unbindallbrushes")
public class UnbindAllBrushesCommand extends CustomCommand {

	public UnbindAllBrushesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Unbind all brushes from all tools")
	void unbind() {
		LocalSession session = WorldEditUtils.getPlugin().getSession(player());
		for (ItemType item : ItemTypes.values())
			if (!item.hasBlockType())
				session.setTool(item, null);

		send("&8(&4&lFAWE&8) &dAll tools unbound");
	}
}
