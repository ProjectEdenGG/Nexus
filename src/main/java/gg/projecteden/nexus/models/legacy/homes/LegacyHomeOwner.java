package gg.projecteden.nexus.models.legacy.homes;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Entity(value = "legacy_homes", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class LegacyHomeOwner implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<LegacyHome> homes = new ArrayList<>();

	public List<String> getNames() {
		return getNames(null);
	}

	public List<String> getNames(String filter) {
		if (homes == null)
			return new ArrayList<>();
		return homes.stream()
				.map(LegacyHome::getName)
				.filter(name -> filter == null || name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	public Optional<LegacyHome> getHome(String name) {
		return homes.stream()
				.filter(home -> home.getName().equalsIgnoreCase(name))
				.findFirst();
	}

	public void add(LegacyHome.LegacyHomeBuilder home) {
		getHome(home.getName()).ifPresent(this::delete);
		homes.add(home.build());
	}

	public void delete(LegacyHome home) {
		homes.remove(home);
	}

}
