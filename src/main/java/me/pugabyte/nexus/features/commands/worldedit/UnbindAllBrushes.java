package me.pugabyte.nexus.features.commands.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldEditUtils;

@DoubleSlash
@Redirect(from = "//brush none all", to = "//unbindallbrushes")
public class UnbindAllBrushes extends CustomCommand {

	public UnbindAllBrushes(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void unbind() {
		LocalSession session = WorldEditUtils.getPlugin().getSession(player());
		for (ItemType item : ItemTypes.values())
			if (!item.hasBlockType())
				session.setTool(item, null);

		send("&8(&4&lFAWE&8) &dAll tools unbound");
	}
}
