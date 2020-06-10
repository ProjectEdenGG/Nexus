package me.pugabyte.bncore.features.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.Utils.getTool;

@Aliases("ench")
@Permission("group.seniorstaff")
public class EnchantCommand extends CustomCommand {
	ItemStack item;

	public EnchantCommand(@NonNull CommandEvent event) {
		super(event);
		item = getTool(player());
		if (item == null)
			error("You are not holding anything");
	}

	@Path("<enchantment> [level]")
	void run(Enchantment enchantment, int level) {
		item.addUnsafeEnchantment(enchantment, level);
		send(PREFIX + "Added enchant " + camelCase(enchantment.getKey().getKey()) + " " + level);
	}

	@Path("remove <enchantment>")
	void run(Enchantment enchantment) {
		item.removeEnchantment(enchantment);
		send(PREFIX + "Removed enchant " + camelCase(enchantment.getKey().getKey()));
	}

}
