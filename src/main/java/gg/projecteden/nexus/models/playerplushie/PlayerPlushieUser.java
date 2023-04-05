package gg.projecteden.nexus.models.playerplushie;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.PlayerPlushie;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Tier;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

@Data
@Entity(value = "player_plushie_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class PlayerPlushieUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private Map<Tier, Integer> vouchers = new HashMap<>();

	public int getVouchers(Tier tier) {
		return vouchers.getOrDefault(tier, 0);
	}

	public void addVouchers(Tier tier, int amount) {
		setVouchers(tier, getVouchers(tier) + amount);
	}

	public void takeVouchers(Tier tier, int amount) {
		setVouchers(tier, getVouchers(tier) - amount);
	}

	public void setVouchers(Tier tier, int amount) {
		if (amount < 0)
			throw new InvalidInputException("You do not have enough vouchers"); // TODO NegativeBalanceException?

		vouchers.put(tier, amount);
	}

	public PlayerPlushie getOrDefault(Pose pose) {
		final PlayerPlushie plushie = pose.asDecoration();

		for (var entry : PlayerPlushieConfig.ALL_MODELS.entrySet())
			if (entry.getValue().getFirst() == pose && entry.getValue().getSecond().equals(getUuid())) {
				plushie.setModelId(entry.getKey());
				break;
			}

		return plushie;
	}

	public PlayerPlushie get(Pose pose) {
		final PlayerPlushie plushie = pose.asDecoration();

		for (var entry : PlayerPlushieConfig.ALL_MODELS.entrySet())
			if (entry.getValue().getFirst() == pose && entry.getValue().getSecond().equals(getUuid())) {
				plushie.setModelId(entry.getKey());
				return plushie;
			}

		throw new InvalidInputException("Cannot spawn " + camelCase(pose) + " pose for " + getNickname() + ", model has not been generated");
	}

	public boolean canPurchase(Pose pose) {
		if (getVouchers(pose.getTier()) > 0)
			return true;

		throw new InvalidInputException("You do not have enough vouchers");
	}

	public boolean hasVouchers(Tier tier) {
		return getVouchers(tier) > 0;
	}

}
