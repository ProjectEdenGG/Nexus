package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;

@Permission("group.staff")
public class SkullCommand extends CustomCommand {

	public SkullCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<owner>")
	void run(String owner) {
		Utils.giveItem(player(), new ItemBuilder(Material.PLAYER_HEAD).name("&fSkull of " + owner).skullOwner(owner).build());
	}

}
