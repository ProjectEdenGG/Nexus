package me.pugabyte.nexus.features.crates;

import me.pugabyte.nexus.features.crates.menus.CrateEditMenu;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

@Permission("group.admin")
public class CrateCommand extends CustomCommand {

	public CrateCommand(CommandEvent event) {
		super(event);
	}

	@Path("<type> [amount]")
	void key(CrateType type, @Arg("1") Integer amount) {
		ItemStack key = type.getKey();
		key.setAmount(amount);
		ItemUtils.giveItem(player(), key);
	}

	@Path("edit")
	void edit() {
		CrateEditMenu.getMenu(CrateType.ALL, null).open(player());
	}

}
