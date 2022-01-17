package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TitleBuilder {
	private final List<Audience> players = new ArrayList<>();
	private ComponentLike title = new JsonBuilder("");
	private ComponentLike subtitle = new JsonBuilder("");
	private int fadeIn = 20;
	private int stay = 200;
	private int fadeOut = 20;

	public TitleBuilder allPlayers() {
		this.players.addAll(OnlinePlayers.getAll());
		return this;
	}

	public TitleBuilder players(Audience... players) {
		this.players.addAll(Arrays.asList(players));
		return this;
	}

	public TitleBuilder title(String title) {
		this.title = new JsonBuilder(title);
		return this;
	}

	public TitleBuilder subtitle(String subtitle) {
		this.subtitle = new JsonBuilder(subtitle);
		return this;
	}

	public TitleBuilder title(ComponentLike title) {
		this.title = title;
		return this;
	}

	public TitleBuilder subtitle(ComponentLike subtitle) {
		this.subtitle = subtitle;
		return this;
	}

	public TitleBuilder stay(int stay) {
		this.stay = stay;
		return this;
	}

	public TitleBuilder fade(int fade) {
		this.fadeIn = fade;
		this.fadeOut = fade;
		return this;
	}

	public TitleBuilder fadeIn(int fadeIn) {
		this.fadeIn = fadeIn;
		return this;
	}

	public TitleBuilder fadeOut(int fadeOut) {
		this.fadeOut = fadeOut;
		return this;
	}

	public TitleBuilder stay(Duration stay) {
		this.stay = durationToTicks(stay);
		return this;
	}

	public TitleBuilder fade(Duration fade) {
		this.fadeIn = durationToTicks(fade);
		this.fadeOut = durationToTicks(fade);
		return this;
	}

	public TitleBuilder fadeIn(Duration fadeIn) {
		this.fadeIn = durationToTicks(fadeIn);
		return this;
	}

	public TitleBuilder fadeOut(Duration fadeOut) {
		this.fadeOut = durationToTicks(fadeOut);
		return this;
	}

	public TitleBuilder times(int fadeIn, int stay, int fadeOut) {
		this.fadeIn = fadeIn;
		this.stay = stay;
		this.fadeOut = fadeOut;
		return this;
	}

	public TitleBuilder times(Duration fadeIn, Duration stay, Duration fadeOut) {
		return times(Times.of(fadeIn, stay, fadeOut));
	}

	public TitleBuilder times(Times times) {
		this.fadeIn = durationToTicks(times.fadeIn());
		this.stay = durationToTicks(times.stay());
		this.fadeOut = durationToTicks(times.fadeOut());
		return this;
	}

	public void send() {
		final Times times = Times.of(ticksToDuration(fadeIn), ticksToDuration(stay), ticksToDuration(fadeOut));
		final Title title = Title.title(this.title.asComponent(), subtitle.asComponent(), times);
		for (Audience player : players)
			player.showTitle(title);
	}

	private static Duration ticksToDuration(long ticks) {
		return Duration.ofSeconds(ticks).dividedBy(20);
	}

	private static int durationToTicks(Duration duration) {
		return (int) duration.toMillis() / 50;
	}

}
