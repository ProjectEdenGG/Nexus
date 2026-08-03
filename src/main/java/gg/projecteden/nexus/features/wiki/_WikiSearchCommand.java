package gg.projecteden.nexus.features.wiki;

import com.google.gson.annotations.SerializedName;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.wiki._WikiSearchCommand.SearchResult.Result;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.HttpUtils;
import gg.projecteden.nexus.utils.Nullables;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class _WikiSearchCommand extends CustomCommand {
	private final WikiType wikiType;

	public _WikiSearchCommand(@NonNull CommandEvent event) {
		super(event);
		wikiType = getWikiType();
	}

	abstract WikiType getWikiType();

	@Getter
	@AllArgsConstructor
	public enum WikiType {
		SERVER("https://wiki." + Nexus.DOMAIN, "/w/api.php", "/wiki/"),
		MINECRAFT("https://minecraft.wiki", "/api.php", "/");

		private final String url, apiPath, basePath;

		public String getApiPath() {
			return url + apiPath + "?";
		}

		public String getBasePath() {
			return url + basePath;
		}
	}

	@Data
	static class SearchResult {
		private Query query;

		static List<Result> search(final WikiType wikiType, String query) {
			Map<String, Result> results = new LinkedHashMap<>();

			SearchResult titleResults = request(wikiType, query, "title", 3);
			if (!Nullables.isNullOrEmpty(titleResults.getQuery().getResults()))
				titleResults.getQuery().getResults().forEach(result -> results.put(result.getTitle(), result));

			SearchResult textResults = request(wikiType, query, "text", 5);
			if (!Nullables.isNullOrEmpty(textResults.getQuery().getResults()))
				textResults.getQuery().getResults().forEach(result -> results.putIfAbsent(result.getTitle(), result));

			return results.values().stream().limit(3).toList();
		}

		private static SearchResult request(final WikiType wikiType, String query, String type, int limit) {
			Map<String, String> parameters = Map.of(
				"action", "query",
				"list", "search",
				"srwhat", type,
				"srlimit", String.valueOf(limit),
				"srsearch", query,
				"format", "json",
				"utf8", ""
			);

			String url = wikiType.getApiPath() + HttpUtils.formatParameters(parameters);
			return HttpUtils.mapJson(SearchResult.class, url);
		}

		@Data
		static class Query {
			@SerializedName("search")
			private List<Result> results;
		}

		@Data
		static class Result {
			private String title;
			private String snippet;

			String getPage() {
				return title.replace(" ", "_");
			}

			String getSnippetFormatted() {
				if (Nullables.isNullOrEmpty(snippet))
					return null;

				return HttpUtils.unescapeHtml(snippet
					.replaceAll("<span class=[\"']searchmatch[\"']>", ChatColor.YELLOW.toString())
					.replace("</span>", ChatColor.DARK_AQUA.toString())
					.replaceAll("\\[\\[(.*?)\\|", "")
					.replace("]]", "")
					.replace("```", ""));
			}
		}
	}

	@Path
	@Override
	@Description("Help menu")
	public void help() {
		send(PREFIX + "Visit the wiki at &e" + getWikiType().getUrl());
		send("&3Or use &c/" + getName().toLowerCase() + " search <query> &3to search the wiki from in-game.");
	}

	@Async
	@Path("search <query...>")
	@Description("Search the wiki for key words")
	void search(String query) {
		if (Nullables.isNullOrEmpty(query))
			error("You did not specify anything to search");

		line();
		send(PREFIX + "Searching for &e" + query + "&3...");

		List<Result> results = SearchResult.search(wikiType, query);

		if (Nullables.isNullOrEmpty(results))
			error("No results found");

		for (Result result : results) {
			var json = json().newline().next("&3Page: &e" + result.getTitle());

			String snippet = result.getSnippetFormatted();
			if (!Nullables.isNullOrEmpty(snippet))
				json.newline().next("&eSnippet: &3" + snippet);

			send(json.hover("&3Click to open").url(wikiType.getBasePath() + result.getPage()));
		}
	}

}
