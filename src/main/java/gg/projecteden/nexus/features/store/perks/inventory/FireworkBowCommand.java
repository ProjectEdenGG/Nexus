package gg.projecteden.nexus.features.store.perks.inventory;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@WikiConfig(rank = "Store", feature = "Inventory")
public class FireworkBowCommand extends CustomCommand {

	public FireworkBowCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Create a firework bow")
	void add() {
		if (!(hasPermission("fireworkbow.single") || hasPermission("fireworkbow.infinite")))
			permissionError();

		final ItemStack tool = getToolRequired(Material.BOW);
		if (tool.getItemMeta().hasEnchant(Enchant.FIREWORK))
			error("That bow is already enchanted with Firework");

		tool.addEnchantment(Enchant.FIREWORK, 1);

		if (player().hasPermission("fireworkbow.single")) {
			send(PREFIX + "You have created your one firework bow! If you lose this bow, you won't be able to get another unless you purchase the command again.");
			PermissionChange.unset().uuid(uuid()).permissions("fireworkbow.single").runAsync();
		}
	}

	@Path("remove")
	@Description("Remove the firework enchantment from a bow")
	void remove() {
		final ItemStack tool = getToolRequired(Material.BOW);
		if (!tool.getItemMeta().hasEnchant(Enchant.FIREWORK))
			error("That bow is not enchanted with Firework");

		tool.removeEnchantment(Enchant.FIREWORK);

		send(PREFIX + "Enchantment removed");
	}

}
