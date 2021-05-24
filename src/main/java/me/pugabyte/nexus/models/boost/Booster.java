package me.pugabyte.nexus.models.boost;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;

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
		private LocalDateTime activated;

		public Boost(@NonNull UUID uuid, Boostable type, double multiplier, int duration) {
			this.id = getBooster().getBoosts().size();
			this.uuid = uuid;
			this.type = type;
			this.multiplier = multiplier;
			this.duration = duration;
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

	}

	public void add(Boost boost) {
		boosts.add(boost);
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

}
