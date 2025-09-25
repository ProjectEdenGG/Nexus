package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@NoArgsConstructor
public class HappyGhastSprint implements Listener {
	private static final NamespacedKey ATTRIBUTE_KEY = new NamespacedKey(Nexus.getInstance(), "happy_ghast_sprint");
	public static double SPEED = .4;

	static {
		Tasks.repeat(0, 1, () -> {
			for (var world : Bukkit.getWorlds())
				for (var entity : world.getEntitiesByClass(HappyGhast.class))
					setHappyGhastSpeed(entity);
		});
	}

	private static void setHappyGhastSpeed(Entity entity) {
		if (!(entity instanceof HappyGhast happyGhast))
			return;

		var attribute = happyGhast.getAttribute(Attribute.FLYING_SPEED);
		if (attribute == null) {
			Nexus.log("Could not find flying speed attribute for Happy Ghast");
			return;
		}

		var passengers = entity.getPassengers();
		if (isNullOrEmpty(passengers)) {
			attribute.removeModifier(ATTRIBUTE_KEY);
			return;
		}

		var driver = passengers.getFirst();
		if (!(driver instanceof Player player)) {
			attribute.removeModifier(ATTRIBUTE_KEY);
			return;
		}

		if (!player.getCurrentInput().isSprint()) {
			attribute.removeModifier(ATTRIBUTE_KEY);
			return;
		}

		var modifier = attribute.getModifier(ATTRIBUTE_KEY);
		if (modifier != null && modifier.getAmount() != SPEED) {
			attribute.removeModifier(ATTRIBUTE_KEY);
			modifier = null;
		}

		if (modifier == null)
			attribute.addModifier(new AttributeModifier(ATTRIBUTE_KEY, SPEED, Operation.MULTIPLY_SCALAR_1));
	}

}

