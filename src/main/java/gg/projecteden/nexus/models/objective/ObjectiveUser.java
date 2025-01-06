package gg.projecteden.nexus.models.objective;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.commands.ObjectivesCommand.CompassState;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Data
@Entity(value = "objective_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class ObjectiveUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Objective> objectives = new ArrayList<>();

	public boolean isCompleting(Objective objective) {
		if (!isOnline())
			return false;

		if (!objective.isActive())
			return false;

		return true;
	}

	public void setActiveObjective(Objective objective) {
		add(objective);
		objectives.forEach(other -> other.setActive(false));
		objective.setActive(true);
	}

	public boolean exists(Objective objective) {
		return objectives.stream().anyMatch(_objective -> _objective.getId().equals(objective.getId()));
	}

	public Objective get(String id) {
		return objectives.stream()
			.filter(_objective -> _objective.getId().equalsIgnoreCase(id))
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("Objective &e" + id + " &cnot found for &e" + getNickname()));
	}

	public void add(Objective objective) {
		add(objective, false);
	}

	public void add(Objective objective, boolean activate) {
		if (!exists(objective)) {
			objectives.add(objective);
			if (activate)
				setActiveObjective(objective);
		}
	}

	public void remove(Objective objective) {
		remove(objective, false);
	}

	public void remove(Objective objective, boolean activateNext) {
		objective.unsubscribe();
		objectives.remove(objective);

		if (activateNext)
			if (objective.isActive() && !objectives.isEmpty())
				setActiveObjective(objectives.get(0));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Objective {
		@Setter(AccessLevel.PACKAGE)
		private boolean active;
		private String id;
		private String description;
		private Location location;

		public Objective(String id, String description, Location location) {
			this.id = id;
			this.description = description;
			this.location = location;
		}

		private transient BossBar objectiveBar;
		private transient BossBar arrowBar;

		public void unsubscribe() {
			Consumer<BossBar> shutdown = bossBar -> {
				if (bossBar == null)
					return;

				bossBar.setVisible(false);
				bossBar.removeAll();
			};

			shutdown.accept(objectiveBar);
			shutdown.accept(arrowBar);
		}

		public void update(ObjectiveUser user) {
			if (!user.isCompleting(this)) {
				unsubscribe();
				return;
			}

			final Player player = user.getOnlinePlayer();

			if (objectiveBar == null)
				if (!Nullables.isNullOrEmpty(description))
					objectiveBar = Bukkit.createBossBar(StringUtils.colorize(description), BarColor.PINK, BarStyle.SOLID);

			if (objectiveBar != null) {
				objectiveBar.setVisible(true);
				objectiveBar.setTitle(StringUtils.colorize(description));
				objectiveBar.addPlayer(player);
			}

			if (arrowBar == null)
				arrowBar = Bukkit.createBossBar("", BarColor.PINK, BarStyle.SOLID);

			arrowBar.setVisible(true);
			arrowBar.setTitle(CompassState.of(player, location).getCharacter());
			arrowBar.addPlayer(player);
		}
	}

}
