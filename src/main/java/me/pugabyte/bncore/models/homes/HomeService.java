package me.pugabyte.bncore.models.homes;

import me.pugabyte.bncore.models.BaseService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomeService extends BaseService {

	@Override
	public CompletableFuture<HomeOwner> get(String uuid) {
		return CompletableFuture.supplyAsync(() -> HomeOwner.builder()
				.uuid(uuid)
				.homes(getHomes(uuid))
				.fullAccessList(database
						.select("accessUuid")
						.table("home_permission_map")
						.where("uuid = ?")
						.and("accessType = ?")
						.args(uuid, HomeOwner.PermissionType.ALLOW_ALL.name())
						.results(String.class))
				.build());
	}

	public Home getHome(String uuid, String name) {
		Home home = database.where("uuid = ?").and("name = ?").args(uuid, name).first(Home.class);
		if (home.getUuid() == null)
			return null;

		home.setAccessList(database
				.select("accessUuid")
				.table("home_permission_map")
				.where("uuid = ?")
				.and("name = ?")
				.and("accessType = ?")
				.args(uuid, name, HomeOwner.PermissionType.ALLOW.name())
				.results(String.class));
		return home;
	}

	public List<Home> getHomes(String uuid) {
		List<Home> homes = database.where("uuid = ?").args(uuid).results(Home.class);
		for (Home home : homes) {
			if (home.getUuid() == null) continue;

			home.setAccessList(database
					.select("accessUuid")
					.table("home_permission_map")
					.where("uuid = ?")
					.and("accessType = ?")
					.args(uuid, HomeOwner.PermissionType.ALLOW.name())
					.results(String.class));
		}

		return homes;
	}

	public List<String> getHomeNames(String uuid) {
		return database.select("name").table("home").where("uuid = ?").args(uuid).results(String.class);
	}

}
