package me.pugabyte.nexus.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.interfaces.Colored;
import me.pugabyte.nexus.framework.interfaces.ColoredAndNamed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.getLastColor;

@NoArgsConstructor
public class JsonBuilder implements ComponentLike {
	@NonNull private Builder builder = Component.text();
	@NonNull private final Builder result = Component.text();

	private final List<String> lore = new ArrayList<>();
	private boolean loreize = true;

	/**
	 * Helper boolean that does not affect anything within the builder itself.
	 * Can be used by external methods to determine if the builder has been setup.
	 * To set this boolean to true, use {@link #initialize()}
	 */
	@Getter
	private boolean initialized = false;

	public JsonBuilder(@Nullable TextColor color) {
		color(color);
	}

	public JsonBuilder(@Nullable Colored color) {
		color(color);
	}

	public JsonBuilder(@Nullable ChatColor color) {
		color(color);
	}

	public JsonBuilder(@Nullable Color color) {
		color(color);
	}

	public JsonBuilder(@Nullable org.bukkit.Color color) {
		color(color);
	}

	public JsonBuilder(@Nullable TextColor color, @NotNull TextDecoration... decorations) {
		color(color);
		decorate(decorations);
	}

	public JsonBuilder(@Nullable Colored color, @NotNull TextDecoration... decorations) {
		color(color);
		decorate(decorations);
	}

	public JsonBuilder(@Nullable ChatColor color, @NotNull TextDecoration... decorations) {
		color(color);
		decorate(decorations);
	}

	public JsonBuilder(@Nullable Color color, @NotNull TextDecoration... decorations) {
		color(color);
		decorate(decorations);
	}

	public JsonBuilder(@Nullable org.bukkit.Color color, @NotNull TextDecoration... decorations) {
		color(color);
		decorate(decorations);
	}

	public JsonBuilder(@NotNull TextDecoration... decorations) {
		decorate(decorations);
	}

	/**
	 * Creates a new builder with a component created from the input text appended
	 */
	public JsonBuilder(@Nullable String text) {
		next(text);
	}

	public JsonBuilder(@Nullable JsonBuilder json) {
		if (json != null) {
			next(json);
			loreize = json.loreize;
			initialized = json.initialized;
		}
	}

	public JsonBuilder(@Nullable ComponentLike component) {
		next(component);
	}

	public JsonBuilder(@Nullable ColoredAndNamed coloredAndNamed) {
		next(coloredAndNamed);
	}

	public JsonBuilder(@NotNull String rawText, @Nullable TextColor color) {
		content(rawText);
		color(color);
	}

	public JsonBuilder(@NotNull String rawText, @Nullable Colored color) {
		content(rawText);
		color(color);
	}

	public JsonBuilder(@NotNull String rawText, @Nullable ChatColor color) {
		content(rawText);
		color(color);
	}

	public JsonBuilder(@NotNull String rawText, @Nullable Color color) {
		content(rawText);
		color(color);
	}

	public JsonBuilder(@NotNull String rawText, @Nullable org.bukkit.Color color) {
		content(rawText);
		color(color);
	}

	public JsonBuilder(@NotNull String rawText, @Nullable TextColor color, @NotNull TextDecoration... decorations) {
		content(rawText);
		color(color);
		decorate(decorations);
	}

	public JsonBuilder(@NotNull String rawText, @Nullable Colored color, @NotNull TextDecoration... decorations) {
		content(rawText);
		color(color);
		decorate(decorations);
	}

	public JsonBuilder(@NotNull String rawText, @Nullable ChatColor color, @NotNull TextDecoration... decorations) {
		content(rawText);
		color(color);
		decorate(decorations);
	}

	public JsonBuilder(@NotNull String rawText, @Nullable Color color, @NotNull TextDecoration... decorations) {
		content(rawText);
		color(color);
		decorate(decorations);
	}

	public JsonBuilder(@NotNull String rawText, @Nullable org.bukkit.Color color, @NotNull TextDecoration... decorations) {
		content(rawText);
		color(color);
		decorate(decorations);
	}

	public JsonBuilder(@NotNull String rawText, @NotNull Style style) {
		content(rawText);
		style(style);
	}

	private void debug(@NotNull String message) {
		Nexus.debug(message);
	}

	@NotNull
	public JsonBuilder next(@Nullable String text) {
		if (text != null)
			builder.append(AdventureUtils.fromLegacyText(getColoredWords(colorize(text))));
		return this;
	}

	@NotNull
	public JsonBuilder next(@Nullable String rawText, @Nullable TextColor color) {
		if (rawText != null)
			builder.append(Component.text(rawText, color));
		return this;
	}

	@NotNull
	public JsonBuilder next(@Nullable String rawText, @Nullable Colored color) {
		return next(rawText, AdventureUtils.textColorOf(color));
	}

	@NotNull
	public JsonBuilder next(@Nullable String rawText, @Nullable ChatColor color) {
		return next(rawText, AdventureUtils.textColorOf(color));
	}

	@NotNull
	public JsonBuilder next(@Nullable String rawText, @Nullable Color color) {
		return next(rawText, AdventureUtils.textColorOf(color));
	}

	@NotNull
	public JsonBuilder next(@Nullable String rawText, @Nullable org.bukkit.Color color) {
		return next(rawText, AdventureUtils.textColorOf(color));
	}

	@NotNull
	public JsonBuilder next(@Nullable String rawText, @Nullable TextColor color, TextDecoration... decorations) {
		if (rawText != null)
			builder.append(Component.text(rawText, color, decorations));
		return this;
	}

	@NotNull
	public JsonBuilder next(@Nullable String rawText, @Nullable Colored color, TextDecoration... decorations) {
		return next(rawText, AdventureUtils.textColorOf(color), decorations);
	}

	@NotNull
	public JsonBuilder next(@Nullable String rawText, @Nullable ChatColor color, TextDecoration... decorations) {
		return next(rawText, AdventureUtils.textColorOf(color), decorations);
	}

	@NotNull
	public JsonBuilder next(@Nullable String rawText, @Nullable Color color, TextDecoration... decorations) {
		return next(rawText, AdventureUtils.textColorOf(color), decorations);
	}

	@NotNull
	public JsonBuilder next(@Nullable String rawText, @Nullable org.bukkit.Color color, TextDecoration... decorations) {
		return next(rawText, AdventureUtils.textColorOf(color), decorations);
	}

	/**
	 * Sets the raw text for the base text component. Does not handle any color codes.
	 * @return this builder
	 */
	@NotNull
	public JsonBuilder content(@NotNull String rawText) {
		builder.content(rawText);
		return this;
	}

	@NotNull
	public JsonBuilder next(@Nullable ComponentLike component) {
		if (component != null)
			builder.append(component);
		return this;
	}

	@NotNull
	public JsonBuilder next(@Nullable ColoredAndNamed coloredAndNamed) {
		if (coloredAndNamed != null)
			builder.append(coloredAndNamed.getComponent());
		return this;
	}

	/**
	 * Applies saved hover text, appends the current working Component to the output Component, and resets the working
	 * component to default.
	 */
	@NotNull
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

	@NotNull
	public JsonBuilder newline() {
		builder.append(Component.text(System.lineSeparator()));
		group();
		return this;
	}

	/**
	 * Creates an empty line (two newlines)
	 */
	@NotNull
	public JsonBuilder line() {
		newline();
		newline();
		return this;
	}

	@NotNull
	public JsonBuilder color(@Nullable TextColor color) {
		builder.color(color);
		return this;
	}

	@NotNull
	public JsonBuilder color(@Nullable Colored color) {
		return color(AdventureUtils.textColorOf(color));
	}

	@NotNull
	public JsonBuilder color(@Nullable ChatColor color) {
		return color(AdventureUtils.textColorOf(color));
	}

	@NotNull
	public JsonBuilder color(@Nullable Color color) {
		return color(AdventureUtils.textColorOf(color));
	}

	@NotNull
	public JsonBuilder color(@Nullable org.bukkit.Color color) {
		return color(AdventureUtils.textColorOf(color));
	}

	/**
	 * Parses a hexadecimal number
	 * @param color number in the format "#FFFFFF" (# optional)
	 * @throws IllegalArgumentException string contained an invalid hexadecimal number
	 */
	@NotNull
	public JsonBuilder color(@Nullable String color) throws IllegalArgumentException {
		return color(AdventureUtils.textColorOf(color));
	}

	@NotNull
	private static TextDecoration.State stateOf(boolean bool) {
		return bool ? TextDecoration.State.TRUE : TextDecoration.State.FALSE;
	}

	@NotNull
	public JsonBuilder decorate(@NotNull TextDecoration... decorations) {
		return decorate(true, decorations);
	}

	@NotNull
	public JsonBuilder decorate(boolean state, @NotNull TextDecoration... decorations) {
		return decorate(state, Arrays.asList(decorations));
	}

	@NotNull
	public JsonBuilder decorate(@NotNull TextDecoration.State state, @NotNull TextDecoration... decorations) {
		return decorate(state, Arrays.asList(decorations));
	}

	@NotNull
	public JsonBuilder decorate(boolean state, @NotNull Collection<TextDecoration> decorations) {
		return decorate(stateOf(state), new HashSet<>(decorations));
	}

	@NotNull
	public JsonBuilder decorate(@NotNull TextDecoration.State state, @NotNull Collection<TextDecoration> decorations) {
		decorations.forEach(decoration -> builder.decoration(decoration, state));
		return this;
	}

	@NotNull
	public JsonBuilder decorate(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
		decorations.forEach((decoration, state) -> builder.decoration(decoration, state));
		return this;
	}

	/**
	 * Enables bold
	 */
	@NotNull
	public JsonBuilder bold() {
		return bold(true);
	}

	@NotNull
	public JsonBuilder bold(boolean state) {
		return bold(stateOf(state));
	}

	@NotNull
	public JsonBuilder bold(@NotNull TextDecoration.State state) {
		return decorate(state, TextDecoration.BOLD);
	}

	/**
	 * Enables italicization
	 */
	@NotNull
	public JsonBuilder italic() {
		return italic(true);
	}

	@NotNull
	public JsonBuilder italic(boolean state) {
		return italic(stateOf(state));
	}

	@NotNull
	public JsonBuilder italic(@NotNull TextDecoration.State state) {
		return decorate(state, TextDecoration.ITALIC);
	}


	/**
	 * Enables strikethrough
	 */
	@NotNull
	public JsonBuilder strikethrough() {
		return strikethrough(true);
	}

	@NotNull
	public JsonBuilder strikethrough(boolean state) {
		return strikethrough(stateOf(state));
	}

	@NotNull
	public JsonBuilder strikethrough(@NotNull TextDecoration.State state) {
		return decorate(state, TextDecoration.STRIKETHROUGH);
	}

	/**
	 * Enables underlines
	 */
	@NotNull
	public JsonBuilder underline() {
		return underline(true);
	}

	@NotNull
	public JsonBuilder underline(boolean state) {
		return underline(stateOf(state));
	}

	@NotNull
	public JsonBuilder underline(@NotNull TextDecoration.State state) {
		return decorate(state, TextDecoration.UNDERLINED);
	}

	/**
	 * Enables obfuscation (the random gibberish text)
	 */
	@NotNull
	public JsonBuilder obfuscate() {
		return obfuscate(true);
	}

	@NotNull
	public JsonBuilder obfuscate(boolean state) {
		return obfuscate(stateOf(state));
	}

	@NotNull
	public JsonBuilder obfuscate(@NotNull TextDecoration.State state) {
		return decorate(state, TextDecoration.OBFUSCATED);
	}

	@NotNull
	public JsonBuilder style(@NotNull Style style) {
		builder.style(style);
		return this;
	}

	@NotNull
	public JsonBuilder style(@NotNull Consumer<Style.Builder> style) {
		builder.style(style);
		return this;
	}

	@NotNull
	public JsonBuilder clickEvent(@Nullable ClickEvent clickEvent) {
		builder.clickEvent(clickEvent);
		return this;
	}

	/**
	 * Prompts the player to open a URL when clicked
	 */
	@NotNull
	public JsonBuilder url(@NotNull String url) {
		return clickEvent(ClickEvent.openUrl(url));
	}

	/**
	 * Prompts the player to open a URL when clicked
	 */
	@NotNull
	public JsonBuilder url(@NotNull URL url) {
		return clickEvent(ClickEvent.openUrl(url));
	}

	/**
	 * Makes the player run a command when clicked
	 * @param command a command, forward slash not required
	 */
	@NotNull
	public JsonBuilder command(@NotNull String command) {
		if (!command.startsWith("/"))
			command = "/" + command;
		return clickEvent(ClickEvent.runCommand(command));
	}

	/**
	 * Suggests a command to a player on click by typing it into their chat window
	 * @param text some text, usually a command
	 */
	@NotNull
	public JsonBuilder suggest(@NotNull String text) {
		return clickEvent(ClickEvent.suggestCommand(text));
	}

	@NotNull
	public JsonBuilder copy(@NotNull String text) {
		return clickEvent(ClickEvent.copyToClipboard(text));
	}

	/**
	 * Sets the page of a book when clicked
	 * @param page page number
	 */
	@NotNull
	public JsonBuilder bookPage(int page) {
		return bookPage(String.valueOf(page));
	}

	/**
	 * Sets the page of a book when clicked
	 * @param page page number
	 */
	@NotNull
	public JsonBuilder bookPage(@NotNull String page) {
		return clickEvent(ClickEvent.changePage(page));
	}

	@NotNull
	public JsonBuilder loreize(boolean loreize) {
		this.loreize = loreize;
		return this;
	}

	/**
	 * Clears this builder's hover text
	 */
	@NotNull
	public JsonBuilder hover() {
		lore.clear();
		builder.hoverEvent(null);
		return this;
	}

	@NotNull
	public JsonBuilder hover(@NonNull String... lines) {
		return hover(Arrays.asList(lines));
	}

	@NotNull
	public JsonBuilder hover(@NonNull List<String> lines) {
		lore.addAll(lines);
		return this;
	}

	@NotNull
	public JsonBuilder hover(@NonNull ItemStack itemStack) {
		builder.hoverEvent(itemStack.asHoverEvent());
		return this;
	}

	@NotNull
	public JsonBuilder hover(@NonNull Entity entity) {
		builder.hoverEvent(entity.asHoverEvent());
		return this;
	}

	@NotNull
	public <V> JsonBuilder hover(@Nullable HoverEventSource<V> hoverEvent) {
		builder.hoverEvent(hoverEvent);
		return this;
	}

	@NotNull
	public JsonBuilder hover(@Nullable Component component) {
		if (component != null)
			builder.hoverEvent(component.asHoverEvent());
		return this;
	}

	@NotNull
	public JsonBuilder insert(@Nullable String insertion) {
		builder.insertion(insertion);
		return this;
	}

	@NotNull
	public JsonBuilder initialize() {
		this.initialized = true;
		return this;
	}

	public void send(@Nullable Object recipient) {
		PlayerUtils.send(recipient, build());
	}

	@NotNull
	public Component build() {
		group();
		return result.build();
	}

	@Override
	@NonNull
	public Component asComponent() {
		return build();
	}

	@Contract("null -> null; !null -> !null")
	private String getColoredWords(@Nullable String text) {
		if (text == null) return null;
		StringBuilder builder = new StringBuilder();
		for (String word : text.split(" "))
			builder.append(getLastColor(builder.toString())).append(word).append(" ");

		// Trim trailing whitespace
		String result = builder.toString().replaceFirst("\\s++$", "");
		if (text.endsWith(" ")) result += " ";
		return result;
	}

	@NotNull
	public String toString() {
		return AdventureUtils.asPlainText(build());
	}

	@NotNull
	public String serialize() {
		return GsonComponentSerializer.gson().serialize(build());
	}


}
