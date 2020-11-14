package me.pugabyte.bncore.features.holidays.pugmas20.models;

import com.sk89q.worldedit.regions.Region;
import me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.models.task.Task;
import me.pugabyte.bncore.models.task.TaskService;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PugmasTree {
	private Region region;

	public enum PugmasTreeType {
		BLOODWOOD,
		MAHOGANY,
		EUCALYPTUS,
		WILLOW,
		CRYSTAL,
		MAGIC,
		OAK,
		TEAK,
		MAPLE,
		BLISTERWOOD;

		public static String taskId = "pugmas-tree-regen";

		public void onBreak(int id) {
			new TaskService().save(new Task(taskId, new HashMap<String, Object>() {{
				put("tree", name());
				put("id", id);
			}}, LocalDateTime.now().plusMinutes(RandomUtils.randomInt(3, 5))));
		}

		static {
			Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(15), () -> {
				TaskService service = new TaskService();
				service.process(taskId).forEach(task -> {
					Map<String, Object> data = task.getJson();

					PugmasTreeType treeType = PugmasTreeType.valueOf((String) data.get("tree"));
					int id = (int) data.get("id");

					String regionName = "pugmas20_trees_" + treeType.name().toLowerCase() + "_" + id;
					String schematicName = regionName.replaceAll("_", "/");
					Region region = Pugmas20.WGUtils.getRegion(regionName);

					Pugmas20.WEUtils.paster()
							.at(region.getMinimumPoint())
							.file(schematicName)
							.air(false)
							.paste();

					service.complete(task);
				});
			});
		}

		@Path("taskTest <message...>")
		void taskTest(String message) {
		}
	}
}

