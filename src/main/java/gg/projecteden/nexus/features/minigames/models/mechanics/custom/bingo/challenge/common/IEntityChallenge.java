package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common;

import gg.projecteden.nexus.utils.EntityUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Set;

public interface IEntityChallenge extends IChallenge {

	Set<EntityType> getTypes();

	@Override
	default Material getDisplayMaterial() {
		return EntityUtils.getSpawnEgg(getTypes().iterator().next());
	}

}
