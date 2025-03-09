package gg.projecteden.nexus.features.titan;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.titan.clientbound.SaturnUpdate;
import gg.projecteden.nexus.features.titan.models.PluginMessage;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.Debug.DebugType.TITAN;

public class ServerClientMessaging extends Feature {

	public final static Gson GSON = new GsonBuilder().create();
	public final static String CHANNEL = "titan:networking";

	private final LocalResourcePackUserService service = new LocalResourcePackUserService();

	@Override
	public void onStart() {
		Bukkit.getMessenger().registerIncomingPluginChannel(Nexus.getInstance(), CHANNEL, new ServerboundListener());
		Bukkit.getMessenger().registerOutgoingPluginChannel(Nexus.getInstance(), CHANNEL);

		Tasks.repeat(1, 1, this::flush);

		Nexus.registerListener(new ClientboundListeners());
	}

	static final List<ClientMessage> toSend = new ArrayList<>();

	private void flush() {
		if (toSend.isEmpty()) return;
		Debug.log(TITAN, "Flushing ServerClientMessaging Queue");

		Map<Player, JsonObject> playerMap = new HashMap<>();

		Collections.reverse(toSend); // Prefer newer messages
		toSend.forEach(message -> {
			message.getPlayers().forEach(player -> {
				if (!service.get(player).shouldUseNewMessagingFormat()) {
					processOld(player, message);
				}
				else {
					JsonObject json = playerMap.computeIfAbsent(player, p -> new JsonObject());
					String type = message.getMessage().getType().name().toLowerCase();

					if (json.has(type)) { // Combine like messages
						JsonObject original = json.getAsJsonObject(type);
						JsonObject duplicate = GSON.fromJson(message.getMessage().getJson(), JsonObject.class);
						duplicate.keySet().forEach(key -> {
							if (original.has(key))
								return;
							original.add(key, duplicate.get(key));
						});
					}
					else
						json.add(type, GSON.fromJson(message.getMessage().getJson(), JsonObject.class));
				}
			});
		});

		playerMap.forEach(((player, jsonObject) -> {
			String json = GSON.toJson(jsonObject);
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF(json);

			Debug.log(TITAN, "Sending message to %s:".formatted(player.getName()));
			Debug.log(TITAN, GSON.toJson(jsonObject));
			Debug.log(TITAN, Arrays.toString(out.toByteArray()));

			player.sendPluginMessage(Nexus.getInstance(), CHANNEL, out.toByteArray());

			toSend.forEach(message -> {
				if (jsonObject.has(message.getMessage().getType().name().toLowerCase()))
					message.getMessage().onSend(player);
			});
		}));

		toSend.clear();
	}

	private void processOld(Player player, ClientMessage message) {
		Debug.log(TITAN, "Sending old Saturn Update style");
		if (message.getMessage() instanceof SaturnUpdate) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("saturn-update");

			player.sendPluginMessage(Nexus.getInstance(), CHANNEL, out.toByteArray());
		}
	}

	public static class ServerboundListener implements PluginMessageListener {

		@Override
		public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] bytes) {
			try {
				String string = new String(bytes);
				string = string.substring(string.indexOf("{"));
				JsonElement jsonElement = GSON.fromJson(string, JsonElement.class);
				JsonObject json = jsonElement.getAsJsonObject();

				if (json == null)
					return;

				int processed = 0;
				for (PluginMessage message : PluginMessage.values()) {
					try {
						if (json.has(message.name().toLowerCase())) {
							message.receive(json.getAsJsonObject(message.name().toLowerCase()), player);
							processed++;
						}
					} catch (Exception ex) {
						Nexus.warn("Error while parsing incoming message (%s) from Titan for %s".formatted(message.name(), player.getName()));
						ex.printStackTrace();
					}
				}
				if (processed == 0)
					throw new InvalidInputException("No matching PluginMessages were found for Titan message (%s)".formatted(player.getName()));
			} catch (Exception ignore) { // Old messaging format?
				try {
					String stringMessage = new String(bytes);
					LocalResourcePackUser.TitanSettings settings = new Gson().fromJson(stringMessage, LocalResourcePackUser.TitanSettings.class);

					new LocalResourcePackUserService().edit(player, user -> {
						user.setTitanSettings(settings);
						Nexus.log("Received Saturn/Titan updates from " + player.getName() + ". Saturn: " + user.getSaturnVersion() + " Titan: " + user.getTitanVersion());
					});
				} catch (Exception ex) {
					Nexus.warn("Error while parsing incoming message from Titan for %s".formatted(player.getName()));
					ex.printStackTrace();
				}
			}
		}
	}

}

