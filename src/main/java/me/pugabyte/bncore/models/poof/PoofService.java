package me.pugabyte.bncore.models.poof;

import me.pugabyte.bncore.models.MySQLService;
import org.bukkit.entity.Player;

public class PoofService extends MySQLService {

	public Poof getBySender(Player sender) {
		Poof request = database.where("sender = ?", sender.getUniqueId().toString()).first(Poof.class);
		return request;
	}

	public Poof getByReceiver(Player receiver) {
		Poof request = database.where("receiver = ?", receiver.getUniqueId().toString()).first(Poof.class);
		return request;
	}

	public void remove(Poof request) {
		database.delete(request);
	}

}
