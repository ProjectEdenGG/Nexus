package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
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
		getColorPicker().open(player());
	}

	private SmartInventory getColorPicker() {
		return SmartInventory.builder()
			.title(StringUtils.colorize("&3Color Picker"))
			.size(3, 9)
			.provider(new HorsePickerColorProvider())
			.build();
	}

	private SmartInventory getMarkingPicker(HorseColor color) {
		String title = "&3Markings Picker (" + color.getColor() + StringUtils.camelCase(color.getName()) + "&3)";
		return SmartInventory.builder()
			.title(StringUtils.colorize(title))
			.size(3, 9)
			.provider(new HorsePickerMarkingsProvider(Horse.Color.valueOf(color.name())))
			.build();

	}

	@AllArgsConstructor
	public enum HorseColor {
		WHITE(new ItemStack(Material.WHITE_WOOL), "&f"),
		GRAY(new ItemStack(Material.LIGHT_GRAY_WOOL), "&7", "Dapple Gray"),
		BLACK(new ItemStack(Material.BLACK_TERRACOTTA), "&8"),
		CREAMY(new ItemStack(Material.OAK_LOG), "&e"),
		CHESTNUT(new ItemStack(Material.RED_TERRACOTTA), "&c"),
		BROWN(new ItemStack(Material.BROWN_WOOL), "&6"),
		DARK_BROWN(new ItemStack(Material.BROWN_TERRACOTTA), "&6");

		@Getter
		ItemStack item;
		@Getter
		String color, name;

		HorseColor(ItemStack item, String color) {
			this.item = item;
			this.color = color;
			this.name = name();
		}
	}

	@AllArgsConstructor
	public enum HorseMarking {
		NONE(new ItemStack(Material.WHITE_WOOL), "&7No Markings"),
		WHITE(new ItemStack(Material.QUARTZ_BLOCK), "&7White Stockings"),
		WHITEFIELD(new ItemStack(Material.WHITE_CONCRETE), "&7White Patches"),
		WHITE_DOTS(new ItemStack(Material.SNOW_BLOCK), "&7White Dots"),
		BLACK_DOTS(new ItemStack(Material.BLACK_WOOL), "&7Black Dots");

		@Getter
		ItemStack item;
		@Getter
		String name;
	}

	public class HorsePickerColorProvider extends MenuUtils implements InventoryProvider {
		@Override
		public void init(Player player, InventoryContents contents) {
			int column = 1;
			for (HorseColor color : HorseColor.values()) {
				contents.set(1, column++, ClickableItem.from(nameItem(color.getItem(),
					color.getColor() + StringUtils.camelCase(color.getName())),
					e -> getMarkingPicker(color).open(player)));
			}
		}
	}

	@AllArgsConstructor
	public class HorsePickerMarkingsProvider extends MenuUtils implements InventoryProvider {
		Horse.Color color;

		@Override
		public void init(Player player, InventoryContents contents) {
			int column = 2;
			for (HorseMarking marking : HorseMarking.values()) {
				contents.set(1, column++, ClickableItem.from(nameItem(marking.getItem(), marking.getName()), e -> {
					spawnHorse(player, color, Horse.Style.valueOf(marking.name()));
					LuckPermsUtils.PermissionChange.unset().permissions("horsepicker.pick").player(player).runAsync();
					player.closeInventory();
				}));
			}
		}
	}

	private void spawnHorse(Player player, Horse.Color color, Horse.Style style) {
		Horse horse = player.getWorld().spawn(player.getLocation().clone().add(0, 1, 0), Horse.class);
		horse.setColor(color);
		horse.setStyle(style);
		horse.setOwner(player);
		horse.setAdult();
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4f);
		horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
		horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setBaseValue(0.96f);
		horse.setHealth(30.0);
	}
}
