package gg.projecteden.nexus.features.recipes.functionals.backpacks;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.events.FakePlayerInteractEvent;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Backpack;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.utils.ItemUtils.find;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.decolorize;
import static gg.projecteden.nexus.utils.StringUtils.paste;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

public class Backpacks extends FunctionalRecipe {

	@Getter
	public static ItemStack defaultBackpack = new ItemBuilder(CustomMaterial.BACKPACK_3D_BASIC).itemFlags(ItemFlag.HIDE_DYE).name("Backpack").build();
	public static final String NBT_KEY = "BackpackId";

	@Override
	public ItemStack getResult() {
		return getDefaultBackpack();
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.BACKPACKS;
	}

	@NonNull
	@Override
	public @NotNull Recipe getRecipe() {
		return shaped("121", "343", "111")
			.add('1', Material.LEATHER)
			.add('2', Material.TRIPWIRE_HOOK)
			.add('3', Material.SHULKER_SHELL)
			.add('4', Material.CHEST)
			.toMake(getResult())
			.getRecipe();
	}

	public static boolean hasBackpack(Player player) {
		return Arrays.stream(player.getInventory().getContents()).anyMatch(Backpacks::isBackpack);
	}

	public static boolean isOldBackpack(ItemStack item) {
		if (!isBackpack(item)) return false;
		return MaterialTag.SHULKER_BOXES.isTagged(item);
	}

	public static ItemStack convertOldToNew(ItemStack old) {
		ItemBuilder newBuilder = new ItemBuilder(defaultBackpack);
		ItemBuilder oldBuilder = new ItemBuilder(old);

		newBuilder.name(oldBuilder.name());
		newBuilder.nbt(nbt -> nbt.setString(NBT_KEY, getBackpackId(old)));

		return copyContents(oldBuilder.build(), newBuilder.build());
	}

	public static boolean isBackpack(ItemStack item) {
		if (isNullOrAir(item))
			return false;

		return !isNullOrEmpty(new NBTItem(item).getString(NBT_KEY));
	}

	public static String getBackpackId(ItemStack item) {
		if (!isBackpack(item))
			return null;

		return new NBTItem(item).getString(NBT_KEY);
	}

	public static void openBackpack(Player player, Decoration decoration) {
		openBackpack(player, decoration.getItem(player), decoration.getItemFrame());
	}

	public static void openBackpack(Player player, ItemStack backpack) {
		openBackpack(player, backpack, null);
	}

	public static void openBackpack(Player player, ItemStack backpack, ItemFrame frame) {
		new SoundBuilder(Sound.BLOCK_CHEST_OPEN).receiver(player).volume(.3f).play();
		new BackpackMenu(player, backpack, frame);
	}

	public static BackpackTier getTier(ItemStack backpack) {
		NBTItem nbtItem = new NBTItem(backpack);
		for (int i = BackpackTier.values().length - 1; i >= 0; i--)
			if (nbtItem.hasKey(BackpackTier.values()[i].getNBTKey()))
				return BackpackTier.values()[i];
		return BackpackTier.BASIC;
	}

	public static ItemStack setTier(ItemStack backpack, BackpackTier tier) {
		return new ItemBuilder(backpack)
			.nbt(nbt -> {
				for (BackpackTier _tier : BackpackTier.values())
					nbt.removeKey(_tier.getNBTKey());

				nbt.setBoolean(tier.getNBTKey(), true);
			})
			.modelId(tier.getModelID())
			.build();
	}

	private static ItemStack copyContents(ItemStack oldBackpack, ItemStack newBackpack) {
		List<ItemStack> contents = ItemUtils.getNBTContentsOfNonInventoryItem(oldBackpack, getTier(newBackpack).getRows() * 9);
		if (MaterialTag.SHULKER_BOXES.isTagged(oldBackpack) && contents.isEmpty())
			contents = new ItemBuilder(oldBackpack).shulkerBoxContents();

		return ItemUtils.setNBTContentsOfNonInventoryItem(newBackpack, contents);
	}

	@EventHandler
	public void onClickBackpack(InventoryClickEvent event) {
		if (!isBackpack(event.getCurrentItem()))
			return;

		if (!event.getClick().isRightClick())
			return;

		if (!(event.getClickedInventory() instanceof PlayerInventory))
			return;

		if (!(event.getWhoClicked() instanceof Player player))
			return;

		Optional<SmartInventory> smartInv = SmartInvsPlugin.manager().getInventory(player);
		if (smartInv.isPresent() && !smartInv.get().isCloseable())
			return;

		event.setCancelled(true);
		player.closeInventory();
		openBackpack(player, event.getCurrentItem());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBackpackPrepareCraft(PrepareItemCraftEvent event) {
		List<ItemStack> matrix = getFilteredMatrix(event);

		if (matrix.size() < 2)
			return;

		ItemStack backpack = find(matrix, Backpacks::isBackpack);

		if (backpack == null)
			return;

		if (isNullOrAir(event.getInventory().getResult()))
			return;

		Component displayName = backpack.getItemMeta().hasDisplayName() ? backpack.getItemMeta().displayName() : Component.text("Backpack");

		ItemStack newBackpack = new ItemBuilder(event.getInventory().getResult().clone()).name(displayName).build();

		ItemStack newBackpackFinal = copyContents(backpack, newBackpack);
        DecorationUtils.updateLore(newBackpackFinal, null);
        event.getInventory().setResult(newBackpackFinal);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onCraftBackpackApplyID(PrepareItemCraftEvent event) {
		if (event.getRecipe() == null)
			return;

		if (!(event.getView().getPlayer() instanceof Player))
			return;

		final ItemStack result = event.getInventory().getResult();
		if (!defaultBackpack.equals(result))
			return;

		final ItemStack backpack = getBackpack(result.clone());
		event.getInventory().setResult(backpack);
	}

	public static ItemStack getBackpack() {
		return getBackpack(null);
	}

	public static ItemStack getBackpack(ItemStack backpack) {
		if (backpack == null)
			backpack = defaultBackpack.clone();

		return new ItemBuilder(backpack)
			.nbt(nbt -> nbt.setString(NBT_KEY, randomAlphabetic(10)))
			.build();
	}

	@EventHandler
	public void onClickBackpack(PlayerInteractEvent event) {
		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		ItemStack item = event.getItem();
		if (!isBackpack(item))
			return;

		if (event.getHand() == EquipmentSlot.OFF_HAND && isBackpack(event.getPlayer().getInventory().getItemInMainHand()))
			return;

		if (event.getPlayer().isSneaking()) // Allow to be placed by Decorations
			return;

		if (event instanceof FakePlayerInteractEvent)
			return;

		if (event.getClickedBlock() != null) {
			if (event.getClickedBlock().getType() == Material.WATER_CAULDRON)
				return;
		}

		event.setCancelled(true);
		openBackpack(event.getPlayer(), item);
	}


	@EventHandler
	public void onClickEntityWithBackpack(PlayerInteractEntityEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (!isBackpack(item)) {
			item = event.getPlayer().getInventory().getItemInOffHand();
			if (!isBackpack(item))
				return;
		}

		if (event.getPlayer().isSneaking())
			return;

		event.setCancelled(true);
		openBackpack(event.getPlayer(), item);
	}

	@EventHandler
	public void on(DecorationInteractEvent event) {
		Decoration decoration = event.getDecoration();
		if (!(decoration.getConfig() instanceof Backpack))
			return;

		if (event.getInteractType() != InteractType.RIGHT_CLICK)
			return;

		if (event.getPlayer().isSneaking())
			return;

		Player player = event.getPlayer();
		if (decoration.canEdit(player)) {
			event.setCancelled(true);
			openBackpack(player, decoration);
		}
	}

	@EventHandler
	public void cancelOffHandInteractions(PlayerInteractEvent event) {
		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		if (event.getHand() != EquipmentSlot.OFF_HAND)
			return;

		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (!isBackpack(item))
			item = event.getPlayer().getInventory().getItemInOffHand();
		if (!isBackpack(item))
			return;

		if (event instanceof FakePlayerInteractEvent)
			return;

		if (event.getPlayer().isSneaking()) // Allow to be placed by Decorations
			return;

		event.setCancelled(true);
	}

	public static class BackpackMenu implements TemporaryMenuListener {
		private static final Map<String, BackpackHolder> HOLDERS = new HashMap<>();

		@Getter
		private final Player player;
		private final ItemStack backpack;
		private final List<ItemStack> originalItems;
		private final ItemFrame frame;

		@Getter
		private final BackpackHolder inventoryHolder;

		public BackpackMenu(Player player, ItemStack backpack, ItemFrame frame) {
			this.player = player;
			this.backpack = backpack;
			this.frame = frame;
			this.originalItems = ItemUtils.getNBTContentsOfNonInventoryItem(backpack, getTier(backpack).getRows() * 9);

			this.inventoryHolder = HOLDERS.computeIfAbsent(getBackpackId(backpack), BackpackHolder::new);

			try {
				verifyInventory(player);
				if (inventoryHolder.getInventory() != null)
					open(inventoryHolder.getInventory());
				else
					open(getTier(backpack).getRows(), originalItems);
			} catch (Exception ex) {
				ex.printStackTrace();
				PlayerUtils.send(player, StringUtils.getPrefix("Backpacks") + "&c" + ex.getMessage());
			}
		}

		@AllArgsConstructor
		public static class BackpackHolder extends CustomInventoryHolder {
			private String id;
		}

		@Override
		public String getTitle() {
			String displayName = backpack.getItemMeta().getDisplayName();
			if (frame != null) {
				final NBTItem nbtItem = new NBTItem(frame.getItem());
				if (nbtItem.hasKey(DecorationConfig.NBT_DECOR_NAME))
					displayName = nbtItem.getString(DecorationConfig.NBT_DECOR_NAME);
			}

			if (!isNullOrEmpty(displayName) && !decolorize(displayName).equalsIgnoreCase("&fBackpack"))
				return "&8" + (decolorize(displayName).startsWith("&f") ? displayName.substring(2) : displayName);

			return "&8Backpack";
		}

		@EventHandler
		public void onDropBackpack(PlayerDropItemEvent event) {
			if (player != event.getPlayer())
				return;

			if (!isBackpack(event.getItemDrop().getItemStack()))
				return;

			event.setCancelled(true);
		}

		// Cancel Moving Shulker Boxes While backpack is open
		@EventHandler
		public void onClickBackpack(InventoryClickEvent event) {
			if (player != event.getWhoClicked())
				return;

			if (event.getClickedInventory() == null)
				return;

			ItemStack item = event.getClickedInventory().getItem(event.getSlot());
			if (event.getClick() == ClickType.NUMBER_KEY)
				item = player.getInventory().getContents()[event.getHotbarButton()];

			if (!MaterialTag.SHULKER_BOXES.isTagged(item) && !isBackpack(item))
				return;

			event.setCancelled(true);
		}

		@EventHandler
		public void on(DecorationDestroyEvent event) {
			if (!(event.getDecoration().getConfig() instanceof Backpack))
				return;

			if (frame == null)
				return;

			if (event.getDecoration().getItemFrame() != frame)
				return;

			player.closeInventory();
		}

		@Override
		public boolean keepAirSlots() {
			return true;
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			if (backpack == null) {
				handleError(contents);
				return;
			}

			ItemUtils.setNBTContentsOfNonInventoryItem(backpack, contents);
			if (frame != null)
				frame.setItem(backpack);

			player.updateInventory();
			Tasks.wait(1, () -> {
				player.updateInventory();

				if (inventoryHolder.getInventory().getViewers().isEmpty())
					HOLDERS.remove(getBackpackId(backpack));
			});
		}

		private void handleError(List<ItemStack> contents) {
			Nexus.warn("There was an error while saving Backpack contents for " + player.getName());
			Nexus.warn("Below is a serialized paste of the original and new contents in the backpack:");
			Nexus.warn("Old Contents: " + paste(Json.toString(Json.serialize(originalItems))));
			Nexus.warn("New Contents: " + paste(Json.toString(Json.serialize(contents))));
			PlayerUtils.send(player, "&cThere was an error while saving your backpack items. Please report this to staff to retrieve your lost items.");
		}

		private static void verifyInventory(Player player) {
			List<String> ids = new ArrayList<>();
			for (ItemStack item : player.getInventory().getContents()) {
				if (!isBackpack(item))
					continue;

				final String id = getBackpackId(item);
				if (ids.contains(id))
					throw new InvalidInputException("Duplicate backpacks found, please contact staff");
				ids.add(id);
			}
		}
	}

	@AllArgsConstructor
	public enum BackpackTier {
		BASIC(3),
		IRON(4),
		GOLD(5),
		DIAMOND(6),
		NETHERITE(6);

		@Getter
		final int rows;

		public int getModelID() {
			return 11000 + ordinal();
		}

		public String getNBTKey() {
			return "BP_TIER_" + name();
		}

		public static void initDecoration() {
			for (BackpackTier tier : values()) {
				new Backpack(tier);
			}
		}
	}

}
