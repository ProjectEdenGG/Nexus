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

	@Path("openAdvent")
	void openAdvent() {
		ItemStack chest = new ItemBuilder(Material.TRAPPED_CHEST).customModelData(1).build();
		Item item = world().dropItem(location(), chest);
		item.setCanPlayerPickup(false);
		item.setCanMobPickup(false);

		item.setVelocity(location().getDirection().multiply(0.5).add(new Vector(0, 0.5, 0)));

		List<ItemStack> items = new ArrayList<>();
		MaterialTag.CONCRETES.getValues().forEach(material -> items.add(new ItemStack(material)));

		Tasks.wait(TickTime.SECOND.x(2), () -> {
			new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(item.getLocation()).play();
			Location location = item.getLocation();
			item.remove();

			for (ItemStack itemStack : items) {
				Item _item = world().dropItem(location, itemStack);
				_item.setCanPlayerPickup(false);
				_item.setCanMobPickup(false);
				_item.setVelocity(VectorUtils.getRandomDirection().multiply(0.25).add(new Vector(0, 0.25, 0)));

				int taskId = Tasks.repeat(0, TickTime.TICK, () -> {
					if (!item.isOnGround())
						new ParticleBuilder(Particle.SMOKE_NORMAL).count(1).extra(0).location(item.getLocation()).spawn();
				});

				int random = RandomUtils.randomInt(0, TickTime.SECOND.x(0.5));
				Tasks.wait(TickTime.SECOND.x(2) + random, () -> {
					Tasks.cancel(taskId);
					_item.remove();
					new SoundBuilder(Sound.ENTITY_CHICKEN_EGG).location(_item.getLocation()).play();
				});
			}
		});
	}
}
