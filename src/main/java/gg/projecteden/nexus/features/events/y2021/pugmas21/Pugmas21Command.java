package gg.projecteden.nexus.features.events.y2021.pugmas21;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.features.particles.VectorUtils;
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
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Pugmas21Command extends CustomCommand {
	private final Pugmas21UserService service = new Pugmas21UserService();
	private Pugmas21User user;

	public Pugmas21Command(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("train spawn <model>")
	@Permission("group.admin")
	@Description("Spawn a train armor stand")
	void train(int model) {
		Train.armorStand(model, location());
	}

	@Path("train spawn all")
	@Permission("group.admin")
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

	@Path("openAdvent [--height1] [--length1] [--particle1] [--ticks1] [--height2] [--length2] [--particle2] [--ticks2] [--randomMax]")
	void openAdvent(
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
		ItemStack chest = new ItemBuilder(Material.TRAPPED_CHEST).customModelData(1).build();
		Item item = spawnItem(location(), chest, length1, height1, location().getDirection());
		int itemTaskId = particleTask(particle1, item);

		List<ItemStack> items = new ArrayList<>();
		MaterialTag.CONCRETES.getValues().forEach(material -> items.add(new ItemStack(material)));

		Tasks.wait(ticks1, () -> {
			Tasks.cancel(itemTaskId);
			Location location = removeItem(item);
			new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(location).play();

			for (ItemStack itemStack : items) {
				Item _item = spawnItem(location, itemStack, length2, height2, VectorUtils.getRandomDirection());
				int _itemTaskId = particleTask(particle2, _item);

				Tasks.wait(ticks2 + RandomUtils.randomInt(0, randomMax), () -> {
					Tasks.cancel(_itemTaskId);
					Location _location = removeItem(_item);
					new SoundBuilder(Sound.ENTITY_CHICKEN_EGG).location(_location).play();
				});
			}
		});
	}

	@Path("openAdventDouble [--height1] [--length1] [--particle1] [--ticks1] [--height2] [--length2] [--particle2] [--ticks2] [--randomMax]")
	void openAdventDouble(
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
		ItemStack chest = new ItemBuilder(Material.TRAPPED_CHEST).customModelData(1).build();
		Item item = spawnItem(location(), chest, length1, height1, location().getDirection());
		int itemTaskId = particleTask(particle1, item);

		List<ItemStack> items = new ArrayList<>();
		MaterialTag.CONCRETES.getValues().forEach(material -> items.add(new ItemStack(material)));

		Tasks.wait(ticks1, () -> {
			Tasks.cancel(itemTaskId);
			Location location = removeItem(item);
			new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(location).play();

			for (ItemStack itemStack : items) {
				Item _item = spawnItem(location, itemStack, length2, height2, VectorUtils.getRandomDirection());
				int _itemTaskId = particleTask(particle2, _item);

				Tasks.wait(ticks2 + RandomUtils.randomInt(0, randomMax), () -> {
					Tasks.cancel(_itemTaskId);
					Location _location = removeItem(_item);
					new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(_location).play();

					for (ItemStack _itemStack : items) {
						Item __item = spawnItem(_location, _itemStack, length2, height2, VectorUtils.getRandomDirection());
						int __itemTaskId = particleTask(particle2, __item);

						Tasks.wait(ticks2 + RandomUtils.randomInt(0, randomMax), () -> {
							Tasks.cancel(__itemTaskId);
							Location __location = removeItem(__item);
							new SoundBuilder(Sound.ENTITY_CHICKEN_EGG).location(__location).play();
						});
					}
				});
			}
		});
	}

	@NotNull
	private Location removeItem(Item item) {
		Location location = item.getLocation();
		item.remove();
		return location;
	}

	private int particleTask(Particle particle1, Item item) {
		return Tasks.repeat(0, TickTime.TICK, () -> {
			if (!item.isOnGround())
				new ParticleBuilder(particle1).count(1).extra(0).location(item.getLocation()).spawn();
		});
	}

	@NotNull
	private Item spawnItem(Location location, ItemStack itemStack, double length, double height, Vector direction) {
		Item _item = world().dropItem(location, itemStack);
		_item.setCanPlayerPickup(false);
		_item.setCanMobPickup(false);
		_item.setVelocity(direction.multiply(length).add(new Vector(0, height, 0)));
		return _item;
	}
}
