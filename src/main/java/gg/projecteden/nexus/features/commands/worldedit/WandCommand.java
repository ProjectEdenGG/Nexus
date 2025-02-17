package gg.projecteden.nexus.features.commands.worldedit;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

@DoubleSlash
@Permission("worldedit.wand")
@SuppressWarnings("UnstableApiUsage")
public class WandCommand extends CustomCommand {
	private static final AttributeModifier MODIFIER = new AttributeModifier(
		new NamespacedKey(Nexus.getInstance(),
		"extended-range-wand"),
		64,
		Operation.ADD_NUMBER,
		EquipmentSlotGroup.HAND
	);

	public WandCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void wand() {
		var existing = getTool();
		player().getInventory().setItemInMainHand(getExtendedWand());
		PlayerUtils.giveItem(player(), existing);
		send("&8(&4&lFAWE&8) &7Left click: select pos #1; Right click: select pos #2");
	}

	private static ItemStack getExtendedWand() {
		return new ItemBuilder(Material.WOODEN_AXE).attribute(Attribute.BLOCK_INTERACTION_RANGE, MODIFIER).build();
	}
}
