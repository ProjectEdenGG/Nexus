package gg.projecteden.nexus.features.legacy.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

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
		if (event.getInventory().getHolder() != null)
			convert(event.getInventory());
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		convert(event.getEntity());
	}

	public static List<ItemStack> convert(List<ItemStack> contents) {
		return contents.stream().map(LegacyItems::convert).toList();
	}

	private static void convert(Inventory inventory) {
		for (var slot = new AtomicInteger(); slot.get() < inventory.getContents().length; slot.getAndIncrement())
			convert(inventory.getItem(slot.get()), converted -> inventory.setItem(slot.get(), converted));
	}

	private static void convert(ItemStack item, Consumer<ItemStack> setter) {
		if (isNullOrAir(item))
			return;

		ItemStack converted = convert(item);
		if (item.equals(converted))
			return;

		setter.accept(converted);
	}

	public static ItemStack convert(ItemStack item) {
		if (isNullOrAir(item))
			return item;

		if (CustomModelData.of(item) == 0)
			return item;

		final CustomModel newModel = CustomModel.convert(item);
		if (newModel == null)
			return item;

		return new ItemBuilder(item)
			.material(newModel.getMaterial())
			.customModelData(newModel.getData())
			.build();
	}

	private static void convert(EntityEquipment equipment) {
		if (equipment == null)
			return;

		for (EquipmentSlot slot : EquipmentSlot.values())
			convert(equipment.getItem(slot), converted -> equipment.setItem(slot, converted, true));
	}

	private static void convert(Entity entity) {
		if (entity instanceof LivingEntity livingEntity)
			convert(livingEntity.getEquipment());

		if (entity instanceof InventoryHolder holder)
			convert(holder.getInventory());

		if (entity instanceof ItemFrame itemFrame)
			convert(itemFrame.getItem(), itemFrame::setItem);

		if (entity instanceof Item droppedItem)
			convert(droppedItem.getItemStack(), droppedItem::setItemStack);
	}

}

