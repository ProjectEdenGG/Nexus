package me.pugabyte.nexus.models.home;

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
import lombok.ToString;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.trust.Trust;
import me.pugabyte.nexus.models.trust.Trust.Type;
import me.pugabyte.nexus.models.trust.TrustService;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.features.homes.HomesFeature.maxHomes;

@Data
@Builder
@Entity("homes")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class HomeOwner implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Home> homes = new ArrayList<>();
	private boolean autoLock;
	private boolean usedDeathHome;

	public List<String> getNames() {
		return getNames(null);
	}

	public List<String> getNames(String filter) {
		if (homes == null)
			return new ArrayList<>();
		return homes.stream()
				.map(Home::getName)
				.filter(name -> filter == null || name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	public Optional<Home> getHome(String name) {
		return homes.stream()
				.filter(home -> home.getName().equalsIgnoreCase(name))
				.findFirst();
	}

	@ToString.Include
	public int getMaxHomes() {
		for (int i = maxHomes; i > 0; i--)
			if (Nexus.getPerms().playerHas(null, getOfflinePlayer(), "homes.limit." + i))
				return i;

		return 0;
	}

	public int getHomesLeft() {
		int homes = this.homes.size();
		int max = getMaxHomes();
		return Math.max(0, max - homes);
	}

	public void checkHomesLimit() {
		if (getHomesLeft() <= 0)
			throw new InvalidInputException("You have used all of your available homes! &3To set more homes, you will need to either &erank up &3or &c/donate");
	}

	public boolean hasGivenAccessTo(OfflinePlayer player) {
		Trust trust = new TrustService().get(getOfflinePlayer());
		return trust.trusts(Type.HOMES, player);
	}

	public void add(Home home) {
		getHome(home.getName()).ifPresent(this::delete);
		homes.add(home);
	}

	public void delete(Home home) {
		homes.remove(home);
	}

}
