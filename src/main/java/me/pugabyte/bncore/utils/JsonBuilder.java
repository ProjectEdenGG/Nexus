package me.pugabyte.bncore.utils;

import me.pugabyte.bncore.BNCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.getLastColor;
import static me.pugabyte.bncore.utils.StringUtils.loreize;

public class JsonBuilder {
	private final ComponentBuilder result = new ComponentBuilder("");
	private ComponentBuilder builder = new ComponentBuilder("");
	// Helper boolean for loops and stuff
	private boolean initialized;

	public JsonBuilder() {
		this("");
	}

	public JsonBuilder(String text) {
		next(text);
	}

	public JsonBuilder(ComponentBuilder builder) {
		this.builder = builder;
	}

	public JsonBuilder(BaseComponent[] builder) {
		this.builder = new ComponentBuilder().append(builder);
	}

	public JsonBuilder next(String text) {
		builder.append(getColoredWords(colorize(text)));
		return this;
	}

	public JsonBuilder group() {
		result.append(builder.create(), FormatRetention.NONE);
		builder = new ComponentBuilder("");
		return this;
	}

	public JsonBuilder newline() {
		builder.append("\n");
		group();
		return this;
	}

	public JsonBuilder line() {
		newline();
		newline();
		return this;
	}

	public JsonBuilder color(ChatColor color) {
		builder.color(color);
		return this;
	}

	public JsonBuilder url(String url) {
		builder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		return this;
	}

	// https://stackoverflow.com/a/6041965
	private static final Pattern URL_PATTERN = Pattern.compile("(https?)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");

	public JsonBuilder urlize(String input) {
		Matcher matcher = URL_PATTERN.matcher(input);
		if (matcher.groupCount() > 0) {
			int index = 0;
			while (matcher.find()) {
				++index;
				debug("Matches");
				debug("  toString() before: " + toString());
				debug("  Input before: " + input);
				String original = matcher.group();
				String[] split = input.split(original.replaceAll("\\?", "\\\\?").replaceAll("\\+", "\\\\+"));
				String url = colorize(original).replaceAll(StringUtils.getColorChar(), "&&f");

				debug("  Match: " + url + ", Split: " + String.join(" // ", split) + " (" + split.length + ")");
				if (split.length == 0) {
					next(url).url(original);
					input = input.replace(original, "");
				} else if (split.length == 1) {
					next(split[0]).group().next(url).url(original);
					input = input.replace(split[0] + original, "");
				} else {
					debug("  Index: " + index + ", Groups: " + matcher.groupCount());
					if (index == matcher.groupCount() - 1) {
						debug("  Final match");
						next(split[0]).group().next(url).url(original).group().next(split[1]);
						input = input.replace(split[0] + original + split[1], "");
					} else {
						debug("  Not final match");
						next(split[0]).group().next(url).url(original);
						input = input.replace(split[0] + original, "");
					}
				}
				group();
				debug("  toString() after: " + toString());
				debug("  Input after: " + input);
			}
		}

		next(input);
		return this;
	}

	private void debug(String message) {
		if (false)
			BNCore.log(message);
	}

	public JsonBuilder command(String command) {
		if (!command.startsWith("/"))
			command = "/" + command;
		builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return this;
	}

	public JsonBuilder suggest(String command) {
		builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
		return this;
	}

	public JsonBuilder insert(String insertion) {
		builder.insertion(insertion);
		return this;
	}

	public JsonBuilder hover(String text) {
		builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(loreize(text).replaceAll("\\|\\|", "\n")).create()));
		return this;
	}

	public JsonBuilder hover(String text, org.bukkit.ChatColor color) {
		builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(loreize(text).replaceAll("\\|\\|", "\n")).create()));
		return this;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void initialize() {
		this.initialized = true;
	}

	public void send(CommandSender sender) {
		if (sender instanceof Player)
			sender.spigot().sendMessage(build());
		else if (sender instanceof OfflinePlayer) {
			OfflinePlayer player = (OfflinePlayer) sender;
			if (player.isOnline() && player.getPlayer() != null)
				player.getPlayer().spigot().sendMessage(build());
		} else
			sender.sendMessage(toString());
	}

	public BaseComponent[] build() {
		group();
		return new ComponentBuilder(result).create();
	}

	private String getColoredWords(String text) {
		StringBuilder builder = new StringBuilder();
		for (String word : text.split(" "))
			builder.append(getLastColor(builder.toString())).append(word).append(" ");

		// Trim trailing whitespace
		String result = builder.toString().replaceFirst("\\s++$", "");
		if (text.endsWith(" ")) result += " ";
		return result;
	}

	public String toString() {
		return BaseComponent.toPlainText(new ComponentBuilder(result).create());
	}

}
