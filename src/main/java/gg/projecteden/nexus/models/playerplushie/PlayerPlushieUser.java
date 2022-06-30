package gg.projecteden.nexus.models.playerplushie;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Tier;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.store.ContributorService;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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

	private Map<Tier, Integer> vouchers = new HashMap<>();

	public boolean isSubscribedAt(Tier tier) {
		if (Dev.GRIFFIN.is(this) || Dev.WAKKA.is(this))
			return true;

		return new ContributorService().get(this).getPurchases().stream()
			.filter(purchase -> tier.getStorePackage().getId().equals(purchase.getPackageId()))
			.anyMatch(purchase -> purchase.getTimestamp().isAfter(LocalDateTime.now().minusDays(32)));
	}

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

}
