package gg.projecteden.nexus.models.creative;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.commands.creative.ReachCommand;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;

import java.util.UUID;

@Data
@Entity(value = "creative_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class CreativeUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean trusted;
	private double reach;

	public void updateReach() {
		if (!isOnline())
			return;

		var player = getOnlinePlayer();
		var attribute = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
		if (attribute == null)
			throw new NullPointerException("Could not find attribute " + Attribute.BLOCK_INTERACTION_RANGE);

		var oldModifier = attribute.getModifier(ReachCommand.KEY);

		if (LuckPermsUtils.hasPermission(player, ReachCommand.PERMISSION) && reach > 0) {
			var newModifier = new AttributeModifier(ReachCommand.KEY, reach - attribute.getBaseValue(), Operation.ADD_NUMBER);
			if (oldModifier == null) {
				attribute.addModifier(newModifier);
			} else if (oldModifier.getAmount() != newModifier.getAmount() || oldModifier.getOperation() != newModifier.getOperation()) {
				attribute.removeModifier(ReachCommand.KEY);
				attribute.addModifier(newModifier);
			}
		} else {
			if (oldModifier != null)
				attribute.removeModifier(ReachCommand.KEY);
		}
	}

}
