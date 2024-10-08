package gg.projecteden.nexus.models.pugmas24;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.commands.staff.HealCommand;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Waystones.Pugmas24Waystone;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "pugmas24_user", noClassnameStored = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Pugmas24User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private boolean readyToVisit = false;
	private boolean visited = false;

	private Set<Pugmas24Waystone> foundWaystones = new HashSet<>();
	private double maxHealth = 10;

	@Getter(AccessLevel.PRIVATE)
	private Advent24User advent;

	public Advent24User advent() {
		if (advent == null)
			advent = new Advent24User(uuid);

		return advent;
	}

	public void updateHealth() {
		Player player = getPlayer();
		if (player == null || !player.isOnline())
			return;

		AttributeInstance maxHealth = HealCommand.getMaxHealthAttribute(player);
		maxHealth.setBaseValue(this.maxHealth);
		if (player.getHealth() > this.maxHealth)
			player.setHealth(this.maxHealth);
	}
}
