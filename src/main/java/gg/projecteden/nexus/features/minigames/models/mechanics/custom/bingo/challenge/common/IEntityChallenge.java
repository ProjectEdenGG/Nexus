package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common;

import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Set;

public interface IEntityChallenge extends IChallenge {

	Set<EntityType> getTypes();

	int getAmount();

	@Override
	default Material getDisplayMaterial() {
		return new ItemBuilder(Material.BARRIER).spawnEgg(getTypes().iterator().next()).material();
	}

}
