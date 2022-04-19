package gg.projecteden.nexus.features.mcmmo;

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

@Permission("horsepicker.pick")
public class HorsePickerCommand extends CustomCommand {

	public HorsePickerCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		new HorsePickerColorProvider().open(player());
	}

	@Getter
	@AllArgsConstructor
	public enum HorseColor {
		WHITE(Material.WHITE_WOOL, "&f"),
		GRAY(Material.LIGHT_GRAY_WOOL, "&7", "Dapple Gray"),
		BLACK(Material.BLACK_TERRACOTTA, "&8"),
		CREAMY(Material.OAK_LOG, "&e"),
		CHESTNUT(Material.RED_TERRACOTTA, "&c"),
		BROWN(Material.BROWN_WOOL, "&6"),
		DARK_BROWN(Material.BROWN_TERRACOTTA, "&6");

		private final Material material;
		private final String color, name;

		HorseColor(Material material, String color) {
			this.material = material;
			this.color = color;
			this.name = name();
		}

		public Horse.Color getColor() {
			return Horse.Color.valueOf(name());
		}
	}

	@Getter
	@AllArgsConstructor
	public enum HorseMarking {
		NONE(Material.WHITE_WOOL, "&7No Markings"),
		WHITE(Material.QUARTZ_BLOCK, "&7White Stockings"),
		WHITEFIELD(Material.WHITE_CONCRETE, "&7White Patches"),
		WHITE_DOTS(Material.SNOW_BLOCK, "&7White Dots"),
		BLACK_DOTS(Material.BLACK_WOOL, "&7Black Dots");

		private final Material material;
		private final String name;
	}

	public class HorsePickerColorProvider extends InventoryProvider {

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
				.provider(this)
				.title("&3Color Picker")
				.rows(3)
				.build()
				.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			int column = 1;
			for (HorseColor color : HorseColor.values()) {
				contents.set(1, column++, ClickableItem.of(color.getMaterial(), color.getColor() + camelCase(color.getName()), e ->
					new HorsePickerMarkingsProvider(color).open(player)));
			}
		}
	}

	@AllArgsConstructor
	public class HorsePickerMarkingsProvider extends InventoryProvider {
		private final HorseColor color;

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
				.provider(this)
				.title("&3Markings Picker (" + color.getColor() + StringUtils.camelCase(color.getName()) + "&3)")
				.rows(3)
				.build()
				.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			int column = 2;
			for (HorseMarking marking : HorseMarking.values()) {
				contents.set(1, column++, ClickableItem.of(marking.getMaterial(), marking.getName(), e -> {
					spawnHorse(player, color.getColor(), Horse.Style.valueOf(marking.name()));
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
