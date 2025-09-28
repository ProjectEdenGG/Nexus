package gg.projecteden.nexus.features.resourcepack.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.TypeConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Tab;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSpawnEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreCurrencyType;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreManager;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStore;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStoreLayouts;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStoreLayouts.StoreLocation;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.MineralChoice;
import gg.projecteden.nexus.features.workbenches.dyestation.CreativeBrushMenu;
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
import gg.projecteden.nexus.models.creative.CreativeUserService;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig.DecorationStorePasteHistory;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Aliases("decor")
public class DecorationCommand extends CustomCommand {

	public DecorationCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Permission(Group.ADMIN)
	@Path("testDebug")
	void testDebug() {
		DecorationLang.debug(player(), "normal debug");
		DecorationLang.deepDebug(player(), "deep debug");
		DecorationLang.debugDot(player(), location(), ColorType.RED);
	}

	@Permission(Group.ADMIN)
	@Description("Replaces the selectedPath in the modelId with the replacePath to all frame items in radius")
	@Path("transplant <radius> <selectPath> <replacePath>")
	void transplant(@Arg(min = 0.5, max = 25) double radius, String fromPath, String toPath) {
		if (fromPath == null || toPath == null)
			error("Both paths must be provided");

		world().getNearbyEntitiesByType(ItemFrame.class, location(), radius).forEach(itemFrame -> {
			ItemStack item = itemFrame.getItem();
			if (Nullables.isNullOrAir(item))
				return;

			ItemBuilder itemBuilder = new ItemBuilder(itemFrame.getItem());
			String oldModel = itemBuilder.model();
			if (!oldModel.contains(fromPath))
				return;

			String newModel = oldModel.replace(fromPath, toPath);
			itemBuilder.model(newModel);
			itemFrame.setItem(itemBuilder.build());
		});
	}

	@Path("wiki")
	@Description("Open the wiki page for Decorations")
	void wiki() {
		send(json(PREFIX).group().next("https://wiki.projecteden.gg/wiki/Decoration")
			.hover("Click to open").url("https://wiki.projecteden.gg/wiki/Decoration"));
	}

	@Path("[--theme] [--currency]")
	@Description("Open the catalog menu")
	void viewCatalog(@Switch @Arg("master") Catalog.Theme theme, @Switch @Arg("money") DecorationStoreCurrencyType currency) {
		checkPermissions();

		Catalog.openCatalog(player(), theme, currency, null);
	}

	@Path("store")
	@Description("Teleport to the decoration store")
	void warp() {
		player().teleportAsync(DecorationStore.getWarpLocation(), TeleportCause.COMMAND);
	}

	@Path("info")
	@Description("Display information on the held decoration")
	void info() {
		ItemStack itemStack = getTool();
		DecorationConfig config = DecorationConfig.of(itemStack);

		if (config == null)
			error("You are not holding a decoration!");

		config.sendInfo(player());
	}

	@Path("infoTarget")
	@Description("Display information on the target decoration")
	void infoTarget() {
		DecorationConfig config = DecorationUtils.getTargetConfig(player());

		if (config == null)
			error("You are not looking at a decoration!");

		config.sendInfo(player());
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

	@Path("dye metal <metal>")
	@Description("Plate an item")
	void dye(MineralChoice metallicChoice) {
		checkPermissions();

		DecorationUtils.dye(getToolRequired(), metallicChoice, player());
	}

	@Path("get <type>")
	@Description("Get the decoration")
	void get(DecorationConfig config) {
		checkPermissions();

		DecorationSpawnEvent spawnEvent = new DecorationSpawnEvent(player(), new Decoration(config), config.getItem());
		if (!spawnEvent.callEvent())
			error("DecorationSpawnEvent was cancelled");

		giveItem(spawnEvent.getItemStack());
		send(PREFIX + "Given: &e" + StringUtils.camelCase(config.getName()));
	}

	@Path("getCatalog <theme>")
	@Description("Get a catalog")
	void getCatalog(Catalog.Theme theme) {
		checkPermissions();

		String themeName = StringUtils.camelCase(theme);
		ItemBuilder catalogTheme = theme.getItemBuilder().name("&3" + themeName + " Catalog");

		giveItem(catalogTheme);
		send(PREFIX + "Given &e" + themeName + " Catalog");
	}

	@Path("getItem magicDye")
	@Description("Spawn a magic dye bottle")
	void get_magicDye() {
		checkPermissions();

		giveItem(DyeStation.getMagicDye().build());
		send(PREFIX + "Given &eMagic Dye");
	}

	@Path("getItem magicStain")
	@Description("Spawn a magic stain bottle")
	void get_magicStain() {
		checkPermissions();

		giveItem(DyeStation.getMagicStain().build());
		send(PREFIX + "Given &eMagic Stain");
	}

	@Path("getItem magicMineral")
	@Description("Spawn a magic mineral bottle")
	void get_magicMineral() {
		checkPermissions();

		giveItem(DyeStation.getMagicMineral().build());
		send(PREFIX + "Given &eMagic Mineral");
	}

	@Path("getItem paintbrush")
	@Description("Spawn a paintbrush")
	void get_paintbrush() {
		checkPermissions();

		giveItem(DyeStation.getPaintbrush().build());
		send(PREFIX + "Given &ePaintbrush");
	}

	@Path("getItem creativeBrush")
	@Description("Spawn a creative brush")
	void get_creativebrush() {
		checkPermissions();

		giveItem(CreativeBrushMenu.getCreativeBrush().build());
		send(PREFIX + "Given &eCreative Brush");
	}

	@Path("publicUse [enable]")
	@Description("Toggle the public use of a decoration")
	void togglePublicUse(Boolean enable) {
		Decoration decoration = DecorationUtils.getTargetDecoration(player());
		if (decoration == null || decoration.getConfig() == null) {
			DecorationError.UNKNOWN_TARGET_DECORATION.send(player());
			return;
		}

		if (!decoration.canEdit(player(), null)) {
			DecorationError.LOCKED.send(player());
			return;
		}

		if (enable == null)
			enable = !decoration.isPublicUse(player());

		decoration.setPublicUse(enable, player());
		if (enable)
			send(PREFIX + "&aAdded &3the &epublic use flag &3to the " + decoration.getConfig().getName());
		else
			send(PREFIX + "&cRemoved &3the &epublic use flag &3from the " + decoration.getConfig().getName());
	}

	// STAFF COMMANDS

	@Permission(Group.STAFF)
	@Path("changeOwner <player>")
	@Description("Change the owner of a decoration")
	void changeOwner(OfflinePlayer newOwner) {
		Decoration decoration = DecorationUtils.getTargetDecoration(player());

		if (decoration == null || decoration.getConfig() == null) {
			DecorationError.UNKNOWN_TARGET_DECORATION.send(player());
			return;
		}

		decoration.setOwner(newOwner.getUniqueId(), player());
		send(PREFIX + "Set the owner of the decoration to " + Nickname.of(decoration.getOwner(player())));
	}

	@Permission(Group.STAFF)
	@Path("stats")
	@Description("Display stats about Decorations")
	void stats() {
		Map<String, List<String>> configInstanceMap = new HashMap<>();
		Map<String, Integer> configInstanceSizeMap = new HashMap<>();
		Map<String, Integer> instanceMap = new HashMap<>();
		for (DecorationConfig config : DecorationConfig.getALL_DECOR_CONFIGS()) {
			var clazzes = DecorationUtils.getSimpleNameInstancesOf(config);
			configInstanceMap.put(config.getId(), clazzes);
			configInstanceSizeMap.put(config.getId(), clazzes.size());

			for (String clazz : clazzes) {
				int count = instanceMap.getOrDefault(clazz, 0);
				instanceMap.put(clazz, ++count);
			}
		}

		send("Decoration Counts:");
		for (String clazz : Utils.sortByValueReverse(instanceMap).keySet()) {
			send(" - " + clazz + ": " + instanceMap.get(clazz));
		}

		line();

		send("Most Instances:");
		String key = Utils.sortByValueReverse(configInstanceSizeMap).keySet().stream().toList().get(0);
		send(" " + key + ": " + configInstanceSizeMap.get(key));
		for (String clazz : configInstanceMap.get(key)) {
			send(" - " + clazz);
		}
	}

	// ADMIN COMMANDS

	@HideFromWiki
	@Path("near [radius] [--showOwner]")
	@Permission(Group.ADMIN)
	@Description("List all decoration within a radius")
	void admin_find(@Arg("50") int radius, @Switch @Arg("false") boolean showOwner) {
		var decorationMap = getNearbyDecoration(null, location(), radius);
		sendNearbyDecoration(decorationMap, showOwner);
		send(decorationMap.size() + " found in radius of " + radius);
	}

	@HideFromWiki
	@Path("find <type> [radius] [--showOwner]")
	@Permission(Group.ADMIN)
	@Description("List all decoration within a radius of a specific type")
	void admin_find_byType(DecorationType type, @Arg("50") int radius, @Switch @Arg("false") boolean showOwner) {
		var decorationMap = getNearbyDecoration(type, location(), radius);
		sendNearbyDecoration(decorationMap, showOwner);
		send(decorationMap.size() + " found in radius of " + radius);
	}

	private void sendNearbyDecoration(LinkedHashMap<Decoration, Long> decorationMap, boolean showOwner) {
		decorationMap.forEach((decoration, count) -> {
			Location location = decoration.getOrigin().toCenterLocation();
			location.setYaw(location().getYaw());
			location.setPitch(location().getPitch());
			String name = decoration.getConfig().getName();

			JsonBuilder json = new JsonBuilder("&7 - &e" + name).hover("Click to TP")
				.command(StringUtils.tppos(location)).group();

			if (showOwner) {
				UUID ownerUUID = decoration.getOwner(player());
				if (ownerUUID != null) {
					String owner = Nickname.of(ownerUUID);
					json.next("&3 - &e").group().next(owner).suggest(String.valueOf(ownerUUID)).hover("Click to insert uuid").group();
				}
			}

			json.send(player());
		});
	}

	private static LinkedHashMap<Decoration, Long> getNearbyDecoration(DecorationType type, Location location, double radius) {
		LinkedHashMap<Decoration, Long> result = new LinkedHashMap<>();

		EntityUtils.getNearbyEntities(location, radius).forEach((entity, count) -> {
			if (entity.getType() != EntityType.ITEM_FRAME || !(entity instanceof ItemFrame itemFrame))
				return;

			DecorationConfig config = DecorationConfig.of(itemFrame);
			if (config == null)
				return;

			if (type != null) {
				if (DecorationType.of(config) != type)
					return;
			}

			Decoration decoration = new Decoration(config, itemFrame);
			if (decoration.getOrigin() == null)
				return;

			result.put(decoration, count);
		});

		return result;
	}

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
	@Description("Test sitting height, use whilst standing on-top of the chair")
	void debug_sitHeight(double height) {
		Location below = block().getRelative(BlockFace.DOWN).getLocation();

		Seat.spawnArmorStandAndSit(player(), below, height);
	}

	// STORE COMMANDS

	@Path("store debug [enabled]")
	@Permission(Group.ADMIN)
	@Description("Toggle debugging the store")
	void setDebug(Boolean enabled) {
		if (enabled == null)
			enabled = !DecorationStoreManager.getDebuggers().contains(uuid());

		if (enabled)
			DecorationStoreManager.getDebuggers().add(uuid());
		else
			DecorationStoreManager.getDebuggers().remove(uuid());

		send(PREFIX + "Store Debug " + (enabled ? "&aEnabled" : "&cDisabled"));
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

	@Path("store layout history")
	@Permission(Group.STAFF)
	@Description("Displays a list of when the last 10 layouts were pasted")
	void pasteHistory() {
		DecorationStoreConfig config = DecorationStore.getConfig();

		if (config.getLayoutHistory().isEmpty())
			error("Layout history is empty");

		send(PREFIX + "Paste History: ");
		for (DecorationStorePasteHistory history : config.getLayoutHistory()) {
			send("&e - &3Pasted schematic id &e" + history.getSchematicId() + " &3at &e" + TimeUtils.shortDateTimeFormat(history.getDateTime()));
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
		checkControlsRegion();
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
		checkControlsRegion();
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
		checkControlsRegion();
		DecorationStoreLayouts.pasteLayout(DecorationStoreLayouts.getEmpty_schematic(), StoreLocation.TEST);
		send(PREFIX + "Pasted empty schematic");
	}

	@Path("store layout pasteTest create")
	@Permission(Group.STAFF)
	@Description("Paste the next layout into the test store")
	void testCreate() {
		checkControlsRegion();
		selectSchemRegion();
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
		checkControlsRegion();
		selectSchemRegion();
		int id = DecorationStore.getConfig().getSchematicIdTest();

		ConfirmationMenu.builder().title("Overwrite schematic " + id + "?").onConfirm(e -> {
			worldedit().getPlayerSelection(player());
			String schemName = DecorationStoreLayouts.getDirectory() + id;
			runCommandAsOp("worldeditutils schem save " + schemName + " --entities=true");
			send(PREFIX + "Overwritten schematic: " + id);
		}).open(player());
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

		if (rank() == Rank.GUEST && worldGroup() == WorldGroup.CREATIVE) {
			if (new CreativeUserService().get(player()).isTrusted())
				return true;

			error("You cannot access Decorations until you are Member rank");
		}

		return true;
	}

	private void updateSign(int id){
		Location signLoc = new Location(Bukkit.getWorld("buildadmin"), 1496, 5, -1169);
		org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLoc.getBlock().getState();

		sign.setLine(1, id + "");
		sign.update();
	}

	public void checkControlsRegion() {
		for (ProtectedRegion region : worldguard().getRegionsAt(player().getLocation())) {
			if (region.getId().equalsIgnoreCase("buildadmin_decor_store_controls"))
				return;
		}

		error("You not within the controls region!");
	}

	public void selectSchemRegion() {
		runCommandAsOp("rg select buildadmin_decor_store_schem");
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
		return DecorationConfig.getALL_DECOR_CONFIGS().stream()
			.filter(config -> {
				DecorationType type = DecorationType.of(config);
				if (type == null)
					return true;

				TypeConfig typeConfig = type.getTypeConfig();
				if (typeConfig == null || typeConfig.tabs().length == 0)
					return true;

				return Arrays.stream(typeConfig.tabs()).toList().getLast() != Tab.INTERNAL;
			})
			.map(DecorationConfig::getId)
			.filter(id -> id.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

}
