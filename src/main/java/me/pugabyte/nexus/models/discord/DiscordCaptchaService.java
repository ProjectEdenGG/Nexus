package me.pugabyte.nexus.models.discord;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DiscordCaptcha.class)
public class DiscordCaptchaService extends MongoService<DiscordCaptcha> {
	private final static Map<UUID, DiscordCaptcha> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, DiscordCaptcha> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public DiscordCaptcha get() {
		return super.get(Nexus.getUUID0());
	}

	@Override
	public void saveSync(DiscordCaptcha captcha) {
		database.delete(captcha);
		database.save(captcha);
	}

}
