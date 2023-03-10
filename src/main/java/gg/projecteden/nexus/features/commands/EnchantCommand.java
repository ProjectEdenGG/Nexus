package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.customenchants.OldCEConverter;
import gg.projecteden.nexus.features.survival.MendingIntegrity;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.NonNull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

@Aliases("ench")
@Permission("enchant.use")
@WikiConfig(rank = "Guest", feature = "Creative")
public class EnchantCommand extends CustomCommand {

	public EnchantCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<enchant> [level] [--unsafe]")
	@Description("Enchant the item you are holding")
	void run(Enchantment enchantment, @Arg(value = "1", max = 127, minMaxBypass = Group.ADMIN) int level, @Switch @Arg("true") boolean unsafe) {
		if (level < 1) {
			remove(enchantment);
			return;
		}

		try {
			final ItemStack tool = getToolRequired();
			final ItemMeta meta = tool.getItemMeta();

			if (meta instanceof EnchantmentStorageMeta storageMeta)
				storageMeta.addStoredEnchant(enchantment, level, unsafe);
			else
				meta.addEnchant(enchantment, level, unsafe);

			tool.setItemMeta(meta);

			if (enchantment.equals(Enchantment.MENDING))
				MendingIntegrity.setMaxIntegrity(tool);

			ItemUtils.update(tool, player());

			send(PREFIX + "Added enchant &e" + camelCase(enchantment.getKey().getKey()) + " " + level);
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException(ex.getMessage());
		}
	}

	@Path("remove <enchant>")
	@Description("Remove an enchant from the item you are holding")
	void remove(Enchantment enchantment) {
		final ItemStack tool = getToolRequired();
		tool.removeEnchantment(enchantment);

		if (enchantment.equals(Enchantment.MENDING))
			MendingIntegrity.removeIntegrity(tool);

		ItemUtils.update(tool, player());

		send(PREFIX + "Removed enchant &e" + camelCase(enchantment.getKey().getKey()));
	}

	@Path("get <enchant>")
	@Description("View the level an enchant on the item you are holding")
	void get(Enchantment enchantment) {
		send(PREFIX + "Level of &e" + camelCase(enchantment.getKey().getKey()) + "&3: &e" + getToolRequired().getEnchantmentLevel(enchantment));
	}

	@Path("convertEnchants")
	@Permission(Group.ADMIN)
	@Description("Convert old custom enchants")
	void convertEnchants() {
		OldCEConverter.convertItem(getToolRequired());
	}

}
