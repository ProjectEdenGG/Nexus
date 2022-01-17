package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.Material;

@Aliases("fw127")
@Permission(Group.ADMIN)
public class Firework127Command extends CustomCommand {

	public Firework127Command(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		PlayerUtils.giveItem(player(), new ItemBuilder(Material.FIREWORK_ROCKET).fireworkPower(127).build());
	}

}
