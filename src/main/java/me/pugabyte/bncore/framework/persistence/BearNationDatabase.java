package me.pugabyte.bncore.framework.persistence;

public enum BearNationDatabase {
	BEARNATION,
	NAMELESS;

	public String getDatabase() {
		String name = name().toLowerCase();

		if (name.equals("bearnation"))
			return name;

		return "bearnation_" + name;
	}
}
