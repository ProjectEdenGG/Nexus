package me.pugabyte.nexus.models.mail;

import eden.mongodb.annotations.PlayerClass;
import eden.utils.Utils;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.utils.WorldGroup;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Mailer.class)
public class MailerService extends MongoService<Mailer> {
	private final static Map<UUID, Mailer> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, Mailer> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected void beforeSave(Mailer mailer) {
		for (WorldGroup worldGroup : mailer.getMail().keySet())
			if (Utils.isNullOrEmpty(mailer.getMail().get(worldGroup)))
				mailer.getMail().remove(worldGroup);

		for (WorldGroup worldGroup : mailer.getPendingMail().keySet())
			if (mailer.getPendingMail().get(worldGroup) == null)
				mailer.getPendingMail().remove(worldGroup);
	}

	@Override
	protected boolean deleteIf(Mailer mailer) {
		return mailer.getMail().isEmpty() && mailer.getPendingMail().isEmpty();
	}

}
