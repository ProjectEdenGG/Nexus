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

package gg.projecteden.nexus.features.menus.api.content;

import lombok.Data;

/**
 * Represents the position (row + column) of a slot
 * in an inventory.
 */
@Data
public class SlotPos {

	private final int row;
	private final int column;

	public static SlotPos of(int row, int column) {
		return new SlotPos(row, column);
	}

	public boolean matches(int row, int column) {
		return this.row == row && this.column == column;
	}

	public boolean matches(SlotPos slotPos) {
		return this.row == slotPos.row && this.column == slotPos.column;
	}
}
