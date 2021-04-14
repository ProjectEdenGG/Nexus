//package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import me.pugabyte.nexus.utils.ColorType;
//import org.bukkit.entity.Snowball;
//
//import java.util.UUID;
//
//@Data
//@RequiredArgsConstructor
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//public class MiniGolfUser {
//	@EqualsAndHashCode.Include
//	@NonNull
//	UUID uuid;
//	Snowball snowball = null;
//	ColorType color = ColorType.WHITE;
//	Integer currentHole = null;
//	int currentStrokes = 0;
//	int totalStrokes = 0;
//
//	public int incStrokes() {
//		return this.currentStrokes += 1;
//	}
//
//	public void incTotalStrokes() {
//		this.totalStrokes += this.currentStrokes;
//	}
//}
