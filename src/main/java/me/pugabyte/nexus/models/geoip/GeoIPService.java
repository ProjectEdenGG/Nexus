package me.pugabyte.nexus.models.geoip;

import com.google.gson.Gson;
import eden.mongodb.annotations.PlayerClass;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.MongoService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(GeoIP.class)
public class GeoIPService extends MongoService<GeoIP> {
	private final static Map<UUID, GeoIP> cache = new HashMap<>();

	public Map<UUID, GeoIP> getCache() {
		return cache;
	}

	private final String KEY = Nexus.getInstance().getConfig().getString("tokens.ipstack");
	// Raven gives a huge boost to Canada with his VPN
	private final static List<String> ignore = new ArrayList<>(); // Arrays.asList("fce1fe67-9514-4117-bcf6-d0c49ca0ba41");

	static {
		Nexus.getInstance().addConfigDefault("tokens.ipstack", "abcdef");
	}

	@Override
	@NotNull
	public GeoIP get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			GeoIP geoIp = database.createQuery(GeoIP.class).field(_id).equal(uuid).first();

			if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
				if (geoIp != null)
					if (!Bukkit.getPlayer(uuid).getAddress().getHostString().equals(geoIp.getIp())) {
						geoIp = request(Bukkit.getPlayer(uuid));
						save(geoIp);
					}

				if (geoIp == null) {
					geoIp = request(Bukkit.getPlayer(uuid));
					save(geoIp);
				}
			}

			return geoIp;
		});

		if (cache.get(uuid) == null)
			throw new InvalidInputException("Could not find " + Bukkit.getOfflinePlayer(uuid).getName() + "'s location");

		return cache.get(uuid);
	}

	@SneakyThrows
	public GeoIP request(Player player) {
		return request(player, player.getAddress().getHostString());
	}

	@SneakyThrows
	public GeoIP request(OfflinePlayer player, String ip) {
		if (ignore.contains(player.getUniqueId().toString()))
			return null;
		Nexus.log("Requesting GeoIP info for " + player.getName() + " (" + ip + ")");

		Request request = new Request.Builder()
				.url("https://api.ipstack.com/" + ip + "?access_key=" + KEY)
				.build();

		try (Response response = new OkHttpClient().newCall(request).execute()) {
			GeoIP geoIp = new Gson().fromJson(response.body().string(), GeoIP.class);
			geoIp.setUuid(player.getUniqueId());
			return geoIp;
		}
	}

	public List<GeoIP> getAll() {
		return database.createQuery(GeoIP.class).find().toList();
	}

	public void save(GeoIP geoIp) {
		if (geoIp != null && geoIp.getIp() != null)
			super.save(geoIp);
	}

}
