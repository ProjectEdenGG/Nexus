package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.VirtualInventoryManager;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.VirtualInventoryUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.VirtualTileManager;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.FurnaceProperties;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualFurnace;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualInventory;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualInventoryType;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles.FurnaceTile;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.trophy.TrophyType;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

@Permission(Group.ADMIN)
public class DecorationCommand extends CustomCommand {

	public DecorationCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		// Init all decoration creators
		DecorationType.init();
		Pose.init();
		TrophyType.init();
	}

	@Path("virtualInv getCookingRecipe <material>")
	void virtualInv_getRecipe_smelt(Material material) {
		List<CookingRecipe<?>> recipes = VirtualInventoryUtils.getCookingRecipe(new ItemStack(material));
		if (recipes.isEmpty())
			error("unknown cooking recipes for " + material);

		send("Found Recipes:");
		for (CookingRecipe<?> recipe : recipes) {
			send(recipe.getKey().getKey());
		}
	}

	@Path("virtualInv getFurnaceRecipe <material>")
	void virtualInv_getRecipe_furnace(Material material) {
		FurnaceRecipe recipe = VirtualInventoryUtils.getFurnaceRecipe(new ItemStack(material));
		if (recipe == null)
			error("unknown furnace recipe for " + material);

		send(recipe.getKey().getKey());
	}

	@Path("virtualInv setTicking <bool>")
	void virtualInv_setTicking(boolean bool) {
		VirtualInventoryManager.setTicking(bool);
		send("ticking set to " + bool);
	}

	@Path("virtualInv debug")
	void virtualInv_debug() {
		VirtualInventory virtualInventory = VirtualInventoryManager.getInventory(player());
		if (virtualInventory == null)
			error("unknown virtual inv");

		send(virtualInventory.toString());
	}

	@Path("virtualInv furnace")
	void virtualInv_furnace() {
		VirtualInventory virtualInventory = VirtualInventoryManager.getOrCreate(player(), VirtualInventoryType.FURNACE, "Virtual Furnace");
		if (virtualInventory == null)
			error("error when creating virtual inventory");

		VirtualFurnace virtualFurnace = (VirtualFurnace) virtualInventory;
		virtualFurnace.openInventory(player());
	}

	@Path("virtualInv furnaceTile")
	void virtualInv_furnaceTile() {
		Block block = getTargetBlockRequired();

		FurnaceTile furnaceTile = VirtualTileManager.createFurnaceTile(block, "Virtual Tile Furnace", FurnaceProperties.FURNACE);
		furnaceTile.openInventory(player());
	}

	@Path("virtualInv reload")
	void virtualInv_reload() {
		VirtualInventoryManager.get().reload();
	}

	@Path("catalog <theme>")
	void catalog_view(Catalog.Theme theme) {
		Catalog.openCatalog(player(), theme, null, null);
	}

	@Path("getCatalog <theme>")
	void catalog_get(Catalog.Theme theme) {
		giveItem(theme.getNamedItem());
		send("Given " + StringUtils.camelCase(theme) + " Catalog");
	}

	@Path("get <type>")
	void get(DecorationConfig config) {
		giveItem(config.getItem());
		send("Given " + StringUtils.camelCase(config.getName()));
	}

	@Path("debug [enabled]")
	void debug(Boolean enabled) {
		if (enabled == null)
			enabled = !DecorationUtils.debuggers.contains(uuid());

		if (enabled)
			DecorationUtils.debuggers.add(uuid());
		else
			DecorationUtils.debuggers.remove(uuid());

		send(PREFIX + "Debug " + (enabled ? "&aEnabled" : "&cDisabled"));
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
