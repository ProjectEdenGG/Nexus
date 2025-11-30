package gg.projecteden.nexus.models.minigolf;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "minigolf_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class MiniGolfConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<MiniGolfCourse> courses = new ArrayList<>();

	public MiniGolfCourse getCourse(String courseId) {
		return courses.stream()
			.filter(course -> course.getName().equalsIgnoreCase(courseId))
			.findFirst()
			.orElse(null);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MiniGolfCourse {
		private String name;
		private ItemStack displayItem;
		private List<MiniGolfHole> holes = new ArrayList<>();

		public MiniGolfCourse(String name) {
			this.name = name;
			this.displayItem = new ItemBuilder(ItemModelType.MINIGOLF_PUTTER).build();
		}

		public MiniGolfHole getHole(int id) {
			return holes.stream()
				.filter(hole -> hole.getId() == id)
				.findFirst()
				.orElse(null);
		}

		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		public static class MiniGolfHole {
			private int id;
			private int par = 1;

			public MiniGolfHole(int id) {
				this.id = id;
			}
		}
	}

}
