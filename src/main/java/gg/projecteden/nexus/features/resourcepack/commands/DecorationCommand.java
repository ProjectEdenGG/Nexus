package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStore;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStoreLayouts;
import gg.projecteden.nexus.features.workbenches.DyeStation;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.StainChoice;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Aliases("decor")
public class DecorationCommand extends CustomCommand {

	public DecorationCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("info")
	@Description("Display information on the held decoration")
	void info() {
		ItemStack itemStack = getToolRequired();
		DecorationConfig config = DecorationConfig.of(itemStack);

		if (config == null)
			error("You are not holding a decoration!");

		config.sendInfo(player());
	}

	@Path("catalog [theme]")
	@Description("Open the catalog menu")
	void viewCatalog(@Arg("All") Catalog.Theme theme) {
		hasPermission();

		Catalog.openCatalog(player(), theme, null);
	}

	@Path("getMasterCatalog")
	void getMasterCatalog() {
		hasPermission();

		giveItem(Catalog.getMASTER_CATALOG());
		send("Given Master Catalog");
	}

	@Path("get <type>")
	@Description("Get the decoration")
	void get(DecorationConfig config) {
		hasPermission();

		giveItem(config.getItem());
		send("&3Given: &e" + StringUtils.camelCase(config.getName()));
	}

	@Path("dye color <color>")
	@Description("Dye an item")
	void dye(ChatColor chatColor) {
		hasPermission();

		DecorationUtils.dye(getToolRequired(), chatColor, player());
	}

	@Path("dye stain <stain>")
	@Description("Stain an item")
	void dye(StainChoice stainChoice) {
		hasPermission();

		DecorationUtils.dye(getToolRequired(), stainChoice, player());
	}

	@Path("getItem magicDye")
	@Description("Spawn a magic dye item")
	void get_magicDye() {
		hasPermission();

		giveItem(DyeStation.getMagicDye().build());
	}

	@Path("getItem magicStain")
	@Description("Spawn a magic stain item")
	void get_magicStain() {
		hasPermission();

		giveItem(DyeStation.getMagicStain().build());
	}

	@Path("getItem paintbrush")
	@Description("Spawn a paintbrush")
	void get_paintbrush() {
		hasPermission();

		giveItem(DyeStation.getPaintbrush().build());
	}

	//

	@HideFromWiki
	@Path("debug tabTypeMap")
	@Permission(Group.ADMIN)
	@Description("Display catalog tabs in console")
	void debug_CatalogTabs() {
		Nexus.log(StringUtils.toPrettyString(DecorationType.getTabTypeMap()));
	}

	@HideFromWiki
	@Path("debug categoryTree")
	@Permission(Group.ADMIN)
	@Description("Display catalog category tree in console")
	void debug_CatalogTree() {
		Nexus.log(StringUtils.toPrettyString(DecorationType.getCategoryTree()));
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
	@Description("Toggle debugging decorations")
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
	@Permission(Group.STAFF)
	@Description("Teleport to the decoration store")
	void warp() {
		player().teleportAsync(DecorationStore.getWarpLocation());
	}

	@Path("store setActive <bool>")
	@Permission(Group.ADMIN)
	@Description("Toggles the ability to use the store")
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

	private boolean hasPermission() {
		if (isAdmin())
			return true;

		if (!DecorationUtils.hasBypass(player())) {
			if (isStaff())
				error("You cannot use this command outside of creative/staff");
			else
				error("You cannot use this outside of creative");

			return false;
		}

		return true;
	}

}
