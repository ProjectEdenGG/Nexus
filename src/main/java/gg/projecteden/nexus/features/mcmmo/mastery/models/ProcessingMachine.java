package gg.projecteden.nexus.features.mcmmo.mastery.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener.CustomInventoryHolder;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.TickableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.models.trust.TrustsUser;
import gg.projecteden.nexus.models.trust.TrustsUser.TrustType;
import gg.projecteden.nexus.models.trust.TrustsUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ProcessingMachine extends FloorThing implements TickableDecoration, Interactable {

	private static final Map<UUID, ProcessingMachineInventoryHolder> HOLDERS = new HashMap<>();
	private static final ItemStack EMPTY_ITEM = new ItemBuilder(ItemModelType.INVISIBLE).hideTooltip().build();

	private static final int[] INPUT_SLOTS = new int[] {  2,  3,  4,  5,  6,
														  11, 12, 13, 14, 15 };
	private static final int[] OUTPUT_SLOTS = new int[] { 28, 29, 30, 31, 32, 33, 34,
														  37, 38, 39, 40, 41, 42, 43,
														  46, 47, 38, 49, 50, 51, 52 };
	private static final int PROCESSING_SLOT = 22;

	private static final Map<UUID, Integer> TICKING = new HashMap<>();

	public ProcessingMachine(String name, ItemModelType itemModelType) {
		super(true, name, itemModelType, HitboxFloor._2x2x2);
		Nexus.registerListener(new ProcessingMachineListener(this));
	}

	public abstract Map<List<Material>, Map<ItemStack, Integer>> getConversions();

	public abstract String getProcessingIconBaseModel();

	@Override
	public void tick(Location location, ItemFrame itemFrame) {
		tick2(location, itemFrame);
		Tasks.wait(2, () -> tick2(location, itemFrame));
	}

	private void tick2(Location location, ItemFrame itemFrame) {
		if (!itemFrame.isValid() || !location.isWorldLoaded() || !location.isChunkLoaded())
			return;

		ProcessingMachineInventoryHolder holder = HOLDERS.computeIfAbsent(itemFrame.getUniqueId(), _ -> getHolder(itemFrame));

		if (holder.getInputItems().stream().noneMatch(Objects::nonNull)) {
			if (TICKING.containsKey(itemFrame.getUniqueId())) {
				TICKING.remove(itemFrame.getUniqueId());
				holder.getInventory().setItem(PROCESSING_SLOT, getProcessItem(0));
			}
			return;
		}
		if (holder.getOutputItems().stream().noneMatch(Objects::isNull)) return;

		Integer tick = TICKING.getOrDefault(itemFrame.getUniqueId(), 0);
		tick++;

		if (tick > 10) {
			tick = 0;
			process(holder, itemFrame);
		}
		TICKING.put(itemFrame.getUniqueId(), tick);

		holder.getInventory().setItem(PROCESSING_SLOT, getProcessItem(tick));
	}

	private ItemStack getProcessItem(int tick) {
		return new ItemBuilder(Material.PAPER)
			.model(getProcessingIconBaseModel() + tick)
			.hideTooltip()
			.build();
	}

	private void process(ProcessingMachineInventoryHolder holder, ItemFrame itemFrame) {
		ItemStack input = holder.getInputItems().stream().filter(Objects::nonNull).findFirst().orElse(null);
		if (input == null) return;

		Material type = input.getType();
		input.subtract();

		Map<ItemStack, Integer> weights = null;
		for (Entry<List<Material>, Map<ItemStack, Integer>> conversions : getConversions().entrySet())
			if (conversions.getKey().contains(type))
				weights = conversions.getValue();
		if (weights == null) return;

		ItemStack output = rollOutput(weights);

		if (output.getType().isAir()) {
			holder.saveToItemFrame(itemFrame);
			return;
		}

		for (int i : OUTPUT_SLOTS) {
			if (Nullables.isNullOrAir(holder.getInventory().getItem(i)) || ItemUtils.isModelMatch(holder.getInventory().getItem(i), EMPTY_ITEM)) {
				holder.getInventory().setItem(i, new ItemStack(output));
				break;
			}

			ItemStack stack = holder.getInventory().getItem(i);
			if (!ItemUtils.isModelMatch(stack, output)) continue;
			if (stack.getAmount() < stack.getMaxStackSize()) {
				stack.add();
				break;
			}
		}

		holder.saveToItemFrame(itemFrame);
	}

	private static ItemStack rollOutput(Map<ItemStack, Integer> weights) {
		double total = 0.0;
		for (double weight : weights.values()) {
			if (!Double.isFinite(weight) || weight < 0.0)
				throw new IllegalArgumentException("Drop weights must be finite and nonnegative");
			total += weight;
		}

		if (!Double.isFinite(total) || total <= 0.0)
			throw new IllegalArgumentException("Drop weights must have a finite, positive total");

		double roll = ThreadLocalRandom.current().nextDouble(total);
		double cumulative = 0.0;
		for (var entry : weights.entrySet()) {
			cumulative += entry.getValue();
			if (roll < cumulative)
				return entry.getKey();
		}

		throw new IllegalStateException("Drop roll exceeded the total weight");
	}

	@AllArgsConstructor
	private static class ProcessingMachineListener implements Listener {

		private static final TrustsUserService TRUST_SERVICE = new TrustsUserService();
		private final ProcessingMachine processingMachine;

		@EventHandler
		public void on(DecorationInteractEvent event) {
			final Player player = event.getPlayer();
			final UUID owner = event.getDecoration().getOwner(player);

			if (event.getInteractType() == InteractType.LEFT_CLICK)
				return;

			if (owner == null)
				return;

			boolean isMachine = event.getDecoration().is(this.processingMachine);
			if (!isMachine)
				return;

			TrustsUser trustsUser = TRUST_SERVICE.get(owner);
			if (!owner.equals(player.getUniqueId()) && !trustsUser.trusts(TrustType.DECORATIONS, player))
				return;

			new ProcessingMachineInventory(player, event.getDecoration().getItemFrame(), processingMachine);
		}

		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void on(DecorationDestroyEvent event) {
			boolean isMachine = event.getDecoration().is(this.processingMachine);
			if (!isMachine)
				return;

			List<ItemStack> input = ItemUtils.getNBTContentsOfNonInventoryItem(event.getDecoration().getItemFrame().getItem(), INPUT_SLOTS.length, "Input");
			List<ItemStack> output = ItemUtils.getNBTContentsOfNonInventoryItem(event.getDecoration().getItemFrame().getItem(), OUTPUT_SLOTS.length, "Output");

			for (ItemStack item : input)
				if (Nullables.isNotNullOrAir(item))
					event.getDrops().add(item);
			for (ItemStack item : output)
				if (Nullables.isNotNullOrAir(item))
					event.getDrops().add(item);
		}
	}

	private ProcessingMachineInventoryHolder getHolder(ItemFrame itemFrame) {
		ProcessingMachineInventoryHolder inventoryHolder = new ProcessingMachineInventoryHolder(itemFrame.getUniqueId());

		JsonBuilder json = new JsonBuilder(InventoryTexture.GUI_MCMMO_PROCESSING_MACHINE.getMenuTexture(6))
			.next("&8" + this.name);

		Inventory inventory = Bukkit.createInventory(inventoryHolder, 6 * 9, json.build());
		for (int i = 0; i < inventory.getSize(); i++)
			inventory.setItem(i, EMPTY_ITEM);
		for (int i : INPUT_SLOTS)
			inventory.setItem(i, null);

		List<ItemStack> input = ItemUtils.getNBTContentsOfNonInventoryItem(itemFrame.getItem(), INPUT_SLOTS.length, "Input");
		List<ItemStack> output = ItemUtils.getNBTContentsOfNonInventoryItem(itemFrame.getItem(), OUTPUT_SLOTS.length, "Output");

		for (int i = 0; i < input.size(); i++)
			inventory.setItem(INPUT_SLOTS[i], input.get(i));
		for (int i = 0; i < output.size(); i++)
			if (Nullables.isNotNullOrAir(output.get(i)))
				inventory.setItem(OUTPUT_SLOTS[i], output.get(i));

		ItemBuilder builder = new ItemBuilder(ItemModelType.GUI_INFO).name("&e" + this.name)
			.lore("&3Converts materials to")
			.lore("&3rarer items")
			.lore(" ")
			.lore("&3Valid Input:");
		for (List<Material> materials : getConversions().keySet())
			for (Material material : materials)
				builder.lore("&e - " + StringUtils.camelCase(material));

		inventory.setItem(8, builder.build());

		inventory.setItem(PROCESSING_SLOT, getProcessItem(0));

		inventoryHolder.setInventory(inventory);

		return inventoryHolder;
	}

	@Data
	@AllArgsConstructor
	private static class ProcessingMachineInventoryHolder extends CustomInventoryHolder {
		private final UUID uuid;

		private List<ItemStack> getInputItems() {
			List<ItemStack> items = new ArrayList<>();
			for (int i : INPUT_SLOTS)
				if (Nullables.isNotNullOrAir(getInventory().getItem(i)))
					items.add(getInventory().getItem(i));
				else
					items.add(null);
			return items;
		}

		private List<ItemStack> getOutputItems() {
			List<ItemStack> items = new ArrayList<>();
			for (int i : OUTPUT_SLOTS)
				if (Nullables.isNotNullOrAir(getInventory().getItem(i)) && !ItemUtils.isModelMatch(getInventory().getItem(i), EMPTY_ITEM))
					items.add(getInventory().getItem(i));
				else
					items.add(null);
			return items;
		}

		private void saveToItemFrame(ItemFrame itemFrame) {
			ItemStack item = itemFrame.getItem();
			ItemUtils.setNBTContentsOfNonInventoryItem(item, getInputItems(), "Input");
			ItemUtils.setNBTContentsOfNonInventoryItem(item, getOutputItems(), "Output");
			itemFrame.setItem(item);
		}
	}

	private static class ProcessingMachineInventory implements TemporaryMenuListener {

		private final Player player;
		private final ItemFrame itemFrame;
		@Getter
		private final ProcessingMachineInventoryHolder inventoryHolder;
		private final ProcessingMachine processingMachine;

		public ProcessingMachineInventory(Player player, ItemFrame itemFrame, ProcessingMachine processingMachine) {
			this.player = player;
			this.itemFrame = itemFrame;
			this.processingMachine = processingMachine;

			this.inventoryHolder = HOLDERS.computeIfAbsent(itemFrame.getUniqueId(), _ -> processingMachine.getHolder(itemFrame));

			open(inventoryHolder.getInventory());
		}

		@Override
		public String getTitle() {
			return InventoryTexture.GUI_MCMMO_PROCESSING_MACHINE.getMenuTexture() + this.processingMachine.getName();
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			inventoryHolder.saveToItemFrame(itemFrame);
		}

		@Override
		public Player getPlayer() {
			return this.player;
		}

		@EventHandler
		public void onClick(InventoryClickEvent event) {
			if (event.getClickedInventory() == null) return;
			if (!event.getClickedInventory().equals(this.inventoryHolder.getInventory())) return;
			if (!event.getWhoClicked().getUniqueId().equals(this.player.getUniqueId())) return;
			for (int i : INPUT_SLOTS)
				if (i == event.getSlot()) return;
			for (int i : OUTPUT_SLOTS)
				if (i == event.getSlot() && !ItemUtils.isModelMatch(event.getCurrentItem(), EMPTY_ITEM)) return;
			event.setCancelled(true);
		}

		@EventHandler(ignoreCancelled = true)
		public void onInventoryClick(InventoryClickEvent event) {
			if (!event.getInventory().equals(inventoryHolder.getInventory()))
				return;

			if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				if (event.getClickedInventory() != event.getView().getBottomInventory())
					return;

				ItemStack item = event.getCurrentItem();
				if (!Nullables.isNullOrAir(item) && !isValidInput(item))
					event.setCancelled(true);

				return;
			}

			if (!isInputSlot(event.getRawSlot()))
				return;

			ItemStack incoming = switch (event.getAction()) {
				case PLACE_ALL, PLACE_ONE, PLACE_SOME, SWAP_WITH_CURSOR -> event.getCursor();
				case HOTBAR_SWAP -> getHotbarItem(event);
				case PLACE_FROM_BUNDLE -> getBundleItem(event.getCursor());
				default -> null;
			};

			if (!Nullables.isNullOrAir(incoming) && !isValidInput(incoming))
				event.setCancelled(true);
		}

		@EventHandler(ignoreCancelled = true)
		public void onInventoryDrag(InventoryDragEvent event) {
			if (!event.getInventory().equals(inventoryHolder.getInventory()))
				return;

			for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
				if (!isInputSlot(entry.getKey()))
					continue;

				if (!isValidInput(entry.getValue())) {
					event.setCancelled(true);
					return;
				}
			}
		}

		private ItemStack getHotbarItem(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked();
			int hotbarButton = event.getHotbarButton();

			if (hotbarButton == -1)
				return player.getInventory().getItemInOffHand();

			return player.getInventory().getItem(hotbarButton);
		}

		private ItemStack getBundleItem(ItemStack bundle) {
			if (Nullables.isNullOrAir(bundle))
				return null;

			if (!(bundle.getItemMeta() instanceof BundleMeta bundleMeta))
				return null;

			List<ItemStack> items = bundleMeta.getItems();
			return items.isEmpty() ? null : items.getFirst();
		}

		private boolean isInputSlot(int rawSlot) {
			for (int inputSlot : INPUT_SLOTS)
				if (inputSlot == rawSlot)
					return true;

			return false;
		}

		private boolean isValidInput(ItemStack item) {
			for (List<Material> itemStacks : processingMachine.getConversions().keySet())
				for (Material itemStack : itemStacks)
					if (itemStack == item.getType())
						return true;
			return false;
		}

		@EventHandler
		public void onClickOutput(InventoryClickEvent event) {
			if (!event.getWhoClicked().getUniqueId().equals(this.player.getUniqueId())) return;
			if (event.getClickedInventory() == null) return;
			if (!event.getClickedInventory().equals(inventoryHolder.getInventory())) return;

			boolean clickedOutput = false;
			for (int i : OUTPUT_SLOTS)
				if (event.getRawSlot() == i)
					clickedOutput = true;
			if (!clickedOutput) return;

			Tasks.wait(1, () -> {
				for (int i : OUTPUT_SLOTS)
					if (Nullables.isNullOrAir(event.getClickedInventory().getItem(i)))
						event.getClickedInventory().setItem(i, EMPTY_ITEM);
			});
		}

	}

}
