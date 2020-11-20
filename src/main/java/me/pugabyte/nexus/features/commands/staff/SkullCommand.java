package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

@Permission("group.staff")
public class SkullCommand extends CustomCommand {

	public SkullCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<owner>")
	void run(@Arg(tabCompleter = OfflinePlayer.class) String owner) {
		ItemUtils.giveItem(player(), new ItemBuilder(Material.PLAYER_HEAD).name("&fSkull of " + owner).skullOwner(owner).build());
	}

}
