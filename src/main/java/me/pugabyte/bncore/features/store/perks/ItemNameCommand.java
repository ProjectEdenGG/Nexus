package me.pugabyte.bncore.features.store.perks;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Aliases("nameitem")
@Permission("itemname.use")
public class ItemNameCommand extends CustomCommand {

	public ItemNameCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("(null|none|reset)")
	void name() {
		rename(null);
	}

	@Path("<name...>")
	void name(String name) {
		rename(name);
	}

	private void rename(String name) {
		ItemStack itemInMainHand = player().getInventory().getItemInMainHand();
		ItemMeta itemMeta = itemInMainHand.getItemMeta();
		itemMeta.setDisplayName(name);
		itemInMainHand.setItemMeta(itemMeta);
	}

}
