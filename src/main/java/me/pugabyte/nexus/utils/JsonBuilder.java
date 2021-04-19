package me.pugabyte.nexus.utils;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.interfaces.Colored;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

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
		loreize = json.loreize;
		initialized = json.initialized;
	}

	private void debug(String message) {
		Nexus.debug(message);
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

	public JsonBuilder color(String color) {
		return color(AdventureUtils.textColorOf(color));
	}

	private static TextDecoration.State stateOf(boolean bool) {
		return bool ? TextDecoration.State.TRUE : TextDecoration.State.FALSE;
	}

	public JsonBuilder decorate(TextDecoration... decorations) {
		return decorate(true, decorations);
	}

	public JsonBuilder decorate(boolean state, TextDecoration... decorations) {
		return decorate(state, Arrays.asList(decorations));
	}

	public JsonBuilder decorate(TextDecoration.State state, TextDecoration... decorations) {
		return decorate(state, Arrays.asList(decorations));
	}

	public JsonBuilder decorate(boolean state, Collection<TextDecoration> decorations) {
		return decorate(stateOf(state), new HashSet<>(decorations));
	}

	public JsonBuilder decorate(TextDecoration.State state, Collection<TextDecoration> decorations) {
		decorations.forEach(decoration -> builder.decoration(decoration, state));
		return this;
	}

	/**
	 * Enables bold
	 */
	public JsonBuilder bold() {
		return bold(true);
	}

	public JsonBuilder bold(boolean state) {
		return bold(stateOf(state));
	}

	public JsonBuilder bold(TextDecoration.State state) {
		return decorate(state, TextDecoration.BOLD);
	}

	/**
	 * Enables italicization
	 */
	public JsonBuilder italic() {
		return italic(true);
	}

	public JsonBuilder italic(boolean state) {
		return italic(stateOf(state));
	}

	public JsonBuilder italic(TextDecoration.State state) {
		return decorate(state, TextDecoration.ITALIC);
	}


	/**
	 * Enables strikethrough
	 */
	public JsonBuilder strikethrough() {
		return strikethrough(true);
	}

	public JsonBuilder strikethrough(boolean state) {
		return strikethrough(stateOf(state));
	}

	public JsonBuilder strikethrough(TextDecoration.State state) {
		return decorate(state, TextDecoration.STRIKETHROUGH);
	}

	/**
	 * Enables underlines
	 */
	public JsonBuilder underline() {
		return underline(true);
	}

	public JsonBuilder underline(boolean state) {
		return underline(stateOf(state));
	}

	public JsonBuilder underline(TextDecoration.State state) {
		return decorate(state, TextDecoration.UNDERLINED);
	}

	/**
	 * Enables obfuscation (the random gibberish text)
	 */
	public JsonBuilder obfuscate() {
		return obfuscate(true);
	}

	public JsonBuilder obfuscate(boolean state) {
		return obfuscate(stateOf(state));
	}

	public JsonBuilder obfuscate(TextDecoration.State state) {
		return decorate(state, TextDecoration.OBFUSCATED);
	}

	public JsonBuilder style(Style style) {
		builder.style(style);
		return this;
	}

	public JsonBuilder style(Consumer<Style.Builder> style) {
		builder.style(style);
		return this;
	}

	public JsonBuilder clickEvent(ClickEvent clickEvent) {
		builder.clickEvent(clickEvent);
		return this;
	}

	/**
	 * Prompts the player to open a URL when clicked
	 */
	public JsonBuilder url(String url) {
		return clickEvent(ClickEvent.openUrl(url));
	}

	/**
	 * Prompts the player to open a URL when clicked
	 */
	public JsonBuilder url(URL url) {
		return clickEvent(ClickEvent.openUrl(url));
	}

	/**
	 * Makes the player run a command when clicked
	 * @param command a command, forward slash not required
	 */
	public JsonBuilder command(String command) {
		if (!command.startsWith("/"))
			command = "/" + command;
		return clickEvent(ClickEvent.runCommand(command));
	}

	/**
	 * Suggests a command to a player on click by typing it into their chat window
	 * @param text some text, usually a command
	 */
	public JsonBuilder suggest(String text) {
		return clickEvent(ClickEvent.suggestCommand(text));
	}

	public JsonBuilder copy(String text) {
		return clickEvent(ClickEvent.copyToClipboard(text));
	}

	/**
	 * Sets the page of a book when clicked
	 * @param page page number
	 */
	public JsonBuilder bookPage(int page) {
		return bookPage(String.valueOf(page));
	}

	/**
	 * Sets the page of a book when clicked
	 * @param page page number
	 */
	public JsonBuilder bookPage(String page) {
		return clickEvent(ClickEvent.changePage(page));
	}

	public JsonBuilder loreize(boolean loreize) {
		this.loreize = loreize;
		return this;
	}

	/**
	 * Clears this builder's hover text
	 */
	public JsonBuilder hover() {
		lore.removeAll(new ArrayList<>(lore));
		return this;
	}

	public JsonBuilder hover(@NonNull String... lines) {
		return hover(Arrays.asList(lines));
	}

	public JsonBuilder hover(@NonNull List<String> lines) {
		lore.addAll(lines);
		return this;
	}

	public JsonBuilder hover(@NonNull ItemStack itemStack) {
		builder.hoverEvent(itemStack.asHoverEvent());
		return this;
	}

	public JsonBuilder hover(@NonNull Entity entity) {
		builder.hoverEvent(entity.asHoverEvent());
		return this;
	}

	public <V> JsonBuilder hover(@Nullable HoverEventSource<V> hoverEvent) {
		builder.hoverEvent(hoverEvent);
		return this;
	}

	public JsonBuilder insert(@Nullable String insertion) {
		builder.insertion(insertion);
		return this;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public JsonBuilder initialize() {
		this.initialized = true;
		return this;
	}

	public void send(Object recipient) {
		PlayerUtils.send(recipient, build());
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
