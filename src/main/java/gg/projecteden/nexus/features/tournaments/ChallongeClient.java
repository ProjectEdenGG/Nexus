package gg.projecteden.nexus.features.tournaments;

import gg.projecteden.nexus.utils.HttpUtils;
import okhttp3.Headers;

public class ChallongeClient {

	private static final String BASE_URL = "https://api.challonge.com/v2.1";

	private final Headers headers;

	public ChallongeClient(String apiKey) {
		this.headers = challongeHeaders(apiKey);
	}

	private static Headers challongeHeaders(String apiKey) {
		return Headers.of(
			"Content-Type", "application/vnd.api+json",
			"Accept", "application/json",
			"Authorization-Type", "v1",
			"Authorization", apiKey
		);
	}

	private void beforeRequest() {
		ChallongeRateLimiter.checkAndIncrement();
	}

	/* ----------------------------- */
	/* API calls                     */
	/* ----------------------------- */

	public String listTournamentsRaw() {
		beforeRequest();
		return HttpUtils.get(BASE_URL + "/tournaments", headers);
	}

	private String cachedTournaments;
	private long cacheTime;

	public String listTournamentsCached() {
		if (cachedTournaments != null && System.currentTimeMillis() - cacheTime < 60_000) {
			return cachedTournaments;
		}

		beforeRequest();
		cachedTournaments = HttpUtils.get(BASE_URL + "/tournaments", headers);
		cacheTime = System.currentTimeMillis();
		return cachedTournaments;
	}

	public String createTournamentRaw(String name, String type) {
		beforeRequest();
		return HttpUtils.post(
			BASE_URL + "/tournaments",
			headers,
			"""
				{
				  "name": "%s",
				  "tournament_type": "%s"
				}
				""".formatted(name, type)
		);
	}

	public String startTournamentRaw(String tournamentId) {
		beforeRequest();
		return HttpUtils.post(
			BASE_URL + "/tournaments/%s/start".formatted(tournamentId),
			headers,
			"{}"
		);
	}


}
