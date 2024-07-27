package gg.projecteden.nexus.models.radio;

import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
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
			for (Radio radio : config.getRadios()) {
				if (Nullables.isNullOrEmpty(radio.getSongPlayers()))
					continue;

				for (SongPlayer songPlayer : radio.getSongPlayers()) {
					if (songPlayer != null)
						songPlayer.destroy();
				}

				radio.getSongPlayers().clear();
			}

		super.clearCache();
	}

}
