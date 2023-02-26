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

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.menus.api.opener.ChestInventoryOpener;
import gg.projecteden.nexus.features.menus.api.opener.InventoryOpener;
import gg.projecteden.nexus.features.menus.api.opener.SpecialInventoryOpener;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class InventoryManager implements Listener {

	private final PluginManager pluginManager;

	@Getter
	private final Map<Player, SmartInventory> inventories;
	private final Map<Player, InventoryContents> contents;
	private final Map<Player, InventoryContents> selfContents;
	private final Map<Player, ItemStack[]> realContents;

	private final Map<Player, Integer> updateTasks;

	private final List<InventoryOpener> defaultOpeners;
	private final List<InventoryOpener> openers;

	public InventoryManager() {
		this.pluginManager = Bukkit.getPluginManager();

		this.inventories = new HashMap<>();
		this.contents = new HashMap<>();
		this.selfContents = new HashMap<>();
		this.realContents = new HashMap<>();

		this.updateTasks = new HashMap<>();

		this.defaultOpeners = Arrays.asList(
			new ChestInventoryOpener(),
			new SpecialInventoryOpener()
		);

		this.openers = new ArrayList<>();
	}

	public void init() {
		Nexus.registerListener(this);
	}

	public InventoryOpener findOpener(InventoryType type) {
		Optional<InventoryOpener> opInv = this.openers.stream()
			.filter(opener -> opener.supports(type))
			.findAny();

		if (opInv.isEmpty()) {
			opInv = this.defaultOpeners.stream()
				.filter(opener -> opener.supports(type))
				.findAny();
		}

		return opInv.orElseThrow(() -> new IllegalStateException("No opener found for the inventory type " + type.name()));
	}

	public void registerOpeners(InventoryOpener... openers) {
		this.openers.addAll(Arrays.asList(openers));
	}

	public List<Player> getOpenedPlayers(SmartInventory inv) {
		List<Player> list = new ArrayList<>();

		this.inventories.forEach((player, playerInv) -> {
			if (inv.equals(playerInv))
				list.add(player);
		});

		return list;
	}

	public Optional<SmartInventory> getInventory(Player player) {
		return Optional.ofNullable(this.inventories.get(player));
	}

	protected void setInventory(Player player, SmartInventory inv) {
		if (inv == null)
			this.inventories.remove(player);
		else
			this.inventories.put(player, inv);
	}

	public Optional<InventoryContents> getContents(Player player) {
		return Optional.ofNullable(this.contents.get(player));
	}

	protected void setContents(Player player, InventoryContents contents) {
		if (contents == null)
			this.contents.remove(player);
		else
			this.contents.put(player, contents);
	}

	public Optional<InventoryContents> getSelfContents(Player player) {
		return Optional.ofNullable(this.selfContents.get(player));
	}

	protected void setSelfContents(Player player, InventoryContents contents) {
		if (contents == null)
			this.selfContents.remove(player);
		else
			this.selfContents.put(player, contents);
	}

	public Optional<ItemStack[]> getRealContents(Player player) {
		return Optional.ofNullable(this.realContents.get(player));
	}

	public void setRealContents(Player player, ItemStack[] contents) {
		if (contents == null)
			this.realContents.remove(player);
		else
			this.realContents.put(player, contents);
	}

	protected void scheduleUpdateTask(Player player, SmartInventory inv) {
		this.updateTasks.put(player, Tasks.repeat(1, inv.getUpdateFrequency(), () -> inv.getProvider().update()));
	}

	protected void cancelUpdateTask(Player player) {
		if (updateTasks.containsKey(player)) {
			int bukkitTaskId = this.updateTasks.get(player);
			Bukkit.getScheduler().cancelTask(bukkitTaskId);
			this.updateTasks.remove(player);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		SmartInventory inv = inventories.get(player);

		if (inv == null)
			return;

		if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getAction() == InventoryAction.NOTHING) {
			event.setCancelled(true);
			return;
		}

		// TODO Logic to check if the slot it will go to is editable
		if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getClickedInventory() == player.getOpenInventory().getBottomInventory()) {
			event.setCancelled(true);
			return;
		}

		int row = event.getSlot() / 9;
		int column = event.getSlot() % 9;
		SlotPos slot = SlotPos.of(row, column);

		InventoryContents clickedContents = null;

		if (event.getClickedInventory() == player.getOpenInventory().getBottomInventory()) {
			if (!SmartInventory.checkSelfBounds(row, column))
				return;

			clickedContents = selfContents.get(player);
		} else if (event.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
			if (!inv.checkBounds(row, column))
				return;

			clickedContents = contents.get(player);
		}

		if (clickedContents == null)
			return;

		if (!clickedContents.isEditable(slot))
			event.setCancelled(true);

		inv.getListeners().stream()
			.filter(listener -> listener.getType() == InventoryClickEvent.class)
			.forEach(listener -> ((InventoryListener<InventoryClickEvent>) listener).accept(event));

		clickedContents.get(slot).ifPresent(item -> item.run(new ItemClickData(event, player, event.getCurrentItem(), slot)));

		if (!clickedContents.isEditable(slot))
			player.updateInventory();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryDrag(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (!inventories.containsKey(player))
			return;

		SmartInventory config = inventories.get(player);

		// TODO Test
		InventoryContents clickedContents = null;
		final Optional<InventoryContents> topContents = config.getManager().getContents(player);
		final Optional<InventoryContents> bottomContents = config.getManager().getSelfContents(player);

		if (topContents.isPresent()) {
			if (event.getInventory() == topContents.get().getInventory())
				clickedContents = topContents.get();
		}
		if (bottomContents.isPresent()) {
			if (event.getInventory() == bottomContents.get().getInventory())
				clickedContents = bottomContents.get();
		}

		if (clickedContents == null) {
			Nexus.log("Could not find clicked inventory");
			Thread.dumpStack();
			return;
		}

		for (int slot : event.getRawSlots()) {
			if (slot >= clickedContents.getInventory().getSize())
				continue;

			event.setCancelled(true);
			break;
		}

		config.getListeners().stream()
			.filter(listener -> listener.getType() == InventoryDragEvent.class)
			.forEach(listener -> ((InventoryListener<InventoryDragEvent>) listener).accept(event));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryOpen(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();

		if (!inventories.containsKey(player))
			return;

		SmartInventory inv = inventories.get(player);

		inv.getListeners().stream()
			.filter(listener -> listener.getType() == InventoryOpenEvent.class)
			.forEach(listener -> ((InventoryListener<InventoryOpenEvent>) listener).accept(event));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();

		if (!inventories.containsKey(player))
			return;

		SmartInventory inv = inventories.get(player);

		inv.getListeners().stream()
			.filter(listener -> listener.getType() == InventoryCloseEvent.class)
			.forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener).accept(event));

		if (inv.isCloseable()) {
			final var items = new ArrayList<>(Arrays.asList(event.getInventory().getContents()));
			inventories.get(player).getProvider().onClose(inv.getManager());
			inventories.get(player).getProvider().onClose(event, items);
			event.getInventory().clear();
			InventoryManager.this.cancelUpdateTask(player);

			setInventory(player, null);
			setContents(player, null);
		} else
			Tasks.sync(() -> player.openInventory(event.getInventory()));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (!inventories.containsKey(player))
			return;

		SmartInventory inv = inventories.get(player);

		inv.getListeners().stream()
			.filter(listener -> listener.getType() == PlayerQuitEvent.class)
			.forEach(listener -> ((InventoryListener<PlayerQuitEvent>) listener).accept(event));

		inventories.remove(player);
		contents.remove(player);
		selfContents.remove(player);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPluginDisable(PluginDisableEvent event) {
		new HashMap<>(inventories).forEach((player, inv) -> {
			inv.getListeners().stream()
				.filter(listener -> listener.getType() == PluginDisableEvent.class)
				.forEach(listener -> ((InventoryListener<PluginDisableEvent>) listener).accept(event));

			inv.close(player);
		});

		inventories.clear();
		contents.clear();
		selfContents.clear();
	}

}
