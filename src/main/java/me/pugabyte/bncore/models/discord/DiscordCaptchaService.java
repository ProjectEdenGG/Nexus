package me.pugabyte.bncore.models.discord;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;
import me.pugabyte.bncore.models.geoip.GeoIP;
import me.pugabyte.bncore.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

@PlayerClass(DiscordCaptcha.class)
public class DiscordCaptchaService extends MongoService {

	@Deprecated
	public Map<UUID, GeoIP> getCache() {
		return null;
	}

	@Override
	@NotNull
	@Deprecated // Use get()
	public <T> T get(UUID uuid) {
		throw new UnsupportedOperationException("Use get()");
	}

	public static DiscordCaptcha captcha;

	// Just a single object, tying it all to Koda's account
	public DiscordCaptcha get() {
		if (captcha == null) {
			captcha = database.createQuery(DiscordCaptcha.class).first();
			if (captcha == null)
				captcha = new DiscordCaptcha(Utils.getPlayer("KodaBear").getUniqueId());
		}

		return captcha;
	}

	@Override
	public <T> void saveSync(T object) {
		database.delete(object);
		database.save(object);
	}

}
