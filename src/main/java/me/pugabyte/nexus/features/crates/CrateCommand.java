package me.pugabyte.nexus.features.crates;

import me.pugabyte.nexus.features.crates.menus.CrateEditMenu;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.OfflinePlayer;

@Permission("group.admin")
public class CrateCommand extends CustomCommand {

	public CrateCommand(CommandEvent event) {
		super(event);
	}

	@Path("give <type> [player] [amount]")
	void key(CrateType type, @Arg("self") OfflinePlayer player, @Arg("1") Integer amount) {
		type.give(player, amount);
		if (player.isOnline())
			send(player.getPlayer(), Crates.PREFIX + "You have been given &e" + amount + " " + StringUtils.camelCase(type.name()) +
					" Crate Key" + (amount == 1 ? "" : "s"));
		if (!isSelf(player))
			send(Crates.PREFIX + "You gave &e" + amount + " " + StringUtils.camelCase(type.name()) + " Crate Key" +
					(amount == 1 ? "" : "s") + "  &3to &e" + player.getName());
	}

	@Path("edit")
	void edit() {
		CrateEditMenu.getMenu(CrateType.ALL, null).open(player());
	}

}
