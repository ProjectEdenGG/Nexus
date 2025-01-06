package gg.projecteden.nexus.features.test.pathfinder;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.api.common.utils.Utils.MinMaxResult;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.webs.WebConfig;
import gg.projecteden.nexus.models.webs.WebConfig.Node;
import gg.projecteden.nexus.models.webs.WebConfig.Route;
import gg.projecteden.nexus.models.webs.WebConfig.Web;
import gg.projecteden.nexus.models.webs.WebConfigService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@HideFromWiki
@NoArgsConstructor
@Permission(Group.ADMIN)
public class PathfinderCommand extends CustomCommand implements Listener {

	public PathfinderCommand(CommandEvent event) {
		super(event);
	}

	@Path("list")
	public void list() {
		WebConfigService service = new WebConfigService();
		WebConfig config = service.get0();
		send("Total Webs: " + config.getWebs().size());
		for (Web web : config.getWebs()) {
			send(" - Web: " + web.getId());
			send(" | Routes: " + web.getRoutes().size());
			send(" | Nodes: " + web.getNodes().size());

			web.getNodes().forEach(node -> {
				send(" | - Loc: " + StringUtils.getShortLocationString(node.getLocation()));
				send(" | - Rad: " + node.getRadius());
				send(" | - Neighbors: " + node.getNeighbors().keySet().size());
				send(" | ");
			});
		}
	}

	@Confirm
	@Path("reset")
	public void resetData() {
		WebConfigService service = new WebConfigService();
		service.deleteAll();
		send("Deleted all webs");
	}

	@Path("new test")
	public void newTest() {
		WebConfigService service = new WebConfigService();
		WebConfig config = service.get0();
		Web web = new Web("test");

		WorldGuardUtils worldguard = new WorldGuardUtils(player());
		Region region = worldguard.getRegion("pathfinder_test");
		WorldEditUtils worldedit = new WorldEditUtils(player());
		for (Block block : worldedit.getBlocks(region)) {
			if (block.getType().equals(Material.BLACK_CONCRETE)) {
				Node node = new Node(block.getLocation());
				web.getNodes().add(node);
			}
		}

		config.getWebs().add(web);
		service.save(config);
		send("&aNew web created: \"" + web.getId() + "\"");
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!Dev.WAKKA.is(player)) return;

		Material type = event.getBlock().getType();
		boolean end = switch (type) {
			case LIME_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, YELLOW_CONCRETE_POWDER, PINK_CONCRETE_POWDER, CYAN_CONCRETE_POWDER, WHITE_CONCRETE_POWDER, RED_CONCRETE_POWDER, ORANGE_CONCRETE_POWDER -> false;
			default -> true;
		};
		if (end)
			return;

		WorldGuardUtils worldguard = new WorldGuardUtils(player);
		if (!worldguard.isInRegion(player, "pathfinder_test"))
			return;

		WebConfigService service = new WebConfigService();
		WebConfig config = service.get0();
		Web web = config.getById("test");
		if (web == null) {
			send(player, "&cWeb is null");
			return;
		}

		Node selectedNode = web.getNodeByLocation(Pathfinder.getSelectedLoc());

		Location currentLoc = event.getBlock().getRelative(BlockFace.DOWN).getLocation();
		Node currentNode = web.getNodeByLocation(currentLoc);
		if (currentNode == null) {
			send(player, "&cThat node doesn't exist");
			return;
		}

		if (type.equals(Material.LIME_CONCRETE_POWDER) && selectedNode == null) {
			Pathfinder.setSelectedLoc(currentNode.getLocation());
			send(player, "&aSelected node at " + StringUtils.getShortLocationString(currentLoc));

		} else if (type.equals(Material.PURPLE_CONCRETE_POWDER) && selectedNode != null) {
			Location selectedLoc = selectedNode.getLocation();
			Distance distance = Distance.distance(selectedLoc, currentLoc);

			selectedNode.getNeighbors().put(currentNode.getUuid(), distance.get());
			currentNode.getNeighbors().put(selectedNode.getUuid(), distance.get());

			service.save(config);
			send(player, "&dAdded the nodes as neighbors");

		} else if (type.equals(Material.WHITE_CONCRETE_POWDER)) {
			for (Node neighbor : web.getNeighborNodes(currentNode))
				neighbor.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.LIGHT_GRAY_CONCRETE_POWDER);

		} else if (type.equals(Material.YELLOW_CONCRETE_POWDER))
			randomPath(web, currentNode, true);

		else if (type.equals(Material.PINK_CONCRETE_POWDER))
			randomPath(web, currentNode, false);

		else if (type.equals(Material.CYAN_CONCRETE_POWDER))
			furthestPath(web, currentNode);

		else if (type.equals(Material.RED_CONCRETE_POWDER)) {
			Pathfinder.setTargetA(currentLoc);
			send("Target A set");
			targetPath(web);
		} else if (type.equals(Material.ORANGE_CONCRETE_POWDER)) {
			Pathfinder.setTargetB(currentLoc);
			send("Target B set");

		}

	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!Dev.WAKKA.is(player)) return;

		Material type = event.getBlock().getType();
		boolean end = switch (type) {
			case LIME_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, WHITE_CONCRETE_POWDER -> false;
			default -> true;
		};
		if (end)
			return;

		WorldGuardUtils worldguard = new WorldGuardUtils(player);
		if (!worldguard.isInRegion(player, "pathfinder_test"))
			return;

		WebConfigService service = new WebConfigService();
		WebConfig config = service.get0();
		Web web = config.getById("test");
		if (web == null) {
			send(player, "&cWeb is null");
			return;
		}

		Location location = event.getBlock().getRelative(BlockFace.DOWN).getLocation();
		Node currentNode = web.getNodeByLocation(location);
		if (currentNode == null) {
			send(player, "&cThat node doesn't exist");
			return;
		}

		if (Material.WHITE_CONCRETE_POWDER.equals(type)) {
			for (Node neighbor : web.getNeighborNodes(currentNode))
				neighbor.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
			return;
		}

		Node selectedNode = web.getNodeByLocation(Pathfinder.getSelectedLoc());
		if (selectedNode == null) {
			send(player, "&cYou don't have a node selected");
			return;
		}

		if (Material.LIME_CONCRETE_POWDER.equals(type)) {
			send(player, "&2Unselected node at " + StringUtils.getShortLocationString(selectedNode.getLocation()));
			Pathfinder.setSelectedLoc(null);

		} else if (Material.PURPLE_CONCRETE_POWDER.equals(type)) {
			send(player, "&5Removed nodes as neighbors");
			selectedNode.getNeighbors().remove(currentNode.getUuid());
			currentNode.getNeighbors().remove(selectedNode.getUuid());
			service.save(config);
		}
	}

	private void furthestPath(Web web, Node currentNode) {
		Location origin = currentNode.getLocation();
		Route route = new Route();
		Node furthest = currentNode;

		int SAFETY = 0;
		while (true) {
			++SAFETY;
			if (SAFETY > web.getNodes().size())
				break;

			List<Node> neighbors = new ArrayList<>(web.getNeighborNodes(furthest));
			MinMaxResult<Node> temp = Utils.getMax(neighbors, neighbor -> Distance.distance(neighbor, origin).get());
			double furthestDistance = temp.getDouble();

			if (Distance.distance(furthest, origin).gt(furthestDistance)) {
				break;
			} else {
				furthest = temp.getObject();
				if (furthest != null)
					route.addNode(furthest);
			}
		}

		walkRoute(web, route);
	}

	private void randomPath(Web web, Node currentNode, boolean repeating) {
		int steps = RandomUtils.randomInt(1, web.getNodes().size());
		Node previous = null;
		Route route = new Route();

		int SAFETY = 0;
		for (int i = 0; i < steps; i++) {
			++SAFETY;
			if (SAFETY > 200)
				break;

			List<Node> neighbors = new ArrayList<>(web.getNeighborNodes(currentNode));
			if (previous != null)
				neighbors.remove(previous);

			Node next = RandomUtils.randomElement(neighbors);
			if (next == null)
				break;

			if (!repeating && route.getNodeUuids().contains(next.getUuid()))
				--i;
			else {
				previous = currentNode;
				currentNode = next;
				route.addNode(currentNode);
			}
		}

		walkRoute(web, route);
	}

	private void targetPath(Web web) {
		Node origin = web.getNodeByLocation(Pathfinder.getTargetA());
		Node target = web.getNodeByLocation(Pathfinder.getTargetB());

	}

	private void walkRoute(Web web, Route route) {
		int wait = 0;
		Material primary = Material.IRON_BLOCK;
		Material secondary = Material.QUARTZ_BLOCK;
		LinkedList<Node> routeNodes = web.getRouteNodes(route);
		int count = 0;
		int size = routeNodes.size();
		for (Node routeNode : routeNodes) {
			++count;
			Location loc = routeNode.getLocation();
			int finalCount = count;

			Tasks.wait(wait += 10, () -> {
				Block block = loc.getBlock().getRelative(BlockFace.UP);
				if (finalCount == size)
					block.setType(Material.REDSTONE_BLOCK);
				else if (!block.getType().equals(Material.AIR))
					block.setType(secondary);
				else
					block.setType(primary);
			});
		}
	}

}
