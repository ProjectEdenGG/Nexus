package me.pugabyte.nexus.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
@NoArgsConstructor
public class BossBarBuilder {
	/**
	 * Title/name of the boss bar
	 */
	@NonNull
	private Component title = Component.text("Placeholder");
	/**
	 * Progress of the boss bar from 0 to 1
	 */
	private float progress = 1;
	/**
	 * Color of the bar itself (not the text)
	 */
	@NonNull
	private BossBar.Color color = BossBar.Color.WHITE;
	/**
	 * Which texture to overlay on top of the boss bar
	 */
	@NonNull
	private BossBar.Overlay overlay = BossBar.Overlay.PROGRESS;
	/**
	 * Flags that control environmental factors for the boss bar
	 */
	@NonNull
	@Setter(value = AccessLevel.PRIVATE)
	private Set<BossBar.Flag> flags = new HashSet<>();

	/**
	 * Sets the title of this boss bar
	 * @param component an adventure component
	 * @return this builder
	 */
	@NonNull
	public BossBarBuilder title(@NonNull Component component) {
		this.title = component;
		return this;
	}

	/**
	 * Sets the title of the boss bar to a string. Automatically applies color codes.
	 * @return this builder
	 */
	@NonNull
	public BossBarBuilder title(@NonNull String title) {
		this.title = AdventureUtils.fromLegacyText(colorize(title));
		return this;
	}

	/**
	 * Sets the title of the boss bar using a JsonBuilder.
	 * @return this builder
	 */
	@NonNull
	public BossBarBuilder title(@NonNull JsonBuilder title) {
		this.title = AdventureUtils.fromJson(title);
		return this;
	}

	/**
	 * Sets the color of the boss bar
	 * @param color color to set
	 * @return this builder
	 */
	@NonNull
	public BossBarBuilder color(@NonNull BossBar.Color color) {
		this.color = color;
		return this;
	}

	/**
	 * Sets the color of the boss bar
	 * @param colorType color to set
	 * @return this builder
	 * @throws IllegalArgumentException color is not supported
	 */
	@NonNull
	public BossBarBuilder color(@NonNull ColorType colorType) throws IllegalArgumentException {
		switch (colorType) {
			case WHITE:
				color = BossBar.Color.WHITE;
				break;
			case BLUE:
				color = BossBar.Color.BLUE;
				break;
			case PURPLE:
				color = BossBar.Color.PURPLE;
				break;
			case GREEN:
				color = BossBar.Color.GREEN;
				break;
			case RED:
				color = BossBar.Color.RED;
				break;
			case PINK:
				color = BossBar.Color.PINK;
				break;
			case YELLOW:
				color = BossBar.Color.YELLOW;
				break;
			default:
				throw new IllegalArgumentException("Color not supported");
		}
		return this;
	}

	/**
	 * Adds flags that control environmental factors for this boss bar
	 * @param flags flag(s) to add
	 * @return this builder
	 */
	@NonNull
	public BossBarBuilder addFlags(@NonNull BossBar.Flag... flags) {
		this.flags.addAll(Arrays.asList(flags));
		return this;
	}

	/**
	 * Removes flags that control environmental factors for this boss bar
	 * @param flags flag(s) to remove
	 * @return this builder
	 */
	@NonNull
	public BossBarBuilder removeFlags(@NonNull BossBar.Flag... flags) {
		this.flags.removeAll(Arrays.asList(flags));
		return this;
	}

	/**
	 * Clears the flags that control environmental factors for this boss bar
	 * @return this builder
	 */
	@NonNull
	public BossBarBuilder clearFlags() {
		this.flags.clear();
		return this;
	}

	/**
	 * Gets the flags that control environmental factors for this boss bar
	 * @return this builder
	 */
	@NonNull
	public Set<BossBar.Flag> flags() {
		return new HashSet<>(flags);
	}

	@NonNull
	public BossBar build() {
		return BossBar.bossBar(title, progress, color, overlay, flags);
	}
}
