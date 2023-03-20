package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStore;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStoreLayouts;
import gg.projecteden.nexus.features.workbenches.DyeStation;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.StainChoice;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig;
import gg.projecteden.nexus.models.trophy.TrophyType;
import gg.projecteden.nexus.utils.FontUtils;
import gg.projecteden.nexus.utils.TitleBuilder;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Permission(Group.STAFF)
public class DecorationCommand extends CustomCommand {

	public DecorationCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		try {
			// Init all decoration creators
			DecorationType.init();
			Pose.init();
			TrophyType.init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Path("info")
	@Description("Display information on the held decoration")
	void info() {
		ItemStack itemStack = getToolRequired();
		DecorationConfig config = DecorationConfig.of(itemStack);
		if (config == null)
			error("You are not holding a decoration!");

		line(5);
		send("&3Name: &e" + config.getName());
		send("&3Id: &e" + config.getId());
		send("&3Material: &e" + config.getMaterial());
		send("&3Model Id: &e" + config.getModelId());
		send("&3Lore: &e" + config.getLore());
		line();

		send("&3Place Sound: &e" + config.getPlaceSound());
		send("&3Hit Sound: &e" + config.getPlaceSound());
		send("&3Break Sound: &e" + config.getPlaceSound());
		line();

		send("&3Rotation Type: &e" + config.getRotationType());
		send("&3Disabled Placements:");
		for (PlacementType disabledPlacement : config.getDisabledPlacements()) {
			send(" &e- " + camelCase(disabledPlacement));
		}
		send("&3Rotatable: &e" + config.isRotatable());
		line();

		send("&3Inherited Classes:");
		for (String clazz : DecorationUtils.getInstancesOf(config)) {
			send(" &e- " + clazz);
		}
		line();

		send("&3Hitboxes: ");
		for (Hitbox hitbox : config.getHitboxes()) {
			String material = StringUtils.camelCase(hitbox.getMaterial());

			String hitboxType = " &e- " + material;
			if (hitbox.getMaterial() == Material.LIGHT)
				hitboxType += "&3, Level: &e" + hitbox.getLightLevel();

			hitboxType += " &3-> ";
			if (hitbox.getOffsets().isEmpty()) {
				hitboxType += "&eOrigin";
			} else {
				String offsets = "&3[&e";
				for (BlockFace blockFace : hitbox.getOffsets().keySet()) {
					offsets += "&e" + StringUtils.camelCase(blockFace) + "&3, &e" + hitbox.getOffsets().get(blockFace) + "&3, ";
				}

				hitboxType += " " + offsets.substring(0, (offsets.length() - 2)) + "&3]";
			}

			send(hitboxType);

		}
		line();
	}

	@Path("catalog [theme]")
	@Description("Open the catalog menu")
	void viewCatalog(@Arg("General") Catalog.Theme theme) {
		Catalog.openCatalog(player(), theme, null, null);
	}

	@Path("getCatalog <theme>")
	@Permission(Group.ADMIN)
	@Description("Get the catalog book")
	void getCatalog(Catalog.Theme theme) {
		giveItem(theme.getNamedItem());
		send("Given " + StringUtils.camelCase(theme) + " Catalog");
	}

	@Path("get <type>")
	@Description("Get the decoration")
	void get(DecorationConfig config) {
		giveItem(config.getItem());
		send("Given " + StringUtils.camelCase(config.getName()));
	}

	@Path("dye color <color>")
	@Permission(Group.STAFF)
	@Description("Dye an item")
	void dye(ChatColor chatColor) {
		ItemStack item = getToolRequired();
		Colored.of(chatColor.getColor()).apply(item);
		// TODO: APPLY LORE
	}

	@Path("dye stain <stain>")
	@Permission(Group.STAFF)
	@Description("Stain an item")
	void dye(StainChoice stainChoice) {
		ItemStack item = getToolRequired();
		Colored.of(stainChoice.getColor()).apply(item);
		// TODO: APPLY LORE
	}

	@Path("getItem magicDye")
	@Permission(Group.ADMIN)
	@Description("Spawn a magic dye item")
	void get_magicDye() {
		giveItem(DyeStation.getMagicDye().build());
	}

	@Path("getItem magicStain")
	@Permission(Group.ADMIN)
	@Description("Spawn a magic stain item")
	void get_magicStain() {
		giveItem(DyeStation.getMagicStain().build());
	}

	@Path("getItem paintbrush")
	@Description("Spawn a paintbrush")
	@Permission(Group.ADMIN)
	void get_paintbrush() {
		giveItem(DyeStation.getPaintbrush().build());
	}

	@HideFromWiki
	@Path("debug tooltip [--line1] [--line2] [--line3] [--addSpaces]")
	@Permission(Group.ADMIN)
	void debug_tooltip(@Switch String line1, @Switch String line2, @Switch String line3, @Switch int addSpaces) {
		new TitleBuilder().subtitle(FontUtils.getToolTip(line1, line2, line3, addSpaces, player())).players(player()).send();
	}

	@HideFromWiki
	@Path("debug sitHeight <double>")
	@Permission(Group.ADMIN)
	@Description("Test sitting height")
	void debug_sitHeight(double height) {
		Location location = location().toCenterLocation().clone().add(0, -1 + height, 0);

		ArmorStand armorStand = world().spawn(location, ArmorStand.class, _armorStand -> {
			_armorStand.setMarker(true);
			_armorStand.setVisible(false);
			_armorStand.setCustomNameVisible(false);
			_armorStand.setCustomName("DecorationSeat" + "-" + uuid());
			_armorStand.setInvulnerable(true);
			_armorStand.setGravity(false);
			_armorStand.setSmall(true);
			_armorStand.setBasePlate(true);
			_armorStand.setDisabledSlots(EquipmentSlot.values());
		});

		if (armorStand.isValid())
			armorStand.addPassenger(player());
	}

	@Path("debug [enabled]")
	@Permission(Group.ADMIN)
	@Description("toggle debugging decorations")
	void debug(Boolean enabled) {
		if (enabled == null)
			enabled = !DecorationUtils.getDebuggers().contains(uuid());

		if (enabled)
			DecorationUtils.getDebuggers().add(uuid());
		else
			DecorationUtils.getDebuggers().remove(uuid());

		send(PREFIX + "Debug " + (enabled ? "&aEnabled" : "&cDisabled"));
	}

	// STORE

	@Path("store warp")
	@Description("Teleport to the decoration store")
	void warp() {
		player().teleportAsync(DecorationStore.getWarpLocation());
	}

	@Path("store setActive <bool>")
	@Permission(Group.ADMIN)
	@Description("Toggles any tasks that affect players within the store")
	void setActive(boolean bool) {
		DecorationStore.setActive(bool);
		send(PREFIX + (bool ? "&aActivated" : "&cDeactivated"));
	}

	@Path("store debug [enabled]")
	@Permission(Group.ADMIN)
	@Description("Toggle debugging the store")
	void setDebug(Boolean enabled) {
		if (enabled == null)
			enabled = !DecorationStore.getDebuggers().contains(player());

		if (enabled)
			DecorationStore.getDebuggers().add(player());
		else
			DecorationStore.getDebuggers().remove(player());

		send(PREFIX + "Debug " + (enabled ? "&aEnabled" : "&cDisabled"));
	}

	@Path("store layout list")
	@Permission(Group.ADMIN)
	@Description("Display a list of the layout schematics")
	void listLayouts() {
		List<File> files = DecorationStoreLayouts.getLayoutFiles();
		send(PREFIX + "Total layouts: " + files.size());
		for (File file : files) {
			send(" - " + DecorationStoreLayouts.getSchematicPath(file));
		}
	}

	@Path("store layout schem <name>")
	@Permission(Group.ADMIN)
	@Description("Create a layout schematic of the selected build")
	void schemLayout(String name) {
		worldedit().getPlayerSelection(player());
		String schemName = DecorationStoreLayouts.getDirectory() + name;
		runCommandAsOp("worldeditutils schem save " + schemName + " true");
	}

	@Path("store layout paste <id>")
	@Permission(Group.ADMIN)
	@Description("Paste the layout into the store")
	void pasteLayout(int id) {
		DecorationStoreLayouts.pasteLayout(DecorationStoreLayouts.getLayoutSchematic(id));
		DecorationStoreConfig config = DecorationStore.getConfig();
		config.setSchematicId(id);
		DecorationStore.saveConfig();
	}

	@Path("store layout paste reset")
	@Permission(Group.ADMIN)
	@Description("Paste the reset layout into the store")
	void pasteLayout() {
		DecorationStoreLayouts.pasteLayout(DecorationStoreLayouts.getReset_schematic());
	}

	@Path("store layout paste next")
	@Permission(Group.ADMIN)
	@Description("Start the next layout process")
	void nextLayout() {
		DecorationStoreLayouts.pasteNextLayout();
	}

	@ConverterFor(DecorationConfig.class)
	DecorationConfig convertToDecorationConfig(String value) {
		final DecorationConfig config = DecorationConfig.of(value);
		if (config != null)
			return config;

		throw new InvalidInputException("Decoration &e" + value + " &cnot found");
	}

	@TabCompleterFor(DecorationConfig.class)
	List<String> tabCompleteDecorationConfig(String filter) {
		return DecorationConfig.getAllDecorationTypes().stream()
			.map(DecorationConfig::getId)
			.filter(id -> id.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

}
