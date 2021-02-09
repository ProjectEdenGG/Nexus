package me.pugabyte.nexus.features.crates;

import me.pugabyte.nexus.features.crates.menus.CrateEditMenu;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Permission("group.admin")
public class CrateCommand extends CustomCommand {

	public CrateCommand(CommandEvent event) {
		super(event);
	}

	@Path("give <type> [player] [amount]")
	void key(CrateType type, @Arg("self") OfflinePlayer player, @Arg("1") Integer amount) {
		type.give(player, amount);
	}

	@Path("edit")
	void edit() {
		CrateEditMenu.getMenu(CrateType.ALL, null).open(player());
	}

}
