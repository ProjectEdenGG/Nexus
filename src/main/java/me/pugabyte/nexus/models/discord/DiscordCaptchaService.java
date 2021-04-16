package me.pugabyte.nexus.models.discord;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DiscordCaptcha.class)
public class DiscordCaptchaService extends MongoService<DiscordCaptcha> {
	private final static Map<UUID, DiscordCaptcha> cache = new HashMap<>();

	public Map<UUID, DiscordCaptcha> getCache() {
		return cache;
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
