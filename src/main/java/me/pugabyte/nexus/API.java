package me.pugabyte.nexus;

import eden.EdenAPI;
import eden.mongodb.DatabaseConfig;
import eden.utils.Env;

public class API extends EdenAPI {

	public API() {
		instance = this;
	}

	@Override
	public Env getEnv() {
		return Nexus.getEnv();
	}

	@Override
	public DatabaseConfig getDatabaseConfig() {
		return DatabaseConfig.builder()
				.password(Nexus.getInstance().getConfig().getString("databases.mongodb.password"))
				.modelPath("me.pugabyte.nexus.models")
				.build();
	}

}
