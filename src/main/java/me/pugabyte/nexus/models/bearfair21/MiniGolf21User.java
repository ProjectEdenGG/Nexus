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
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.Particle;
import org.bukkit.entity.Snowball;

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
	private ColorType color = ColorType.WHITE;
	private Particle particle = null;
	private boolean rainbow = false;
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

}
