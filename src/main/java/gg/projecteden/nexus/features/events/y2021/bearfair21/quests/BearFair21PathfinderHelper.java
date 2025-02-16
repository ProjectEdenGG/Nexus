package gg.projecteden.nexus.features.events.y2021.bearfair21.quests;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class BearFair21PathfinderHelper {
	@Getter
	@Setter
	private static Location selectedLoc;
	@Getter
	@Setter
	private static List<Integer> lineTasks = new ArrayList<>();
	@Getter
	@Setter
	private static Entity selectedEntity;
}
