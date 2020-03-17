package me.pugabyte.bncore.models.poof;

import com.dieselpoint.norm.Query;
import me.pugabyte.bncore.models.MySQLService;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PoofService extends MySQLService {

	public Poof getBySender(Player sender) {
		Poof request = database.where("sender = ?", sender.getUniqueId().toString())
				.and("expired = 0").first(Poof.class);
		return request;
	}

	public Poof getByReceiver(Player receiver) {
		Poof request = database.where("receiver = ?", receiver.getUniqueId().toString())
				.and("expired = 0").first(Poof.class);
		return request;
	}

	public List<Poof> getActivePoofs() {
		return getActivePoofs(null);
	}

	public List<Poof> getActivePoofs(UUID uuid) {
		Query query = database.where("expired = 0");
		if (uuid != null)
			query.and("sender = ?", uuid);
		return query.results(Poof.class);
	}

}
