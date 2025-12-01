package gg.projecteden.nexus.features.events.waypoints;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.waypoints.Waypoint.Icon;
import net.minecraft.world.waypoints.WaypointTransmitter;
import net.minecraft.world.waypoints.WaypointTransmitter.Connection;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class WaypointsManager extends Feature implements Listener {
	private static final List<WaypointInstance> WAYPOINTS = new ArrayList<>();
	private static final NamespacedKey NBT_KEY = new NamespacedKey(Nexus.getInstance(), "WAYPOINT_STAND");

	@Override
	public void onStart() {
		Tasks.repeat(5, TickTime.SECOND.x(5), WaypointsManager::cleanupDeadWaypoints);
		Tasks.repeat(5, TickTime.SECOND.x(2), WaypointsManager::updateVisibleWaypoints);
	}

	private static void updateVisibleWaypoints() {
		WAYPOINTS.removeIf(waypointInstance -> {
			Player player = waypointInstance.getPlayer();
			if (player == null || !player.isOnline())
				return false;

			if (!waypointInstance.isSameWorld()) {
				waypointInstance.shutdown();
				return true;
			}

			if (!waypointInstance.isNearby())
				return false;

			waypointInstance.shutdown();
			return true;
		});

		for (Player player : Bukkit.getOnlinePlayers()) {
			var quester = Quester.of(player);

			var incompleteQuests = quester.getIncompleteQuests();
			if (incompleteQuests.isEmpty()) {
				for (WaypointInstance waypointInstance : new ArrayList<>(WAYPOINTS))
					if (waypointInstance.getWaypoint().isQuestWaypoint())
						hideWaypoint(player, waypointInstance.getWaypoint());

				continue;
			}

			for (Quest quest : incompleteQuests) {
				var questWaypoint = quest.getCurrentTaskStep().getWaypoint();
				if (questWaypoint == null)
					continue;

				if (quest.isComplete()) {
					hideWaypoint(player, questWaypoint);
					continue;
				}

				if (!questWaypoint.getLocation().getWorld().equals(player.getWorld()))
					continue;

				for (WaypointInstance waypointInstance : new ArrayList<>(WAYPOINTS)) {
					var sameWaypoint = waypointInstance.getWaypoint().equals(questWaypoint);
					var isForQuest = waypointInstance.getWaypoint().isQuestWaypoint();

					if (!sameWaypoint && isForQuest) {
						hideWaypoint(player, waypointInstance.getWaypoint());
					}
				}

				boolean shouldShow = true;
				boolean nearby = Distance.distance(questWaypoint.getLocation(), player.getLocation()).lte(3);
				for (WaypointInstance waypointInstance : new ArrayList<>(WAYPOINTS)) {
					var sameWaypoint = waypointInstance.getWaypoint().equals(questWaypoint);
					if (!sameWaypoint) {
						continue;
					}

					if (waypointInstance.isNearby()) {
						hideWaypoint(player, questWaypoint);
						shouldShow = false;
					} else {
						shouldShow = true;
					}
				}

				if (nearby)
					shouldShow = false;

				if (shouldShow) {
					showWaypoint(player, questWaypoint);
				}
			}
		}
	}

	private static void cleanupDeadWaypoints() {
		WAYPOINTS.removeIf(waypointInstance -> {
			var waypointStand = waypointInstance.getArmorStand();
			if (waypointStand != null && !waypointStand.isDead())
				return false;

			waypointInstance.shutdown();
			return true;
		});
	}

	@Override
	public void onStop() {
		WAYPOINTS.forEach(WaypointInstance::shutdown);
		WAYPOINTS.clear();
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		Tasks.wait(5, () -> {
			if (!(event.getEntity() instanceof ArmorStand armorStand))
				return;

			var isWaypointStand = armorStand.getPersistentDataContainer().has(NBT_KEY, PersistentDataType.BOOLEAN);
			if (!isWaypointStand)
				return;

			var tracked = WAYPOINTS.stream().anyMatch(waypointInstance -> waypointInstance.getArmorStand().getUniqueId().equals(armorStand.getUniqueId()));
			if (tracked)
				return;

			armorStand.remove();
		});
	}

	public static void hideAllWaypoints(@NotNull Player player) {
		WAYPOINTS.removeIf(waypointInstance -> {
			if (!waypointInstance.getUuid().equals(player.getUniqueId()))
				return false;

			waypointInstance.shutdown();
			return true;
		});
	}

	public static void hideWaypoint(@NonNull HasUniqueId uuid, IWaypoint waypoint) {
		Player player = Bukkit.getPlayer(uuid.getUniqueId());;
		if (player == null || !player.isOnline())
			return;

		WAYPOINTS.removeIf(waypointInstance -> {
			if (!waypointInstance.getUuid().equals(player.getUniqueId()))
				return false;

			if (!waypointInstance.getWaypoint().equals(waypoint))
				return false;

			waypointInstance.shutdown();
			return true;
		});
	}

	public static void showWaypoint(@NotNull HasUniqueId uuid, IWaypoint waypoint) {
		Player player = Bukkit.getPlayer(uuid.getUniqueId());;
		if (player == null || !player.isOnline())
			return;

		hideWaypoint(player, waypoint);

		ArmorStand armorStand = spawnWaypointStand(waypoint);

		net.minecraft.world.entity.decoration.ArmorStand nmsArmorStand = (net.minecraft.world.entity.decoration.ArmorStand) NMSUtils.toNMS(armorStand);
		Icon waypointIcon = ((WaypointTransmitter) nmsArmorStand).waypointIcon();
		ServerPlayer nmsPlayer = NMSUtils.toNMS(player);

		Connection connection;

		if (WaypointTransmitter.isReallyFar(nmsArmorStand, nmsPlayer)) {
			connection = new WaypointTransmitter.EntityAzimuthConnection(nmsArmorStand, waypointIcon, nmsPlayer);
		} else {
			if (!WaypointTransmitter.isChunkVisible(nmsArmorStand.chunkPosition(), nmsPlayer))
				connection = new WaypointTransmitter.EntityChunkConnection(nmsArmorStand, waypointIcon, nmsPlayer);
			else
				connection = new WaypointTransmitter.EntityBlockConnection(nmsArmorStand, waypointIcon, nmsPlayer);
		}

		WAYPOINTS.add(new WaypointInstance(player.getUniqueId(), armorStand, connection, waypoint));
		connection.connect();
	}

	private static ArmorStand spawnWaypointStand(IWaypoint waypoint) {
		Color color = waypoint.getColor();
		if (color == null)
			color = ColorType.WHITE.getBukkitColor();

		var armorStand = waypoint.getLocation().getWorld().spawn(waypoint.getLocation(), ArmorStand.class, stand -> {
			stand.setRightArmPose(EulerAngle.ZERO);
			stand.setLeftArmPose(EulerAngle.ZERO);
			stand.setHeadPose(EulerAngle.ZERO);
			stand.setSmall(true);
			stand.setGravity(false);
			stand.setInvulnerable(true);
			stand.setInvisible(true);
			stand.setMarker(true);
			stand.setDisabledSlots(EquipmentSlot.values());
			stand.registerAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE);
			stand.getPersistentDataContainer().set(NBT_KEY, PersistentDataType.BOOLEAN, true);
			var attribute = stand.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE);
			if (attribute != null)
				attribute.setBaseValue(500);
		});

		ServerLevel nmsWorld = NMSUtils.toNMS(waypoint.getLocation().getWorld());

		net.minecraft.world.entity.decoration.ArmorStand nmsArmorStand = (net.minecraft.world.entity.decoration.ArmorStand) NMSUtils.toNMS(armorStand);
		WaypointTransmitter nmsWaypointTransmitter = nmsArmorStand;

		nmsWorld.getWaypointManager().untrackWaypoint(nmsWaypointTransmitter);

		nmsWaypointTransmitter.waypointIcon().style = waypoint.getIcon().getAsset();
		nmsWaypointTransmitter.waypointIcon().color = Optional.of(color.asRGB());

		return armorStand;
	}
}
