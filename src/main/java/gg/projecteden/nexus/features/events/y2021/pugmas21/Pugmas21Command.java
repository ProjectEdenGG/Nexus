package gg.projecteden.nexus.features.events.y2021.pugmas21;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.commands.staff.MultiCommandCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Pugmas21Command extends CustomCommand {

	public Pugmas21Command(@NonNull CommandEvent event) {
		super(event);
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
	void npcs_interact(Pugmas21InteractableNPC npc) {
		npc.interact(player());
	}

	@Path("regions shift")
	void regions_shift() {
		final Set<ProtectedRegion> regions = worldguard().getRegionsLike("^pugmas21.*");

		List<String> commands = new ArrayList<>();
		for (ProtectedRegion region : regions) {
			commands.add("rg sel " + region.getId());
			commands.add("wait 3");
			commands.add("/shift 909 w");
			commands.add("wait 3");
			commands.add("/shift 368 n");
			commands.add("wait 3");
			commands.add("rg redefine " + region.getId());
			commands.add("wait 3");
		}

		MultiCommandCommand.run(sender(), commands);
	}

	@Path("chunks load [state]")
	void chunks_load(boolean state) {
		final Set<BlockVector2> chunks = worldedit().getPlayerSelection(player()).getChunks();
		for (BlockVector2 vector : chunks)
			world().getChunkAtAsync(vector.getX(), vector.getZ()).thenAccept(chunk ->
				chunk.setForceLoaded(state));

		send(PREFIX + "Set " + chunks.size() + " chunks to " + (state ? "" : "not ") + "be force loaded");
	}

}
