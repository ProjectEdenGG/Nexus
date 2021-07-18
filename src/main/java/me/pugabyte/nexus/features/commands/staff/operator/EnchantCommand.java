package me.pugabyte.nexus.features.commands.staff.operator;

import lombok.NonNull;
import me.pugabyte.nexus.features.customenchants.CustomEnchants;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Switch;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@Aliases("ench")
@Permission("group.seniorstaff")
public class EnchantCommand extends CustomCommand {

	public EnchantCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<enchant> [level]")
	void run(Enchantment enchantment, @Arg("1") int level, @Switch @Arg("true") boolean unsafe) {
		if (level < 1)
			remove(enchantment);
		else {
			final ItemStack tool = getToolRequired();
			if (unsafe)
				tool.addUnsafeEnchantment(enchantment, level);
			else
				try {
					tool.addEnchantment(enchantment, level);
				} catch (IllegalArgumentException ex) {
					throw new InvalidInputException(ex.getMessage());
				}

			CustomEnchants.update(tool);
			send(PREFIX + "Added enchant " + camelCase(enchantment.getKey().getKey()) + " " + level);
		}
	}

	@Path("remove <enchant>")
	void remove(Enchantment enchantment) {
		final ItemStack tool = getToolRequired();
		tool.removeEnchantment(enchantment);
		CustomEnchants.update(tool);
		send(PREFIX + "Removed enchant " + camelCase(enchantment.getKey().getKey()));
	}

	@Path("get <enchant>")
	void get(Enchantment enchantment) {
		send(PREFIX + "Level of " + camelCase(enchantment.getKey().getKey()) + ": " + getToolRequired().getEnchantmentLevel(enchantment));
	}

}
