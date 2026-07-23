package gg.projecteden.nexus.features.tournaments;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;

/*
	TODO:
	 - SAVE RATE-LIMIT USAGE IN MONGO
 */
@Disabled
public class Tournament extends Feature {

	private ChallongeClient challonge;

	@Override
	public void onStart() {
		String apiKey = Nexus.getInstance().getConfig().getString("challonge.api-key");
		if (apiKey == null || apiKey.isBlank()) {
			throw new IllegalStateException("Challonge API key is missing from config.yml");
		}

		Dev.WAKKA.send("Challonge API enabled");

		//this.challonge = new ChallongeClient(apiKey);
	}
}
