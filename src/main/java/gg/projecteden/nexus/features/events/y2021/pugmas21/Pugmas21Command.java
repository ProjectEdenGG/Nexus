package gg.projecteden.nexus.features.events.y2021.pugmas21;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User;
import gg.projecteden.nexus.models.pugmas21.Pugmas21UserService;
import lombok.NonNull;
import org.bukkit.Particle;

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

}
