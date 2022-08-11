package gg.projecteden.nexus.models.friends;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "friends_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class FriendsUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	List<UUID> friends = new ArrayList<>();
	List<UUID> requests_sent = new ArrayList<>();
	List<UUID> requests_received = new ArrayList<>();

	public boolean isFriendsWith(Player player) {
		return friends.contains(player.getUniqueId());
	}
}
