package gg.projecteden.nexus.features.legacy.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.legacy.LegacyCommand.LegacyVaultMenu.LegacyVaultHolder;
import gg.projecteden.nexus.features.listeners.Beehives;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider.SmartInventoryHolder;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelInstance;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

		if (location != null)
			item = convertIfShulkerBox(location.getWorld(), item, null);
		item = Beehives.addLore(item);
		item = convertCrateKeys(item);

		ItemBuilder builder = new ItemBuilder(item);
		if (builder.customModelData() == 0)
			return item;

		item = manualConversions(item);
		builder = new ItemBuilder(item);
		if (Objects.equals(new ItemBuilder(item).model(), ItemModelType.GUI_NUMBER.getModel()))
			return item;

		if (builder.customModelData() == 0)
			return item;

		ItemModelInstance newModel;
		try {
			newModel = ItemModelInstance.convert(item, location);
		} catch (Exception e) {
			e.printStackTrace();
			return item;
		}

		if (newModel == null)
			return item;

		final var converted = new ItemBuilder(item)
			.material(newModel.getMaterial())
			.model(newModel.getItemModel());

		if (location == null)
			return converted.build();
		return convertIfShulkerBox(location.getWorld(), item, converted);
	}

	private static ItemStack manualConversions(ItemStack item) {
		ItemBuilder builder = new ItemBuilder(item);
		if (builder.material() == Material.LEATHER_HORSE_ARMOR && builder.customModelData() >= 2000 && builder.customModelData() < 3000)
			item = builder.number(builder.customModelData() - 2000).build();
		if (builder.material() == Material.LEATHER_HORSE_ARMOR && builder.customModelData() == 4009)
			item = builder.customModelData(4027).build(); // Straw hat was duped
		if (builder.material() == Material.LEATHER_HORSE_ARMOR && builder.customModelData() == 4005)
			item = builder.customModelData(4023).build(); // Mushroom hat was duped

		// stews
		if (builder.material() == Material.COOKIE && builder.customModelData() == 10000)
			item = builder.material(Material.BEETROOT_SOUP).maxStackSize(64).removeCustomModelData().resetName().build();
		if (builder.material() == Material.COOKIE && builder.customModelData() == 10001)
			item = builder.material(Material.MUSHROOM_STEW).maxStackSize(64).removeCustomModelData().resetName().build();
		if (builder.material() == Material.COOKIE && builder.customModelData() == 10002)
			item = builder.material(Material.RABBIT_STEW).maxStackSize(64).removeCustomModelData().resetName().build();

		return item;
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

	public static final Map<Integer, String> PLUSHIES = new HashMap<>() {{
		put(1, "decoration/plushies/player/standing/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
		put(2, "decoration/plushies/player/standing/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(3, "decoration/plushies/player/standing/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(4, "decoration/plushies/player/standing/d1de9ca8-78f6-4aae-87a1-8c112f675f12");
		put(5, "decoration/plushies/player/standing/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(6, "decoration/plushies/player/standing/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(7, "decoration/plushies/player/standing/56cb00fd-4738-47bc-be08-cb7c4f9a5a94");
		put(8, "decoration/plushies/player/standing/79f66fc9-a975-4043-8b6d-b4823182de62");
		put(9, "decoration/plushies/player/standing/9c27c601-0d86-4ff0-9e65-974c65734798");
		put(10, "decoration/plushies/player/standing/a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3");
		put(11, "decoration/plushies/player/standing/1d70383f-21ba-4b8b-a0b4-6c327fbdade1");
		put(12, "decoration/plushies/player/standing/1450eb3c-f1ec-4061-98b3-7857dad8f00d");
		put(13, "decoration/plushies/player/standing/4f06f692-0b42-4706-9193-bcc716ce5936");
		put(10001, "decoration/plushies/player/walking/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
		put(10002, "decoration/plushies/player/walking/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(10003, "decoration/plushies/player/walking/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(10004, "decoration/plushies/player/walking/d1de9ca8-78f6-4aae-87a1-8c112f675f12");
		put(10005, "decoration/plushies/player/walking/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(10006, "decoration/plushies/player/walking/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(10007, "decoration/plushies/player/walking/56cb00fd-4738-47bc-be08-cb7c4f9a5a94");
		put(10008, "decoration/plushies/player/walking/79f66fc9-a975-4043-8b6d-b4823182de62");
		put(10009, "decoration/plushies/player/walking/9c27c601-0d86-4ff0-9e65-974c65734798");
		put(10010, "decoration/plushies/player/walking/a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3");
		put(10011, "decoration/plushies/player/walking/1d70383f-21ba-4b8b-a0b4-6c327fbdade1");
		put(10012, "decoration/plushies/player/walking/1450eb3c-f1ec-4061-98b3-7857dad8f00d");
		put(10013, "decoration/plushies/player/walking/4f06f692-0b42-4706-9193-bcc716ce5936");
		put(20001, "decoration/plushies/player/t_pose/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
		put(20002, "decoration/plushies/player/t_pose/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(20003, "decoration/plushies/player/t_pose/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(20004, "decoration/plushies/player/t_pose/d1de9ca8-78f6-4aae-87a1-8c112f675f12");
		put(20005, "decoration/plushies/player/t_pose/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(20006, "decoration/plushies/player/t_pose/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(20007, "decoration/plushies/player/t_pose/56cb00fd-4738-47bc-be08-cb7c4f9a5a94");
		put(20008, "decoration/plushies/player/t_pose/79f66fc9-a975-4043-8b6d-b4823182de62");
		put(20009, "decoration/plushies/player/t_pose/9c27c601-0d86-4ff0-9e65-974c65734798");
		put(20010, "decoration/plushies/player/t_pose/a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3");
		put(20011, "decoration/plushies/player/t_pose/1d70383f-21ba-4b8b-a0b4-6c327fbdade1");
		put(20012, "decoration/plushies/player/t_pose/1450eb3c-f1ec-4061-98b3-7857dad8f00d");
		put(20013, "decoration/plushies/player/t_pose/4f06f692-0b42-4706-9193-bcc716ce5936");
		put(30001, "decoration/plushies/player/handstand/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
		put(30002, "decoration/plushies/player/handstand/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(30003, "decoration/plushies/player/handstand/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(30004, "decoration/plushies/player/handstand/d1de9ca8-78f6-4aae-87a1-8c112f675f12");
		put(30005, "decoration/plushies/player/handstand/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(30006, "decoration/plushies/player/handstand/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(30007, "decoration/plushies/player/handstand/56cb00fd-4738-47bc-be08-cb7c4f9a5a94");
		put(30008, "decoration/plushies/player/handstand/79f66fc9-a975-4043-8b6d-b4823182de62");
		put(30009, "decoration/plushies/player/handstand/9c27c601-0d86-4ff0-9e65-974c65734798");
		put(30010, "decoration/plushies/player/handstand/a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3");
		put(30011, "decoration/plushies/player/handstand/1d70383f-21ba-4b8b-a0b4-6c327fbdade1");
		put(30012, "decoration/plushies/player/handstand/1450eb3c-f1ec-4061-98b3-7857dad8f00d");
		put(30013, "decoration/plushies/player/handstand/4f06f692-0b42-4706-9193-bcc716ce5936");
		put(40001, "decoration/plushies/player/sitting/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
		put(40002, "decoration/plushies/player/sitting/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(40003, "decoration/plushies/player/sitting/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(40004, "decoration/plushies/player/sitting/d1de9ca8-78f6-4aae-87a1-8c112f675f12");
		put(40005, "decoration/plushies/player/sitting/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(40006, "decoration/plushies/player/sitting/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(40007, "decoration/plushies/player/sitting/56cb00fd-4738-47bc-be08-cb7c4f9a5a94");
		put(40008, "decoration/plushies/player/sitting/79f66fc9-a975-4043-8b6d-b4823182de62");
		put(40009, "decoration/plushies/player/sitting/9c27c601-0d86-4ff0-9e65-974c65734798");
		put(40010, "decoration/plushies/player/sitting/a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3");
		put(40011, "decoration/plushies/player/sitting/1d70383f-21ba-4b8b-a0b4-6c327fbdade1");
		put(40012, "decoration/plushies/player/sitting/1450eb3c-f1ec-4061-98b3-7857dad8f00d");
		put(40013, "decoration/plushies/player/sitting/4f06f692-0b42-4706-9193-bcc716ce5936");
		put(50001, "decoration/plushies/player/dabbing/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
		put(50002, "decoration/plushies/player/dabbing/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(50003, "decoration/plushies/player/dabbing/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(50004, "decoration/plushies/player/dabbing/d1de9ca8-78f6-4aae-87a1-8c112f675f12");
		put(50005, "decoration/plushies/player/dabbing/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(50006, "decoration/plushies/player/dabbing/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(50007, "decoration/plushies/player/dabbing/56cb00fd-4738-47bc-be08-cb7c4f9a5a94");
		put(50008, "decoration/plushies/player/dabbing/79f66fc9-a975-4043-8b6d-b4823182de62");
		put(50009, "decoration/plushies/player/dabbing/9c27c601-0d86-4ff0-9e65-974c65734798");
		put(50010, "decoration/plushies/player/dabbing/a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3");
		put(50011, "decoration/plushies/player/dabbing/1d70383f-21ba-4b8b-a0b4-6c327fbdade1");
		put(50012, "decoration/plushies/player/dabbing/1450eb3c-f1ec-4061-98b3-7857dad8f00d");
		put(50013, "decoration/plushies/player/dabbing/4f06f692-0b42-4706-9193-bcc716ce5936");
		put(60001, "decoration/plushies/player/riding_minecart/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
		put(60002, "decoration/plushies/player/riding_minecart/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(60003, "decoration/plushies/player/riding_minecart/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(60004, "decoration/plushies/player/riding_minecart/d1de9ca8-78f6-4aae-87a1-8c112f675f12");
		put(60005, "decoration/plushies/player/riding_minecart/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(60006, "decoration/plushies/player/riding_minecart/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(60007, "decoration/plushies/player/riding_minecart/56cb00fd-4738-47bc-be08-cb7c4f9a5a94");
		put(60008, "decoration/plushies/player/riding_minecart/79f66fc9-a975-4043-8b6d-b4823182de62");
		put(60009, "decoration/plushies/player/riding_minecart/9c27c601-0d86-4ff0-9e65-974c65734798");
		put(60010, "decoration/plushies/player/riding_minecart/a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3");
		put(60011, "decoration/plushies/player/riding_minecart/1d70383f-21ba-4b8b-a0b4-6c327fbdade1");
		put(60012, "decoration/plushies/player/riding_minecart/1450eb3c-f1ec-4061-98b3-7857dad8f00d");
		put(60013, "decoration/plushies/player/riding_minecart/4f06f692-0b42-4706-9193-bcc716ce5936");
		put(70001, "decoration/plushies/player/holding_globe/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
		put(70002, "decoration/plushies/player/holding_globe/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(70003, "decoration/plushies/player/holding_globe/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(70004, "decoration/plushies/player/holding_globe/d1de9ca8-78f6-4aae-87a1-8c112f675f12");
		put(70005, "decoration/plushies/player/holding_globe/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(70006, "decoration/plushies/player/holding_globe/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(70007, "decoration/plushies/player/holding_globe/56cb00fd-4738-47bc-be08-cb7c4f9a5a94");
		put(70008, "decoration/plushies/player/holding_globe/79f66fc9-a975-4043-8b6d-b4823182de62");
		put(70009, "decoration/plushies/player/holding_globe/9c27c601-0d86-4ff0-9e65-974c65734798");
		put(70010, "decoration/plushies/player/holding_globe/a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3");
		put(70011, "decoration/plushies/player/holding_globe/1d70383f-21ba-4b8b-a0b4-6c327fbdade1");
		put(70012, "decoration/plushies/player/holding_globe/1450eb3c-f1ec-4061-98b3-7857dad8f00d");
		put(70013, "decoration/plushies/player/holding_globe/4f06f692-0b42-4706-9193-bcc716ce5936");
		put(80001, "decoration/plushies/player/waving/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
		put(80002, "decoration/plushies/player/waving/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(80003, "decoration/plushies/player/waving/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(80004, "decoration/plushies/player/waving/d1de9ca8-78f6-4aae-87a1-8c112f675f12");
		put(80005, "decoration/plushies/player/waving/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(80006, "decoration/plushies/player/waving/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(80007, "decoration/plushies/player/waving/56cb00fd-4738-47bc-be08-cb7c4f9a5a94");
		put(80008, "decoration/plushies/player/waving/79f66fc9-a975-4043-8b6d-b4823182de62");
		put(80009, "decoration/plushies/player/waving/9c27c601-0d86-4ff0-9e65-974c65734798");
		put(80010, "decoration/plushies/player/waving/a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3");
		put(80011, "decoration/plushies/player/waving/1d70383f-21ba-4b8b-a0b4-6c327fbdade1");
		put(80012, "decoration/plushies/player/waving/1450eb3c-f1ec-4061-98b3-7857dad8f00d");
		put(80013, "decoration/plushies/player/waving/4f06f692-0b42-4706-9193-bcc716ce5936");
		put(90001, "decoration/plushies/player/funko_pop/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
		put(90002, "decoration/plushies/player/funko_pop/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(90003, "decoration/plushies/player/funko_pop/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(90004, "decoration/plushies/player/funko_pop/d1de9ca8-78f6-4aae-87a1-8c112f675f12");
		put(90005, "decoration/plushies/player/funko_pop/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(90006, "decoration/plushies/player/funko_pop/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(90007, "decoration/plushies/player/funko_pop/56cb00fd-4738-47bc-be08-cb7c4f9a5a94");
		put(90008, "decoration/plushies/player/funko_pop/79f66fc9-a975-4043-8b6d-b4823182de62");
		put(90009, "decoration/plushies/player/funko_pop/9c27c601-0d86-4ff0-9e65-974c65734798");
		put(90010, "decoration/plushies/player/funko_pop/a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3");
		put(90011, "decoration/plushies/player/funko_pop/1d70383f-21ba-4b8b-a0b4-6c327fbdade1");
		put(90012, "decoration/plushies/player/funko_pop/1450eb3c-f1ec-4061-98b3-7857dad8f00d");
		put(90013, "decoration/plushies/player/funko_pop/4f06f692-0b42-4706-9193-bcc716ce5936");
		put(100002, "decoration/plushies/player/funko_pop_admin/e9e07315-d32c-4df7-bd05-acfe51108234");
		put(100003, "decoration/plushies/player/funko_pop_admin/a4274d94-10f2-4663-af3b-a842c7ec729c");
		put(100005, "decoration/plushies/player/funko_pop_admin/0a2221e4-000c-4818-82ea-cd43df07f0d4");
		put(100006, "decoration/plushies/player/funko_pop_admin/88f9f7f6-7703-49bf-ad83-a4dec7e8022c");
		put(110001, "decoration/plushies/player/funko_pop_owner/86d7e0e2-c95e-4f22-8f99-a6e83b398307");
	}};


}

