package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.parchment.entity.EntityData;
import gg.projecteden.parchment.entity.EntityDataFragment;
import gg.projecteden.parchment.entity.EntityDataKey;
import lombok.Data;
import org.bukkit.entity.Entity;

@Data
public class DecorationEntityData extends EntityDataFragment<Entity> {

	private static final EntityDataKey<DecorationEntityData, Entity> key = EntityData.createKey(DecorationEntityData::new, Entity.class);

	public static DecorationEntityData of(Entity entity) {
		return entity.getStoredEntityData().get(key);
	}

	public boolean processDestroy = false;
}
