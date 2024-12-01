package gg.projecteden.nexus.features.blockmechanics.mechanics;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import net.minecraft.util.Mth;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.CookingRecipe;

// Displays particles in front of furnace if contains 1400+ experience (30L from 0)
public class FurnaceExperience implements Listener {

	private final ParticleBuilder particle = new ParticleBuilder(Particle.VILLAGER_HAPPY).count(5).extra(0).offset(0.1, 0.05, 0.1);

	public FurnaceExperience() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(FurnaceSmeltEvent event) {
		Block block = event.getBlock();
		if (!(WorldGroup.SURVIVAL.contains(block.getWorld())))
			return;

		org.bukkit.block.Furnace furnace = (org.bukkit.block.Furnace) block.getState();
		Directional directional = (Directional) block.getBlockData();

		for (CookingRecipe<?> recipe : furnace.getRecipesUsed().keySet()) {
			int amount = furnace.getRecipeUsedCount(recipe.getKey());
			double experience = calculateExperience(amount, recipe.getExperience());
			if (experience >= 1400) {
				sendParticles(block, directional.getFacing());
				return;
			}
		}
	}

	// AbstractFurnaceBlockEntity#createExperience
	private double calculateExperience(int i, float f) {
		int j = Mth.floor((float) i * f);
		float f1 = Mth.frac((float) i * f);

		if (f1 != 0.0F && Math.random() < (double) f1)
			++j;

		return j;
	}

	private void sendParticles(Block block, BlockFace facing) {
		Location location = getParticleLocation(block, facing);

		int randomLoops = RandomUtils.randomInt(2, 7);
		for (int i = 0; i < randomLoops; i++) {
			Tasks.wait(TickTime.TICK.x(30 * i), () -> particle.location(location).spawn());
		}
	}

	private Location getParticleLocation(Block block, BlockFace facing) {
		Location location = block.getLocation().toCenterLocation();

		double distance = 0.4;
		location = location.add(0, -0.2, 0);

		return location.add(facing.getModX() * distance, 0, facing.getModZ() * distance);
	}
}
