package gg.projecteden.nexus.models.radio;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.models.radio.RadioConfig.Radio;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(RadioConfig.class)
public class RadioConfigService extends MongoPlayerService<RadioConfig> {
	private final static Map<UUID, RadioConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, RadioConfig> getCache() {
		return cache;
	}

	@Override
	public void clearCache() {
		for (RadioConfig config : getCache().values())
			for (Radio radio : config.getRadios())
				if (radio.getSongPlayer() != null) {
					radio.getSongPlayer().destroy();
					radio.setSongPlayer(null);
				}

		super.clearCache();
	}

}
