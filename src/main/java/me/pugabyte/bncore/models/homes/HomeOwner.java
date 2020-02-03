package me.pugabyte.bncore.models.homes;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Entity("homes")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class HomeOwner extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Home> homes;
	private List<UUID> fullAccessList;
	private boolean autoLock;

	public List<String> getNames() {
		if (homes == null)
			return new ArrayList<>();
		return homes.stream().map(Home::getName).collect(Collectors.toList());
	}

	public Home getHome(String name) {
		return homes.stream()
				.filter(home -> home.getName().equalsIgnoreCase(name))
				.findFirst()
				.orElseThrow(() -> new InvalidInputException("That home does not exist"));
	}

	public void allowAll(OfflinePlayer player) {
		fullAccessList.add(player.getUniqueId());
	}

	public void removeAll(OfflinePlayer player) {
		fullAccessList.remove(player.getUniqueId());
		homes.forEach(home -> home.remove(player));
	}

	public boolean hasGivenAccessTo(OfflinePlayer player) {
		return fullAccessList.contains(player.getUniqueId());
	}

}
