package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.utils.EntityUtils.forcePacket;

public class TrainBackground {
	@Getter
	private static final String REGION = Pugmas21.region("train_background");
	private static final String LOOP_REGION = REGION + "_loop";
	private static final List<String> armorstandUuids = List.of(
		"7d0aa14c-4930-41a3-ac8e-913b0dbcdf9f",
		"34cd3cd6-cea5-4369-aed8-ed7199b81d22",
		"525e86b0-053d-4a24-9479-873eb0a50790",
		"5ac2901e-daf2-4b1d-96b0-c897e50ccdb0",
		"c662e789-ab9b-4e05-be2f-fbd6d141a22e",
		"ffe393da-57cc-406a-8a71-0310220974c5",
		"f3db037c-3982-4eae-9517-53d1d201466c",
		"8d27bbef-0c67-47dd-9c1c-642477310e86",
		"bb25d13b-c8ae-44b7-94e8-e4f1f9b964c1"
	);

	private static final Location loopLocation = LocationUtils.getCenteredLocation(Pugmas21.location(31, 11, -60, -180, 0));
	private static final double speed = 0.2;
	private static final BlockFace forwards = BlockFace.WEST;
	private static final List<Integer> randomModels = Arrays.asList(203, 204, 205);
	private static final WorldGuardUtils WGUtils = Pugmas21.worldguard();

	@Getter
	private static boolean active;
	private static final List<ArmorStand> armorStands = new ArrayList<>();
	private static final List<Integer> taskIds = new ArrayList<>();
	@Getter
	private static final List<Player> chugs = new ArrayList<>();

	public TrainBackground() {
		Tasks.repeat(1, TickTime.TICK.x(2), () -> {
			if (WGUtils.getPlayersInRegion(REGION).size() == 0)
				stop();
		});
	}

	public static void start() {
		if (active) return;
		active = true;

		loadArmorStands();

		Tasks.wait(2, () -> taskIds.add(Tasks.repeat(0, 1, TrainBackground::move)));

		taskIds.add(Tasks.repeat(0, TickTime.SECOND, () -> {
			for (Player player : chugs) {
				new SoundBuilder(CustomSound.TRAIN_CHUG)
					.receiver(player)
					.location(player)
					.category(SoundCategory.AMBIENT)
					.volume(0.8)
					.play();
			}
		}));

	}

	public static void stop() {
		if (!active) return;
		active = false;

		taskIds.forEach(Tasks::cancel);
		chugs.clear();
	}

	private static void loadArmorStands() {
		armorStands.clear();

		for (String uuid : armorstandUuids) {
			Entity entity = Bukkit.getEntity(UUID.fromString(uuid));
			if (entity instanceof ArmorStand armorStand)
				armorStands.add(armorStand);
		}
	}

	private static void move() {
		for (ArmorStand armorStand : armorStands)
			move(armorStand);
	}

	private static void move(ArmorStand armorStand) {
		if (!armorStand.isValid()) return;

		forcePacket(armorStand);

		Vector _forwards = forwards.getDirection().multiply(speed);
		Location to = armorStand.getLocation().clone().add(_forwards);

		ItemStack headItem = armorStand.getItem(EquipmentSlot.HEAD).clone();

		if (!isLoop(to)) {
			armorStand.teleport(to);
		} else {
			to = loopLocation.clone().add(_forwards);

			if (randomModels.contains(ModelId.of(headItem)))
				headItem = new ItemBuilder(headItem).modelId(RandomUtils.randomElement(randomModels)).build();

			armorStand.setItem(EquipmentSlot.HEAD, new ItemStack(Material.AIR));
			armorStand.teleport(to);

			ItemStack finalHeadItem = headItem;
			Tasks.wait(10, () -> armorStand.setItem(EquipmentSlot.HEAD, finalHeadItem));
		}
	}

	private static boolean isLoop(Location to) {
		return !WGUtils.getRegionsLikeAt(LOOP_REGION, to).isEmpty();
	}
}
