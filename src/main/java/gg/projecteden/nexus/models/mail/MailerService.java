package gg.projecteden.nexus.models.mail;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.Utils;

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
