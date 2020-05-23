package me.pugabyte.bncore.models.scoreboard;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.features.scoreboard.ScoreboardLine;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.BNScoreboard;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Entity("scoreboard_user")
@NoArgsConstructor
@AllArgsConstructor
@Converters(UUIDConverter.class)
public class ScoreboardUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<ScoreboardLine, Boolean> lines = new HashMap<>();
	private boolean active = false;
	@Transient
	private BNScoreboard scoreboard;
	@Transient
	private int headerTaskId;
	@Transient
	private int renderTaskId;

	private static final int UPDATE_INTERVAL = 2;

	public ScoreboardUser(UUID uuid) {
		this.uuid = uuid;
	}

	public void start() {
		if (scoreboard == null)
			scoreboard = new BNScoreboard("bnsb-" + uuid.toString().replace("-", ""), "&e> &3Bear Nation &e<", getPlayer());
		else
			scoreboard.subscribe(getPlayer());
		active = true;
		headerTaskId = Tasks.repeatAsync(0, (headers.size() + 1) * UPDATE_INTERVAL, new Header(getPlayer()));
		renderTaskId = Tasks.repeatAsync(0, UPDATE_INTERVAL, this::render);
	}

	public void stop() {
		active = false;
		scoreboard.unsubscribe(getPlayer());
		scoreboard.delete();
		Tasks.cancel(headerTaskId);
		Tasks.cancel(renderTaskId);
		headerTaskId = -1;
		renderTaskId = -1;
	}

	public void render() {
		if (!active) return;
		Tasks.async(() -> {
			List<String> rendered = new ArrayList<>();
			Arrays.asList(ScoreboardLine.values()).forEach(line -> {
				if (lines.containsKey(line) && lines.get(line))
					rendered.add(line.render(getPlayer()));
			});
			scoreboard.setLines(rendered);
		});
	}

	public static class Header implements Runnable {
		private final ScoreboardUser user;
		private final List<String> headers = new ArrayList<>(ScoreboardUser.headers);

		public Header(Player player) {
			user = new ScoreboardService().get(player);
		}

		@Override
		public void run() {
			AtomicInteger wait = new AtomicInteger(0);
			headers.iterator().forEachRemaining(header ->
					Tasks.waitAsync(wait.getAndAdd(UPDATE_INTERVAL), () -> {
						if (user.isActive())
							user.getScoreboard().setTitle(header);
					}));
		}
	}

	private static List<String> headers = Arrays.asList(
			"&e< &3Bear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &bBear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &bBear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &bB&3ear Nation&3 &e>",
			"&e< &3B&be&3ar Nation &e>",
			"&e< &3Be&ba&3r Nation &e>",
			"&e< &3Bea&br&3 Nation &e>",
			"&e< &3Bear&b N&3ation &e>",
			"&e< &3Bear N&ba&3tion &e>",
			"&e< &3Bear Na&bt&3ion &e>",
			"&e< &3Bear Nat&bi&3on &e>",
			"&e< &3Bear Nati&bo&3n &e>",
			"&e< &3Bear Natio&bn&3 &e>",
			"&e< &3Bear Nati&bo&3n &e>",
			"&e< &3Bear Nat&bi&3on &e>",
			"&e< &3Bear Na&bt&3ion &e>",
			"&e< &3Bear N&ba&3tion &e>",
			"&e< &3Bear&b N&3ation &e>",
			"&e< &3Bea&br&3 Nation &e>",
			"&e< &3Be&ba&3r Nation &e>",
			"&e< &3B&be&3ar Nation &e>",
			"&e< &bB&3ear Nation&3 &e>",
			"&e< &3B&be&3ar Nation &e>",
			"&e< &3Be&ba&3r Nation &e>",
			"&e< &3Bea&br&3 Nation &e>",
			"&e< &3Bear&b N&3ation &e>",
			"&e< &3Bear N&ba&3tion &e>",
			"&e< &3Bear Na&bt&3ion &e>",
			"&e< &3Bear Nat&bi&3on &e>",
			"&e< &3Bear Nati&bo&3n &e>",
			"&e< &3Bear Natio&bn&3 &e>",
			"&e< &3Bear Nati&bo&3n &e>",
			"&e< &3Bear Nat&bi&3on &e>",
			"&e< &3Bear Na&bt&3ion &e>",
			"&e< &3Bear N&ba&3tion &e>",
			"&e< &3Bear&b N&3ation &e>",
			"&e< &3Bea&br&3 Nation &e>",
			"&e< &3Be&ba&3r Nation &e>",
			"&e< &3B&be&3ar Nation &e>"
	);
}
