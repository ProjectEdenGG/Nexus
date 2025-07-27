package gg.projecteden.nexus.features.events.y2021.bearfair21.commands;

import com.destroystokyo.paper.entity.Pathfinder;
import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.api.common.utils.Utils.MinMaxResult;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.BearFair21PathfinderHelper;
import gg.projecteden.nexus.features.particles.effects.LineEffect;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.bearfair21.BearFair21WebConfig;
import gg.projecteden.nexus.models.bearfair21.BearFair21WebConfig.Node;
import gg.projecteden.nexus.models.bearfair21.BearFair21WebConfig.Route;
import gg.projecteden.nexus.models.bearfair21.BearFair21WebConfig.Web;
import gg.projecteden.nexus.models.bearfair21.BearFair21WebConfigService;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.GlowUtils;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Disabled
@HideFromWiki
@NoArgsConstructor
@Permission(Group.ADMIN)
public class BearFair21PathfinderCommand extends CustomCommand implements Listener {
	private static int wait = 0;

	public BearFair21PathfinderCommand(CommandEvent event) {
		super(event);
	}

	@Path("list")
	public void list() {
		BearFair21WebConfigService service = new BearFair21WebConfigService();
		BearFair21WebConfig config = service.get0();
		send("Total Webs: " + config.getWebs().size());
		for (Web web : config.getWebs()) {
			send(" - Web: " + web.getId());
			send(" | Nodes: " + web.getNodes().size());

			web.getNodes().forEach(node -> {
				send(" | - Loc: " + StringUtils.xyzw(node.getLocation()));
				send(" | - Rad: " + node.getRadius());
				send(" | - Neighbors: " + node.getNeighbors().keySet().size());
				send(" | ");
			});
		}
	}

	@Confirm
	@Path("reset")
	public void resetData() {
		BearFair21WebConfigService service = new BearFair21WebConfigService();
		service.deleteAll();
		send("Deleted all webs");
	}

	@Path("create")
	public void create() {
		BearFair21WebConfigService service = new BearFair21WebConfigService();
		BearFair21WebConfig config = service.get0();
		Web web = new Web("beehive");

		WorldGuardUtils worldguard = new WorldGuardUtils(player());
		Region region = worldguard.getRegion("pathfinder_beehive");

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

	@Path("entity select")
	public void entitySelect() {
		BearFair21PathfinderHelper.setSelectedEntity(player().getTargetEntity(10, true));
		send("Selected entity: " + BearFair21PathfinderHelper.getSelectedEntity().getType());
	}

	@Path("entity next")
	public void entityNext() {
		Node random = RandomUtils.randomElement(getWeb().getNodes());
		Location location = random.getPathLocation();
		Mob mob = (Mob) BearFair21PathfinderHelper.getSelectedEntity();
		Pathfinder pathfinder = mob.getPathfinder();
		pathfinder.moveTo(random.getPathLocation());
		send("pathfinding to: " + StringUtils.xyzw(random.getPathLocation()));

		GlowUtils.glow(location.getBlock(), TickTime.SECOND.x(10), player());
	}

	@Path("entity stop")
	public void entityReset() {
		Mob mob = (Mob) BearFair21PathfinderHelper.getSelectedEntity();
		Pathfinder pathfinder = mob.getPathfinder();
		pathfinder.stopPathfinding();
		send("stopped entity pathfinding");
	}

	@Path("random")
	public void random() {
		Web web = getWeb();
		Node startNode = web.getNodeByLocation(BearFair21PathfinderHelper.getSelectedLoc());
		if (startNode == null)
			error("selected node is null");

		Set<Node> nodes = web.getNodes();
		Node endNode = RandomUtils.randomElement(nodes);

		long ticks = TickTime.SECOND.x(5);
		wait = 0;

		startNode.getPathLocation().getBlock().setType(Material.RED_CONCRETE);
		Route route = findRoute(player(), web, startNode, endNode, null, startNode, new Route(startNode), new HashSet<>());

		Tasks.wait(wait += 10, () -> {
			GlowUtils.glow(endNode.getPathLocation().getBlock(), ticks, player(), GlowColor.BLUE);
			GlowUtils.glow(startNode.getPathLocation().getBlock(), ticks, player(), GlowColor.RED);

			for (UUID nodeUuid : route.getNodeUuids()) {
				Node node = web.getNodeById(nodeUuid);
				Block block = node.getPathLocation().getBlock();

				if (!route.getNodeUuids().getFirst().equals(nodeUuid) && !route.getNodeUuids().getLast().equals(nodeUuid))
					GlowUtils.glow(block, ticks, player(), GlowColor.WHITE);
			}
		});
	}

	// prioritize neighbor nodes by their distance, depending on the distance from the currentnode to the end node
	private Route findRoute(Player player, Web web, Node startNode, Node endNode, Node previousNode, Node currentNode, Route route, Set<Node> visited) {
		Block block = currentNode.getPathLocation().getBlock();
		Tasks.wait(wait += 10, () -> {
			player.sendMessage("node visited");
			if (block.getType().equals(Material.AIR))
				block.setType(Material.YELLOW_CONCRETE);
		});

		visited.add(currentNode);
		List<Node> neighbors = web.getNeighborNodes(currentNode);

		if (neighbors.isEmpty()) {
			Tasks.wait(wait += 10, () -> {
				block.setType(Material.GRAY_CONCRETE);
				player.sendMessage("dead end, going back a node");
			});
			route.removeNode(currentNode);
			return findRoute(player, web, startNode, endNode, currentNode, previousNode, route, visited);

		} else {
			for (Node neighbor : neighbors) {
				if (visited.contains(neighbor)) {
					continue;
				}

				route.addNode(neighbor);
				Block neighborBlock = neighbor.getPathLocation().getBlock();
				if (neighbor.getUuid().equals(endNode.getUuid())) {
					Tasks.wait(wait += 10, () -> {
						neighborBlock.setType(Material.BLUE_CONCRETE);
						player.sendMessage("found end node, returning");
					});
					return route;
				} else {
					Tasks.wait(wait += 10, () -> {
						neighborBlock.setType(Material.WHITE_CONCRETE);
						player.sendMessage("next node");
					});
					return findRoute(player, web, startNode, endNode, currentNode, neighbor, route, visited);
				}
			}

			LinkedList<UUID> routeUuids = route.getNodeUuids();
			if (routeUuids.isEmpty() || (routeUuids.size() == 1 && routeUuids.getLast().equals(startNode.getUuid()))) {
				player.sendMessage("no nodes left in route, returning");
				return route;
			}

			Node last = web.getNodeById(route.getLast());
			Block lastBlock = last.getPathLocation().getBlock();
			if (!last.getUuid().equals(startNode.getUuid()))
				route.removeNode(last);

			Tasks.wait(wait += 10, () -> {
				player.sendMessage("previous node");
				if (!lastBlock.getType().equals(Material.RED_CONCRETE) && !lastBlock.getType().equals(Material.BLUE_CONCRETE))
					lastBlock.setType(Material.AIR);
			});
			return findRoute(player, web, startNode, endNode, currentNode, last, route, visited);
		}
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!Dev.WAKKA.is(player)) return;

		Material type = event.getBlock().getType();
		boolean end = switch (type) {
			case LIME_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, WHITE_CONCRETE_POWDER,
				YELLOW_CONCRETE_POWDER, CYAN_CONCRETE_POWDER, PINK_CONCRETE_POWDER -> false;
			default -> true;
		};

		if (end)
			return;

		WorldGuardUtils worldguard = new WorldGuardUtils(player);
		if (!worldguard.isInRegion(player, "pathfinder_beehive"))
			return;

		BearFair21WebConfigService service = new BearFair21WebConfigService();
		BearFair21WebConfig config = service.get0();
		Web web = config.getById("beehive");
		if (web == null) {
			send(player, "&cWeb is null");
			return;
		}

		Node selectedNode = web.getNodeByLocation(BearFair21PathfinderHelper.getSelectedLoc());

		Location currentLoc = event.getBlock().getRelative(BlockFace.DOWN).getLocation();
		Node currentNode = web.getNodeByLocation(currentLoc);
		if (currentNode == null) {
			send(player, "&cThat node doesn't exist");
			return;
		}

		if (type.equals(Material.LIME_CONCRETE_POWDER) && selectedNode == null) {
			BearFair21PathfinderHelper.setSelectedLoc(currentNode.getLocation());
			send(player, "&aSelected node at " + StringUtils.xyzw(currentLoc));

		} else if (type.equals(Material.PURPLE_CONCRETE_POWDER) && selectedNode != null) {
			double distance = Distance.distance(selectedNode, currentNode).get();

			selectedNode.getNeighbors().put(currentNode.getUuid(), distance);
			currentNode.getNeighbors().put(selectedNode.getUuid(), distance);

			service.save(config);
			send(player, "&dAdded the nodes as neighbors");

		} else if (type.equals(Material.WHITE_CONCRETE_POWDER)) {
			for (Node neighbor : web.getNeighborNodes(currentNode)) {
				Block block = neighbor.getLocation().getBlock().getRelative(BlockFace.UP);
				block.setType(Material.LIGHT_GRAY_CONCRETE_POWDER);

				BearFair21PathfinderHelper.getLineTasks().add(LineEffect.builder()
					.owner(new ParticleService().get(player))
					.entity(player)
					.startLoc(LocationUtils.getCenteredLocation(currentLoc.clone().add(0, 1, 0)))
					.endLoc(LocationUtils.getCenteredLocation(block.getLocation()))
					.density(0.5)
					.count(15)
					.color(ColorType.RED.getBukkitColor())
					.ticks(-1)
					.start()
					.getTaskId());
			}

		} else if (type.equals(Material.YELLOW_CONCRETE_POWDER))
			randomPath(web, currentNode, true);

		else if (type.equals(Material.PINK_CONCRETE_POWDER))
			randomPath(web, currentNode, false);

		else if (type.equals(Material.CYAN_CONCRETE_POWDER))
			furthestPath(web, currentNode);
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
		if (!worldguard.isInRegion(player, "pathfinder_beehive"))
			return;

		BearFair21WebConfigService service = new BearFair21WebConfigService();
		BearFair21WebConfig config = service.get0();
		Web web = config.getById("beehive");
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

			for (Integer taskId : BearFair21PathfinderHelper.getLineTasks())
				Tasks.cancel(taskId);

			return;
		}

		Node selectedNode = web.getNodeByLocation(BearFair21PathfinderHelper.getSelectedLoc());
		if (selectedNode == null) {
			send(player, "&cYou don't have a node selected");
			return;
		}

		if (Material.LIME_CONCRETE_POWDER.equals(type)) {
			send(player, "&2Unselected node at " + StringUtils.xyzw(selectedNode.getLocation()));
			BearFair21PathfinderHelper.setSelectedLoc(null);

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

	private Web getWeb() {
		BearFair21WebConfigService service = new BearFair21WebConfigService();
		BearFair21WebConfig config = service.get0();
		Web web = config.getById("beehive");
		if (web == null)
			error("web is null");

		return web;
	}

}
