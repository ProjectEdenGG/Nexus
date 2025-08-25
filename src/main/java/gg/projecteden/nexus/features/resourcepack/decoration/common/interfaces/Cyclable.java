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
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Cyclable extends Interactable, MultiState {

	ItemModelType getNextItemModel();

	ItemModelType getPreviousItemModel();

	default boolean tryCycle(Player player, Block soundOrigin, ItemFrame itemFrame) {
		if (!Nullables.isNullOrAir(ItemUtils.getTool(player)))
			return false;

		if (itemFrame == null)
			return false;

		ItemStack item = itemFrame.getItem();
		if (Nullables.isNullOrAir(item))
			return false;

		ItemModelType itemModelType = getNextItemModel();
		if (player.isSneaking())
			itemModelType = getPreviousItemModel();

		if (itemModelType == null)
			return false;

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

		if (soundOrigin != null)
			playSound(soundOrigin);

		return true;
	}

	default void playSound(@NonNull Block origin) {
	}
}
