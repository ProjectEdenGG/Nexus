package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.entity.Snowball;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class MiniGolfUser {
	@NonNull UUID uuid;
	Snowball snowball = null;
	ColorType color = ColorType.WHITE;
	Integer currentHole = null;
	int currentStrokes = 0;
	int totalStrokes = 0;

	public int incrementStrokes() {
		return this.currentStrokes += 1;
	}

	public void addTotalStrokes(int strokes) {
		this.totalStrokes += strokes;
	}
}
