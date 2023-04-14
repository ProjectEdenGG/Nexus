package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Permission(Group.SENIOR_STAFF)
public class FixCommand extends CustomCommand {

	public FixCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
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

	@Description("Repairs all items in a players inventory")
	void all() {
		for (ItemStack item : player().getInventory().getContents())
			if (!isNullOrAir(item))
				fix(item);
		send(PREFIX + "All items repaired");
	}

	private void fix(ItemStack item) {
		if (!(item.getItemMeta() instanceof Damageable damageable))
			return;

		damageable.setDamage(0);
		item.setItemMeta(damageable);
	}

}
