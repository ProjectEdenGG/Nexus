package gg.projecteden.nexus.framework.persistence;

public enum MySQLDatabase {
	BEARNATION,
	LITEBANS,
	NAMELESS,
	SMP_LWC;

	public String getDatabase() {
		String name = name().toLowerCase();

		if (name.equals("bearnation"))
			return name;

		return "bearnation_" + name;
	}
}
