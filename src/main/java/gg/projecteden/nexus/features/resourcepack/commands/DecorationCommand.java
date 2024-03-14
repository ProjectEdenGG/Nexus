package gg.projecteden.nexus.features.resourcepack.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStore;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStoreLayouts;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStoreLayouts.StoreLocation;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
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
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
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
		checkPermissions();

		Catalog.openCatalog(player(), theme, null);
	}

	@Path("getMasterCatalog")
	void getMasterCatalog() {
		checkPermissions();

		giveItem(Catalog.getMASTER_CATALOG());
		send("Given Master Catalog");
	}

	@Path("get <type>")
	@Description("Get the decoration")
	void get(DecorationConfig config) {
		checkPermissions();

		giveItem(config.getItem());
		send("&3Given: &e" + StringUtils.camelCase(config.getName()));
	}

	@Path("dye color <color>")
	@Description("Dye an item")
	void dye(ChatColor chatColor) {
		checkPermissions();

		DecorationUtils.dye(getToolRequired(), chatColor, player());
	}

	@Path("dye stain <stain>")
	@Description("Stain an item")
	void dye(ColorChoice.StainChoice stainChoice) {
		checkPermissions();

		DecorationUtils.dye(getToolRequired(), stainChoice, player());
	}

	@Path("getItem magicDye")
	@Description("Spawn a magic dye item")
	void get_magicDye() {
		checkPermissions();

		giveItem(DyeStation.getMagicDye().build());
	}

	@Path("getItem magicStain")
	@Description("Spawn a magic stain item")
	void get_magicStain() {
		checkPermissions();

		giveItem(DyeStation.getMagicStain().build());
	}

	@Path("getItem paintbrush")
	@Description("Spawn a paintbrush")
	void get_paintbrush() {
		checkPermissions();

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


	@Path("debug [enabled] [--deep]")
	@Permission(Group.ADMIN)
	@Description("Toggle debugging decorations")
	void debug(Boolean enabled, @Switch boolean deep) {
		if (enabled == null)
			enabled = !DecorationLang.isDebugging(uuid());

		if (enabled) {
			DecorationLang.startDebugging(uuid(), deep);
		} else
			DecorationLang.stopDebugging(uuid());

		send(PREFIX + "Debug " + (enabled ? "&aEnabled" : "&cDisabled"));
	}

	// STORE
	@Path("store effect [enable]")
	@Permission(Group.STAFF)
	@Description("Toggle store buying effects for yourself")
	void toggleGlow(Boolean enable) {
		if (enable == null)
			enable = !DecorationStore.getDisabledPlayers().contains(uuid());

		if (enable) {
			DecorationStore.getDisabledPlayers().add(uuid());
			DecorationStore.unglow(uuid());
		} else
			DecorationStore.getDisabledPlayers().remove(uuid());

		send(PREFIX + "Store Effects " + (enable ? "&aEnabled" : "&cDisabled"));
	}

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
	@Permission(Group.STAFF)
	@Description("Display a list of the layout schematics")
	void listLayouts() {
		List<File> files = DecorationStoreLayouts.getLayoutFiles();
		send(PREFIX + "Total layouts: " + files.size());
		for (File file : files) {
			send(" - " + DecorationStoreLayouts.getSchematicPath(file));
		}
	}

	@Path("store layout schem <id>")
	@Permission(Group.ADMIN)
	@Description("Create a layout schematic of the selected build")
	void schemLayout(int id) {
		worldedit().getPlayerSelection(player());
		String schemName = DecorationStoreLayouts.getDirectory() + id;
		runCommandAsOp("worldeditutils schem save " + schemName + " --entities=true");
	}

	@Path("store layout paste <id> [--storeLocation]")
	@Permission(Group.ADMIN)
	@Description("Paste the layout into the store")
	void pasteLayout(int id, @Switch @Arg("survival") StoreLocation storeLocation) {
		DecorationStoreLayouts.pasteLayout(DecorationStoreLayouts.getLayoutSchematic(id), storeLocation);

		if(storeLocation == StoreLocation.SURVIVAL) {
			DecorationStoreConfig config = DecorationStore.getConfig();
			config.setSchematicId(id);
			DecorationStore.saveConfig();
		}
	}

	@Path("store layout paste reset")
	@Permission(Group.ADMIN)
	@Description("Paste the reset layout into the store")
	void pasteLayout() {
		DecorationStoreLayouts.pasteLayout(DecorationStoreLayouts.getReset_schematic(), StoreLocation.SURVIVAL);
	}

	@Path("store layout paste next [--override]")
	@Permission(Group.ADMIN)
	@Description("Start the next layout process")
	void nextLayout(@Switch @Arg("false") boolean override) {
		DecorationStoreLayouts.pasteNextLayout(override);
	}

	@Path("store layout pasteTest next")
	@Permission(Group.STAFF)
	@Description("Paste the next layout into the test store")
	void testNext() {
		checkRegion();
		DecorationStoreConfig config = DecorationStore.getConfig();

		int id = config.getSchematicIdTest();
		int size = DecorationStoreLayouts.getLayoutFiles().size();
		if (id == size)
			id = 1;
		else
			id++;

		DecorationStoreLayouts.pasteLayout(DecorationStoreLayouts.getLayoutSchematic(id), StoreLocation.TEST);
		config.setSchematicIdTest(id);
		DecorationStore.saveConfig();
		updateSign(id);
		send(PREFIX + "Pasted schematic " + id);
	}

	@Path("store layout pasteTest previous")
	@Permission(Group.STAFF)
	@Description("Paste the next layout into the test store")
	void testPrevious() {
		checkRegion();
		DecorationStoreConfig config = DecorationStore.getConfig();

		int id = config.getSchematicIdTest();
		int size = DecorationStoreLayouts.getLayoutFiles().size();
		if (id <= 1)
			id = size;
		else
			id--;

		DecorationStoreLayouts.pasteLayout(DecorationStoreLayouts.getLayoutSchematic(id), StoreLocation.TEST);
		config.setSchematicIdTest(id);
		DecorationStore.saveConfig();
		updateSign(id);
		send(PREFIX + "Pasted schematic " + id);
	}

	@Path("store layout pasteTest empty")
	@Permission(Group.STAFF)
	@Description("Paste the next layout into the test store")
	void testEmpty() {
		checkRegion();
		DecorationStoreLayouts.pasteLayout(DecorationStoreLayouts.getEmpty_schematic(), StoreLocation.TEST);
		send(PREFIX + "Pasted empty schematic");
	}

	@Path("store layout pasteTest create")
	@Permission(Group.STAFF)
	@Description("Paste the next layout into the test store")
	void testCreate() {
		checkRegion();
		int id = DecorationStore.getConfig().getSchematicIdTest() + 1;

		ConfirmationMenu.builder().title("Create schematic " + id + "?").onConfirm(e -> {
			worldedit().getPlayerSelection(player());
			String schemName = DecorationStoreLayouts.getDirectory() + id;
			runCommandAsOp("worldeditutils schem save " + schemName + " --entities=true");
			send(PREFIX + "Created new schematic: " + id);
		});
	}

	@Path("store layout pasteTest overwrite")
	@Permission(Group.STAFF)
	@Description("Paste the next layout into the test store")
	void testSave() {
		checkRegion();
		int id = DecorationStore.getConfig().getSchematicIdTest();

		ConfirmationMenu.builder().title("Overwrite schematic " + id + "?").onConfirm(e -> {
			worldedit().getPlayerSelection(player());
			String schemName = DecorationStoreLayouts.getDirectory() + id;
			runCommandAsOp("worldeditutils schem save " + schemName + " --entities=true");
			send(PREFIX + "Overwritten schematic: " + id);
		});
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

	private boolean checkPermissions() {
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

	private void updateSign(int id){
		Location signLoc = new Location(Bukkit.getWorld("buildadmin"), 1496, 5, -1169);
		org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLoc.getBlock().getState();

		sign.setLine(1, id + "");
		sign.update();
	}

	public void checkRegion(){
		for (ProtectedRegion region : worldguard().getRegionsAt(player().getLocation())) {
			if(region.getId().equalsIgnoreCase("buildadmin_decor_store_controls"))
				return;
		}

		error("You not within the controls region!");
	}

}
