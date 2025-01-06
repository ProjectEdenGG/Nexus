package gg.projecteden.nexus.models.powertool;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "powertool", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class PowertoolUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = true;
	private Map<Material, String> powertools = new ConcurrentHashMap<>();

	public void use(Material material) {
		Player player = getPlayer();
		if (player != null) {
			String command = powertools.get(material);
			PlayerUtils.runCommand(player, command);
			Nexus.log("[PT] " + player.getName() + " issued server command: /" + command);
		}
	}

}
