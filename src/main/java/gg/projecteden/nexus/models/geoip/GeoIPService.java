package gg.projecteden.nexus.models.geoip;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.HttpUtils;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(GeoIP.class)
public class GeoIPService extends MongoPlayerService<GeoIP> {
	private final static Map<UUID, GeoIP> cache = new ConcurrentHashMap<>();

	public Map<UUID, GeoIP> getCache() {
		return cache;
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
		geoip.setMitigated(original.isMitigated());
		geoip.setTimeFormat(original.getTimeFormat());
		getCache().put(uuid, geoip);
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
