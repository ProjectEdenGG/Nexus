package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.resourcepack.models.font.CustomTexture;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TitleBuilder {
	private final List<Audience> players = new ArrayList<>();
	private ComponentLike title = new JsonBuilder("");
	private ComponentLike subtitle = new JsonBuilder("");
	private long fadeIn = 20;
	private long stay = 200;
	private long fadeOut = 20;

	public TitleBuilder allPlayers() {
		this.players.addAll(OnlinePlayers.getAll());
		return this;
	}

	public TitleBuilder players(List<Player> players) {
		this.players.addAll(players);
		return this;
	}

	public TitleBuilder players(Audience... players) {
		this.players.addAll(Arrays.asList(players));
		return this;
	}

	public TitleBuilder title(CustomTexture title) {
		this.title = new JsonBuilder(title.getChar());
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

	public TitleBuilder stay(long stay) {
		this.stay = stay;
		return this;
	}

	public TitleBuilder fade(long fade) {
		this.fadeIn = fade;
		this.fadeOut = fade;
		return this;
	}

	public TitleBuilder fadeIn(long fadeIn) {
		this.fadeIn = fadeIn;
		return this;
	}

	public TitleBuilder fadeOut(long fadeOut) {
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

	public TitleBuilder times(long fadeIn, long stay, long fadeOut) {
		this.fadeIn = fadeIn;
		this.stay = stay;
		this.fadeOut = fadeOut;
		return this;
	}

	public TitleBuilder times(Duration fadeIn, Duration stay, Duration fadeOut) {
		return times(Times.times(fadeIn, stay, fadeOut));
	}

	public TitleBuilder times(Times times) {
		this.fadeIn = durationToTicks(times.fadeIn());
		this.stay = durationToTicks(times.stay());
		this.fadeOut = durationToTicks(times.fadeOut());
		return this;
	}

	public CompletableFuture<Void> send() {
		final CompletableFuture<Void> future = new CompletableFuture<>();
		final Times times = Times.times(ticksToDuration(fadeIn), ticksToDuration(stay), ticksToDuration(fadeOut));
		final Title title = Title.title(this.title.asComponent(), subtitle.asComponent(), times);
		for (Audience player : players)
			player.showTitle(title);

		Tasks.wait(fadeIn, () -> future.complete(null));

		return future;
	}

	private static Duration ticksToDuration(long ticks) {
		return Duration.ofSeconds(ticks).dividedBy(20);
	}

	private static int durationToTicks(Duration duration) {
		return (int) duration.toMillis() / 50;
	}

}
