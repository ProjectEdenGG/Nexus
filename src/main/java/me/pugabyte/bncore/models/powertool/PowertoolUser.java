package me.pugabyte.bncore.models.powertool;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.bncore.utils.Utils.runCommand;

@Data
@Builder
@Entity("powertool")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class PowertoolUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = true;
	private Map<Material, String> powertools = new HashMap<>();

	public void use(Material material) {
		if (getOfflinePlayer().isOnline() && getPlayer() != null) {
			String command = powertools.get(material);
			runCommand(getPlayer(), command);
			BNCore.log("[PT] " + getPlayer().getName() + " issued server command: /" + command);
		}
	}

}
