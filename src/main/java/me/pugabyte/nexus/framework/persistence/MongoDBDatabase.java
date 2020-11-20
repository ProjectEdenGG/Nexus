package me.pugabyte.nexus.framework.persistence;

public enum MongoDBDatabase {
	BEARNATION;

	public String getDatabase() {
		String name = name().toLowerCase();

		if (name.equals("bearnation"))
			return name;

		return "bearnation_" + name;
	}
}
