package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationCooldown;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration.DecorationEditType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Cyclable extends Interactable, MultiState {

	ItemModelType getNextItemModel(Player player, ItemStack tool);

	ItemModelType getPreviousItemModel(Player player, ItemStack tool);

	// Defaults to require nothing in hand
	default boolean canCycle(Player player, ItemStack tool) {
		return Nullables.isNullOrAir(tool);
	}

	default boolean canCycleToPrevious() {
		return true;
	}

	default boolean tryCycle(Player player, Block soundOrigin, ItemFrame itemFrame) {
		if (itemFrame == null)
			return false;

		ItemStack item = itemFrame.getItem();
		if (Nullables.isNullOrAir(item))
			return false;

		ItemStack tool = ItemUtils.getTool(player);
		if (!canCycle(player, tool))
			return false;

		ItemModelType itemModelType = getNextItemModel(player, tool);
		if (canCycleToPrevious() && player.isSneaking())
			itemModelType = getPreviousItemModel(player, tool);

		if (itemModelType == null)
			return false;

		if (!setDecoration(player, itemFrame, item, itemModelType))
			return false;

		if (soundOrigin != null)
			playSound(soundOrigin);

		return true;
	}

	default boolean setDecoration(Player player, ItemFrame itemFrame, ItemStack item, ItemModelType itemModelType) {
		Decoration decoration = new Decoration((DecorationConfig) this, itemFrame);
		if (!decoration.canEdit(player, DecorationEditType.INTERACT)) {
			if (!DecorationCooldown.LOCKED.isOnCooldown(player, TickTime.SECOND.x(2)))
				DecorationError.LOCKED.send(player);
			DecorationLang.debug(player, "locked decoration (interact)");

			return false;
		}

		ItemBuilder itemBuilder = new ItemBuilder(item);
		itemBuilder.material(itemModelType);
		itemBuilder.resetName();

		itemFrame.setItem(itemBuilder.build(), false);
		return true;
	}

	default void playSound(@NonNull Block origin) {
	}
}
