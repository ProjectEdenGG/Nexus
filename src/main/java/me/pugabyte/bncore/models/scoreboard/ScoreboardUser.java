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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity("shop")
@NoArgsConstructor
@AllArgsConstructor
@Converters(UUIDConverter.class)
public class ScoreboardUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<ScoreboardLine> lines = new ArrayList<>();
	private boolean active = false;
	@Transient
	private BNScoreboard scoreboard;

	private static final String header = "&e> &3Bear Nation &e<";

	public ScoreboardUser(UUID uuid) {
		this.uuid = uuid;
	}

	public void start() {
		if (scoreboard == null)
			scoreboard = new BNScoreboard("bnsb-" + uuid.toString().replace("-", ""), header, getPlayer());
		else
			scoreboard.subscribe(getPlayer());
		active = true;
		render();
	}

	public void stop() {
		scoreboard.unsubscribe(getPlayer());
	}

	public void render() {
		if (!active) return;
		Tasks.async(() -> {
			List<String> rendered = new ArrayList<>();
			lines.forEach(line -> rendered.add(line.render(getPlayer())));
			scoreboard.setLines(rendered);
		});
	}

}
