package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Nullables;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

@Permission(Group.SENIOR_STAFF)
public class FixCommand extends CustomCommand {

	public FixCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Repairs held item")
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
	@Description("Repairs all items in a players inventory")
	void all() {
		for (ItemStack item : player().getInventory().getContents())
			if (!Nullables.isNullOrAir(item))
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
