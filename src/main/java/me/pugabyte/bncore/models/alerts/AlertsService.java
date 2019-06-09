package me.pugabyte.bncore.models.alerts;

import com.dieselpoint.norm.Database;
import com.dieselpoint.norm.Transaction;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.persistence.BearNationDatabase;
import me.pugabyte.bncore.models.persistence.Persistence;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlertsService {
	private Database database = Persistence.getConnection(BearNationDatabase.BEARNATION);

	public Alerts get(Player player) {
		return get(player.getUniqueId().toString());
	}

	public Alerts get(String uuid) {
		List<Alerts.Highlight> highlights = database.where("uuid = ?", uuid).results(Alerts.Highlight.class);
		return new Alerts(uuid, highlights);
	}

	public List<Alerts> getAll() {
		List<String> uuids = Bukkit.getOnlinePlayers().stream()
				.map(p -> p.getUniqueId().toString())
				.collect(Collectors.toList());
		return getAll(uuids);
	}

	public List<Alerts> getAll(List<String> uuids) {
		List<Alerts.Highlight> highlights = database
				.where("uuid IN (\"" + String.join("\",\"", uuids) + "\")")
				.results(Alerts.Highlight.class);

		Map<String, Alerts> alertsMap = new HashMap<>();
		for (Alerts.Highlight highlight : highlights) {
			if (alertsMap.get(highlight.getUuid()) != null) continue;

			List<Alerts.Highlight> playerHighlights = highlights.stream()
					.filter(_highlight -> highlight.getUuid().equals(_highlight.getUuid()))
					.collect(Collectors.toList());

			alertsMap.put(highlight.getUuid(), new Alerts(highlight.getUuid(), playerHighlights));
		}

		return new ArrayList<>(alertsMap.values());
	}

	public void save(Alerts alerts) {
		BNCore.runTaskAsync(() -> {
			Transaction trans = database.startTransaction();
			database.transaction(trans).sql("delete from alerts where uuid = ?", alerts.getUuid()).execute();
			alerts.getHighlights().forEach(highlight -> database.transaction(trans).insert(highlight));
			trans.commit();
		});
	}

}
