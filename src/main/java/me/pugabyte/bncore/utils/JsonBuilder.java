package me.pugabyte.bncore.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.getLastColor;
import static me.pugabyte.bncore.utils.StringUtils.loreize;

public class JsonBuilder {
	private ComponentBuilder result = new ComponentBuilder("");
	private ComponentBuilder builder = new ComponentBuilder("");
	// Helper boolean for loops and stuff
	private boolean initialized;

	public JsonBuilder() {
		this("");
	}

	public JsonBuilder(String text) {
		next(text);
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

	public void send(Player player) {
		player.spigot().sendMessage(build());
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
