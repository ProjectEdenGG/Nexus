package gg.projecteden.nexus.framework.persistence.mysql;

public enum MySQLDatabase {
	BEARNATION,
	LITEBANS,
	NAMELESS,
	SMP_LWC;

	public String getDatabase() {
		String name = name().toLowerCase();

		if ("bearnation".equals(name))
			return name;

		return "bearnation_" + name;
	}
}
