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
import gg.projecteden.nexus.features.menus.api.InventoryManager;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.SmartInventory.Builder;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener.CustomInventoryHolder;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.annotations.Uncloseable;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.ResourcePack.ResourcePackNumber;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.parchment.HasPlayer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckReturnValue;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static gg.projecteden.nexus.features.menus.api.SignMenuFactory.ARROWS;

public abstract class InventoryProvider {
	@Getter
	protected Player viewer;
	@Setter
	protected InventoryContents contents;
	@Setter
	protected InventoryContents selfContents;
	@Getter
	@Setter
	protected Inventory bukkitInventory;
	@Getter
	protected InventoryHolder holder;

	@Data
	public static class SmartInventoryHolder extends CustomInventoryHolder {
		private final InventoryProvider provider;
	}

	public abstract void init();

	@SuppressWarnings("unused")
	public void update() {}

	protected boolean isOpen() {
		Optional<SmartInventory> inventory = SmartInvsPlugin.manager().getInventory(viewer);
		return inventory.isPresent() && this.equals(inventory.get().getProvider());
	}

	public void close() {
		SmartInvsPlugin.close(viewer);
	}

	public void refresh() {
		open(viewer, contents.pagination());
	}

	public void open(Player viewer) {
		open(viewer, 0);
	}

	public void open(Player viewer, int page) {
		this.viewer = viewer;
		this.holder = new SmartInventoryHolder(this);
		getInventory(page).rows(getRows(page)).build().open(viewer, page);
	}

	public final void open(HasPlayer viewer) {
		open(viewer.getPlayer());
	}

	public final void open(HasPlayer viewer, Pagination pagination) {
		open(viewer.getPlayer(), pagination.getPage());
	}

	public final void open(HasPlayer viewer, int page) {
		open(viewer.getPlayer(), page);
	}

	public void onClose(InventoryManager manager) {
		if (manager.getSelfContents(viewer).isPresent())
			manager.getRealContents(viewer).ifPresent(realContents -> {
				viewer.getInventory().setContents(realContents);
				manager.setRealContents(viewer, null);

				manager.getHeldSlot(viewer).ifPresent(slot -> {
					viewer.getInventory().setHeldItemSlot(slot);
					manager.setHeldSlot(viewer, null);
				});
			});
	}

	public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {}

	public void onPageTurn(Player viewer) {}

	public SmartInventory.Builder getInventory(int page) {
		Builder inv = SmartInventory.builder()
			.provider(this)
			.rows(getRows(null))
			.closeable(isCloseable())
			.updateFrequency(getUpdateFrequency())
			.title(getTitle(page));

		if (Nullables.isNullOrEmpty(getTitle(page)))
			inv.title(getTitleComponent(page));

		return inv;
	}

	public int getUpdateFrequency() {
		return 1;
	}

	public ComponentLike getTitleComponent() {
		return Component.empty();
	}

	public ComponentLike getTitleComponent(int page) {
		return getTitleComponent();
	}

	public String getTitle() {
		final Title annotation = Utils.getAnnotation(getClass(), Title.class);
		if (annotation != null)
			return annotation.value();

		return "";
	}

	public String getTitle(int page) {
		return getTitle();
	}

	protected int getRows(Integer page) {
		final Rows annotation = Utils.getAnnotation(getClass(), Rows.class);
		if (annotation != null)
			return annotation.value();

		return 6;
	}

	protected boolean isCloseable() {
		final Uncloseable annotation = Utils.getAnnotation(getClass(), Uncloseable.class);
		return annotation == null;
	}

	protected void addBackItem(Consumer<ItemClickData> consumer) {
		addBackItem(0, 0, consumer);
	}

	protected void addBackItem(InventoryProvider previousMenu) {
		addBackItem(0, 0, e -> previousMenu.open(viewer, previousMenu.contents.pagination().getPage()));
	}

	protected void addBackItem(int row, int col, Consumer<ItemClickData> consumer) {
		contents.set(row, col, ClickableItem.of(backItem(), consumer));
	}

	protected void addBackItemBottomInventory(Consumer<ItemClickData> consumer) {
		addBackItemBottomInventory(0, 0, consumer);
	}

	protected void addBackItemBottomInventory(InventoryProvider previousMenu) {
		addBackItemBottomInventory(0, 0, e -> previousMenu.open(viewer));
	}

	protected void addBackItemBottomInventory(int row, int col, Consumer<ItemClickData> consumer) {
		selfContents.set(row, col, ClickableItem.of(backItem(), consumer));
	}

	protected void addCloseItem() {
		addCloseItem(0, 0);
	}

	protected void addCloseItem(int row, int col) {
		contents.set(row, col, ClickableItem.of(closeItem(), e -> e.getPlayer().closeInventory()));
	}

	protected void addCloseItemBottomInventory() {
		addCloseItemBottomInventory(0, 0);
	}

	protected void addCloseItemBottomInventory(int row, int col) {
		selfContents.set(row, col, ClickableItem.of(closeItem(), e -> e.getPlayer().closeInventory()));
	}

	protected void addBackOrCloseItem(@Nullable InventoryProvider previousMenu) {
		if (previousMenu == null) {
			addCloseItem();
			return;
		}

		addBackItem(previousMenu);
	}

	protected void addBackOrCloseItemBottomInventory(@Nullable InventoryProvider previousMenu) {
		if (previousMenu == null) {
			addCloseItemBottomInventory();
			return;
		}

		addBackItemBottomInventory(previousMenu);
	}

	protected ItemStack backItem() {
		return new ItemBuilder(CustomMaterial.GUI_ARROW_PREVIOUS).dyeColor(ColorType.RED).itemFlags(ItemFlags.HIDE_ALL).name("&cBack").build();
	}

	protected ItemStack closeItem() {
		return new ItemBuilder(CustomMaterial.GUI_CLOSE).dyeColor(ColorType.RED).itemFlags(ItemFlags.HIDE_ALL).name("&cClose").build();
	}

	public static ItemBuilder checkmark() {
		return CustomMaterial.GUI_CHECK.getNoNamedItem().dyeColor(ColorType.LIGHT_GREEN);
	}

	protected void warp(String warp) {
		PlayerUtils.runCommand(viewer, "warp " + warp);
	}

	public void command(String commandNoSlash) {
		PlayerUtils.runCommand(viewer, commandNoSlash);
	}
	
	public final void paginate(Collection<ClickableItem> items) {
		paginator().items(items).build();
	}

	@CheckReturnValue
	public final Paginator paginator() {
		return new Paginator();
	}

	public String getPrefix() {
		return StringUtils.getPrefix(getClass().getSimpleName().replaceFirst("Menu$", ""));
	}

	public class Paginator {
		private Boolean hasResourcePack;
		private Collection<ClickableItem> items;
		private int perPage = 36;
		private int startingRow = 1;
		private SlotPos previousSlot;
		private SlotPos nextSlot;
		private SlotIterator iterator;
		private boolean guiArrows = false;

		@CheckReturnValue
		public Paginator hasResourcePack(boolean hasResourcePack) {
			this.hasResourcePack = hasResourcePack;
			return this;
		}

		@CheckReturnValue
		public Paginator items(Collection<ClickableItem> items) {
			this.items = items;
			return this;
		}

		@CheckReturnValue
		public Paginator perPage(int perPage) {
			this.perPage = perPage;
			return this;
		}

		@CheckReturnValue
		public Paginator startingRow(int startingRow) {
			this.startingRow = startingRow;
			return this;
		}

		@CheckReturnValue
		public Paginator previousSlot(int row, int column) {
			return previousSlot(SlotPos.of(row, column));
		}

		@CheckReturnValue
		public Paginator previousSlot(SlotPos slot) {
			this.previousSlot = slot;
			return this;
		}

		@CheckReturnValue
		public Paginator nextSlot(int row, int column) {
			return nextSlot(SlotPos.of(row, column));
		}

		@CheckReturnValue
		public Paginator nextSlot(SlotPos slot) {
			this.nextSlot = slot;
			return this;
		}

		@CheckReturnValue
		public Paginator iterator(SlotIterator iterator) {
			this.iterator = iterator;
			return this;
		}

		@CheckReturnValue
		public Paginator useGUIArrows() {
			this.guiArrows = true;
			return this;
		}

		public void build() {
			if (hasResourcePack == null)
				this.hasResourcePack = ResourcePack.isEnabledFor(viewer);

			if (previousSlot == null)
				previousSlot = SlotPos.of(contents.config().getRows() - 1, 0);
			if (nextSlot == null)
				nextSlot = SlotPos.of(contents.config().getRows() - 1, 8);

			if (iterator == null)
				iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, startingRow, 0);

			Pagination page = contents.pagination();

			if (page.getPage() > items.size() / perPage)
				page.page(items.size() / perPage);
			int currentPage = page.getPage() + 1;

			int previousPage = Math.max(currentPage - 1, 1);
			int nextPage = currentPage + 1;

			String[] lore = {"&f", "&7Right click to jump to a page"};

			ItemBuilder previous = ResourcePackNumber.of(previousPage, ColorType.CYAN).get()
				.name("&fPrevious Page")
				.lore(lore);

			ItemBuilder next = ResourcePackNumber.of(nextPage, ColorType.CYAN).get()
				.name("&fNext Page")
				.lore(lore);

			if (guiArrows) {
				previous.material(CustomMaterial.GUI_ARROW_PREVIOUS).dyeColor(ColorType.CYAN).itemFlags(ItemFlag.HIDE_DYE);
				next.material(CustomMaterial.GUI_ARROW_NEXT).dyeColor(ColorType.CYAN).itemFlags(ItemFlag.HIDE_DYE);
			}

			page.setItemsPerPage(perPage);
			page.setItems(items.toArray(ClickableItem[]::new));
			page.addToIterator(iterator);

			if (page.getPage() > items.size() / perPage)
				page.page(items.size() / perPage);

			if (!page.isFirst())
				contents.set(previousSlot, ClickableItem.of(previous.build(), e -> {
					if (e.isRightClick())
						jumpToPage(page.getPage());
					else {
						onPageTurn(viewer);
						open(viewer, page.previous().getPage());
					}
				}));

			if (!page.isLast())
				contents.set(nextSlot, ClickableItem.of(next.build(), e -> {
					if (e.isRightClick())
						jumpToPage(page.getPage());
					else {
						onPageTurn(viewer);
						open(viewer, page.next().getPage());
					}
				}));
		}

		private void jumpToPage(int currentPage) {
			Nexus.getSignMenuFactory()
				.lines("", ARROWS, "Enter a", "page number")
				.prefix(Shops.PREFIX)
				.onError(() -> open(viewer, currentPage))
				.response(lines -> {
					if (lines[0].length() > 0) {
						String input = lines[0].replaceAll("[^\\d.-]+", "");
						if (!Utils.isInt(input))
							throw new InvalidInputException("Could not parse &e" + lines[0] + " &cas a page number");
						int pageNumber = Math.max(0, Integer.parseInt(input) - 1);
						open(viewer, pageNumber);
					} else
						open(viewer, currentPage);
				}).open(viewer);
		}
	}

}
