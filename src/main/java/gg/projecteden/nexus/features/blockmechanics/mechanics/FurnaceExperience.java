package gg.projecteden.nexus.features.blockmechanics.mechanics;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import net.minecraft.util.Mth;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.CookingRecipe;

// Displays particles in front of furnace if ...
public class FurnaceExperience implements Listener {

	private final ParticleBuilder particle = new ParticleBuilder(Particle.DUST).count(5).extra(0).offset(0.1, 0.05, 0.1);
	private static final int MIN_EXPERIENCE = (int) getTotalExperience(10);
	private static final int MAX_EXPERIENCE = (int) getTotalExperience(30);

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
			float experience = calculateExperience(amount, recipe.getExperience());
			if (experience >= MIN_EXPERIENCE)
				sendParticles(block, directional.getFacing(), experience);
		}
	}

	// AbstractFurnaceBlockEntity#createExperience
	private float calculateExperience(int i, float f) {
		int j = Mth.floor((float) i * f);
		float f1 = Mth.frac((float) i * f);

		if (f1 != 0.0F && Math.random() < (double) f1)
			++j;

		return j;
	}

	private void sendParticles(Block block, BlockFace facing, float exp) {
		exp -= MIN_EXPERIENCE;

		Color color = generateTransitionColor(exp);
		Location location = getParticleLocation(block, facing);

		int randomLoops = RandomUtils.randomInt(2, 7);
		for (int i = 0; i < randomLoops; i++) {
			Tasks.wait(TickTime.TICK.x(30 * i), () -> particle.color(color).location(location).spawn());
		}
	}

	public static org.bukkit.Color generateTransitionColor(float step) {
		float midPoint = (float) (MAX_EXPERIENCE / 2.0); // The point where green is fully saturated (255)

		int red, green;

		if (step <= midPoint) {
			// First phase: red increases from 0 to 255, green stays at 255
			red = (int) (255.0 * (step / midPoint));
			green = 255;
		} else {
			// Second phase: green decreases from 255 to 0, red stays at 255
			red = 255;
			green = (int) (255 * (1 - (step - midPoint) / (MAX_EXPERIENCE - midPoint)));
		}

		red = MathUtils.clamp(red, 0, 255);
		green = MathUtils.clamp(green, 0, 255);

		return Color.fromRGB(red, green, 50);
	}

	//

	private Location getParticleLocation(Block block, BlockFace facing) {
		Location location = block.getLocation().toCenterLocation();

		double distance = 0.5;
		location = location.add(0, -0.2, 0);

		return location.add(facing.getModX() * distance, 0, facing.getModZ() * distance);
	}

	public static double getNextLevelRequiredExperience(int currentLevel) {
		if (currentLevel >= 0 && currentLevel <= 15)
			return 2 * currentLevel + 7;

		if (currentLevel >= 16 && currentLevel <= 30)
			return 5 * currentLevel - 38;

		return 9 * currentLevel - 158;
	}

	public static double getTotalExperience(int level) {
		if (level >= 0 && level <= 16)
			return Math.pow(level, 2) + 6 * level;

		if (level >= 17 && level <= 31)
			return 2.5 * Math.pow(level, 2) - 40.5 * level + 360;

		return 4.5 * Math.pow(level, 2) - 162.5 * level + 2220;
	}

	public static double getExpLevel(int exp) {
		if (exp >= 0 && exp <= 352)
			return Math.sqrt(exp + 9) - 3;

		if (exp >= 353 && exp <= 1507)
			return (81d / 10d) + Math.sqrt((2d / 5d) * (exp - (7839d / 40d)));

		return (325d / 18d) + Math.sqrt((2d / 9d) * (exp - (54215d / 72d)));
	}
}
