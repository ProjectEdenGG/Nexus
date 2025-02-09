package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationTagType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PlayerPlushie extends DecorationConfig {
	@Getter
	private final Pose pose;

	private static final String ITEM_MODEL_PATH = "decoration/plushies/player/";

	public PlayerPlushie(Pose pose, UUID uuid) {
		this.pose = pose;
		this.id = "player_plushie_" + pose.name().toLowerCase();
		this.name = StringUtils.camelCase(pose) + " Player Plushie";
		this.material = PlayerPlushieConfig.MATERIAL;
		String poseBasePath = ITEM_MODEL_PATH + pose.name().toLowerCase() + "/";
		String modelPath = pose.getCustomMaterial() != null ? pose.getCustomMaterial().getModel() : poseBasePath + "<UUID>";
		this.model = modelPath.replace("<UUID>", uuid.toString());
		this.modelPredicate = model -> model.startsWith(poseBasePath);
		this.hitboxes = Hitbox.NONE();
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();

		DecorationTagType.setLore(List.of("&f" + StringUtils.camelCase(pose), "&7Player Plushie"), this);
	}

	@Override
	public void sendInfo(Player player) {
		super.sendInfo(player);

		PlayerUtils.send(player, "&3[&ePlayer Plushie&3]");
		PlayerUtils.send(player, "&3Pose: &e" + StringUtils.camelCase(pose));
		PlayerUtils.sendLine(player);
	}

	@Override
	public ItemBuilder getItemBuilder() {
		return super.getItemBuilder().soulbound();
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
			Adds Soulbound
		 */

		@EventHandler
		public void onDrop(EntityAddToWorldEvent event) {
			if (!(event.getEntity() instanceof Item item))
				return;

			final ItemStack itemStack = item.getItemStack();
			if (isNotPlayerPlushie(itemStack))
				return;

			DecorationLang.debug("player plushie - dropped");

			item.setCanMobPickup(false);
			item.setUnlimitedLifetime(true);
			item.setWillAge(false);
			item.setInvulnerable(true);
			item.setPersistent(true);
			item.setItemStack(new ItemBuilder(itemStack).soulbound().build());
		}

		@EventHandler
		public void onBurn(EntityCombustEvent event) {
			if (!(event.getEntity() instanceof Item item))
				return;

			final ItemStack itemStack = item.getItemStack();
			if (isNotPlayerPlushie(itemStack))
				return;

			DecorationLang.debug("player plushie - prevented combust");

			event.setCancelled(true);
		}
	}

	private static boolean isNotPlayerPlushie(ItemStack item) {
		return !isPlayerPlushie(item);
	}

	public static boolean isPlayerPlushie(ItemStack item) {
		String model = Model.of(item);
		return item.getType() == Material.LAPIS_LAZULI && model != null && model.startsWith(ITEM_MODEL_PATH);
	}

}
