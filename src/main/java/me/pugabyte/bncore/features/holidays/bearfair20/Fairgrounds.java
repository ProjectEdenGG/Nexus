package me.pugabyte.bncore.features.holidays.bearfair20;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.Listener;

public class Fairgrounds implements Listener {

	public static boolean pugaDunkBool = false;
	private static Location pugaDunkButton = new Location(BearFair20.world, -960, 139, -1594);

	public Fairgrounds() {
		BNCore.registerListener(this);
		pugaDunkTask();
	}

	public static void setPugaDunkBool(boolean bool) {
		if (!bool)
			pugaDunkButton.getBlock().setType(Material.AIR);
		pugaDunkBool = bool;
	}

	private void pugaDunkTask() {
		Tasks.repeat(0, 5, () -> {
			if (pugaDunkBool) {
				if (Utils.chanceOf(25)) {
					pugaDunkButton.getBlock().setType(Material.DARK_OAK_BUTTON);
					Directional data = (Directional) pugaDunkButton.getBlock().getBlockData();
					data.setFacing(BlockFace.EAST);
					pugaDunkButton.getBlock().setBlockData(data);
				} else {
					pugaDunkButton.getBlock().setType(Material.AIR);
				}
			}
		});
	}


}
