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
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ServerClientMessaging extends Feature {

	public final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public final static String CHANNEL_CLIENTBOUND = "titan:clientbound";
	public final static String CHANNEL_SERVERBOUND = "titan:serverbound";

	private final LocalResourcePackUserService service = new LocalResourcePackUserService();

	@Override
	public void onStart() {
		Bukkit.getMessenger().registerIncomingPluginChannel(Nexus.getInstance(), CHANNEL_SERVERBOUND, new ServerboundListener());
		Bukkit.getMessenger().registerOutgoingPluginChannel(Nexus.getInstance(), CHANNEL_CLIENTBOUND);

		Tasks.repeat(1, 1, this::flush);

		Nexus.registerListener(new ClientboundListeners());
	}

	static final List<ClientMessage> toSend = new ArrayList<>();

	private void flush() {
		if (toSend.isEmpty()) return;
		Nexus.debug("Flushing ServerClientMessaging Queue");

		Map<Player, JsonObject> playerMap = new HashMap<>();

		toSend.forEach(message -> {
			message.getPlayers().forEach(player -> {
				if (!service.get(player).shouldUseNewMessagingFormat()) {
					processOld(player, message);
				}
				else {
					JsonObject json = playerMap.computeIfAbsent(player, p -> new JsonObject());
					json.add(message.getMessage().getType().name().toLowerCase(), GSON.fromJson(message.getMessage().getJson(), JsonObject.class));
				}
			});
		});

		playerMap.forEach(((player, jsonObject) -> {
			String json = GSON.toJson(jsonObject);
			byte[] bytes = json.getBytes();

			Nexus.debug("Sending Titan Message to %s:".formatted(player.getName()));
			Nexus.debug(GSON.toJson(jsonObject));
			Nexus.debug(Arrays.toString(bytes));

			player.sendPluginMessage(Nexus.getInstance(), CHANNEL_CLIENTBOUND, bytes);

			toSend.forEach(message -> {
				if (jsonObject.has(message.getMessage().getType().name().toLowerCase()))
					message.getMessage().onSend(player);
			});
		}));

		toSend.clear();
	}

	private void processOld(Player player, ClientMessage message) {
		Nexus.debug("Sending old Saturn Update style");
		if (message.getMessage() instanceof SaturnUpdate) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("saturn-update");

			player.sendPluginMessage(Nexus.getInstance(), CHANNEL_CLIENTBOUND, out.toByteArray());
		}
	}

	public static class ServerboundListener implements PluginMessageListener {

		@Override
		public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] bytes) {
			try {
				String string = new String(bytes);
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

