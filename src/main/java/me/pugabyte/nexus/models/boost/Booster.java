package me.pugabyte.nexus.models.boost;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static eden.utils.StringUtils.camelCase;

@Data
@Builder
@Entity("booster")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Booster implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Boost> boosts = new ArrayList<>();

	@Data
	public static class Boost implements PlayerOwnedObject {
		private int id;
		@NonNull
		private UUID uuid;
		private Boostable type;
		private double multiplier;
		private int duration;
		private LocalDateTime received;
		private LocalDateTime activated;

		public Boost(@NonNull UUID uuid, Boostable type, double multiplier, Time duration) {
			this(uuid, type, multiplier, duration.get() / 20);
		}

		public Boost(@NonNull UUID uuid, Boostable type, double multiplier, int duration) {
			this.id = getBooster().getBoosts().size();
			this.uuid = uuid;
			this.type = type;
			this.multiplier = multiplier;
			this.duration = duration;
			this.received = LocalDateTime.now();
		}

		public Booster getBooster() {
			return new BoosterService().get(uuid);
		}

		public String getRefId() {
			return uuid + "#" + id;
		}

		public String getNicknameId() {
			return getNickname() + "#" + id;
		}

		public ItemBuilder getDisplayItem() {
			return type.getDisplayItem().name(camelCase(type) + " &7- &6" + getMultiplierFormatted());
		}

		@NotNull
		private String getMultiplierFormatted() {
			return StringUtils.getDf().format(multiplier) + "x";
		}

		public void activate() {
			if (config().hasBoost(type))
				throw new InvalidInputException("There is already an active " + camelCase(type) + " boost");

			activated = LocalDateTime.now();
			// TODO Task
		}

		private BoostConfig config() {
			return new BoostConfigService().get();
		}

		public void expire() {
			config().removeBoost(this);
			// TODO Auto start next?
		}

		public boolean isActive() {
			boolean active = activated != null && !isExpired();
			if (!active)
				return false;

			Boost activeBoost = config().getBoost(type);
			if (!activeBoost.equals(this))
				throw new InvalidInputException("Active boost (" + getNicknameId() + ") is not active server boost (" + activeBoost.getNicknameId() + ")");

			return true;
		}

		public boolean isExpired() {
			if (activated == null)
				return false;

			return getExpiration().isBefore(LocalDateTime.now());
		}

		@NotNull
		private LocalDateTime getExpiration() {
			return activated.plusSeconds(duration);
		}

	}

	public void add(Boost boost) {
		boosts.add(boost);
	}

	public void add(Boostable type, double multiplier, Time duration) {
		add(new Boost(uuid, type, multiplier, duration));
	}

	public Boost get(int id) {
		// Shortcut
		Boost index = boosts.get(id);
		if (index.getId() == id)
			return index;

		for (Boost boost : boosts)
			if (boost.getId() == id)
				return boost;

		throw new InvalidInputException("Boost " + getNickname() + "#" + id + " not found");
	}

	public List<Boost> get(Boostable type) {
		return boosts.stream().filter(boost -> boost.getType() == type).toList();
	}

	public int count(Boostable type) {
		return get(type).size();
	}

}
