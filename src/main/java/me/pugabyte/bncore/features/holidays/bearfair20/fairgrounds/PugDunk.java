package me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.utils.CitizensUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PugDunk implements Listener {

	private static boolean pugDunkBool = false;
	private static Location pugDunkButton = new Location(BearFair20.world, -960, 139, -1594);

	public PugDunk() {
		BNCore.registerListener(this);
		pugDunkTask();
	}

	public static void setPugDunkBool(boolean bool) {
		if (!bool)
			pugDunkButton.getBlock().setType(Material.AIR);
		pugDunkBool = bool;
	}

	public static void resetPugDunkNPC() {
		Location loc = new Location(BearFair20.world, -959.5, 141, -1587.5, -90, 0);
		NPC npc = CitizensUtils.getNPC(2720);
		npc.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
	}

	private void pugDunkTask() {
		Tasks.repeat(0, 5, () -> {
			if (pugDunkBool) {
				if (Utils.chanceOf(25)) {
					pugDunkButton.getBlock().setType(Material.DARK_OAK_BUTTON);
					Directional data = (Directional) pugDunkButton.getBlock().getBlockData();
					data.setFacing(BlockFace.EAST);
					pugDunkButton.getBlock().setBlockData(data);
				} else {
					pugDunkButton.getBlock().setType(Material.AIR);
				}
			}
		});
	}
}
