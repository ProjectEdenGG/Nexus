package me.pugabyte.nexus.features.wiki;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Wiki {
	private static final String SEP = "%20";

	static String format(String snippetString) {
		snippetString = snippetString.replace("<span class=\"searchmatch\">", "§e");
		snippetString = snippetString.replace("</span>", "§3");
		snippetString = snippetString.replace("&lt;", "<");
		snippetString = snippetString.replace("&gt;", ">");
		snippetString = snippetString.replace("&quot;", "\"");
		return snippetString;
	}

	private static String getQuery(String[] args) {
		StringBuilder query = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			query.append(args[i]);
			if (i < args.length - 1) {
				query.append(" ");
			}
		}
		return query.toString();
	}

	private static String getResponse(final String URL, String queryEscaped) throws IOException {
		URL search = new URL(URL + "?action=query&list=search&srprop=snippet|sectiontitle&srsearch=" + queryEscaped + "&utf8=&format=json");

		HttpURLConnection connection = (HttpURLConnection) search.openConnection();
		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String response = rd.readLine();
		connection.disconnect();
		return response;
	}

	static void search(CommandSender sender, String[] args, final String WHICH) {
		String prefix;
		String url;
		String api;
		String wiki;
		if (WHICH.equalsIgnoreCase("MCWiki")) {
			prefix = StringUtils.getPrefix("MCWiki");
			url = "https://minecraft.gamepedia.com";
			api = "/api.php";
			wiki = "/";
		} else {
			prefix = StringUtils.getPrefix("Wiki");
			url = "https://wiki.projecteden.gg";
			api = "/w/api.php";
			wiki = "/wiki/";
		}
		if (args.length >= 1) {
			if (args[0].length() != 0) {
				if (sender instanceof Player) {
					Bukkit.getScheduler().runTaskAsynchronously(Nexus.getInstance(), () -> {
						String query = getQuery(args);

						PlayerUtils.send(sender, "");
						PlayerUtils.send(sender, "");
						PlayerUtils.send(sender, prefix + "Searching for §e" + query + "§3...");

						try {
							String queryEscaped = query.replaceAll(" ", SEP);
							String response = getResponse(url + api, queryEscaped);

							JsonParser jsonParser = new JsonParser();
							JsonObject resultCount = jsonParser.parse(response)
									.getAsJsonObject().get("query")
									.getAsJsonObject()
									.getAsJsonObject("searchinfo");

							int results = resultCount.get("totalhits").getAsInt();
							if (results > 0) {
								PlayerUtils.send(sender, "§eResults found!");

								for (int i = 0; i < results && i < 2; i++) {

									JsonObject result = jsonParser.parse(response)
											.getAsJsonObject().get("query")
											.getAsJsonObject().getAsJsonArray("search").get(i)
											.getAsJsonObject();

									String title = result.get("title").getAsString();
									String page = title.replace(" ", "_");

									String section = "None";
									if (result.get("sectiontitle") != null && !result.get("sectiontitle").getAsString().isEmpty()) {
										section = result.get("sectiontitle").getAsString();
										page += "#" + section;
									}

									String snippetString = format(result.get("snippet").getAsString());

									TextComponent header = new TextComponent("§3Page: §e§l" + title);
									TextComponent snippet = new TextComponent(" §eSnippet: §3..." + snippetString + "...");

									header.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url + wiki + page));
									header.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§3Section: §e" + section).create()));
									snippet.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url + wiki + page));
									snippet.setColor(ChatColor.DARK_AQUA);

									PlayerUtils.send(sender, "");
									PlayerUtils.send(sender, header);
									PlayerUtils.send(sender, snippet);
								}
							} else {
								PlayerUtils.send(sender, prefix + "No results found.");
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					});

				} else {
					PlayerUtils.send(sender, prefix + "You must be in-game to use this command.");
				}
			} else {
				PlayerUtils.send(sender, prefix + "You did not specify a search query.");
			}
		} else {
			PlayerUtils.send(sender, prefix + "You did not specify a search query.");
		}
	}

}
