package gg.projecteden.nexus.features.virtualinventories;

import gg.projecteden.nexus.features.virtualinventories.managers.VirtualInventoryManager;
import gg.projecteden.nexus.features.virtualinventories.managers.VirtualSharedInventoryManager;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventoryType;
import gg.projecteden.nexus.features.virtualinventories.models.tiles.impl.FurnaceTile;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.block.Block;

@HideFromWiki
@Permission(Group.ADMIN)
public class VirtualInventoriesCommand extends CustomCommand {

	public VirtualInventoriesCommand(CommandEvent event) {
		super(event);
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
