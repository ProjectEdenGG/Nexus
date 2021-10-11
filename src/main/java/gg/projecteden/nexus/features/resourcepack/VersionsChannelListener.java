package gg.projecteden.nexus.features.resourcepack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class VersionsChannelListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
		if (!channel.equalsIgnoreCase("titan:out"))
			return;

		String stringMessage = new String(message);
		JsonObject json = new Gson().fromJson(stringMessage, JsonObject.class);
		String titanVersion = json.has("titan") ? json.get("titan").toString() : null;
		String saturnVersion = json.has("saturn") ? json.get("saturn").toString() : null;

		Nexus.log("Received Saturn/Titan updates from " + player.getName() + ". Saturn: " + saturnVersion + " Titan: " + titanVersion);
		new LocalResourcePackUserService().edit(player, user -> {
			user.setSaturnVersion(saturnVersion);
			user.setTitanVersion(titanVersion);
		});
	}

}
