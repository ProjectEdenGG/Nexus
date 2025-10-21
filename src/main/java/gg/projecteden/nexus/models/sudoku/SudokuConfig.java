package gg.projecteden.nexus.models.sudoku;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "sudoku_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class SudokuConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, List<UUID>> boards = new ConcurrentHashMap<>();
	private List<Integer> mapIds = new ArrayList<>();

	public void create(String id, List<UUID> board) {
		var mapIds = getMapIds().iterator();
		for (UUID uuid : board) {
			var entity = Bukkit.getEntity(uuid);
			if (!(entity instanceof ItemFrame itemFrame))
				throw new InvalidInputException("Entity " + uuid + " is not an ItemFrame");
			var map = new ItemStack(Material.FILLED_MAP);
			if (!(map.getItemMeta() instanceof MapMeta mapMeta))
				throw new InvalidInputException("Item meta is not a MapMeta (" + map.getItemMeta().getClass().getSimpleName() + ")");
			mapMeta.setMapId(mapIds.next());
			map.setItemMeta(mapMeta);
			itemFrame.setItem(map);
		}
		boards.computeIfAbsent(id, $ -> new ArrayList<>()).addAll(board);
	}

	public boolean isBoardFrame(ItemFrame itemFrame) {
		if (!(itemFrame.getItem().getItemMeta() instanceof MapMeta mapMeta))
			return false;
		if (!mapIds.contains(mapMeta.getMapId()))
			return false;
		if (boards.values().stream().noneMatch(board -> board.contains(itemFrame.getUniqueId())))
			return false;

		return true;
	}
}
