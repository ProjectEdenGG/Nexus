package gg.projecteden.nexus.features.commands.staff.admin;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.CompletableFutures;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Aliases("chunk")
@Permission(Group.ADMIN)
public class ChunksCommand extends CustomCommand {

	public ChunksCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("forceLoaded get")
	void forceLoaded_get() {
		send(PREFIX + "Current chunk &eis" + (location().getChunk().isForceLoaded() ? "" : " not") + " &3force loaded");
	}

	@Path("forceLoaded set <state>")
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
		} catch (Exception ex) {
			location().getChunk().setForceLoaded(state);
			complete.accept(1);
		}
	}

}
