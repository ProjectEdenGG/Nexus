package gg.projecteden.nexus.models.playerplushie;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.PlayerPlushie;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.*;

import java.util.UUID;

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

	private int vouchers;

	public void addVouchers(int amount) {
		setVouchers(vouchers + amount);
	}

	public void takeVouchers(int amount) {
		setVouchers(vouchers - amount);
	}

	public void takeVouchers(Pose pose) {
		takeVouchers(pose.getCost());
	}

	public void setVouchers(int amount) {
		if (amount < 0)
			throw new InvalidInputException("You do not have enough vouchers"); // TODO NegativeBalanceException?

		vouchers = amount;
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

		throw new InvalidInputException("Cannot spawn " + StringUtils.camelCase(pose) + " pose for " + getNickname() + ", model has not been generated");
	}

	public boolean canPurchase(Pose pose) {
		if (vouchers >= pose.getCost())
			return true;

		return false;
	}

	public void checkPurchase(Pose pose) {
		if (!canPurchase(pose))
			throw new InvalidInputException("You do not have enough vouchers");
	}

}
