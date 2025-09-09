package gg.projecteden.nexus.models.pugmas25;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.commands.staff.operator.HealCommand;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waystones.Pugmas25Waystone;
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
@Entity(value = "pugmas25_user", noClassnameStored = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Pugmas25User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private boolean readyToVisit = false;
	private boolean visited = false;

	private Set<Pugmas25Waystone> foundWaystones = new HashSet<>();

	@Getter(AccessLevel.PRIVATE)
	private Advent25User advent;

	public Advent25User advent() {
		if (advent == null)
			advent = new Advent25User(uuid);

		return advent;
	}

	@Deprecated
	public void updateHealth() {
		Player player = getPlayer();
		if (player == null || !player.isOnline())
			return;

		AttributeInstance maxHealth = HealCommand.getMaxHealthAttribute(player);
		maxHealth.setBaseValue(20);
	}
}
