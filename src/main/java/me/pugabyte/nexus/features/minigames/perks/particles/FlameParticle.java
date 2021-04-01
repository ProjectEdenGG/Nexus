package me.pugabyte.nexus.features.minigames.perks.particles;

import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.features.minigames.models.perks.common.TickablePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FlameParticle extends TickablePerk {
	@Override
	public String getName() {
		return "Flame";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.MAGMA_BLOCK);
	}

	@Override
	public String[] getDescription() {
		return new String[]{"Burn like the fire",
							"that fuels your",
							"craving for blood!"};
	}

	@Override
	public PerkCategory getPerkCategory() {
		return PerkCategory.PARTICLE;
	}

	@Override
	public int getPrice() {
		return 0;
	}

	@Override
	public void tick(Player player) {
		particle(player, Particle.FLAME, 0.002d, player.getWorld().getPlayers());
	}
}
