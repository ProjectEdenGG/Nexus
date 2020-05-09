package me.pugabyte.bncore.features.scoreboard;

import lombok.Data;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.BNScoreboard;
import me.pugabyte.bncore.utils.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ScoreboardUser extends PlayerOwnedObject {
	private UUID uuid;
	private BNScoreboard scoreboard;
	private List<ScoreboardLine> lines = new ArrayList<>();

	private static final String header = "&e> &3Bear Nation &e<";

	public ScoreboardUser(UUID uuid) {
		this.uuid = uuid;
		this.scoreboard = new BNScoreboard(getPlayer().getName() + "-scoreboard", header, getPlayer());
	}

	public void render() {
		Tasks.async(() -> {
			List<String> rendered = new ArrayList<>();
			lines.forEach(line -> rendered.add(line.render(getPlayer())));
			scoreboard.setLines(rendered);
		});
	}

}
