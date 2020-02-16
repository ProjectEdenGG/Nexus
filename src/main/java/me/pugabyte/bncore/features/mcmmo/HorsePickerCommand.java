package me.pugabyte.bncore.features.mcmmo;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Permission("horsepicker.pick")
public class HorsePickerCommand extends CustomCommand {

	public HorsePickerCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void horsePicker() {
		SmartInventory colorINV = SmartInventory.builder()
				.title(Utils.colorize("&3Color Picker"))
				.size(3, 9)
				.provider(new HorsePickerColorProvider())
				.build();
		colorINV.open(player());
	}

	public enum HorseColor {
		WHITE(new ItemStack(Material.WOOL), "&f"),
		GRAY(new ItemStack(Material.WOOL, 1, (byte) 8), "&7", "Dapple Gray"),
		BLACK(new ItemStack(Material.STAINED_CLAY, 1, (byte) 15), "&8"),
		CREAMY(new ItemStack(Material.WOOD), "&e"),
		CHESTNUT(new ItemStack(Material.STAINED_CLAY, 1, (byte) 14), "&c"),
		BROWN(new ItemStack(Material.WOOL, 1, (byte) 12), "&6"),
		DARK_BROWN(new ItemStack(Material.STAINED_CLAY, 1, (byte) 12), "&6");

		ItemStack item;
		String color, name;

		HorseColor(ItemStack item, String color) {
			this.item = item;
			this.color = color;
			this.name = name();
		}

		HorseColor(ItemStack item, String color, String name) {
			this.item = item;
			this.color = color;
			this.name = name;
		}

		ItemStack getItem() {
			return item;
		}

		String getColor() {
			return color;
		}

		String getName() {
			return name;
		}
	}

	public enum HorseMarking {
		NONE(new ItemStack(Material.WOOL), "&7No Markings"),
		WHITE(new ItemStack(Material.QUARTZ_BLOCK), "&7White Stockings"),
		WHITEFIELD(new ItemStack(Material.CONCRETE), "&7White Patches"),
		WHITE_DOTS(new ItemStack(Material.SNOW_BLOCK), "&7White Dots"),
		BLACK_DOTS(new ItemStack(Material.WOOL, 1, (byte) 7), "&7Black Dots");

		ItemStack item;
		String name;

		HorseMarking(ItemStack item, String name) {
			this.item = item;
			this.name = name;
		}

		ItemStack getItem() {
			return item;
		}

		String getName() {
			return name;
		}
	}

	public class HorsePickerColorProvider extends MenuUtils implements InventoryProvider {

		@Override
		public void init(Player player, InventoryContents contents) {
			int column = 1;
			for (HorseColor color : HorseColor.values()) {
				contents.set(1, column++, ClickableItem.from(nameItem(color.getItem(), color.getColor() +
						Utils.camelCase(color.getName())), e -> {
					SmartInventory markingProvider = SmartInventory.builder()
							.title(Utils.colorize("&3Markings Picker (" +
									color.getColor() + Utils.camelCase(color.getName()) + "&3)"))
							.size(3, 9)
							.provider(new HorsePickerMarkingsProvider(Horse.Color.valueOf(color.name())))
							.build();
					markingProvider.open(player);
				}));
			}
		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {
		}
	}

	public class HorsePickerMarkingsProvider extends MenuUtils implements InventoryProvider {

		Horse.Color color;

		public HorsePickerMarkingsProvider(Horse.Color color) {
			this.color = color;
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			int column = 2;
			for (HorseMarking marking : HorseMarking.values()) {
				contents.set(1, column++, ClickableItem.from(nameItem(marking.getItem(), marking.getName()), e -> {
					spawnHorse(player, color, Horse.Style.valueOf(marking.name()));
					runConsoleCommand("pex user " + player.getName() + " remove horsepicker.pick");
				}));
			}
		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {
		}
	}

	private void spawnHorse(Player player, Horse.Color color, Horse.Style style) {
		Horse horse = player.getWorld().spawn(player.getLocation().clone().add(0, 1, 0), Horse.class);
		horse.setColor(color);
		horse.setStyle(style);
		horse.setOwner(player);
	}
}
