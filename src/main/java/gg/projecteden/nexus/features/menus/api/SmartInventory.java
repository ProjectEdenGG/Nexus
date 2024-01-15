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

import com.google.common.base.Preconditions;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.menus.api.opener.InventoryOpener;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
@Getter
public class SmartInventory {

	private final InventoryManager manager;
	private String id;
	private Component title;
	private InventoryType type;
	private int rows, columns;
	@Setter
	private boolean closeable;
	private int updateFrequency;
	private InventoryProvider provider;
	private SmartInventory parent;
	private List<InventoryListener<? extends Event>> listeners;

	private SmartInventory(InventoryManager manager) {
		this.manager = manager;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Inventory open(Player player) {
		return open(player, 0, Collections.emptyMap());
	}

	public Inventory open(Player player, int page) {
		return open(player, page, Collections.emptyMap());
	}

	public Inventory open(Player player, Map<String, Object> properties) {
		return open(player, 0, properties);
	}

	public Inventory open(Player player, int page, Map<String, Object> properties) {
		Optional<SmartInventory> oldInv = manager.getInventory(player);

		oldInv.ifPresent(inv -> {
			final InventoryCloseEvent event = new InventoryCloseEvent(player.getOpenInventory());

			final var contents = new ArrayList<>(Arrays.asList(event.getInventory().getContents()));
			inv.getProvider().onClose(manager);
			inv.getProvider().onClose(event, contents);

			inv.getListeners().stream()
				.filter(listener -> listener.getType() == InventoryCloseEvent.class)
				.forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
					.accept(event));

			player.getOpenInventory().getTopInventory().clear();

			manager.setInventory(player, null);
		});

		InventoryContents contents = new InventoryContents(this, player);
		contents.pagination().page(page);
		properties.forEach(contents::setProperty);

		InventoryContents selfContents = new InventoryContents(this, player);
		contents.pagination().page(page);
		properties.forEach(contents::setProperty);

		manager.setContents(player, contents);
		manager.setSelfContents(player, selfContents);
		try {
			provider.setContents(contents);
			provider.setSelfContents(selfContents);
			provider.init();
		} catch (NexusException ex) {
			player.closeInventory();
			player.sendMessage(ex.withPrefix(getProvider().getPrefix()));
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			player.closeInventory();
			player.sendMessage(ChatColor.RED + "An unknown error occurred while trying to open the menu");
			return null;
		}

		InventoryOpener opener = manager.findOpener(type);
		Inventory inventory = opener.open(this, player);
		contents.setInventory(inventory);

		manager.setInventory(player, this);
		manager.scheduleUpdateTask(player, this);

		return inventory;
	}

	public void close(Player player) {
		final InventoryCloseEvent event = new InventoryCloseEvent(player.getOpenInventory());

		manager.getInventory(player).ifPresent(inv -> {
			final var contents = new ArrayList<>(Arrays.asList(event.getInventory().getContents()));
			inv.getProvider().onClose(manager);
			inv.getProvider().onClose(event, contents);
		});

		listeners.stream()
			.filter(listener -> listener.getType() == InventoryCloseEvent.class)
			.forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
				.accept(event));

		manager.setInventory(player, null);
		player.closeInventory();

		manager.setContents(player, null);
		manager.cancelUpdateTask(player);
	}

	/**
	 * Checks if this inventory has a slot at the specified position
	 *
	 * @param row Slot row (starts at 0)
	 * @param col Slot column (starts at 0)
	 */
	public boolean checkBounds(int row, int col) {
		if (row < 0 || col < 0)
			return false;
		return row < rows && col < columns;
	}

	public static boolean checkSelfBounds(int row, int col) {
		if (row < 0 || col < 0)
			return false;
		return row < 4 && col < 9;
	}

	@Getter
	public static final class Builder {

		private String id = "unknown";
		private Component title = Component.text("");
		private InventoryType type = InventoryType.CHEST;
		private Optional<Integer> rows = Optional.of(6);
		private Optional<Integer> columns = Optional.of(9);
		private boolean closeable = true;
		private int updateFrequency = 1;

		private InventoryManager manager;
		private InventoryProvider provider;
		private SmartInventory parent;

		private final List<InventoryListener<? extends Event>> listeners = new ArrayList<>();

		private Builder() {
		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder title(String title) {
			return title(new JsonBuilder(title));
		}

		public Builder title(ComponentLike title) {
			this.title = title.asComponent();
			return this;
		}

		public Builder type(InventoryType type) {
			this.type = type;
			return this;
		}

		public Builder size(int rows, int columns) {
			this.rows = Optional.of(rows);
			this.columns = Optional.of(columns);
			return this;
		}

		public Builder rows(int rows) {
			return size(rows, 9);
		}

		public Builder closeable(boolean closeable) {
			this.closeable = closeable;
			return this;
		}

		/**
		 * This method is used to configure the frequency at which the {@link InventoryProvider#update()}
		 * method is called. Defaults to 1
		 *
		 * @param frequency The inventory update frequency, in ticks
		 * @throws IllegalArgumentException If frequency is smaller than 1.
		 */
		public Builder updateFrequency(int frequency) {
			Preconditions.checkArgument(frequency > 0, "frequency must be > 0");
			this.updateFrequency = frequency;
			return this;
		}

		public Builder provider(InventoryProvider provider) {
			this.provider = provider;
			return this;
		}

		public Builder parent(SmartInventory parent) {
			this.parent = parent;
			return this;
		}

		public Builder listener(InventoryListener<? extends Event> listener) {
			this.listeners.add(listener);
			return this;
		}

		public Builder manager(InventoryManager manager) {
			this.manager = manager;
			return this;
		}

		public SmartInventory build() {
			if (this.provider == null)
				throw new IllegalStateException("The provider of the SmartInventory.Builder must be set");

			if (this.manager == null) {          // if it's null, use the default instance
				this.manager = SmartInvsPlugin.manager();
				if (this.manager == null) {      // if it's still null, throw an exception
					throw new IllegalStateException("Manager of the SmartInventory.Builder must be set, or SmartInvs should be loaded as a plugin");
				}
			}

			SmartInventory inv = new SmartInventory(manager);
			inv.id = this.id;
			inv.title = this.title;
			inv.type = this.type;
			inv.rows = this.rows.orElseGet(() -> getDefaultDimensions(type).getRow());
			inv.columns = this.columns.orElseGet(() -> getDefaultDimensions(type).getColumn());
			inv.closeable = this.closeable;
			inv.updateFrequency = this.updateFrequency;
			inv.provider = this.provider;
			inv.parent = this.parent;
			inv.listeners = this.listeners;
			return inv;
		}

		private SlotPos getDefaultDimensions(InventoryType type) {
			InventoryOpener opener = this.manager.findOpener(type);
			SlotPos size = opener.defaultSize(type);
			if (size == null)
				throw new IllegalStateException(String.format("%s returned null for input InventoryType %s", opener.getClass().getSimpleName(), type));

			return size;
		}

	}

}
