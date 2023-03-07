package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.VirtualInventoryManager;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.VirtualInventoryUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.VirtualTileManager;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.FurnaceProperties;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualFurnace;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualInventory;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualInventoryType;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles.FurnaceTile;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Permission(Group.ADMIN)
public class DecorationInvCommand extends CustomCommand {
	@Path("getCookingRecipe <material>")
	@Permission(Group.ADMIN)
	void getRecipe_smelt(Material material) {
		List<CookingRecipe<?>> recipes = VirtualInventoryUtils.getCookingRecipe(new ItemStack(material));
		if (recipes.isEmpty())
			error("unknown cooking recipes for " + material);

		send("Found Recipes:");
		for (CookingRecipe<?> recipe : recipes) {
			send(recipe.getKey().getKey());
		}
	}

	@Path("getFurnaceRecipe <material>")
	@Permission(Group.ADMIN)
	void getRecipe_furnace(Material material) {
		FurnaceRecipe recipe = VirtualInventoryUtils.getFurnaceRecipe(new ItemStack(material));
		if (recipe == null)
			error("unknown furnace recipe for " + material);

		send(recipe.getKey().getKey());
	}

	@Path("setTicking <bool>")
	@Permission(Group.ADMIN)
	void setTicking(boolean bool) {
		VirtualInventoryManager.setTicking(bool);
		send("ticking set to " + bool);
	}

	@Path("debug")
	@Permission(Group.ADMIN)
	void debug() {
		VirtualInventory virtualInventory = VirtualInventoryManager.getInventory(player());
		if (virtualInventory == null)
			error("unknown virtual inv");

		send(virtualInventory.toString());
	}

	@Path("furnace")
	@Permission(Group.ADMIN)
	void furnace() {
		VirtualInventory virtualInventory = VirtualInventoryManager.getOrCreate(player(), VirtualInventoryType.FURNACE, "Virtual Furnace");
		if (virtualInventory == null)
			error("error when creating virtual inventory");

		VirtualFurnace virtualFurnace = (VirtualFurnace) virtualInventory;
		virtualFurnace.openInventory(player());
	}

	@Path("furnaceTile")
	@Permission(Group.ADMIN)
	void furnaceTile() {
		Block block = getTargetBlockRequired();

		FurnaceTile furnaceTile = VirtualTileManager.createFurnaceTile(block, "Virtual Tile Furnace", FurnaceProperties.FURNACE);
		furnaceTile.openInventory(player());
	}

	@Path("reload")
	@Permission(Group.ADMIN)
	void reload() {
		VirtualInventoryManager.get().reload();
	}
}
