package gg.projecteden.nexus.features.minigames.commands.mechanics;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.api.BlockPartyWebSocketServer;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.BlockParty;
import gg.projecteden.nexus.features.minigames.mechanics.BlockParty.BlockPartySong;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.BlockPartyArena;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import tech.blastmc.lights.LxBoard;
import tech.blastmc.lights.cue.CueTimesBuilder;
import tech.blastmc.lights.map.Channel;
import tech.blastmc.lights.map.ChannelList;
import tech.blastmc.lights.type.base.SmartLight;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static com.mysql.cj.util.StringUtils.isNullOrEmpty;

@Permission(Group.ADMIN)
public class BlockPartyCommand extends CustomCommand {

	public BlockPartyCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("songs load")
	@Description("Load BlockParty songs from disk")
	void loadBlockPartySongs() {
		BlockParty.read(false);
		send(PREFIX + "Loaded &e" + BlockParty.songList.size() + " &3block party songs");
	}

	@Async
	@Path("songs list [page]")
	@Description("List BlockParty songs")
	void listBlockPartySongs(@Arg("1") int page) {
		new Paginator<BlockPartySong>()
			.values(BlockParty.songList.stream().sorted(Comparator.comparing(BlockPartySong::getTitle)).toList())
			.formatter((song, index) -> json(index + " &e" + song.getTitle() + " &7- &e" + song.getArtist()))
			.command("/blockparty songs list")
			.page(page)
			.send();
	}

	@Async
	@Path("songs removePadding")
	@Description("Remove padding from song metadata")
	void removePaddingInSongFiles() {
		BlockParty.read(true);
		send(PREFIX + "Done");
	}

	@Async
	@Path("songs removeSilence")
	@Description("Trim silent parts of songs")
	void removeSilence() {
		send(PREFIX + "Starting silence removal from all Block Party songs");
		BlockParty.removeSilence()
			.thenRun(() -> send(PREFIX + "Done"))
			.exceptionally(ex -> {
				MenuUtils.handleException(player(), PREFIX, ex);
				return null;
			});
	}

	@Async
	@Path("songs getMinimumLength")
	@Description("Calculate the minimum song length needed to finish a full BlockParty game")
	void getMinimumSongLength() {
		double seconds = 5;
		for (int i = 0; i < BlockParty.MAX_ROUNDS; i++)
			seconds += BlockParty.getRoundSpeed(i) + 5;
		send(PREFIX + "Songs should be at least &e" + seconds + " &3seconds long &e(" + ((int) seconds / 60) + "m" + (seconds % 60) + "s)");
	}

	@Async
	@Path("webUsers")
	@Description("View players connected to the BlockParty web client")
	void blockParty_webUsers() {
		String collect = BlockPartyWebSocketServer.getClients().keySet().stream().map(Nerd::of).map(Nerd::getNickname).collect(Collectors.joining(", "));
		if (isNullOrEmpty(collect))
			error("No users connected to the web player");

		send(PREFIX + collect);
	}

	private static boolean STOP;

	@Path("createStack [--delay] [--stop]")
	@Description("Create the BlockParty floor stack from /sw blockparty-floors")
	void blockParty_createStack(
		@Switch @Arg("5") int delay,
		@Switch @Arg("true") boolean clearStack,
		@Switch boolean stop
	) {
		if (stop) {
			STOP = true;
			send(PREFIX + "Stopping...");
			return;
		}

		STOP = false;
		var floors = worldguard().getRegion("blockparty_floors");
		var stack = worldguard().getRegion("blockparty_stack");
		var logo = worldguard().getRegion("blockparty_logo");

		int y = floors.getMinimumY();
		AtomicInteger stackY = new AtomicInteger(stack.getMinimumY());

		BiFunction<BlockVector2, BlockVector2, Integer> colors = (min, max) -> {
			List<Block> blocks = worldedit().getBlocks(worldguard().getRegion(min.toBlockVector3(y), max.toBlockVector3(y)));
			Set<Material> materials = new HashSet<>();
			for (var block : blocks) {
				materials.add(block.getType());
				if (materials.size() > 1) // We only care that there's more than 1
					return materials.size();
			}
			return materials.size();
		};

		BiPredicate<Integer, Integer> isOutOfBounds = (x, z) -> !floors.contains(x, y, z);

		BiConsumer<BlockVector2, BlockVector2> paste = (min, max) -> {
			worldedit().paster()
				.clipboard(min.toBlockVector3(y), max.toBlockVector3(y))
				.at(BlockVector3.at(stack.getMinimumPoint().x(), stackY.incrementAndGet(), stack.getMinimumPoint().z()))
				.pasteAsync();
		};

		BiConsumer<Integer, Integer> copy = (minX, minZ) -> {
			int maxX = minX + 47;
			int maxZ = minZ + 47;

			if (colors.apply(BlockVector2.at(minX, minZ), BlockVector2.at(maxX, maxZ)) == 1)
				return;

			paste.accept(BlockVector2.at(minX, minZ), BlockVector2.at(maxX, maxZ));
		};

		BlockVector3 stackMin = stack.getMinimumPoint().withY(stack.getMinimumY() + 1);
		BlockVector3 stackMax = stack.getMaximumPoint().withY(world().getMaxHeight());
		worldedit().setSelection(player(), stackMin, stackMax);
		worldedit().set(worldguard().getRegion(stackMin, stackMax), BlockTypes.AIR).thenRun(() -> {
			copy.accept(logo.getMinimumPoint().x(), logo.getMinimumPoint().z());

			AtomicInteger originX = new AtomicInteger(floors.getMinimumPoint().x() + 1);
			AtomicInteger originZ = new AtomicInteger(floors.getMinimumPoint().z() + 1);

			AtomicReference<Runnable> loop = new AtomicReference<>();
			loop.set(() -> {
				if (STOP)
					return;

				copy.accept(originX.get(), originZ.get());
				DotEffect.builder().player(player()).location(new Location(world(), originX.get(), y + 1, originZ.get())).start();

				originZ.addAndGet(49);
				if (isOutOfBounds.test(originX.get(), originZ.get())) {
					originX.addAndGet(49);
					originZ.set(floors.getMinimumPoint().z() + 1);

					if (isOutOfBounds.test(originX.get(), originZ.get())) {
						send(json(PREFIX + "Click to update real stack")
							.command("mcmd " +
								"tppos 1661.70 4 -4234.43 -90 0 buildadmin ;; " +
								"/copy ;; " +
								"wait 10 ;; " +
								"tppos 1724.70 34 -4335.37 -90 0 gameworld ;; " +
								"wait 5 ;; " +
								"/paste"
							));
						return;
					}
				}
				Tasks.wait(delay, loop.get());
			});

			Tasks.wait(delay, loop.get());
		});
	}

	@Path("lights add <channel>")
	void addLight(int channel) {
		LxBoard board = getBoardRequired();

		Location location = getTargetBlockRequired().getLocation();
		BlockFace face = player().getTargetBlockFace(500);

		SmartLight light = new SmartLight();
		light.spawn(location, face);

		Channel channelObj = new Channel();
		channelObj.getAddresses().add(light);
		channelObj.setId(channel);

		board.getChannels().add(channelObj);

		BlockPartyArena arena = getArenaRequired();
		arena.setLights(board);
		arena.write();
	}

	@Path("lights test [color]")
	void testLights(DyeColor color) {
		LxBoard board = getBoardRequired();

		if (color != null) {
			int cue = (color.ordinal() * 5) + 10;
			board.goToCue(cue, new CueTimesBuilder()
				.intensity(0)
				.color(0)
				.direction(1.25)
				.autoFollow(0)
				.build());
		}
		else {
			board.goToCue(5);
		}
	}

	@Path("lights out")
	void lightsOut() {
		getBoardRequired().goToCue(0);
	}

	@Confirm
	@Path("lights removeAllEntitiesFromChannels")
	void removeAllLights() {
		getBoardRequired().setChannels(new ChannelList());
		getArenaRequired().write();
	}

	@Path("lights debug [on/off]")
	void debugLights(boolean on) {
		getBoardRequired().setDebug(on);
	}

	@Path("lights updateCues")
	void updateCues() {
		LxBoard board = getBoardRequired();
		board.setCues(BlockPartyArena.getDefaultBoard().getCues());
		getArenaRequired().write();
	}

	private BlockPartyArena getArenaRequired() {
		Arena arena = ArenaManager.getFromLocation(location());
		if (!(arena instanceof BlockPartyArena))
			error("You must be inside of a BlockParty arena to run this command");

		return (BlockPartyArena) arena;
	}

	private LxBoard getBoardRequired() {
		LxBoard board = getArenaRequired().getLights();
		if (board == null)
			error("This arena's board is not currently setup");

		return board;
	}

}
