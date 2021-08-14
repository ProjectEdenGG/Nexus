package gg.projecteden.nexus.models.discord;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(DiscordCaptcha.class)
public class DiscordCaptchaService extends MongoService<DiscordCaptcha> {
	private final static Map<UUID, DiscordCaptcha> cache = new ConcurrentHashMap<>();

	public Map<UUID, DiscordCaptcha> getCache() {
		return cache;
	}

	public DiscordCaptcha get() {
		return super.get0();
	}

	@Override
	public void saveSync(DiscordCaptcha captcha) {
		database.delete(captcha);
		database.save(captcha);
	}

}
