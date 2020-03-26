package me.pugabyte.bncore.features.store.perks;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.inventory.ItemStack;

@Aliases("nameitem")
@Permission("itemname.use")
public class ItemNameCommand extends CustomCommand {

	public ItemNameCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("(null|none|reset)")
	void name() {
		name(null);
	}

	@Path("<name...>")
	void name(String name) {
		ItemStack item = player().getInventory().getItemInMainHand();
		if (Utils.isNullOrAir(item))
			error("You are not holding an item");
		ItemBuilder.setName(item, name);
	}

}
