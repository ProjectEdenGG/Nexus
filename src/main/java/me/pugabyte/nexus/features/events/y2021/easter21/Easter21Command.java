package me.pugabyte.nexus.features.events.y2021.easter21;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.warps.commands._WarpCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.warps.WarpType;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@NoArgsConstructor
@Permission("group.admin")
public class Easter21Command extends _WarpCommand implements Listener {

	public Easter21Command(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.EASTER21;
	}

	@EventHandler
	public void onEggInteract(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event))
			return;

		Block block = event.getClickedBlock();
		if (block == null)
			return;

		if (block.getType() != Material.DRAGON_EGG)
			return;

		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(block.getWorld());
		if (!worldGuardUtils.isInRegion(block.getLocation(), "spawn"))
			return;

		event.setCancelled(true);
	}
}
