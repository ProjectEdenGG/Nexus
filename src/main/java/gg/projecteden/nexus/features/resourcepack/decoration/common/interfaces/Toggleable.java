package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationCooldown;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.debug;

public interface Toggleable extends Interactable {

	CustomMaterial getToggledMaterial();

	default boolean tryToggle(Player player, Block block, ItemFrame itemFrame) {
		if (!Nullables.isNullOrAir(ItemUtils.getTool(player)))
			return false;

		if (itemFrame == null)
			return false;

		ItemStack item = itemFrame.getItem();
		if (Nullables.isNullOrAir(item))
			return false;

		CustomMaterial toggledMaterial = getToggledMaterial();
		if (toggledMaterial == null)
			return false;

		Decoration decoration = new Decoration((DecorationConfig) this, itemFrame);
		if (!decoration.canEdit(player)) {
			if (DecorationCooldown.LOCKED.isOnCooldown(player, TickTime.SECOND.x(2)))
				DecorationError.LOCKED.send(player);
			debug(player, "locked decoration (interact)");

			return false;
		}

		ItemBuilder itemBuilder = new ItemBuilder(item);
		itemBuilder.material(toggledMaterial);
		itemBuilder.resetName();

		itemFrame.setItem(itemBuilder.build(), false);
		playToggledSound(block);
		return true;
	}

	default void playToggledSound(Block origin) {
	}
}
