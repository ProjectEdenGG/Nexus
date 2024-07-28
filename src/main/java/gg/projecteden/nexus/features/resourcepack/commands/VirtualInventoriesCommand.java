package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.nexus.features.virtualinventory.VirtualInventoryUtils;
import gg.projecteden.nexus.features.virtualinventory.managers.VirtualInventoryManager;
import gg.projecteden.nexus.features.virtualinventory.managers.VirtualSharedInventoryManager;
import gg.projecteden.nexus.features.virtualinventory.models.inventories.VirtualInventoryType;
import gg.projecteden.nexus.features.virtualinventory.models.tiles.impl.FurnaceTile;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@HideFromWiki
@Permission(Group.ADMIN)
public class VirtualInventoriesCommand extends CustomCommand {

	public VirtualInventoriesCommand(CommandEvent event) {
		super(event);
	}

	@Path("debug getCookingRecipe <material>")
	void debug_getCookingRecipe(Material material) {
		List<CookingRecipe<?>> recipes = VirtualInventoryUtils.getCookingRecipe(new ItemStack(material));
		if (recipes.isEmpty())
			error("unknown cooking recipes for " + material);

		send("Found Recipes:");
		for (CookingRecipe<?> recipe : recipes) {
			send(recipe.getKey().getKey());
		}
	}

	@Path("debug getFurnaceRecipe <material>")
	void debug_getFurnaceRecipe(Material material) {
		FurnaceRecipe recipe = VirtualInventoryUtils.getFurnaceRecipe(new ItemStack(material));
		if (recipe == null)
			error("unknown furnace recipe for " + material);

		send(recipe.getKey().getKey());
	}

	@Path("ticking <state>")
	void ticking(boolean state) {
		VirtualInventoryManager.setTicking(state);
		send(PREFIX + "Ticking " + (state ? "&aenabled" : "&cdisabled"));
	}

	@Path("open <type>")
	void open(VirtualInventoryType type) {
		VirtualInventoryManager.getOrCreate(player(), type).openInventory(player());
	}

	@Path("furnaceTile")
	void furnaceTile() {
		Block block = getTargetBlockRequired();
		FurnaceTile furnaceTile = VirtualSharedInventoryManager.createFurnaceTile(VirtualInventoryType.FURNACE, block);
		furnaceTile.openInventory(player());
	}
}
