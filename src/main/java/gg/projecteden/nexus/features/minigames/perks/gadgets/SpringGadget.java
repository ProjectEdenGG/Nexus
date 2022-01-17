package gg.projecteden.nexus.features.minigames.perks.gadgets;

import gg.projecteden.nexus.features.minigames.models.perks.common.GadgetPerk;
import gg.projecteden.nexus.features.particles.ParticleUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SpringGadget implements GadgetPerk {
	@Override
	public @NotNull String getName() {
		return "Spring";
	}

	@Override
	public @NotNull String getDescription() {
		return "Launch into the air with this portable spring";
	}

	@Override
	public int getPrice() {
		return 25;
	}

	@Override
	public ItemStack getItem() {
		return basicItem(Material.WHITE_WOOL);
	}

	@Override
	public void useGadget(Player player) {
		ParticleUtils.display(Particle.CLOUD, player.getLocation(), 15, .3d, .15d, .3d, .01d);
		player.setVelocity(player.getVelocity().setY(1));
	}

	@Override
	public boolean cancelEvent() {
		return true;
	}

	@Override
	public int getCooldown() {
		return TickTime.SECOND.x(1.5);
	}
}
