package gg.projecteden.nexus.features.titan.models;

import gg.projecteden.nexus.features.titan.ServerClientMessaging;
import org.bukkit.entity.Player;

public abstract class Clientbound implements Message {

	public void onSend(Player player) {}

	public String getJson() {
		return ServerClientMessaging.GSON.toJson(this);
	}

	public abstract PluginMessage getType();
}
