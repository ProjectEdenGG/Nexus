package me.pugabyte.bncore.framework.persistence;


import com.google.common.base.Strings;
import me.pugabyte.bncore.BNCore;
import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseConfig {
	private String host;
	private int port;
	private String username;
	private String password;
	private String prefix;

	public DatabaseConfig(String type) {
		FileConfiguration config = BNCore.getInstance().getConfig();
		host = config.getString("databases." + type + ".host");
		port = config.getInt("databases." + type + ".port");
		username = config.getString("databases." + type + ".username");
		password = config.getString("databases." + type + ".password");
		prefix = config.getString("databases." + type + ".prefix");
		prefix = (Strings.isNullOrEmpty(prefix)) ? "" : prefix + "_";
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPrefix() {
		return prefix;
	}

}
