package gg.projecteden.nexus.features.commands.staff;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.CompletableFutures;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.FakeWorldEdit;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldEditUtils.Paster;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Aliases("weutils")
@Permission(Group.STAFF)
public class WorldEditUtilsCommand extends CustomCommand {
	private WorldEditUtils worldedit;

	public WorldEditUtilsCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			worldedit = new WorldEditUtils(player());
	}

	@Path("rotate [y] [x] [z]")
	@Description("Test rotating your selection")
	void rotate(int y, int x, int z) {
		final WorldEditUtils worldedit = new WorldEditUtils(player());
		worldedit.paster("Testing rotate")
			.clipboard(worldedit.getPlayerSelection(player()))
			.at(location())
			.transform(new AffineTransform().rotateY(y).rotateX(x).rotateZ(z))
			.pasteAsync();
	}

	@Path("clipboard [--build] [--async] [--entities]")
	@Description("Test pasting your clipboard")
	void clipboard(
		@Switch @Arg("false") boolean build,
		@Switch @Arg("false") boolean async,
		@Switch @Arg("false") boolean entities
	) {
		final WorldEditUtils utils = new WorldEditUtils(world());
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		Runnable task = () -> {
			final Paster paster = utils.paster("Testing clipboard").clipboard(player()).at(location()).entities(entities);

			if (build) {
				send("Building " + (async ? "async" : "sync"));
				for (int i = 0; i < 5; i++)
					futures.add(paster.build());
			} else {
				send("Pasting " + (async ? "async" : "sync"));
				for (int i = 0; i < 5; i++)
					futures.add(paster.pasteAsync());
			}

			CompletableFutures.allOf(futures).thenRun(() -> send("done"));
		};

		if (async)
			Tasks.async(task);
		else
			task.run();
	}

	@Path("fake")
	@Description("Test a custom copy/paste implementation")
	void fake() {
		new FakeWorldEdit(world())
			.clipboard()
			.min(location())
			.max(location().add(2, 2, 2))
			.filter(MaterialTag.ALL_AIR::isNotTagged)
			.copy()
			.paste(location().add(9, 9, 9));
	}

	@Confirm
	@Path("breakNaturally")
	@Description("Break all blocks in your selection with drops and particles")
	void breakNaturally() {
		Region selection = worldedit.getPlayerSelection(player());
		if (selection.getArea() > 50000)
			error("Max selection size is 50000");

		for (Block block : worldedit.getBlocks(selection)) {
			if (block.getType() == Material.AIR)
				continue;

			block.breakNaturally();
		}
	}

	@Path("smartReplace <from> <to>")
	@Description("Swap materials on blocks without deleting block data such as rotation")
	void smartReplace(Material from, Material to) {
		Region selection = worldedit.getPlayerSelection(player());

		for (Block block : worldedit.getBlocks(selection)) {
			if (block.getType() != from)
				continue;

			block.setBlockData(Bukkit.createBlockData(block.getBlockData().getAsString().replace(from.name().toLowerCase(), to.name().toLowerCase())));
		}
	}

	@Path("colorReplace <from> <to>")
	@Description("Swap colored materials (i.e. wool concrete)")
	void smartReplace(String from, String to) {
		for (DyeColor dyeColor : DyeColor.values()) {
			String fromColored = (dyeColor.name() + "_" + from).toUpperCase();
			String toColored = (dyeColor.name() + "_" + to).toUpperCase();
			runCommand("/replace " + fromColored + " " + toColored);
		}
	}

	@Path("tagReplace <from> <to>")
	@Description("Replace all materials in a tag with another material")
	void smartReplace(Tag<Material> from, Material to) {
		Region selection = worldedit.getPlayerSelection(player());

		for (Block block : worldedit.getBlocks(selection)) {
			if (!from.isTagged(block.getType()))
				continue;

			block.setType(to);
		}
	}

	@Path("schem buildQueue <schematic> <seconds>")
	@Description("Procedurally build a schematic")
	void schemBuildQueue(String schematic, int seconds) {
		worldedit.paster()
			.file(schematic)
			.at(location().add(-10, 0, 0))
			.duration(TickTime.SECOND.x(seconds))
			.buildQueue();
	}

	@Path("schem saveReal <name>")
	@Description("Save a schematic using the API")
	void schemSaveReal(String name) {
		worldedit.save(name, worldedit.getPlayerSelection(player()));
		send("Saved schematic " + name);
	}

	@Path("schem save <name> [--entities]")
	@Description("Save a schematic using commands")
	void schemSave(String name, @Switch(shorthand = 'e') boolean entities) {
		String copyCommand = "/copy";
		if (entities)
			copyCommand += " -e";

		GameMode originalGameMode = player().getGameMode();
		Location originalLocation = location().clone();
		Location location = worldedit.toLocation(worldedit.getPlayerSelection(player()).getMinimumPoint());
		player().setGameMode(GameMode.SPECTATOR);
		player().teleportAsync(location);
		runCommand("mcmd "+ copyCommand + " ;; wait 10 ;; /schem save " + name + " -f");
		Tasks.wait(20, () -> {
			player().teleportAsync(originalLocation);
			player().setGameMode(originalGameMode);
		});

		send("Saved schematic " + name);
	}

	@Path("schem paste <name> [--entities]")
	@Description("Paste a schematic")
	void schemPaste(String name, @Switch(shorthand = 'e') boolean entities) {
		worldedit.paster().file(name).entities(entities).at(location()).pasteAsync();
		send("Pasted schematic " + name);
	}

	private static final Map<UUID, Clipboard> clipboards = new HashMap<>();

	@Path("clipboard copy")
	@Description("Copy your selection to an internal clipboard")
	void clipboardCopy() {
		worldedit.copy(worldedit.getPlayerSelection(player()), worldedit.paster()).thenAccept(clipboard -> {
			clipboards.put(uuid(), clipboard);
			send(PREFIX + "Copied selection");
		});
	}

	@Path("clipboard paste")
	@Description("Paste your internal clipboard at your location")
	void clipboardPaste() {
		if (!clipboards.containsKey(uuid()))
			error("You have not copied anything");

		worldedit.paster().clipboard(clipboards.get(uuid())).at(location()).pasteAsync();
		send(PREFIX + "Pasted clipboard");
	}

	final List<Material> KEEP_GOING_DOWN = List.of(
		Material.WATER,
		Material.AIR,
		Material.RED_CONCRETE,
		Material.SEAGRASS,
		Material.TALL_SEAGRASS,
		Material.KELP
	);

	@Path("oceanflora <radius> [--kelpChance] [--seagrassChance] [--tallSeagrassChance] [--nothingWeight] [--minKelpAge] [--maxKelpAge]")
	@Description("Generate ocean flora")
	void oceanflora(
		@Arg(min = 1, max = 50) int radius,
		@Switch @Arg(value = "7.5", min = 1, max = 100) double kelpWeight,
		@Switch @Arg(value = "20", min = 1, max = 100) double seagrassWeight,
		@Switch @Arg(value = "7", min = 1, max = 100) double tallSeagrassWeight,
		@Switch @Arg(value = "60", min = 1, max = 100) double nothingWeight,
		@Switch @Arg(value = "12", min = 4, max = 25) int minKelpAge,
		@Switch @Arg(value = "24", min = 4, max = 25) int maxKelpAge

	) {
		final Map<Material, Double> weights = Map.of(
			Material.KELP, kelpWeight,
			Material.SEAGRASS, seagrassWeight,
			Material.TALL_SEAGRASS, tallSeagrassWeight,
			Material.WATER, nothingWeight
		);

		int count = 0;
		for (Block block : BlockUtils.getBlocksInRadius(block(), radius, 0, radius)) {
			final Location floor = world().getHighestBlockAt(block.getLocation()).getLocation();
			while (KEEP_GOING_DOWN.contains(floor.getBlock().getType()))
				floor.add(0, -1, 0);

			floor.add(0, 1, 0);

			if (floor.getBlock().getType() != Material.WATER)
				continue;

			final Material material = RandomUtils.getWeightedRandom(weights);
			++count;
			switch (material) {
				case KELP -> {
					int age = RandomUtils.randomInt(minKelpAge, maxKelpAge);
					while (age <= 25) {
						if (floor.getBlock().getType() != Material.WATER)
							break;

						final BlockData blockData = (age == 25 ? Material.KELP : Material.KELP_PLANT).createBlockData();
						if (blockData instanceof Waterlogged waterlogged)
							waterlogged.setWaterlogged(true);

						if (blockData instanceof Ageable ageable)
							ageable.setAge(age);

						age++;

						floor.getBlock().setBlockData(blockData);
						floor.add(0, 1, 0);
					}
				}
				case SEAGRASS -> {
					final BlockData blockData = material.createBlockData();
					if (blockData instanceof Waterlogged waterlogged)
						waterlogged.setWaterlogged(true);

					floor.getBlock().setBlockData(blockData);
				}
				case TALL_SEAGRASS -> {
					if (floor.clone().add(0, 1, 0).getBlock().getType() != Material.WATER) {
						--count;
						break;
					}

					final BiConsumer<Location, Half> consumer = (location, half) -> {
						final BlockData upper = Material.TALL_SEAGRASS.createBlockData();
						if (upper instanceof Bisected bisected)
							bisected.setHalf(half);

						if (upper instanceof Waterlogged waterlogged)
							waterlogged.setWaterlogged(true);

						location.getBlock().setBlockData(upper);
					};

					consumer.accept(floor, Half.BOTTOM);
					consumer.accept(floor.add(0, 1, 0), Half.TOP);
				}
				default -> --count;
			}
		}

		send(count + " blocks changed");
	}

}
