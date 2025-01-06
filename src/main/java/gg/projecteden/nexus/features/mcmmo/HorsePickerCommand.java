package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
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
@WikiConfig(rank = "Guest", feature = "McMMO Prestige")
public class HorsePickerCommand extends CustomCommand {

	public HorsePickerCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Open the horse picker menu")
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

	@Title("&3Color Picker")
	@Rows(3)
	public class HorsePickerColorProvider extends InventoryProvider {
		@Override
		public void init() {
			int column = 1;
			for (HorseColor color : HorseColor.values()) {
				contents.set(1, column++, ClickableItem.of(color.getMaterial(), color.getColor() + camelCase(color.getName()), e ->
					new HorsePickerMarkingsProvider(color).open(viewer)));
			}
		}
	}

	@Rows(3)
	@AllArgsConstructor
	public class HorsePickerMarkingsProvider extends InventoryProvider {
		private final HorseColor color;

		@Override
		public String getTitle() {
			return "&3Markings Picker (" + color.getColor() + StringUtils.camelCase(color.getName()) + "&3)";
		}

		@Override
		public void init() {
			int column = 2;
			for (HorseMarking marking : HorseMarking.values()) {
				contents.set(1, column++, ClickableItem.of(marking.getMaterial(), marking.getName(), e -> {
					spawnHorse(viewer, color.getColor(), Horse.Style.valueOf(marking.name()));
					LuckPermsUtils.PermissionChange.unset().permissions("horsepicker.pick").player(viewer).runAsync();
					viewer.closeInventory();
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
		horse.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.4f);
		horse.getAttribute(Attribute.MAX_HEALTH).setBaseValue(30);
		horse.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0.96f);
		horse.setHealth(30.0);
	}
}
