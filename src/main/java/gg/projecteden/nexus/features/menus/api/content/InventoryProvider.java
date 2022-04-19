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

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.ResourcePack.ResourcePackNumber;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils;
import me.lexikiq.HasPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.CheckReturnValue;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static gg.projecteden.nexus.features.menus.SignMenuFactory.ARROWS;

public abstract class InventoryProvider {

	public abstract void init(Player player, InventoryContents contents);

	@SuppressWarnings("unused")
	public void update(Player player, InventoryContents contents) {}

	protected boolean isOpen(Player player) {
		Optional<SmartInventory> inventory = SmartInvsPlugin.manager().getInventory(player);
		return inventory.isPresent() && this.equals(inventory.get().getProvider());
	}

	public void open(Player player) {
		open(player, 0);
	}

	public abstract void open(Player player, int page);

	public final void open(HasPlayer player) {
		open(player.getPlayer());
	}

	public final void open(HasPlayer player, Pagination page) {
		open(player.getPlayer(), page.getPage());
	}

	public final void open(HasPlayer player, int page) {
		open(player.getPlayer(), page);
	}

	protected void addBackItem(InventoryContents contents, Consumer<ItemClickData> consumer) {
		addBackItem(contents, 0, 0, consumer);
	}

	protected void addBackItem(InventoryContents contents, int row, int col, Consumer<ItemClickData> consumer) {
		contents.set(row, col, ClickableItem.of(backItem(), consumer));
	}

	protected void addCloseItem(InventoryContents contents) {
		addCloseItem(contents, 0, 0);
	}

	protected void addCloseItem(InventoryContents contents, int row, int col) {
		contents.set(row, col, ClickableItem.of(closeItem(), e -> e.getPlayer().closeInventory()));
	}

	protected ItemStack backItem() {
		return new ItemBuilder(Material.BARRIER).name("&cBack").build();
	}

	protected ItemStack closeItem() {
		return new ItemBuilder(Material.BARRIER).name("&cClose").build();
	}

	protected void warp(Player player, String warp) {
		PlayerUtils.runCommand(player, "warp " + warp);
	}

	public void command(Player player, String command) {
		PlayerUtils.runCommand(player, command);
	}

	@CheckReturnValue
	public final Paginator paginator(Player player, InventoryContents contents, List<ClickableItem> items) {
		return paginator().player(player).contents(contents).items(items);
	}

	public final Paginator paginator() {
		return new Paginator();
	}

	public class Paginator {
		private Player player;
		private boolean hasResourcePack;
		private InventoryContents contents;
		private List<ClickableItem> items;
		private int perPage = 36;
		private SlotPos previousSlot;
		private SlotPos nextSlot;
		private SlotIterator iterator;

		public Paginator player(Player player) {
			this.player = player;
			this.hasResourcePack = ResourcePack.isEnabledFor(player);
			return this;
		}

		public Paginator hasResourcePack(boolean hasResourcePack) {
			this.hasResourcePack = hasResourcePack;
			return this;
		}

		public Paginator contents(InventoryContents contents) {
			this.contents = contents;
			return this;
		}

		public Paginator items(List<ClickableItem> items) {
			this.items = items;
			return this;
		}

		public Paginator perPage(int perPage) {
			this.perPage = perPage;
			return this;
		}

		public Paginator previousSlot(int row, int column) {
			return previousSlot(SlotPos.of(row, column));
		}

		public Paginator previousSlot(SlotPos slot) {
			this.previousSlot = slot;
			return this;
		}

		public Paginator nextSlot(int row, int column) {
			return nextSlot(SlotPos.of(row, column));
		}

		public Paginator nextSlot(SlotPos slot) {
			this.nextSlot = slot;
			return this;
		}

		public Paginator iterator(SlotIterator iterator) {
			this.iterator = iterator;
			return this;
		}

		public void build() {
			if (previousSlot == null)
				previousSlot = SlotPos.of(contents.inventory().getRows() - 1, 0);
			if (nextSlot == null)
				nextSlot = SlotPos.of(contents.inventory().getRows() - 1, 8);

			if (iterator == null)
				iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0);

			Pagination page = contents.pagination();

			if (page.getPage() > items.size() / perPage)
				page.page(items.size() / perPage);
			int currentPage = page.getPage() + 1;

			int previousPage = Math.max(currentPage - 1, 1);
			int nextPage = currentPage + 1;

			String[] lore = {"&f", "&7Right click to jump to a page"};

			ItemBuilder previous = ResourcePackNumber.of(previousPage)
				.hasResourcePack(hasResourcePack)
				.color(ColorType.CYAN)
				.get()
				.name("&fPrevious Page")
				.lore(lore);

			ItemBuilder next = ResourcePackNumber.of(nextPage)
				.hasResourcePack(hasResourcePack)
				.color(ColorType.CYAN)
				.get()
				.name("&fNext Page")
				.lore(lore);

			page.setItemsPerPage(perPage);
			page.setItems(items.toArray(ClickableItem[]::new));
			page.addToIterator(iterator);

			if (page.getPage() > items.size() / perPage)
				page.page(items.size() / perPage);

			if (!page.isFirst())
				contents.set(previousSlot, ClickableItem.of(previous.build(), e -> {
					if (e.isRightClick())
						jumpToPage(player, page.getPage());
					else
						open(player, page.previous().getPage());
				}));

			if (!page.isLast())
				contents.set(nextSlot, ClickableItem.of(next.build(), e -> {
					if (e.isRightClick())
						jumpToPage(player, page.getPage());
					else
						open(player, page.next().getPage());
				}));
		}

		private void jumpToPage(Player player, int currentPage) {
			Nexus.getSignMenuFactory()
				.lines("", ARROWS, "Enter a", "page number")
				.prefix(Shops.PREFIX)
				.onError(() -> open(player, currentPage))
				.response(lines -> {
					if (lines[0].length() > 0) {
						String input = lines[0].replaceAll("[^\\d.-]+", "");
						if (!Utils.isInt(input))
							throw new InvalidInputException("Could not parse &e" + lines[0] + " &cas a page number");
						int pageNumber = Math.max(0, Integer.parseInt(input) - 1);
						open(player, pageNumber);
					} else
						open(player, currentPage);
				}).open(player);
		}
	}

}
