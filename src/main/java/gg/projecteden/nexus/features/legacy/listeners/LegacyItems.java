package gg.projecteden.nexus.features.legacy.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.features.legacy.LegacyCommand.LegacyVaultMenu.LegacyVaultHolder;
import gg.projecteden.nexus.features.listeners.Beehives;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.vaults.VaultCommand.VaultMenu.VaultHolder;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

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
		if (isNullOrAir(item))
			return;

		ItemStack converted = convert(world, item);
		if (item.equals(converted))
			return;

		setter.accept(converted);
	}

	public static ItemStack convert(World world, ItemStack item) {
		if (isNullOrAir(item))
			return item;

		item = convertIfShulkerBox(world, item, null);
		item = Beehives.addLore(item);

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

	private static ItemStack convertIfShulkerBox(World world, ItemStack item, ItemBuilder builder) {
		final ItemBuilder converted = builder == null ? new ItemBuilder(item) : builder;
		if (MaterialTag.SHULKER_BOXES.isTagged(converted.material()))
			converted
				.shulkerBox(new ItemBuilder(item).shulkerBoxContents())
				.nbt(nbt -> {
					if (WorldGroup.of(world) == WorldGroup.LEGACY) {
						nbt.removeKey("BackpackId");
						nbt.setString(LegacyShulkerBoxes.NBT_KEY, randomAlphabetic(10));
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

