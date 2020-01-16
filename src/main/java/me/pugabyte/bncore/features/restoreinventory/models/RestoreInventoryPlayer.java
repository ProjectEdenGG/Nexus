package me.pugabyte.bncore.features.restoreinventory.models;

import com.onarandombox.multiverseinventories.utils.configuration.json.JsonConfiguration;
import org.bukkit.entity.Player;

public class RestoreInventoryPlayer {
	private final Player restorer;
	private final Player owner;
	private final JsonConfiguration jsonConfig;
	private final String code;

	public RestoreInventoryPlayer(Player restorer, Player owner, JsonConfiguration jsonConfig, String code) {
		this.restorer = restorer;
		this.owner = owner;
		this.jsonConfig = jsonConfig;
		this.code = code;
	}

	public Player getRestorer() {
		return restorer;
	}

	public Player getOwner() {
		return owner;
	}

	public JsonConfiguration getJsonConfig() {
		return jsonConfig;
	}

	public String getCode() {
		return code;
	}
} 