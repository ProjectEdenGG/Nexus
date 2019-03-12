package me.pugabyte.bncore.models.persistence;

public enum BearNationDatabase {
	ALERTS("smp_alerts"),
	ANTIBOTS("antibots"),
	NAMELESS("nameless");

	String database;

	BearNationDatabase(String database) {
		this.database = database;
	}

	public String getDatabase() {
		return database;
	}
}
