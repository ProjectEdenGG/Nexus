package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationInteractData;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualFurnace;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FurnaceTile extends Tile<VirtualFurnace> {
	SoundBuilder crackle = new SoundBuilder(Sound.BLOCK_FURNACE_FIRE_CRACKLE);
	ParticleBuilder flames = new ParticleBuilder(Particle.FLAME).count(2).extra(0).offset(0.1, 0.05, 0.1);

	public FurnaceTile(@NotNull VirtualFurnace virtualInv, Location location) {
		super(virtualInv, location);

		crackle.location(getLocation());
		flames.location(getParticleLocation());
	}

	public Location getParticleLocation() {
		Location location = getLocation().toCenterLocation();
		DecorationInteractData data = new DecorationInteractData(getBlock(), BlockFace.UP);
		if (data.getDecoration() == null)
			return location.add(0, 0.55, 0);

		double distance = 0.4;
		location = location.add(0, -0.2, 0);
		BlockFace facing = data.getDecoration().getRotation().getOppositeRotation().getBlockFace();

		return location.add(facing.getModX() * distance, 0, facing.getModZ() * distance);
	}

	@Override
	public void tick() {
		super.tick();

		if (getVirtualInv().isLit()) {
			if (getTick() % TickTime.SECOND.x(1) == 0) {
				if (RandomUtils.chanceOf(50)) {
					crackle.play();
					flames.spawn();
				}
			}
		}
	}

	@Override
	public void breakTile() {
		final ItemStack fuel = virtualInv.getFuel();
		final ItemStack input = virtualInv.getInput();
		final ItemStack output = virtualInv.getOutput();
		final int xp = (int) virtualInv.extractExperience();

		World world = getBukkitWorld();
		Location dropLoc = getLocation().toCenterLocation();

		if (Nullables.isNotNullOrAir(fuel))
			world.dropItemNaturally(dropLoc, fuel);

		if (Nullables.isNotNullOrAir(input))
			world.dropItemNaturally(dropLoc, input);

		if (Nullables.isNotNullOrAir(output))
			world.dropItemNaturally(dropLoc, output);

		if (xp > 0)
			world.spawn(dropLoc, ExperienceOrb.class, orb -> orb.setExperience(xp));

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
