package gg.projecteden.nexus.models.home;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Entity(value = "homes", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class HomeOwner implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Home> homes = new ArrayList<>();
	private boolean autoLock = true;
	private boolean usedDeathHome;
	private int extraHomes;

	public List<String> getNames() {
		return getNames(null);
	}

	public List<String> getNames(String filter) {
		if (homes == null)
			return Collections.emptyList();

		return homes.stream()
			.map(Home::getName)
			.filter(name -> filter == null || name.toLowerCase().startsWith(filter.toLowerCase()))
			.sorted(Comparator.comparing(String::toLowerCase))
			.collect(Collectors.toList());
	}

	public Optional<Home> getHome(String name) {
		return homes.stream()
				.filter(home -> home.getName().equalsIgnoreCase(name))
				.findFirst();
	}

	@ToString.Include
	public int getHomesLimit() {
		Rank rank = getRank();
		if (rank.isAdmin())
			return 999;
		return rank.ordinal() + 3 + extraHomes;
	}

	public void addExtraHomes(int extraHomes) {
		this.extraHomes += extraHomes;
	}

	public void removeExtraHomes(int extraHomes) {
		this.extraHomes -= extraHomes;
	}

	public int getHomesLeft() {
		int homes = this.homes.size();
		int max = getHomesLimit();
		return Math.max(0, max - homes);
	}

	public void checkHomesLimit() {
		if (getHomesLeft() <= 0)
			throw new InvalidInputException("You have used all of your available homes! &3To set more homes, you will need to either &erank up &3or purchase more from the &c/store");
	}

	public boolean hasGivenAccessTo(OfflinePlayer player) {
		Trust trust = new TrustService().get(this);
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
