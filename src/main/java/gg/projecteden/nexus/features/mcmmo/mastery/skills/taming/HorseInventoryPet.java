package gg.projecteden.nexus.features.mcmmo.mastery.skills.taming;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mcmmo.HorsePickerCommand.HorseColor;
import gg.projecteden.nexus.features.mcmmo.HorsePickerCommand.HorseMarking;
import gg.projecteden.nexus.features.mcmmo.HorsePickerCommand.HorsePickerColorProvider;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class HorseInventoryPet implements Listener {

	public static final ItemStack ITEM = new ItemBuilder(ItemModelType.HORSE_INVENTORY_PET)
		.maxStackSize(1)
		.name("&eHorse Pet")
		.lore("&3Right click in your inventory")
		.lore("&3to summon a perfect horse")
		.lore(" ")
		.lore("&3Shift-Right click to configure")
		.lore("&3horse color and markings")
		.build();

	private static final NamespacedKey KEY = new NamespacedKey(Nexus.getInstance(), "horse_pet_horse");
	private static final NamespacedKey COLOR_KEY = new NamespacedKey(Nexus.getInstance(), "horse_pet_color");
	private static final NamespacedKey MARKING_KEY = new NamespacedKey(Nexus.getInstance(), "horse_pet_markings");

	static {
		Nexus.registerListener(new HorseInventoryPet());
	}

	@EventHandler
	public void onInventoryShiftClick(InventoryClickEvent event) {
		if (!event.isRightClick()) return;
		if (!event.getClick().isShiftClick()) return;
		if (!ItemUtils.isModelMatch(event.getClickedInventory().getItem(event.getSlot()), ITEM)) return;
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getClickedInventory().getItem(event.getSlot());
		new HorsePickerColorProvider((color, marking) -> {
			item.editPersistentDataContainer(pdc -> {
				pdc.set(COLOR_KEY, PersistentDataType.STRING, color.name());
				pdc.set(MARKING_KEY, PersistentDataType.STRING, marking.name());
			});
		}).open(player);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!event.isRightClick()) return;
		if (event.getClick().isShiftClick()) return;
		ItemStack item = event.getClickedInventory().getItem(event.getSlot());
		if (!ItemUtils.isModelMatch(item, ITEM)) return;

		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		Location loc = player.getLocation();

		if (player.getVehicle() != null) {
			PlayerUtils.send(player, "&eYou are already riding an entity");
			return;
		}

		boolean isRoom = true;
		Location corner = loc.clone().subtract(1, 1, 1);
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 3; z++) {
					Location block = corner.clone().add(x, y, z);
					if (y == 0) {
						if (!block.getBlock().isSolid()) {
							isRoom = false;
							break;
						}
					} else {
						if (block.getBlock().isSolid()) {
							isRoom = false;
							break;
						}
					}
				}
			}
		}

		if (!isRoom) {
			PlayerUtils.send(player, "&eThere is not enough room for a horse here");
			return;
		}

		HorseColor color = RandomUtils.randomElement(HorseColor.values());
		HorseMarking marking = RandomUtils.randomElement(HorseMarking.values());

		if (item.getPersistentDataContainer().has(COLOR_KEY))
			color = HorseColor.valueOf(item.getPersistentDataContainer().get(COLOR_KEY, PersistentDataType.STRING));
		if (item.getPersistentDataContainer().has(MARKING_KEY))
			marking = HorseMarking.valueOf(item.getPersistentDataContainer().get(MARKING_KEY, PersistentDataType.STRING));

		HorseColor finalColor = color;
		HorseMarking finalMarking = marking;
		Horse horse = loc.getWorld().spawn(loc, Horse.class, _horse -> {
			_horse.setAdult();
			_horse.getAttribute(Attribute.MAX_HEALTH).setBaseValue(30.0);
			_horse.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3375);
			_horse.setJumpStrength(1.0);
			_horse.setHealth(30.0);

			_horse.setColor(finalColor.getHorseColor());
			_horse.setStyle(Style.valueOf(finalMarking.name()));

			_horse.setOwner(player);
			_horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));

			_horse.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
		});

		Tasks.wait(1, () -> horse.addPassenger(player));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDismount(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (!(event.getDismounted() instanceof Horse horse)) return;

		if (!horse.getPersistentDataContainer().has(KEY)) return;
		horse.remove();
	}

}
