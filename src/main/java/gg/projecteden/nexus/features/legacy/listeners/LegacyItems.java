package gg.projecteden.nexus.features.legacy.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.legacy.LegacyCommand.LegacyVaultMenu.LegacyVaultHolder;
import gg.projecteden.nexus.features.listeners.Beehives;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider.SmartInventoryHolder;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.vaults.VaultCommand.VaultMenu.VaultHolder;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class LegacyItems implements Listener {

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent event) {
		Tasks.sync(() -> {
			for (World world : Bukkit.getWorlds())
				for (Entity entity : world.getEntities())
					convert(entity);
		});
	}

	@EventHandler
	public void on(InventoryOpenEvent event) {
		final InventoryHolder holder = event.getInventory().getHolder();
		if (holder == null)
			return;

		if (holder instanceof Player player && !PlayerUtils.isSelf(event.getPlayer(), player))
			return;

		if (holder instanceof VaultHolder && WorldGroup.of(event.getPlayer()) == WorldGroup.LEGACY)
			return;

		if (holder instanceof LegacyVaultHolder && WorldGroup.of(event.getPlayer()) != WorldGroup.LEGACY)
			return;

		if (holder instanceof SmartInventoryHolder)
			return;

		convert(holder.getInventory().getLocation(), event.getInventory());
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		Tasks.wait(3, () -> {
			if (event.getEntity().isValid())
				convert(event.getEntity());
		});
	}

	@EventHandler
	public void on(ChunkLoadEvent event) {
		Tasks.sync(() -> {
			for (@NotNull Entity entity : event.getChunk().getEntities())
				convert(entity);
		});
	}

	public static List<ItemStack> convert(Location location, List<ItemStack> contents) {
		return contents.stream().map(item -> convert(location, item)).toList();
	}

	private static void convert(Location location, Inventory inventory) {
		for (var slot = new AtomicInteger(); slot.get() < inventory.getContents().length; slot.getAndIncrement())
			convert(location, inventory.getItem(slot.get()), converted -> inventory.setItem(slot.get(), converted));
	}

	private static void convert(Location location, ItemStack item, Consumer<ItemStack> setter) {
		if (Nullables.isNullOrAir(item))
			return;

		ItemStack converted = convert(location, item);
		if (ItemUtils.isModelMatch(item, converted))
			return;

		setter.accept(converted);
	}

	public static ItemStack convert(Location location, ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return item;

		item = convertIfShulkerBox(location.getWorld(), item, null);
		item = Beehives.addLore(item);
		item = convertCrateKeys(item);

		ItemBuilder builder = new ItemBuilder(item);
		if (builder.customModelData() == 0)
			return item;

		if (builder.material() == Material.LEATHER_HORSE_ARMOR && builder.customModelData() >= 2000 && builder.customModelData() < 3000)
			return builder.number(builder.customModelData() - 2000).build();

		CustomModel newModel;
		try {
			newModel = CustomModel.convert(item, location);
		} catch (Exception e) {
			e.printStackTrace();
			return item;
		}

		if (newModel == null)
			return item;

		final var converted = new ItemBuilder(item)
			.material(newModel.getMaterial())
			.model(newModel.getData());

		return convertIfShulkerBox(location.getWorld(), item, converted);
	}

	private static ItemStack convertCrateKeys(ItemStack item) {
		CrateType type = CrateType.fromOldKey(item);
		ItemStack key = type == null ? item : type.getKey().clone();
		key.setAmount(item.getAmount());
		return key;
	}

	private static ItemStack convertIfShulkerBox(World world, ItemStack item, ItemBuilder builder) {
		final ItemBuilder converted = builder == null ? new ItemBuilder(item) : builder;
		if (MaterialTag.SHULKER_BOXES.isTagged(converted.material()))
			converted
				.shulkerBox(new ItemBuilder(item).shulkerBoxContents())
				.nbt(nbt -> {
					if (WorldGroup.of(world) == WorldGroup.LEGACY) {
						nbt.removeKey("BackpackId");
						nbt.setString(LegacyShulkerBoxes.NBT_KEY, RandomStringUtils.randomAlphabetic(10));
					}
				});

		return converted.build();
	}

	private static void convert(Location location, EntityEquipment equipment) {
		if (equipment == null)
			return;

		for (EquipmentSlot slot : EnumUtils.valuesExcept(EquipmentSlot.class, EquipmentSlot.BODY))
			convert(location, equipment.getItem(slot), converted -> equipment.setItem(slot, converted, true));
	}

	private static void convert(Entity entity) {
		final Location location = entity.getLocation();
		if (entity instanceof LivingEntity livingEntity)
			convert(location, livingEntity.getEquipment());

		if (entity instanceof InventoryHolder holder)
			convert(location, holder.getInventory());

		if (entity instanceof ItemFrame itemFrame)
			convert(location, itemFrame.getItem(), itemFrame::setItem);

		if (entity instanceof Item droppedItem)
			convert(location, droppedItem.getItemStack(), droppedItem::setItemStack);

		if (entity instanceof ItemDisplay display)
			convert(location, display.getItemStack(), display::setItemStack);
	}

}

