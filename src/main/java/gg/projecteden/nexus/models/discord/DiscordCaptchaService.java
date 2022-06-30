package gg.projecteden.nexus.models.discord;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DiscordCaptcha.class)
public class DiscordCaptchaService extends MongoPlayerService<DiscordCaptcha> {
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
