package gg.projecteden.nexus.features.minigolf;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.ResourcePack.ResourcePackNumber;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse.MiniGolfHole;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfigService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.ArrayList;

@Title("MiniGolf Config")
public class MiniGolfConfigMenu extends InventoryProvider {
	private final MiniGolfConfigService service = new MiniGolfConfigService();
	private final MiniGolfConfig config = service.get0();

	public void save() {
		service.save(config);
	}

	@Override
	public void init() {
		addCloseItem();

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(ItemModelType.GUI_PLUS).dyeColor(ColorType.LIGHT_GREEN).name("Add Course"), e -> {
			new DialogBuilder()
				.title("Add Course")
				.bodyText("Enter the name of the course")
				.inputText("name", "Course name")
				.confirmation()
				.submitText("Create course")
				.onSubmit(input -> {
					config.getCourses().add(new MiniGolfCourse(input.getText("name")));
					save();
					refresh();
				})
				.onCancel(action -> refresh())
				.open(viewer);
		}));

		var items = new ArrayList<ClickableItem>();
		for (MiniGolfCourse course : config.getCourses()) {
			ItemBuilder item = new ItemBuilder(Material.STONE)
				.name("&e" + course.getName())
				.lore("&fHoles: " + course.getHoles().size());

			items.add(ClickableItem.of(item, e -> {
				new MiniGolfCourseConfigMenu(course, this).open(viewer);
			}));
		}

		paginate(items);
	}

	@RequiredArgsConstructor
	public class MiniGolfCourseConfigMenu extends InventoryProvider {
		private final MiniGolfCourse course;
		private final MiniGolfConfigMenu previousMenu;

		@Override
		public void init() {
			addBackItem(previousMenu);

			var items = new ArrayList<ClickableItem>();
			items.add(ClickableItem.of(new ItemBuilder(Material.HOPPER).name("Holes"), e -> {
				new MiniGolfCourseHolesConfigMenu(course, this).open(viewer);
			}));

			paginate(items);
		}

		@RequiredArgsConstructor
		public class MiniGolfCourseHolesConfigMenu extends InventoryProvider {
			private final MiniGolfCourse course;
			private final InventoryProvider previousMenu;

			@Override
			public void init() {
				addBackItem(previousMenu);

				var items = new ArrayList<ClickableItem>();
				contents.set(0, 8, ClickableItem.of(new ItemBuilder(ItemModelType.GUI_PLUS).dyeColor(ColorType.LIGHT_GREEN).name("Add Hole"), e -> {
					course.getHoles().add(new MiniGolfHole(course.getHoles().size() + 1));
					save();
					refresh();
				}));

				for (MiniGolfHole hole : course.getHoles()) {
					var item = ResourcePackNumber.of(hole.getId()).get().name("&eHole #" + hole.getId())
						.lore("&fPar: &e" + hole.getPar())
						.lore("")
						.lore("&e <&e&m &e&m &e&m &f Left click to decrease par")
						.lore("&e &e&m &e&m &e&m &e>&f Right click to increase par");

					items.add(ClickableItem.of(item, e -> {
						if (e.isLeftClick())
							hole.setPar(Math.max(1, hole.getPar() - 1));
						else
							hole.setPar(hole.getPar() + 1);
						save();
						refresh();
					}));
				}

				paginate(items);
			}
		}
	}
}
