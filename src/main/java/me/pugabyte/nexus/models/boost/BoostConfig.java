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
import me.pugabyte.nexus.models.boost.Booster.Boost;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
@Entity("boost_config")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class BoostConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private Map<Boostable, String> boosts = new ConcurrentHashMap<>();

	public static BoostConfig get() {
		return new BoostConfigService().get();
	}

	public static double multiplierOf(Boostable boostable) {
		return get().getMultiplier(boostable);
	}

	public boolean hasBoost(Boostable boostable) {
		return boosts.containsKey(boostable);
	}

	public Boost getBoost(Boostable boostable) {
		String id = boosts.get(boostable);
		String[] split = id.split("#");
		Booster booster = new BoosterService().get(UUID.fromString(split[0]));
		return booster.get(Integer.parseInt(split[1]));
	}

	public double getMultiplier(Boostable boostable) {
		if (hasBoost(boostable))
			return getBoost(boostable).getMultiplier();
		return 1d;
	}

	public void removeBoost(Boost boost) {
		Boost active = getBoost(boost.getType());
		if (!active.equals(boost))
			throw new InvalidInputException("Specified boost (" + boost.getNicknameId() + ") is not the active boost (" + active.getNicknameId() + ")");

		boosts.remove(boost.getType());
		save();
	}

	public void addBoost(Boost boost) {
		if (hasBoost(boost.getType()))
			throw new InvalidInputException("Cannot activate boost " + boost.getNicknameId() + ", boost " + getBoost(boost.getType()).getNicknameId() + " is already active");

		boosts.put(boost.getType(), boost.getRefId());
		save();
	}

	private void save() {
		new BoostConfigService().save(this);
	}

}
