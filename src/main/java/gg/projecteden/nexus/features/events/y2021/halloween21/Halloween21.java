package gg.projecteden.nexus.features.events.y2021.halloween21;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.halloween21.models.Candy;
import gg.projecteden.nexus.features.events.y2021.halloween21.models.Pumpkin;
import gg.projecteden.nexus.features.mobheads.MobHeads;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateStartEvent;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.Booster;
import gg.projecteden.nexus.models.halloween21.Halloween21UserService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

@Disabled
public class Halloween21 implements Listener {

	public Halloween21() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		final LivingEntity victim = event.getEntity();
		final Player killer = event.getEntity().getKiller();

		if (!EntityUtils.isHostile(victim))
			return;

		if (killer == null)
			return;

		if (MobHeads.shouldIgnore(killer, victim))
			return;

		if (!RandomUtils.chanceOf(10 * Booster.getTotalBoost(killer, Boostable.HALLOWEEN_CANDY)))
			return;

		event.getDrops().add(Candy.random().getDisplayItem());
	}

	@EventHandler
	public void onEntityPickupItem(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!Candy.isCandy(event.getItem().getItemStack()))
			return;

		if (new Halloween21UserService().get(player).isPickupCandy())
			return;

		event.setCancelled(true);
	}

	@Getter
	@AllArgsConstructor
	private enum PumpkinableEntity {
		ZOMBIE,
		HUSK,
		DROWNED,
		SKELETON,
		STRAY,
		WITHER_SKELETON,
		VEX,
		;

		public static ItemBuilder getPumpkin(LivingEntity entity) {
			try {
				of(entity);
			} catch (IllegalArgumentException ex) {
				return null;
			}

			final String bits = String.valueOf(entity.getUniqueId().getLeastSignificantBits());
			final int modelId = Pumpkin.MIN + Integer.parseInt(StringUtils.right(bits, 2));
			return Pumpkin.itemOf(modelId);
		}

		public static PumpkinableEntity of(LivingEntity entity) {
			return valueOf(entity.getType().name());
		}
	}

	private static final List<WorldGroup> PUMPKINABLE_WORLD_GROUPS = List.of(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK);

	public static int taskId;

	@EventHandler
	public void onResourcePackUpdateStart(ResourcePackUpdateStartEvent event) {
		Tasks.cancel(taskId);
	}

	@EventHandler
	public void onResourcePackUpdate(ResourcePackUpdateCompleteEvent event) {
		taskId = Tasks.repeat(0, 1, () -> {
			for (WorldGroup worldGroup : PUMPKINABLE_WORLD_GROUPS) {
				for (World world : worldGroup.getWorlds()) {
					final List<Player> players = OnlinePlayers.where().world(world).get();

					for (LivingEntity entity : world.getLivingEntities()) {
						final ItemBuilder pumpkin = PumpkinableEntity.getPumpkin(entity);
						if (pumpkin == null)
							continue;

						PacketUtils.sendFakeItem(entity, players, pumpkin.build(), EquipmentSlot.HEAD);
					}
				}
			}
		});
	}

}
