package me.pugabyte.bncore.models.geoip;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.MongoService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GeoIPService extends MongoService {
	private final static Map<UUID, GeoIP> cache = new HashMap<>();
	private final String KEY = BNCore.getInstance().getConfig().getString("ipstack.apiKey");

	static {
		BNCore.getInstance().addConfigDefault("ipstack.apiKey", "abcdef");
	}

	public void clearCache() {
		cache.clear();
	}

	@Override
	public GeoIP get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			GeoIP geoIp = database.createQuery(GeoIP.class).field(_id).equal(uuid).first();

			if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
				if (geoIp != null)
					if (!Bukkit.getPlayer(uuid).getAddress().getHostString().equals(geoIp.getIp()))
						geoIp = request(Bukkit.getPlayer(uuid));

				if (geoIp == null)
					geoIp = request(Bukkit.getPlayer(uuid));
			}

			if (geoIp != null && geoIp.getIp() != null)
				save(geoIp);

			return geoIp;
		});

		if (cache.get(uuid) == null)
			throw new InvalidInputException("Could not find " + Bukkit.getOfflinePlayer(uuid).getName() + "'s location");

		return cache.get(uuid);
	}

	@SneakyThrows
	public GeoIP request(Player player) {
		if (player.getAddress().getHostString().equals("217.182.168.113"))
			throw new InvalidInputException("Player on EU IP, unable to get their location.");

		BNCore.log("Requesting GeoIP info for " + player.getName() + " (" + player.getAddress().getHostString() + ")");

		Request request = new Request.Builder()
				.url("https://api.ipstack.com/" + player.getAddress().getHostString() + "?access_key=" + KEY)
				.build();

		try (Response response = new OkHttpClient().newCall(request).execute()) {
			GeoIP geoIp = new Gson().fromJson(response.body().string(), GeoIP.class);
			geoIp.setUuid(player.getUniqueId());
			return geoIp;
		}
	}

}
