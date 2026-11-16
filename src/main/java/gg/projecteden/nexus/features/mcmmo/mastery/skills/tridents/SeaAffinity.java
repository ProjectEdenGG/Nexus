package gg.projecteden.nexus.features.mcmmo.mastery.skills.tridents;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public final class SeaAffinity implements Listener {

	public static final String PERMISSION = "nexus.mcmmo.tridents.sea_affinity";

	private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(Nexus.getInstance(), "sea_affinity");
	private static final AttributeModifier MODIFIER = new AttributeModifier(MODIFIER_KEY, -0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Tasks.wait(1, () -> refresh(event.getPlayer()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Tasks.wait(1, () -> refresh(event.getPlayer()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onRespawn(PlayerPostRespawnEvent event) {
		Tasks.wait(1, () -> refresh(event.getPlayer()));
	}

	public void refresh(Player player) {
		AttributeInstance burningTime = player.getAttribute(Attribute.BURNING_TIME);
		if (burningTime == null)
			return;

		if (player.hasPermission(PERMISSION)) {
			if (burningTime.getModifier(MODIFIER_KEY) == null)
				burningTime.addTransientModifier(MODIFIER);
		} else {
			burningTime.removeModifier(MODIFIER_KEY);
		}
	}
}
