package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

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

		fix(item);
		send(PREFIX + "Item repaired");
	}

	@Path("all")
	void all() {
		for (ItemStack item : player().getInventory().getContents())
			if (!isNullOrAir(item))
				fix(item);
		send(PREFIX + "All items repaired");
	}

	private void fix(ItemStack item) {
		if (!(item.getItemMeta() instanceof Damageable))
			return;

		ItemMeta meta = item.getItemMeta();
		((Damageable) meta).setDamage(0);
		item.setItemMeta(meta);
	}

}