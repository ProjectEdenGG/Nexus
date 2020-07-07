package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

@Permission("group.seniorstaff")
public class FixCommand extends CustomCommand {

	public FixCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		ItemStack item = getToolRequired();

		if (!(item.getItemMeta() instanceof Damageable))
			error(item.getType().name() + " is not damageable");

		Damageable damage = (Damageable) item.getItemMeta();

		if (!damage.hasDamage())
			error(item.getType().name() + " is not damaged");

		ItemMeta meta = item.getItemMeta();
		((Damageable) meta).setDamage(0);
		item.setItemMeta(meta);
		send(PREFIX + "Item repaired");
	}

}
