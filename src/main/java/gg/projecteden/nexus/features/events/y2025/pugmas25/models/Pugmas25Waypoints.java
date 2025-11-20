package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.models.pugmas25.Advent25Present;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.waypoints.Waypoint.Icon;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import net.minecraft.world.waypoints.WaypointTransmitter;
import net.minecraft.world.waypoints.WaypointTransmitter.Connection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Pugmas25Waypoints {

	private static final Map<ArmorStand, Pair<UUID, Connection>> playerWaypointConnections = new ConcurrentHashMap<>();

	public static void startup() {
		Tasks.repeat(5, TickTime.SECOND.x(5), () -> {
			playerWaypointConnections.keySet().forEach(waypointStand -> {
				if (waypointStand == null || !waypointStand.isDead())
					return;

				var pair = playerWaypointConnections.remove(waypointStand);
				pair.getSecond().disconnect();
			});
		});

		Tasks.repeat(5, TickTime.SECOND.x(2), () -> {
			playerWaypointConnections.keySet().forEach(waypointStand -> {
				if (waypointStand == null)
					return;

				var pair = playerWaypointConnections.get(waypointStand);
				Player player = Bukkit.getPlayer(pair.getFirst());
				if (player == null || !player.isOnline())
					return;

				if (Distance.distance(waypointStand.getLocation(), player.getLocation()).lte(5)) {
					pair.getSecond().disconnect();
					playerWaypointConnections.remove(waypointStand);
					waypointStand.remove();
				}
			});
		});
	}

	public static void shutdown() {
		playerWaypointConnections.keySet().forEach(waypointStand -> {
			var pair = Pugmas25Waypoints.playerWaypointConnections.get(waypointStand);
			pair.getSecond().disconnect();
			waypointStand.remove();
		});

		playerWaypointConnections.clear();
	}

	public static void hideWaypoint(@NotNull Player player) {
		playerWaypointConnections.keySet().forEach(waypointStand -> {
			var pair = playerWaypointConnections.get(waypointStand);
			if (!pair.getFirst().equals(player.getUniqueId()))
				return;

			playerWaypointConnections.remove(waypointStand);
			pair.getSecond().disconnect();
			waypointStand.remove();
		});
	}

	public static void showWaypoint(@NotNull Player player, WaypointTarget target, ColorType color) {
		showWaypoint(player, target.getLocation(), color);
	}

	public static void showWaypoint(@NotNull Player player, Advent25Present present) {
		showWaypoint(player, present.getLocation(), ColorType.CYAN);
	}

	private static void showWaypoint(@NotNull Player player, Location location, ColorType color) {
		hideWaypoint(player);

		ArmorStand armorStand = spawnWaypointStand(location.toCenterLocation(), color);

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

		playerWaypointConnections.put(armorStand, new Pair<>(player.getUniqueId(), connection));
		connection.connect();
	}

	private static ArmorStand spawnWaypointStand(Location location, ColorType color) {
		if (color == null)
			color = ColorType.WHITE;

		var armorStand = location.getWorld().spawn(location, ArmorStand.class, stand -> {
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
			var attribute = stand.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE);
			if (attribute != null)
				attribute.setBaseValue(500);
		});

		ServerLevel nmsWorld = NMSUtils.toNMS(location.getWorld());

		net.minecraft.world.entity.decoration.ArmorStand nmsArmorStand = (net.minecraft.world.entity.decoration.ArmorStand) NMSUtils.toNMS(armorStand);
		WaypointTransmitter nmsWaypointTransmitter = nmsArmorStand;

		nmsWorld.getWaypointManager().untrackWaypoint(nmsWaypointTransmitter);

		nmsWaypointTransmitter.waypointIcon().style = ResourceKey.create(WaypointStyleAssets.ROOT_ID, ResourceLocation.withDefaultNamespace("x"));
		nmsWaypointTransmitter.waypointIcon().color = Optional.of(color.getBukkitColor().asRGB());

		return armorStand;
	}

	@Getter
	@AllArgsConstructor
	public enum WaypointTarget {
		INN(loc(-740.5, 121, -3147.5)),
		;

		private final Location location;

		private static Location loc(double x, double y, double z) {
			return Pugmas25.get().location(x, y, z);
		}
	}
}
