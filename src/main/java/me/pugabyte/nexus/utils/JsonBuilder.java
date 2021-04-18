package me.pugabyte.nexus.utils;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.interfaces.Colored;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.getLastColor;

public class JsonBuilder {
	@NonNull private Builder builder = Component.text();
	@NonNull private final Builder result = Component.text();

	private final List<String> lore = new ArrayList<>();
	private boolean loreize = true;

	// Helper boolean for loops and stuff
	private boolean initialized;

	public JsonBuilder() {
		this("");
	}

	public JsonBuilder(String text) {
		next(text);
	}

	public JsonBuilder(JsonBuilder json) {
		next(json);
	}

	private void debug(String message) {
		if (false)
			Nexus.log(message);
	}

	public JsonBuilder next(String text) {
		builder.append(AdventureUtils.fromLegacyText(getColoredWords(colorize(text))));
		return this;
	}

	public JsonBuilder next(JsonBuilder json) {
		builder.append(json.build());
		return this;
	}

	public JsonBuilder group() {
		if (!lore.isEmpty()) {
			List<String> lines = new ArrayList<>();
			lore.forEach(line -> {
				if (loreize) line = StringUtils.loreize(line);
				line = line.replaceAll("\\|\\|", System.lineSeparator()); // TODO remove...
				lines.addAll(Arrays.asList(colorize(line).split(System.lineSeparator())));
			});

			Builder hover = Component.text();

			Iterator<String> iterator = lines.iterator();
			while (iterator.hasNext()) {
				hover.append(Component.text(iterator.next()));
				if (iterator.hasNext())
					hover.append(Component.newline());
			}

			builder.hoverEvent(hover.build().asHoverEvent());
			lore.clear();
		}

		result.append(builder);
		builder = Component.text();
		return this;
	}

	public JsonBuilder newline() {
		builder.append(Component.text(System.lineSeparator()));
		group();
		return this;
	}

	public JsonBuilder line() {
		newline();
		newline();
		return this;
	}

	public JsonBuilder color(TextColor color) {
		builder.color(color);
		return this;
	}

	public JsonBuilder color(Colored color) {
		return color(AdventureUtils.textColorOf(color));
	}

	public JsonBuilder color(ChatColor color) {
		return color(AdventureUtils.textColorOf(color));
	}

	public JsonBuilder color(Color color) {
		return color(AdventureUtils.textColorOf(color));
	}

	public JsonBuilder color(org.bukkit.Color color) {
		return color(AdventureUtils.textColorOf(color));
	}

	public JsonBuilder color(ColorType color) {
		return color(AdventureUtils.textColorOf(color));
	}

	public JsonBuilder url(String url) {
		builder.clickEvent(ClickEvent.openUrl(url));
		return this;
	}

	public JsonBuilder command(String command) {
		if (!command.startsWith("/"))
			command = "/" + command;
		builder.clickEvent(ClickEvent.runCommand(command));
		return this;
	}

	public JsonBuilder suggest(String command) {
		builder.clickEvent(ClickEvent.suggestCommand(command));
		return this;
	}

	public JsonBuilder copy(String command) {
		builder.clickEvent(ClickEvent.copyToClipboard(command));
		return this;
	}

	public JsonBuilder loreize(boolean loreize) {
		this.loreize = loreize;
		return this;
	}

	public JsonBuilder hover(String... lines) {
		return hover(Arrays.asList(lines));
	}

	public JsonBuilder hover(List<String> lines) {
		lore.addAll(lines);
		return this;
	}

	public JsonBuilder hover(ItemStack itemStack) {
		builder.hoverEvent(itemStack.asHoverEvent());
		return this;
	}

	public JsonBuilder hover(Entity entity) {
		builder.hoverEvent(entity.asHoverEvent());
		return this;
	}

	public JsonBuilder insert(String insertion) {
		builder.insertion(insertion);
		return this;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void initialize() {
		this.initialized = true;
	}

	public void send(Object sender) {
		PlayerUtils.send(sender, build());
	}

	public Component build() {
		group();
		return result.asComponent();
	}

	private String getColoredWords(String text) {
		if (text == null) return null;
		StringBuilder builder = new StringBuilder();
		for (String word : text.split(" "))
			builder.append(getLastColor(builder.toString())).append(word).append(" ");

		// Trim trailing whitespace
		String result = builder.toString().replaceFirst("\\s++$", "");
		if (text.endsWith(" ")) result += " ";
		return result;
	}

	public String toString() {
		return AdventureUtils.asPlainText(build());
	}

	public String serialize() {
		return GsonComponentSerializer.gson().serialize(build());
	}


}
