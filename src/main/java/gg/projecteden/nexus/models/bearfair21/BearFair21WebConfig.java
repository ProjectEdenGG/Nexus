package gg.projecteden.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "bearfair21_web", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class BearFair21WebConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	@Embedded
	private Set<Web> webs = new HashSet<>();

	public Web getById(String id) {
		return webs.stream().filter(web -> web.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
	}

	public void add(Web web) {
		if (getById(web.getId()) != null)
			throw new InvalidInputException("Web &e" + web.getId() + " &calready exists");

		webs.add(web);
	}

	/*
	id - unique identifier
	nodes - list of all nodes
	routes - list of all saved routes
 */
	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	public static class Web {
		@EqualsAndHashCode.Include
		@NonNull
		String id;
		@ToString.Exclude
		Set<Node> nodes = new HashSet<>();

		public Node getNodeByLocation(Location location) {
			if (location == null)
				return null;

			for (Node node : nodes) {
				if (node.getLocation().equals(location))
					return node;
			}
			return null;
		}

		public List<Node> getNeighborNodes(Node node) {
			List<Node> result = new ArrayList<>();
			Set<UUID> uuids = node.getNeighbors().keySet();

			for (Node webNode : this.nodes) {
				if (uuids.contains(webNode.getUuid()))
					result.add(webNode);
			}

			return result;
		}

		public LinkedList<Node> getRouteNodes(Route route) {
			LinkedList<Node> result = new LinkedList<>();
			LinkedList<UUID> uuids = route.getNodeUuids();

			for (UUID uuid : uuids) {
				for (Node node : this.nodes)
					if (node.getUuid().equals(uuid))
						result.add(node);
			}

			return result;
		}

		public Node getFurthestNode(Node origin) {
			return Utils.getMax(getNodes(), node -> node.getLocation().distance(origin.getLocation())).getObject();
		}

		public Node getNodeById(UUID uuid) {
			for (Node node : getNodes()) {
				if (node.getUuid().equals(uuid))
					return node;
			}

			return null;
		}
	}

	/*
	 * location - location in the world
	 * radius - radius around location, will be used for determining if an NPC has gotten close enough to this node
	 * neighborMap - map of connected nodes & their distance from each other
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@RequiredArgsConstructor
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	public static class Node {
		@EqualsAndHashCode.Include
		@NonNull
		UUID uuid;
		@NonNull
		Location location;
		Integer radius = null;
		@ToString.Exclude
		Map<UUID, Double> neighbors = new ConcurrentHashMap<>();

		public Node(@NotNull Location location) {
			this.uuid = UUID.randomUUID();
			this.location = location;
		}

		public Location getPathLocation() {
			return this.location.getBlock().getRelative(BlockFace.UP).getLocation();
		}
	}

	/*
	route - list of nodes in order, forming a route
	length - sum of all node distances between each other
 */
	@Data
	@RequiredArgsConstructor
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	public static class Route {
		@EqualsAndHashCode.Include
		@ToString.Exclude
		LinkedList<UUID> nodeUuids = new LinkedList<>();
		Double length = null;

		public Route(Node startNode) {
			this.nodeUuids.add(startNode.getUuid());
		}

		public void addNode(Node node) {
			nodeUuids.add(node.getUuid());
		}

		public void removeNode(Node node) {
			nodeUuids.remove(node.getUuid());
		}

		public UUID getLast() {
			return nodeUuids.getLast();
		}
	}
}
