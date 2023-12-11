package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.Utils.moveLastToFirst;

public class SplitEnchantsCommand extends CustomCommand {

	public SplitEnchantsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		new SplitEnchantsMenu().open(player());
	}

	private static class SplitEnchantsMenu extends InventoryProvider {
		private static final Map<Material, SlotPos> SLOTS = Map.of(
			Material.ENCHANTED_BOOK, SlotPos.of(1, 2),
			Material.LAPIS_LAZULI, SlotPos.of(1, 4),
			Material.BOOK, SlotPos.of(1, 6)
		);

		private static final int LAPIS_COST = 3;
		private static final int LEVELS_COST = 3;

		private final Map<Material, ItemStack> inputs = new HashMap<>();
		private List<ItemBuilder> resultBooks;
		private int rotation;

		@Override
		public void init() {
			contents.clear();
			addCloseItem();

			SLOTS.forEach((material, slot) -> {
				var item = inputs.getOrDefault(material, new ItemStack(Material.AIR)).clone();
				contents.set(slot, ClickableItem.of(item, this::handleSlotClick));
			});

			this.resultBooks = getResultBooks();
			var results = populateResults();

			if (resultBooks.stream().anyMatch(result -> result.enchants().size() > 1)) {
				var rotateItem = new ItemBuilder(Material.ARROW).name("&eRotate Enchants");
				contents.set(3, 1, ClickableItem.of(rotateItem.build(), e -> rotateEnchants()));
			}

			// TODO Exp orb item
			var levelsCost = getLevelsCost();
			String expCostName = "&eExperience cost: " + (viewer.getLevel() < levelsCost ? "&c" : "&a") + levelsCost + " Levels";
			contents.set(4, 8, ClickableItem.empty(new ItemBuilder(Material.EXPERIENCE_BOTTLE).name(expCostName).amount(levelsCost)));

			var checkmark = checkmark();
			if (!results || viewer.getLevel() < LEVELS_COST)
				checkmark.name("&7Confirm").dyeColor(Color.GRAY);
			else
				checkmark.name("&aConfirm").lore();

			contents.set(5, 8, ClickableItem.of(checkmark.build(), this::confirm));
			Tasks.wait(1, () -> viewer.updateInventory());
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			for (ItemStack input : inputs.values())
				PlayerUtils.giveItem(viewer, input);
		}

		private void handleSlotClick(ItemClickData event) {
			var player = event.getPlayer();
			var cursorItem = player.getItemOnCursor();
			var slotItem = event.getItem();

			if (isNullOrAir(slotItem) && isNullOrAir(cursorItem))
				return;

			var expected = SLOTS.keySet().stream().filter(material -> SLOTS.get(material).equals(event.getSlot())).findFirst();

			if (expected.isEmpty())
				return;

			if (!isNullOrAir(cursorItem))
				if (cursorItem.getType() != expected.get())
					return;

			if (cursorItem.getType() == Material.ENCHANTED_BOOK)
				if (new ItemBuilder(cursorItem).enchants().size() <= 1)
					return;

			inputs.put(expected.get(), cursorItem.clone());
			player.setItemOnCursor(isNullOrAir(slotItem) ? new ItemStack(Material.AIR) : slotItem.clone());

			init();
		}

		private void rotateEnchants() {
			++rotation;
			init();
		}

		public int getLapisCost() {
			return resultBooks.isEmpty() ? 0 : (resultBooks.size() - 1) * LAPIS_COST;
		}

		public int getLevelsCost() {
			return resultBooks.isEmpty() ? 0 : (resultBooks.size() - 1) * LEVELS_COST;
		}

		public int getBooksCost() {
			return resultBooks.isEmpty() ? 0 : resultBooks.size() - 1;
		}

		private void confirm(ItemClickData event) {
			if (!validateInputSlots())
				return;

			if (resultBooks.isEmpty())
				return;

			var lapis = getInputItem(Material.LAPIS_LAZULI).getAmount();
			var books = getInputItem(Material.BOOK).getAmount();
			var levels = viewer.getLevel();

			lapis -= getLapisCost();
			levels -= getLevelsCost();
			books -= getBooksCost();

			if (levels < 0) {
				init();
				return;
			}

			for (ItemBuilder book : resultBooks)
				PlayerUtils.giveItem(viewer, book.sortEnchants().build());

			PlayerUtils.giveItem(viewer, new ItemStack(Material.LAPIS_LAZULI, lapis));
			PlayerUtils.giveItem(viewer, new ItemStack(Material.BOOK, books));
			viewer.setLevel(levels);

			inputs.clear();

			init();
		}

		private boolean populateResults() {
			clearResults();

			if (!validateInputSlots())
				return false;

			var enchantedBook = getInputItem(Material.ENCHANTED_BOOK);
			var repairCost = ((Repairable) enchantedBook.getItemMeta()).getRepairCost();

			var resultSlots = getResultSlots();
			var slots = resultSlots.iterator();

			if (resultBooks.isEmpty())
				return false;

			resultBooks.stream()
				.map(result -> new ItemBuilder(result).lore("", "&7Repair Cost: " + repairCost))
				.forEach(lore -> contents.set(slots.next(), ClickableItem.empty(lore)));

			return true;
		}

		private boolean validateInputItem(Material material) {
			var item = inputs.get(material);
			if (isNullOrAir(item))
				return false;
			if (item.getType() != material)
				return false;
			if (item.getType() == Material.ENCHANTED_BOOK && new ItemBuilder(item).enchants().size() <= 1)
				return false;
			if (item.getType() == Material.LAPIS_LAZULI && item.getAmount() < LAPIS_COST)
				return false;

			return true;
		}

		@NotNull
		private ItemStack getInputItem(Material material) {
			if (!validateInputItem(material))
				throw new InvalidInputException("Input item " + StringUtils.camelCase(material) + " invalid");

			return inputs.get(material);
		}

		private boolean validateInputSlots() {
			for (Material material : SLOTS.keySet())
				if (!validateInputItem(material))
					return false;

			return true;
		}

		@NotNull
		private List<ItemBuilder> getResultBooks() {
			if (!validateInputSlots())
				return Collections.emptyList();

			var lapis = getInputItem(Material.LAPIS_LAZULI);
			var books = getInputItem(Material.BOOK);
			var meta = getInputItem(Material.ENCHANTED_BOOK).getItemMeta();
			var repairCost = ((Repairable) meta).getRepairCost();
			var storedEnchants = new LinkedHashMap<>(((EnchantmentStorageMeta) meta).getStoredEnchants());
			var levels = viewer.getLevel();

			final int lapisAvailable = (lapis.getAmount() / LAPIS_COST) + 1;
			final int levelsAvailable = (levels / LEVELS_COST) + 1;
			final int booksAvailable = books.getAmount() + 1;
			final int slotsAvailable = getResultSlots().size();

			if (lapisAvailable == 0 || levelsAvailable == 0)
				return Collections.emptyList();

			var rotation = this.rotation % storedEnchants.size();
			while (rotation-- > 0)
				moveLastToFirst(storedEnchants);

			final List<ItemBuilder> results = new ArrayList<>() {{
				storedEnchants.forEach((enchant, level) -> {
					if (size() == slotsAvailable || size() == booksAvailable || size() == lapisAvailable || size() == levelsAvailable)
						get(size() - 1).enchant(enchant, level);
					else
						add(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(enchant, level).repairCost(repairCost));
				});
			}};

			if (results.size() == 1)
				return Collections.emptyList();

			return results;
		}

		private List<SlotPos> getResultSlots() {
			return new ArrayList<>() {{
				for (int row : List.of(3, 4))
					for (int col : List.of(2, 3, 4, 5, 6))
						add(SlotPos.of(row, col));
			}};
		}

		private void clearResults() {
			for (SlotPos slot : getResultSlots())
				contents.set(slot, ClickableItem.AIR);
		}

	}

}
