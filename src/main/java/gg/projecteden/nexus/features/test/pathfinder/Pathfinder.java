package gg.projecteden.nexus.features.test.pathfinder;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

//TODO:
// 	give nodes a class enum value: low, medium, high, castle
//	when an NPC is pathfinding, they can only pathfind on their level node, or lower
public class Pathfinder {
	@Getter
	@Setter
	private static Location selectedLoc;
	@Getter
	@Setter
	private static Location targetA = null;
	@Getter
	@Setter
	private static Location targetB = null;
}
