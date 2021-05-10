package me.pugabyte.nexus.models.geoip;

import eden.mongodb.annotations.PlayerClass;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.HttpUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(GeoIP.class)
public class GeoIPService extends MongoService<GeoIP> {
	private final static Map<UUID, GeoIP> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, GeoIP> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	private static final String URL = "https://api.ipstack.com/%s?access_key=%s";
	private static final String KEY = Nexus.getInstance().getConfig().getString("tokens.ipstack");
	private static final List<String> ignore = List.of();

	static {
		Nexus.getInstance().addConfigDefault("tokens.ipstack", "abcdef");
	}

	@SneakyThrows
	public GeoIP request(UUID uuid, String ip) {
		GeoIP geoip = get(uuid);

		if (geoip.isOnline()) {
			if (!ip.equals(geoip.getIp())) {
				geoip = call(uuid, ip);
				save(geoip);
			}
		}

		if (geoip.getTimestamp() == null) {
			geoip = call(uuid, ip);
			save(geoip);
		}

		cache(geoip);
		return geoip;
	}

	@SneakyThrows
	private GeoIP call(UUID uuid, String ip) {
		GeoIP original = get(uuid);

		if (ignore.contains(uuid.toString()))
			return original;

		Nexus.log("Requesting GeoIP info for " + Nickname.of(uuid) + " (" + ip + ")");

		GeoIP geoip = HttpUtils.mapJson(GeoIP.class, URL, ip, KEY);
		geoip.setUuid(uuid);
		geoip.setTimestamp(LocalDateTime.now());
		geoip.setSecurity(original.getSecurity());
		return geoip;
	}

	public List<GeoIP> getAll() {
		return database.createQuery(GeoIP.class).find().toList();
	}

	@Override
	public void save(GeoIP geoip) {
		if (geoip != null && geoip.getIp() != null)
			super.save(geoip);
	}

}
