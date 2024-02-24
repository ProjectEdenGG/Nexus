package gg.projecteden.nexus.models.offline;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.utils.JsonBuilder;

import java.util.UUID;

public class OfflineMessage {

	private static final OfflineMessageService service = new OfflineMessageService();

	public static void send(HasUniqueId uuid, String message) {
		send(uuid, new JsonBuilder(message));
	}

	public static void send(UUID uuid, String message) {
		send(uuid, new JsonBuilder(message));
	}

	public static void send(HasUniqueId uuid, JsonBuilder builder) {
		send(uuid.getUniqueId(), builder);
	}

	public static void send(UUID uuid, JsonBuilder builder) {
		service.get(uuid).send(builder);
	}

}
