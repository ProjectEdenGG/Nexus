package me.pugabyte.bncore.models.homes;

import com.dieselpoint.norm.Query;
import com.dieselpoint.norm.Transaction;
import me.pugabyte.bncore.models.BaseService;
import me.pugabyte.bncore.utils.Tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeService extends BaseService {
	private final static Map<String, HomeOwner> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public HomeOwner get(String uuid) {
		if (!cache.containsKey(uuid)) {
			HomeOwner homeOwner = HomeOwner.builder()
					.uuid(uuid)
					.homes(getHomes(uuid))
					.fullAccessList(getAccessList(uuid, HomeOwner.PermissionType.ALLOW_ALL))
					.build();

			cache.put(homeOwner.getUuid(), homeOwner);
		}

		return cache.get(uuid);
	}

	public void save(HomeOwner homeOwner) {
		Tasks.async(() -> {
			Transaction transaction = database.startTransaction();

			database.transaction(transaction)
					.table("home")
					.where("uuid = ?", homeOwner.getUuid())
					.delete();

			database.transaction(transaction)
					.table("home_permission_map")
					.where("uuid = ?", homeOwner.getUuid())
					.delete();

			homeOwner.getFullAccessList().forEach(uuid -> database.transaction(transaction)
					.sql("INSERT INTO home_permission_map VALUES (?, ?, ?)")
					.args(homeOwner.getUuid(), HomeOwner.PermissionType.ALLOW_ALL.name(), uuid)
					.execute());

			homeOwner.getHomes().forEach(home -> {
				database.transaction(transaction)
						.insert(home);
				home.getAccessList().forEach(uuid -> database.transaction(transaction)
						.sql("INSERT INTO home_permission_map VALUES (?, ?, ?, ?)")
						.args(home.getUuid(), home.getName(), HomeOwner.PermissionType.ALLOW.name(), uuid)
						.execute());
			});

			transaction.commit();
		});
	}

	private List<Home> getHomes(String uuid) {
		List<Home> homes = database.where("uuid = ?").args(uuid).results(Home.class);
		for (Home home : homes)
			if (home.getUuid() != null)
				home.setAccessList(getAccessList(uuid, HomeOwner.PermissionType.ALLOW));

		return homes;
	}

	private Home getHome(String uuid, String name) {
		Home home = database.where("uuid = ?").and("name = ?").args(uuid, name).first(Home.class);
		if (home.getUuid() == null)
			return null;

		home.setAccessList(getAccessList(uuid, name, HomeOwner.PermissionType.ALLOW));
		return home;
	}

	private List<String> getAccessList(String uuid, HomeOwner.PermissionType accessType) {
		return getAccessList(uuid, null, accessType);
	}

	private List<String> getAccessList(String uuid, String name, HomeOwner.PermissionType accessType) {
		Query query = database
				.select("accessUuid")
				.table("home_permission_map")
				.where("accessType = ?")
				.and("uuid = ?");

		if (name != null)
			query.and("name = ?").args(accessType, uuid, name);
		else
			query.args(accessType, uuid);

		return query.results(String.class);
	}

}
