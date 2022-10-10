package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles;

import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualFurnace;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class FurnaceTile extends Tile<VirtualFurnace> {

	public FurnaceTile(@NotNull VirtualFurnace virtualInv, int x, int y, int z, @NotNull World world) {
		super(virtualInv, x, y, z, world);
	}

	@Override
	public void breakTile() {
		final ItemStack fuel = virtualInv.getFuel();
		final ItemStack input = virtualInv.getInput();
		final ItemStack output = virtualInv.getOutput();

		final float xp = virtualInv.extractExperience();

		final Vector vec = new Vector(0, 0, 0);

		World world = getBukkitWorld();
		Location drop = getLocation().toCenterLocation();

		Tasks.sync(() -> {
			if (Nullables.isNotNullOrAir(fuel))
				world.dropItem(drop, fuel).setVelocity(vec);

			if (Nullables.isNotNullOrAir(input))
				world.dropItem(drop, input).setVelocity(vec);

			if (Nullables.isNotNullOrAir(output))
				world.dropItem(drop, output).setVelocity(vec);

			if (xp > 0)
				world.spawn(drop, ExperienceOrb.class, orb -> orb.setExperience((int) xp)).setVelocity(vec);
		});

		super.breakTile();
	}

	@Override
	public String toString() {
		return "FurnaceTile{" +
			"virtualInv=" + getVirtualInv() +
			", x=" + x +
			", y=" + y +
			", z=" + z +
			", world='" + world + '\'' +
			"} ";
	}
}
