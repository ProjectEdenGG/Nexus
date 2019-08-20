package me.pugabyte.bncore.framework.persistence;

public enum BearNationDatabase {
	BEARNATION(""),
	ALERTS("smp_alerts"),
	ANTIBOTS("antibots"),
	NAMELESS("nameless");

	String database;

	BearNationDatabase(String database) {
		this.database = database;
	}

	public String getDatabase() {
		if (database.length() > 0)
			return "bearnation_" + database;

		return "bearnation";
	}
}
