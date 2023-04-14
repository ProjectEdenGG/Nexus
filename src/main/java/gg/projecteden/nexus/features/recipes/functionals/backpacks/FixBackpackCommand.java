package gg.projecteden.nexus.features.recipes.functionals.backpacks;

import gg.projecteden.nexus.features.legacy.listeners.LegacyShulkerBoxes;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

@Permission(Permission.Group.ADMIN)
public class FixBackpackCommand extends CustomCommand {

	public FixBackpackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Add necessary NBT to a shulker box to make it a backpack")
	public void fix() {
		ItemStack item = player().getInventory().getItemInMainHand();
		if (item.getType() != CustomMaterial.BACKPACK.getMaterial()) return;
		player().getInventory().setItemInMainHand(new ItemBuilder(item)
				.nbt(nbt -> {
					nbt.removeKey(LegacyShulkerBoxes.NBT_KEY);
					nbt.setString(Backpacks.NBT_KEY, randomAlphabetic(10));
				})
				.build());
	}

}
