package gg.projecteden.nexus.models.sudoku;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(SudokuConfig.class)
public class SudokuConfigService extends MongoBukkitService<SudokuConfig> {
	private final static Map<UUID, SudokuConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, SudokuConfig> getCache() {
		return cache;
	}

}
