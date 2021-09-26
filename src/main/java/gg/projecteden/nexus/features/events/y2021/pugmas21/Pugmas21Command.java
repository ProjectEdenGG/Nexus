package gg.projecteden.nexus.features.events.y2021.pugmas21;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User;
import gg.projecteden.nexus.models.pugmas21.Pugmas21UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.EnumUtils.IteratableEnum;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Permission("group.staff")
public class Pugmas21Command extends CustomCommand {
	private final Pugmas21UserService service = new Pugmas21UserService();
	private Pugmas21User user;

	public Pugmas21Command(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("train spawn <model>")
	@Description("Spawn a train armor stand")
	void train(int model) {
		Train.armorStand(model, location());
	}

	@Path("train spawn all")
	@Description("Spawn all train armor stands")
	void train() {
		Train.builder()
			.location(location())
			.direction(player().getFacing())
			.build()
			.spawnArmorStands();
	}

	@Path("train start")
	@Description("Start a moving train")
	void train(
		@Arg(".3") @Switch double speed,
		@Arg("60") @Switch int seconds,
		@Arg("4") @Switch double smokeBack,
		@Arg("5.3") @Switch double smokeUp,
		@Arg("false") @Switch boolean test
	) {
		Train.builder()
			.location(location())
			.direction(player().getFacing())
			.speed(speed)
			.seconds(seconds)
			.smokeBack(smokeBack)
			.smokeUp(smokeUp)
			.build()
			.start();
	}

	@Path("npcs interact <npc>")
	void npcs_interact(Pugmas21InstructionNPC npc) {
		npc.execute(player());
	}

	@Path("candycane cannon")
	void candycane_cannon() {
		giveItem(CandyCaneCannon.getItem().build());
	}

	@Path("advent animation [--twice] [--height1] [--length1] [--particle1] [--ticks1] [--height2] [--length2] [--particle2] [--ticks2] [--randomMax]")
	void advent_animation(
		@Arg("false") @Switch boolean twice,
		@Arg("0.25") @Switch double length1,
		@Arg("0.5") @Switch double height1,
		@Arg("crit") @Switch Particle particle1,
		@Arg("40") @Switch int ticks1,
		@Arg("0.25") @Switch double length2,
		@Arg("0.25") @Switch double height2,
		@Arg("crit") @Switch Particle particle2,
		@Arg("40") @Switch int ticks2,
		@Arg("40") @Switch int randomMax
	) {
		final AdventAnimation animation = AdventAnimation.builder()
			.location(location())
			.length1(length1)
			.height1(height1)
			.particle1(particle1)
			.ticks1(ticks1)
			.length2(length2)
			.height2(height2)
			.particle2(particle2)
			.ticks2(ticks2)
			.randomMax(randomMax)
			.build();

		if (twice)
			animation.openTwice();
		else
			animation.open();
	}

	@Path("advent")
	void advent(
		@Arg("30") @Switch int frameTicks,
		@Switch int day,
		@Arg(value = "-1", type = Integer.class) @Switch List<Integer> opened
	) {
		new AdventMenu(frameTicks, Pugmas21.EPOCH.plusDays(day - 1), opened).open(player());
	}

	@RequiredArgsConstructor
	public static class AdventMenu extends MenuUtils implements InventoryProvider {
		@NonNull
		private int frameTicks;
		@NonNull
		private LocalDate date;
		@NonNull
		private List<Integer> opened;
		private Title title = Title.FRAME_1;

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
				.provider(this)
				.size(6, 9)
				.title(title.getTitle())
				.build()
				.open(player);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			int row = 1;
			int column = 4;

			for (int day = 1; day <= 25; day++) {
				LocalDate dateIndex = Pugmas21.EPOCH.plusDays(day - 1);
				final Icon icon;
				if (opened.contains(day))
					icon = Icon.OPENED;
				else if (dateIndex.isAfter(this.date))
					icon = Icon.LOCKED;
				else if (dateIndex.equals(this.date) || this.date.isAfter(Pugmas21.PUGMAS.plusDays(-1)))
					icon = Icon.AVAILABLE;
				else
					icon = Icon.MISSED;

				final ItemBuilder item = new ItemBuilder(icon.getItem(day));
				contents.set(row, column, ClickableItem.empty(item.build()));

				if (column == 7) {
					column = 1;
					row++;
				} else
					column++;
			}

			updateTask(player, contents);
		}

		private void updateTask(Player player, InventoryContents contents) {
			Tasks.wait(frameTicks, () -> {
				if (!isOpen(player))
					return;

				title = title.nextWithLoop();
				open(player, contents.pagination().getPage());
			});
		}

		@AllArgsConstructor
		public enum Title implements IteratableEnum {
			FRAME_1("ꈉ盆"),
			FRAME_2("ꈉ鉊"),
			;

			private String title;

			public String getTitle() {
				return colorize("&f" + title);
			}

		}

		@AllArgsConstructor
		public enum Icon {
			MISSED(Material.TRAPPED_CHEST, 3),
			OPENED(Material.TRAPPED_CHEST, 5),
			AVAILABLE(Material.TRAPPED_CHEST, 4),
			LOCKED(Material.WHITE_STAINED_GLASS_PANE, 1),
			;

			private final Material material;
			private final int customModelData;

			public ItemBuilder getItem(int day) {
				return new ItemBuilder(material)
					.customModelData(customModelData)
					.name(StringUtils.camelCase(name()));
			}
		}

	}

}
