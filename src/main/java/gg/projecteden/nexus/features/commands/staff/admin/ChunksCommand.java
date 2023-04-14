package gg.projecteden.nexus.features.commands.staff.admin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.CompletableFutures;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Aliases("chunk")
@Permission(Group.ADMIN)
public class ChunksCommand extends CustomCommand {

	public ChunksCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Description("List force loaded chunks in a world")
	void forceLoaded_list(@Optional("current") World world, @Optional("1") int page) {
		final var chunks = Arrays.stream(world.getLoadedChunks())
			.filter(Chunk::isForceLoaded)
			.toList();

		if (chunks.isEmpty())
			error("No force loaded chunks in world &e" + world.getName());

		send(PREFIX + "Force loaded chunks in &e" + world.getName());

		final BiFunction<@NotNull Chunk, String, JsonBuilder> formatter = (chunk, index) ->
			json("&3" + index + " &e" + chunk.getX() + ", " + chunk.getZ())
				.command("/tppos " + ((chunk.getX() << 4) + 8) + " 200 " + ((chunk.getZ() << 4) + 8) + " " + world.getName());

		paginate(chunks, formatter, "/chunks forceLoaded list " + world.getName(), page);
	}

	@Description("Check whether the chunk you are in is force loaded")
	void forceLoaded_get() {
		send(PREFIX + "Current chunk &eis" + (location().getChunk().isForceLoaded() ? "" : " not") + " &3force loaded");
	}

	@Description("Set whether all chunks in your selection or your current chunk are force loaded")
	void forceLoaded_set(boolean state) {
		Consumer<Integer> complete = count -> send(PREFIX + "Set &e" + count + plural(" chunk", count) + " &3to &e" + (state ? "" : "not ") + "be force loaded");

		try {
			final Region selection = worldedit().getPlayerSelection(player());
			List<CompletableFuture<Chunk>> tasks = selection.getChunks().stream()
				.map(vector -> world().getChunkAtAsync(vector.getX(), vector.getZ()))
				.toList();

			send(PREFIX + "Loading &e" + tasks.size() + " &3chunks...");

			CompletableFutures.allOf(tasks).thenAccept(chunks ->
				chunks.forEach(chunk ->
					chunk.setForceLoaded(state)));

			complete.accept(tasks.size());
		} catch (IncompleteRegionException ex) {
			location().getChunk().setForceLoaded(state);
			complete.accept(1);
		}
	}

}
