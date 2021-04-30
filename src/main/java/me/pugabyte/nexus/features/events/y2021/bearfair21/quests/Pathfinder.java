package me.pugabyte.nexus.features.events.y2021.bearfair21.quests;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;

public class Pathfinder {
	@Getter
	@Setter
	private static Location selectedLoc;
	@Getter
	@Setter
	private static List<Integer> lineTasks;
	@Getter
	@Setter
	private static Entity selectedEntity;
}
