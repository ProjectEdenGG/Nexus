package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

@Permission(Group.STAFF)
public class SkullCommand extends CustomCommand {

	public SkullCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[owner]")
	void run(@Arg("self") OfflinePlayer owner) {
		PlayerUtils.giveItem(player(), new ItemBuilder(Material.PLAYER_HEAD).name("&fSkull of " + owner.getName()).skullOwner(owner).build());
	}

}
