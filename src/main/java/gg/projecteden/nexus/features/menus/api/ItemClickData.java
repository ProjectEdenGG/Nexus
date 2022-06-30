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

package gg.projecteden.nexus.features.menus.api;

import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Data
@AllArgsConstructor
public class ItemClickData {

	private final Event event;
	private final Player player;
	private final ItemStack item;
	private final SlotPos slot;

	public boolean isRightClick() {
		return isClickType(ClickType.RIGHT);
	}

	public boolean isAnyRightClick() {
		return isRightClick() || isShiftRightClick();
	}

	public boolean isAnyLeftClick() {
		return isLeftClick() || isShiftLeftClick();
	}

	public boolean isLeftClick() {
		return isClickType(ClickType.LEFT);
	}

	public boolean isShiftClick() {
		return isClickType(ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT);
	}

	public boolean isShiftLeftClick() {
		return isClickType(ClickType.SHIFT_LEFT);
	}

	public boolean isShiftRightClick() {
		return isClickType(ClickType.SHIFT_RIGHT);
	}

	public boolean isClickType(ClickType... clickTypes) {
		return event instanceof InventoryClickEvent clickEvent && Arrays.asList(clickTypes).contains(clickEvent.getClick());
	}

}
