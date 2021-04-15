package me.pugabyte.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfColor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Snowball;
import org.inventivetalent.glow.GlowAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@Entity("bearfair21_minigolf_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MiniGolf21User extends PlayerOwnedObject {
	@Id
	@NonNull
	@EqualsAndHashCode.Include
	private UUID uuid;
	private MiniGolfColor miniGolfColor = MiniGolfColor.WHITE;
	private Particle particle = null;
	private boolean playing = false;
	//
	private transient Snowball snowball = null;
	private transient Integer currentHole = null;
	private transient int currentStrokes = 0;
	private transient int totalStrokes = 0;

	public int incStrokes() {
		return this.currentStrokes += 1;
	}

	public void incTotalStrokes() {
		this.totalStrokes += this.currentStrokes;
	}

	public void removeBall() {
		if (snowball != null) {
			snowball.remove();
			snowball = null;
		}
	}

	public Color getColor() {
		return this.miniGolfColor.getColorType().getColor();
	}

	public List<Color> getFireworkColor() {
		if (miniGolfColor.equals(MiniGolfColor.RAINBOW)) {
			List<Color> rainbow = new ArrayList<>();
			for (MiniGolfColor color : MiniGolfColor.values())
				rainbow.add(color.getColorType().getColor());
			return rainbow;
		}

		return Collections.singletonList(this.miniGolfColor.getColorType().getColor());
	}

	public ChatColor getChatColor() {
		return miniGolfColor.getColorType().getChatColor();
	}

	public GlowAPI.Color getGlowColor() {
		return this.miniGolfColor.getColorType().getGlowColor();
	}

}
