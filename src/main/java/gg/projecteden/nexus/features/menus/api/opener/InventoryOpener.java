/*
 * Copyright 2018-2020 Isaac Montagne
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gg.projecteden.nexus.features.menus.api.opener;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public interface InventoryOpener {

	Inventory open(SmartInventory inv, Player player);

	boolean supports(InventoryType type);

	default void fill(Inventory inventory, InventoryContents contents, Player player) {
		ClickableItem[][] items = contents.all();

		for (int row = 0; row < items.length; row++) {
			for (int column = 0; column < items[row].length; column++) {
				if (items[row][column] != null)
					inventory.setItem(9 * row + column, items[row][column].getItem(player));
			}
		}
	}

	/**
	 * This method is used to configure the default inventory size(s)
	 * for inventories supported by this opener. These values will only
	 * be applied if the size is not set explicitly. (See {@link SmartInventory.Builder#size(int, int)}).
	 * <p>
	 * This method must return a non-null value for all supported inventory types.
	 *
	 * @param type inventory type
	 * @return The desired default dimensions, this default implementation returns
	 * (3x9) for type (ender)chest, (3x3) for dispenser & dropper and
	 * (1x_sizeOfInventoryType_) for everything else.
	 */
	default SlotPos defaultSize(InventoryType type) {
		return switch (type) {
			case CHEST, ENDER_CHEST -> SlotPos.of(3, 9);
			case DISPENSER, DROPPER -> SlotPos.of(3, 3);
			default -> SlotPos.of(1, type.getDefaultSize());
		};
	}

}
