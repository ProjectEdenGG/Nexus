package gg.projecteden.nexus.features.wiki;

import com.google.gson.annotations.SerializedName;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.wiki._WikiSearchCommand.SearchResult.Query.Result;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.HttpUtils.unescapeHtml;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

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

		static SearchResult of(final WikiType wikiType, String query) {
			Map<String, String> parameters = Map.of(
					"action", "query",
					"list", "search",
					"srwhat", "text",
					"srlimit", "3",
					"srsearch", StringEscapeUtils.escapeHtml(query),
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

			@Data
			static class Result {
				private String title;
				private String snippet;

				String getPage() {
					return title.replaceAll(" ", "_");
				}

				String getSnippetFormatted() {
					return unescapeHtml(snippet
							.replaceAll("<span class='searchmatch'>", ChatColor.YELLOW.toString())
							.replaceAll("</span>", ChatColor.DARK_AQUA.toString())
							.replaceAll("\\[\\[(.*?)\\|", "")
							.replaceAll("]]", "")
							.replaceAll("```", ""));
				}
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
		if (isNullOrEmpty(query))
			error("You did not specify anything to search");

		line();
		send(PREFIX + "Searching for &e" + query + "&3...");

		SearchResult results = SearchResult.of(wikiType, query);

		if (isNullOrEmpty(results.getQuery().getResults()))
			error("No results found");

		for (Result result : results.getQuery().getResults()) {
			var json = json().newline().next("&3Page: &e" + result.getTitle());

			if (isNullOrEmpty(result.getSnippetFormatted().trim()))
				json.newline().next("&e Snippet: &3" + result.getSnippetFormatted());

			send(json.hover("&3Click to open").url(wikiType.getBasePath() + result.getPage()));
		}
	}

}
