package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

import java.util.List;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.PlayerUtils.send;
import static gg.projecteden.nexus.utils.PlayerUtils.sendLine;

public class PlayerPlushie extends DecorationConfig {
	@Getter
	private final Pose pose;

	public PlayerPlushie(Pose pose) {
		this.pose = pose;
		this.id = "player_plushie_" + pose.name().toLowerCase();
		this.name = camelCase(pose) + " Player Plushie";
		this.material = PlayerPlushieConfig.MATERIAL;
		this.modelId = pose.getStartingIndex() + 1;
		this.modelIdPredicate = modelId -> MathUtils.isBetween(modelId, pose.getStartingIndex(), pose.getEndingIndex());
		this.hitboxes = Hitbox.NONE();
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	@Override
	public void sendInfo(Player player) {
		super.sendInfo(player);

		send(player, "&3[&ePlayer Plushie&3]");
		send(player, "&3Pose: &e" + StringUtils.camelCase(pose));
		sendLine(player);
	}

	static {
		Nexus.registerListener(new PlayerPlushieListener());
	}

	private static class PlayerPlushieListener implements Listener {
		/*
			Prevents:
				- Mob Pickup
				- Despawn
				- Combust
				- Damage
		 */

		@EventHandler
		public void onDrop(EntityAddToWorldEvent event) {
			if (!(event.getEntity() instanceof Item item))
				return;

			DecorationConfig config = DecorationConfig.of(item.getItemStack());
			if (config == null || !config.isPlushie())
				return;

			DecorationUtils.debug("player plushie - dropped");

			item.setCanMobPickup(false);
			item.setUnlimitedLifetime(true);
			item.setWillAge(false);
			item.setInvulnerable(true);
			item.setPersistent(true);
		}

		@EventHandler
		public void onBurn(EntityCombustEvent event) {
			if (!(event.getEntity() instanceof Item item))
				return;

			DecorationConfig config = DecorationConfig.of(item.getItemStack());
			if (config == null || !config.isPlushie())
				return;

			DecorationUtils.debug("player plushie - prevented combust");

			event.setCancelled(true);
		}
	}
}
