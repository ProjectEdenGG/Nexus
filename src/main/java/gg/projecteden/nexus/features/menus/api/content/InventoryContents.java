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

import com.google.common.base.Preconditions;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.util.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 * Represents the content of an inventory.
 * </p>
 *
 * <p>
 * This contains several methods which let you get and modify
 * the content of the inventory.
 * </p>
 *
 * <p>
 * For example, you can get the item at a given slot by
 * using {@link InventoryContents#get(SlotPos)}. You can
 * also fill an entire column with the use of the method
 * {@link InventoryContents#fillColumn(int, ClickableItem)}.
 * </p>
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class InventoryContents {
	private final SmartInventory config;
	private final Player player;
	@Getter
	@Setter
	private Inventory inventory;

	private ClickableItem[][] contents;

	private final Pagination pagination = new Pagination.Impl();
	private final Map<String, SlotIterator> iterators = new HashMap<>();
	private final Map<String, Object> properties = new HashMap<>();

	private final Set<SlotPos> editableSlots = new HashSet<>();

	public InventoryContents(SmartInventory config, Player player) {
		this.config = config;
		this.player = player;
		this.clear();
	}

	public void clear() {
		this.contents = new ClickableItem[config.getRows()][config.getColumns()];
	}

	/**
	 * Gets the inventory linked to this {@link InventoryContents}.
	 * <br>
	 * Cannot be <code>null</code>.
	 *
	 * @return the inventory
	 */
	public SmartInventory config() {
		return config;
	}

	/**
	 * Gets the pagination system linked to this {@link InventoryContents}.
	 * <br>
	 * Cannot be <code>null</code>.
	 *
	 * @return the pagination
	 */
	public Pagination pagination() {
		return pagination;
	}

	/**
	 * Gets a previously registered iterator named with the given id.
	 * <br>
	 * If no iterator is found, this will return <code>Optional.empty()</code>.
	 *
	 * @param id the id of the iterator
	 * @return the found iterator, if there is one
	 */
	public Optional<SlotIterator> iterator(String id) {
		return Optional.ofNullable(this.iterators.get(id));
	}

	/**
	 * Creates and registers an iterator using a given id.
	 *
	 * <p>
	 * You can retrieve the iterator at any time using
	 * the {@link InventoryContents#iterator(String)} method.
	 * </p>
	 *
	 * @param id          the id of the iterator
	 * @param type        the type of the iterator
	 * @param startRow    the starting row of the iterator
	 * @param startColumn the starting column of the iterator
	 * @return the newly created iterator
	 */
	public SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn) {
		SlotIterator iterator = new SlotIterator.Impl(this, config, type, startRow, startColumn);
		this.iterators.put(id, iterator);
		return iterator;
	}

	/**
	 * Same as {@link InventoryContents#newIterator(String, SlotIterator.Type, int, int)},
	 * but using a {@link SlotPos} instead.
	 *
	 * @see InventoryContents#newIterator(String, SlotIterator.Type, int, int)
	 */
	public SlotIterator newIterator(String id, SlotIterator.Type type, SlotPos startPos) {
		return newIterator(id, type, startPos.getRow(), startPos.getColumn());
	}

	/**
	 * Creates and returns an iterator.
	 *
	 * <p>
	 * This does <b>NOT</b> registers the iterator,
	 * thus {@link InventoryContents#iterator(String)} will not be
	 * able to return the iterators created with this method.
	 * </p>
	 *
	 * @param type        the type of the iterator
	 * @param startRow    the starting row of the iterator
	 * @param startColumn the starting column of the iterator
	 * @return the newly created iterator
	 */
	public SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn) {
		return new SlotIterator.Impl(this, config, type, startRow, startColumn);
	}

	/**
	 * Same as {@link InventoryContents#newIterator(SlotIterator.Type, int, int)},
	 * but using a {@link SlotPos} instead.
	 *
	 * @see InventoryContents#newIterator(SlotIterator.Type, int, int)
	 */
	public SlotIterator newIterator(SlotIterator.Type type, SlotPos startPos) {
		return newIterator(type, startPos.getRow(), startPos.getColumn());
	}

	/**
	 * Returns a 2D array of ClickableItems containing
	 * all the items of the inventory.
	 * The ClickableItems can be null when there is no
	 * item in the corresponding slot.
	 *
	 * @return the items of the inventory
	 */
	public ClickableItem[][] all() {
		return contents;
	}

	/**
	 * Returns a list of all the slots in the inventory.
	 *
	 * @return the inventory slots
	 */
	public List<SlotPos> slots() {
		List<SlotPos> slotPos = new ArrayList<>();
		for (int row = 0; row < contents.length; row++) {
			for (int column = 0; column < contents[0].length; column++) {
				slotPos.add(SlotPos.of(row, column));
			}
		}
		return slotPos;
	}

	/**
	 * Returns the position of the first empty slot
	 * in the inventory, or <code>Optional.empty()</code> if
	 * there is no free slot.
	 *
	 * @return the first empty slot, if there is one
	 */
	public Optional<SlotPos> firstEmpty() {
		for (int row = 0; row < contents.length; row++) {
			for (int column = 0; column < contents[0].length; column++) {
				if (this.get(row, column).isEmpty())
					return Optional.of(new SlotPos(row, column));
			}
		}

		return Optional.empty();
	}

	/**
	 * Returns the position of the first non-empty slot
	 * in the inventory, or <code>Optional.empty()</code> if
	 * there is no filled slot.
	 *
	 * @return the first non-empty slot, if there is one
	 */
	public Optional<SlotPos> firstPresent() {
		for (int row = 0; row < contents.length; row++) {
			for (int column = 0; column < contents[0].length; column++) {
				if (this.get(row, column).isPresent())
					return Optional.of(new SlotPos(row, column));
			}
		}

		return Optional.empty();
	}

	/**
	 * Checks if there are any slots with an item
	 *
	 * @return true if the menu is empty
	 */
	public boolean anyPresent() {
		return firstPresent().isPresent();
	}

	/**
	 * Returns the item in the inventory at the given
	 * slot index, or <code>Optional.empty()</code> if
	 * the slot is empty or if the index is out of bounds.
	 *
	 * @param index the slot index
	 * @return the found item, if there is one
	 */
	public Optional<ClickableItem> get(int index) {
		int columnCount = this.config.getColumns();

		return get(index / columnCount, index % columnCount);
	}

	/**
	 * Same as {@link InventoryContents#get(int)},
	 * but with a row and a column instead of the index.
	 *
	 * @see InventoryContents#get(int)
	 */
	public Optional<ClickableItem> get(int row, int column) {
		if (row < 0 || row >= contents.length)
			return Optional.empty();
		if (column < 0 || column >= contents[row].length)
			return Optional.empty();

		return Optional.ofNullable(contents[row][column]);
	}

	/**
	 * Same as {@link InventoryContents#get(int)},
	 * but with a {@link SlotPos} instead of the index.
	 *
	 * @see InventoryContents#get(int)
	 */
	public Optional<ClickableItem> get(SlotPos slotPos) {
		return get(slotPos.getRow(), slotPos.getColumn());
	}

	/**
	 * Sets the item in the inventory at the given
	 * slot index.
	 *
	 * @param index the slot index
	 * @param item  the item to set, or <code>null</code> to clear the slot
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents set(int index, ClickableItem item) {
		int columnCount = this.config.getColumns();

		return set(index / columnCount, index % columnCount, item);
	}

	/**
	 * Same as {@link InventoryContents#set(int, ClickableItem)},
	 * but with a row and a column instead of the index.
	 *
	 * @see InventoryContents#set(int, ClickableItem)
	 */
	public InventoryContents set(int row, int column, ClickableItem item) {
		if (row < 0 || row >= contents.length)
			return this;
		if (column < 0 || column >= contents[row].length)
			return this;

		contents[row][column] = item;
		update(row, column, item == null ? null : item.getItem(player));
		return this;
	}

	/**
	 * Same as {@link InventoryContents#set(int, ClickableItem)},
	 * but with a {@link SlotPos} instead of the index.
	 *
	 * @see InventoryContents#set(int, ClickableItem)
	 */
	public InventoryContents set(SlotPos slotPos, ClickableItem item) {
		return set(slotPos.getRow(), slotPos.getColumn(), item);
	}

	/**
	 * Adds an item to the <b>first empty slot</b> of the inventory.
	 * <br>
	 * <b>Warning:</b> If there is already a stack of the same item,
	 * this will not add the item to the stack, this will always
	 * add the item into an empty slot.
	 *
	 * @param item the item to add
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents add(ClickableItem item) {
		for (int row = 0; row < contents.length; row++) {
			for (int column = 0; column < contents[0].length; column++) {
				if (contents[row][column] == null) {
					set(row, column, item);
					return this;
				}
			}
		}

		return this;
	}

	/**
	 * Looks for the given item and compares them using {@link ItemStack#isSimilar(ItemStack)},
	 * ignoring the amount.
	 * <br>
	 * This method searches row for row from left to right.
	 *
	 * @param itemStack the item to look for
	 * @return an optional containing the position where the item first occurred, or an empty optional
	 */
	public Optional<SlotPos> findItem(ItemStack itemStack) {
		Preconditions.checkNotNull(itemStack, "The itemstack to look for cannot be null!");
		for (int row = 0; row < contents.length; row++) {
			for (int column = 0; column < contents[0].length; column++) {
				if (contents[row][column] != null &&
					itemStack.isSimilar(contents[row][column].getItem(this.player))) {
					return Optional.of(SlotPos.of(row, column));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Looks for the given item and compares them using {@link ItemStack#isSimilar(ItemStack)},
	 * ignoring the amount.
	 * <br>
	 * This method searches row for row from left to right.
	 *
	 * @param clickableItem the clickable item with the item stack to look for
	 * @return an optional containing the position where the item first occurred, or an empty optional
	 */
	public Optional<SlotPos> findItem(ClickableItem clickableItem) {
		Preconditions.checkNotNull(clickableItem, "The clickable item to look for cannot be null!");
		return findItem(clickableItem.getItem(this.player));
	}

	/**
	 * Fills the inventory with the given item.
	 *
	 * @param item the item
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents fill(ClickableItem item) {
		for (int row = 0; row < contents.length; row++)
			for (int column = 0; column < contents[row].length; column++)
				set(row, column, item);

		return this;
	}

	/**
	 * Fills the given inventory row with the given item.
	 *
	 * @param row  the row to fill
	 * @param item the item
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents fillRow(int row, ClickableItem item) {
		if (row < 0 || row >= contents.length)
			return this;

		for (int column = 0; column < contents[row].length; column++)
			set(row, column, item);

		return this;
	}

	/**
	 * Fills the given inventory column with the given item.
	 *
	 * @param column the column to fill
	 * @param item   the item
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents fillColumn(int column, ClickableItem item) {
		if (column < 0 || column >= contents[0].length)
			return this;

		for (int row = 0; row < contents.length; row++)
			set(row, column, item);

		return this;
	}

	/**
	 * Fills the inventory borders with the given item.
	 *
	 * @param item the item
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents outline(ClickableItem item) {
		outline(0, 0, config.getRows() - 1, config.getColumns() - 1, item);
		return this;
	}

	/**
	 * Outlines an area inside the inventory using the given positions.
	 * <br>
	 * The created rectangle will have its top-left position at
	 * the given <b>from slot index</b> and its bottom-right position at
	 * the given <b>to slot index</b>.
	 *
	 * @param fromIndex the slot index at the top-left position
	 * @param toIndex   the slot index at the bottom-right position
	 * @param item      the item
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents outline(int fromIndex, int toIndex, ClickableItem item) {
		int columnCount = this.config.getColumns();

		return outline(
			fromIndex / columnCount, fromIndex % columnCount,
			toIndex / columnCount, toIndex % columnCount,
			item
		);
	}

	/**
	 * Same as {@link InventoryContents#outline(int, int, ClickableItem)},
	 * but with {@link SlotPos} instead of the indexes.
	 *
	 * @see InventoryContents#outline(int, int, ClickableItem)
	 */
	public InventoryContents outline(int fromRow, int fromColumn, int toRow, int toColumn, ClickableItem item) {
		for (int row = fromRow; row <= toRow; row++) {
			for (int column = fromColumn; column <= toColumn; column++) {
				if (row != fromRow && row != toRow && column != fromColumn && column != toColumn)
					continue;

				set(row, column, item);
			}
		}

		return this;
	}

	/**
	 * Same as {@link InventoryContents#outline(int, int, ClickableItem)},
	 * but with rows and columns instead of the indexes.
	 *
	 * @see InventoryContents#outline(int, int, ClickableItem)
	 */
	public InventoryContents outline(SlotPos fromPos, SlotPos toPos, ClickableItem item) {
		return outline(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
	}

	/**
	 * Completely fills the provided area with the given {@link ClickableItem}.
	 *
	 * @param fromIndex the slot index of the upper left corner
	 * @param toIndex   the slot index of the lower right corner
	 * @param item      the item
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents fill(int fromIndex, int toIndex, ClickableItem item) {
		int columnCount = this.config.getColumns();

		return fill(
			fromIndex / columnCount, fromIndex % columnCount,
			toIndex / columnCount, toIndex % columnCount,
			item
		);
	}

	/**
	 * Completely fills the provided area with the given {@link ClickableItem}.
	 *
	 * @param fromRow    the row of the upper left corner
	 * @param fromColumn the column of the upper-left corner
	 * @param toRow      the row of the lower right corner
	 * @param toColumn   the column of the lower right corner
	 * @param item       the item
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents fill(int fromRow, int fromColumn, int toRow, int toColumn, ClickableItem item) {
		Preconditions.checkArgument(fromRow < toRow, "The start row needs to be lower than the end row");
		Preconditions.checkArgument(fromColumn < toColumn, "The start column needs to be lower than the end column");

		for (int row = fromRow; row <= toRow; row++) {
			for (int column = fromColumn; column <= toColumn; column++) {
				set(row, column, item);
			}
		}
		return this;
	}

	/**
	 * Completely fills the provided area with the given {@link ClickableItem}.
	 *
	 * @param fromPos the slot position of the upper left corner
	 * @param toPos   the slot position of the lower right corner
	 * @param item    the item
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents fill(SlotPos fromPos, SlotPos toPos, ClickableItem item) {
		return fill(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
	}

	/**
	 * Fills the inventory with the given {@link Pattern}.
	 * <br>
	 * The pattern will start at the first slot.
	 *
	 * @param pattern the filling pattern
	 * @return <code>this</code>, for chained calls
	 * @see #fillPattern(Pattern, int) to fill the pattern from the provided slot index
	 * @see #fillPattern(Pattern, int, int) to fill the pattern from the provided row and column
	 * @see #fillPattern(Pattern, SlotPos) to fill the pattern from the provided slot pos
	 */
	public InventoryContents fillPattern(Pattern<ClickableItem> pattern) {
		return fillPattern(pattern, 0, 0);
	}

	/**
	 * Fills the inventory with the given {@link Pattern}.
	 * <br>
	 * The pattern will start at the given slot index.
	 *
	 * @param pattern    the filling pattern
	 * @param startIndex the start slot index for the filling
	 * @return <code>this</code>, for chained calls
	 * @see #fillPattern(Pattern) to fill the pattern from the first slot
	 * @see #fillPattern(Pattern, int, int) to fill the pattern from the provided row and column
	 * @see #fillPattern(Pattern, SlotPos) to fill the pattern from the provided slot pos
	 */
	public InventoryContents fillPattern(Pattern<ClickableItem> pattern, int startIndex) {
		int columnCount = this.config.getColumns();

		return fillPattern(pattern, startIndex / columnCount, startIndex % columnCount);
	}

	/**
	 * Fills the inventory with the given {@link Pattern}.
	 * <br>
	 * The pattern will start at the given slot position.
	 *
	 * @param pattern  the filling pattern
	 * @param startPos the start position of the slot for filling
	 * @return <code>this</code>, for chained calls
	 * @see #fillPattern(Pattern) to fill the pattern from the first slot
	 * @see #fillPattern(Pattern, int) to fill the pattern from the provided slot index
	 * @see #fillPattern(Pattern, int, int) to fill the pattern from the provided row and column
	 */
	public InventoryContents fillPattern(Pattern<ClickableItem> pattern, SlotPos startPos) {
		return fillPattern(pattern, startPos.getRow(), startPos.getColumn());
	}

	/**
	 * Fills the inventory with the given {@link Pattern}.
	 * <br>
	 * The pattern will start at the first slot and end at the last slot.
	 * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern.
	 * <br>
	 * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern.
	 * <br>
	 * <b>For this to work the pattern needs to be created with <code>wrapAround</code> enabled.</b>
	 *
	 * @param pattern the filling pattern
	 * @return <code>this</code>, for chained calls
	 * @see #fillPatternRepeating(Pattern, int, int) to fill a repeating pattern using slot indexes
	 * @see #fillPatternRepeating(Pattern, int, int, int, int) to fill a repeating pattern using slot positions contructed from their rows and columns
	 * @see #fillPatternRepeating(Pattern, SlotPos, SlotPos) to fill a repeating pattern using slot positions
	 */
	public InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern) {
		return fillPatternRepeating(pattern, 0, 0, -1, -1);
	}

	/**
	 * Fills the inventory with the given {@link Pattern}.
	 * <br>
	 * The pattern will start at the first slot index and end at the second slot index.
	 * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern.
	 * <br>
	 * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern.
	 * <br>
	 * If <code>endIndex</code> is a negative value it is set to the bottom-right corner.
	 * <br>
	 * <b>For this to work the pattern needs to be created with <code>wrapAround</code> enabled.</b>
	 *
	 * @param pattern    the filling pattern
	 * @param startIndex the start slot index where the pattern should begin
	 * @param endIndex   the end slot index where the pattern should end
	 * @return <code>this</code>, for chained calls
	 * @see #fillPatternRepeating(Pattern) to fill a repeating pattern into the whole inventory
	 * @see #fillPatternRepeating(Pattern, int, int, int, int) to fill a repeating pattern using slot positions contructed from their rows and columns
	 * @see #fillPatternRepeating(Pattern, SlotPos, SlotPos) to fill a repeating pattern using slot positions
	 */
	public InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern, int startIndex, int endIndex) {
		int columnCount = this.config.getColumns();
		boolean maxSize = endIndex < 0;

		return fillPatternRepeating(pattern, startIndex / columnCount, startIndex % columnCount, (maxSize ? -1 : endIndex / columnCount), (maxSize ? -1 : endIndex % columnCount));
	}

	/**
	 * Fills the inventory with the given {@link Pattern}.
	 * <br>
	 * The pattern will start at the given slot position and end at the second slot position.
	 * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern.
	 * <br>
	 * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern.
	 * <br>
	 * If <code>endRow</code> is a negative value, endRow is automatically set to the max row size,
	 * if <code>endColumn</code> is a negative value, endColumn is automatically set to the max column size.
	 * <br>
	 * <b>For this to work the pattern needs to be created with <code>wrapAround</code> enabled.</b>
	 *
	 * @param pattern     the filling pattern
	 * @param startRow    the start row of the slot for filling
	 * @param startColumn the start column of the slot for filling
	 * @param endRow      the end row of the slot for filling
	 * @param endColumn   the end column of the slot for filling
	 * @return <code>this</code>, for chained calls
	 * @see #fillPatternRepeating(Pattern) to fill a repeating pattern into the whole inventory
	 * @see #fillPatternRepeating(Pattern, int, int) to fill a repeating pattern using slot indexes
	 * @see #fillPatternRepeating(Pattern, SlotPos, SlotPos) to fill a repeating pattern using slot positions
	 */
	public InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern, int startRow, int startColumn, int endRow, int endColumn) {
		Preconditions.checkArgument(pattern.isWrapAround(), "To fill in a repeating pattern wrapAround needs to be enabled for the pattern to work!");

		if (endRow < 0)
			endRow = this.config.getRows();
		if (endColumn < 0)
			endColumn = this.config.getColumns();

		Preconditions.checkArgument(startRow < endRow, "The start row needs to be lower than the end row");
		Preconditions.checkArgument(startColumn < endColumn, "The start column needs to be lower than the end column");

		int rowDelta = endRow - startRow, columnDelta = endColumn - startColumn;
		for (int row = 0; row <= rowDelta; row++) {
			for (int column = 0; column <= columnDelta; column++) {
				ClickableItem item = pattern.getObject(row, column);

				if (item != null)
					set(startRow + row, startColumn + column, item);
			}
		}
		return this;
	}

	/**
	 * Fills the inventory with the given {@link Pattern}.
	 * <br>
	 * The pattern will start at the given slot position and end at the second slot position.
	 * If the pattern is not big enough, it will wrap around to the other side and repeat the pattern.
	 * <br>
	 * The top-left corner of the specified inventory area is also the top-left corner of the specified pattern.
	 * <br>
	 * If the row of <code>endPos</code> is a negative value, endRow is automatically set to the max row size,
	 * if the column of <code>endPos</code> is a negative value, endColumn is automatically set to the max column size.
	 * <br>
	 * <b>For this to work the pattern needs to be created with <code>wrapAround</code> enabled.</b>
	 *
	 * @param pattern  the filling pattern
	 * @param startPos the position where the pattern should start
	 * @param endPos   the position where the pattern should end
	 * @return <code>this</code>, for chained calls
	 * @see #fillPatternRepeating(Pattern) to fill a repeating pattern into the whole inventory
	 * @see #fillPatternRepeating(Pattern, int, int) to fill a repeating pattern using slot indexes
	 * @see #fillPatternRepeating(Pattern, int, int, int, int) to fill a repeating pattern using slot positions contructed from their rows and columns
	 */
	public InventoryContents fillPatternRepeating(Pattern<ClickableItem> pattern, SlotPos startPos, SlotPos endPos) {
		return fillPatternRepeating(pattern, startPos.getRow(), startPos.getColumn(), endPos.getRow(), endPos.getColumn());
	}

	/**
	 * Fills the inventory with the given {@link Pattern}.
	 * <br>
	 * The pattern will start at the given slot position based on the provided row and column.
	 *
	 * @param pattern     the filling pattern
	 * @param startRow    the start row of the slot for filling
	 * @param startColumn the start column of the slot for filling
	 * @return <code>this</code>, for chained calls
	 * @see #fillPattern(Pattern) to fill the pattern from the first slot
	 * @see #fillPattern(Pattern, int) to fill the pattern from the provided slot index
	 * @see #fillPattern(Pattern, SlotPos) to fill the pattern from the provided slot pos
	 */
	public InventoryContents fillPattern(Pattern<ClickableItem> pattern, int startRow, int startColumn) {
		for (int row = 0; row < pattern.getRowCount(); row++) {
			for (int column = 0; column < pattern.getColumnCount(); column++) {
				ClickableItem item = pattern.getObject(row, column);

				if (item != null)
					set(startRow + row, startColumn + column, item);
			}
		}

		return this;
	}

	/**
	 * Gets the value of the property with the given name.
	 *
	 * @param name the property's name
	 * @param <T>  the type of the value
	 * @return the property's value
	 */
	public <T> T property(String name) {
		return (T) properties.get(name);
	}

	/**
	 * Gets the value of the property with the given name,
	 * or a default value if the property isn't set.
	 *
	 * @param name the property's name
	 * @param def  the default value
	 * @param <T>  the type of the value
	 * @return the property's value, or the given default value
	 */
	public <T> T property(String name, T def) {
		return properties.containsKey(name) ? (T) properties.get(name) : def;
	}

	/**
	 * Sets the value of the property with the given name.
	 * <br>
	 * This will replace the existing value for the property,
	 * if there is one.
	 *
	 * @param name  the property's name
	 * @param value the new property's value
	 * @return <code>this</code>, for chained calls
	 */
	public InventoryContents setProperty(String name, Object value) {
		properties.put(name, value);
		return this;
	}

	private void update(int row, int column, ItemStack item) {
		if (!config.getManager().getOpenedPlayers(config).contains(player))
			return;

		inventory.setItem(config.getColumns() * row + column, item);
	}

	/**
	 * Makes a slot editable, which enables the player to
	 * put items in and take items out of the inventory in the
	 * specified slot.
	 *
	 * @param row      The row of the slot
	 * @param column   The column of the slot
	 * @param editable {@code true} to make a slot editable, {@code false}
	 *                 to make it 'static' again.
	 */
	public void setEditable(int row, int column, boolean editable) {
		setEditable(SlotPos.of(row, column), editable);
	}

	/**
	 * Makes a slot editable, which enables the player to
	 * put items in and take items out of the inventory in the
	 * specified slot.
	 *
	 * @param slot     The slot to set editable
	 * @param editable {@code true} to make a slot editable, {@code false}
	 *                 to make it 'static' again.
	 */
	public void setEditable(SlotPos slot, boolean editable) {
		if (editable)
			editableSlots.add(slot);
		else
			editableSlots.remove(slot);
	}

	public void setEditable(int index, boolean editable) {
		int columnCount = this.config.getColumns();
		this.setEditable(SlotPos.of(index / columnCount, index % columnCount), editable);
	}

	/**
	 * Returns if a given slot is editable or not.
	 *
	 * @param slot The slot to check
	 * @return {@code true} if the editable.
	 * @see #setEditable(SlotPos, boolean)
	 */
	public boolean isEditable(SlotPos slot) {
		return editableSlots.contains(slot);
	}

}
