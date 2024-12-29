package gg.projecteden.nexus.features.legacy.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.features.legacy.LegacyCommand.LegacyVaultMenu.LegacyVaultHolder;
import gg.projecteden.nexus.features.listeners.Beehives;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider.SmartInventoryHolder;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.vaults.VaultCommand.VaultMenu.VaultHolder;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.*;

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

		convert(event.getPlayer().getWorld(), event.getInventory());
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		Tasks.wait(3, () -> {
			if (event.getEntity().isValid())
				convert(event.getEntity());
		});
	}

	public static List<ItemStack> convert(World world, List<ItemStack> contents) {
		return contents.stream().map(item -> convert(world, item)).toList();
	}

	private static void convert(World world, Inventory inventory) {
		for (var slot = new AtomicInteger(); slot.get() < inventory.getContents().length; slot.getAndIncrement())
			convert(world, inventory.getItem(slot.get()), converted -> inventory.setItem(slot.get(), converted));
	}

	private static void convert(World world, ItemStack item, Consumer<ItemStack> setter) {
		if (Nullables.isNullOrAir(item))
			return;

		ItemStack converted = convert(world, item);
		if (item.equals(converted))
			return;

		setter.accept(converted);
	}

	public static ItemStack convert(World world, ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return item;

		item = convertIfShulkerBox(world, item, null);
		item = Beehives.addLore(item);
		item = convertCrateKeys(item);

		if (ModelId.of(item) == 0)
			return item;

		final CustomModel newModel = CustomModel.convert(item);
		if (newModel == null)
			return item;

		final var converted = new ItemBuilder(item)
			.material(newModel.getMaterial())
			.modelId(newModel.getData());

		return convertIfShulkerBox(world, item, converted);
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

	private static void convert(World world, EntityEquipment equipment) {
		if (equipment == null)
			return;

		for (EquipmentSlot slot : EquipmentSlot.values())
			convert(world, equipment.getItem(slot), converted -> equipment.setItem(slot, converted, true));
	}

	private static void convert(Entity entity) {
		final World world = entity.getWorld();
		if (entity instanceof LivingEntity livingEntity)
			convert(world, livingEntity.getEquipment());

		if (entity instanceof InventoryHolder holder)
			convert(world, holder.getInventory());

		if (entity instanceof ItemFrame itemFrame)
			convert(world, itemFrame.getItem(), itemFrame::setItem);

		if (entity instanceof Item droppedItem)
			convert(world, droppedItem.getItemStack(), droppedItem::setItemStack);
	}

}

