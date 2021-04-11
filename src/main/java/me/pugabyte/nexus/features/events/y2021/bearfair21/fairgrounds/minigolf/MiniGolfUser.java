package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Snowball;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class MiniGolfUser {
	@NonNull UUID uuid;
	Snowball snowball;
	ChatColor color;
	Integer currentHole;
	int currentStrokes;
	int totalStrokes;

	public int incrementStrokes() {
		return this.currentStrokes += 1;
	}

	public void addTotalStrokes(int strokes) {
		this.totalStrokes += strokes;
	}
}
