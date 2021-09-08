package gg.projecteden.nexus.models.mail;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Mailer.class)
public class MailerService extends MongoService<Mailer> {
	private final static Map<UUID, Mailer> cache = new ConcurrentHashMap<>();

	public Map<UUID, Mailer> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(Mailer mailer) {
		return mailer.getMail().isEmpty() && mailer.getPendingMail().isEmpty();
	}

}
