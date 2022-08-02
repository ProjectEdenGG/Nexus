package gg.projecteden.nexus.framework.persistence.mongodb;

import gg.projecteden.nexus.framework.interfaces.EntityOwnedObject;

public abstract class MongoEntityService<T extends EntityOwnedObject> extends MongoBukkitService<T> {

	@Override
	protected String pretty(T object) {
		return object.getUniqueId().toString();
	}

}
