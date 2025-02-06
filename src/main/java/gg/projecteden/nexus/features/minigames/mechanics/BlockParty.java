package gg.projecteden.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.api.BlockPartyWebSocketServer;
import gg.projecteden.nexus.features.api.BlockPartyWebSocketServer.BlockPartyClientConnectedEvent;
import gg.projecteden.nexus.features.api.BlockPartyWebSocketServer.BlockPartyClientMessage;
import gg.projecteden.nexus.features.api.BlockPartyWebSocketServer.Song;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerStartSpectatingEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.BlockPartyMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.utils.PowerUpUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.blockparty.BlockPartyStatsService;
import gg.projecteden.nexus.models.blockparty.BlockPartyStatsUser.BlockPartyStats;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4d;
import org.joml.Matrix4f;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockParty extends TeamlessMechanic {

	public static List<BlockPartySong> songList = new ArrayList<>();
	private static final String FOLDER = "plugins/Nexus/minigames/blockparty/music/";
	public static final int MAX_ROUNDS = 25;
	private static final CooldownService COOLDOWN_SERVICE = new CooldownService();

	// region Minigame framework
	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemBuilder(Material.MUSIC_DISC_CAT).build();
	}

	@Override
	public @NotNull String getName() {
		return "Block Party";
	}

	@Override
	public @NotNull String getDescription() {
		return "Dance to music, and stand on the correct block";
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);
		Match match = event.getMatch();

		selectSongsForVoting(match);
		setupColorChangingAreas(match).thenRun(() -> {
			pasteLogoFloor(match);
		});
		updateEqWalls(match, 0);
		resetDiscoBalls(match);
		startDiscoBallAnimation(match);

		setupPowerUps(match);
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		super.onJoin(event);

		Minigamer minigamer = event.getMinigamer();
		Player player = minigamer.getOnlinePlayer();

		if (!(minigamer.getMatch().getMechanic() instanceof BlockParty))
			return;

		if (!event.getMatch().isStarted()) {
			ItemStack menuItem = new ItemBuilder(Material.MUSIC_DISC_CAT).name("Vote for a Song!").build();
			player.getInventory().setItem(0, menuItem);
		}

		sendMusicLink(minigamer);
	}

	@EventHandler
	public void onSpectatingJoin(MinigamerStartSpectatingEvent event) {
		Minigamer minigamer = event.getMinigamer();

		if (!(minigamer.getMatch().getMechanic() instanceof BlockParty))
			return;

		sendMusicLink(minigamer);

		BlockPartyMatchData matchData = event.getMatch().getMatchData();
		if (matchData.getSong() != null) {
			BlockPartySong song = matchData.getSong();
			BlockPartyClientMessage.to(minigamer.getUniqueId()).song(
				new Song(
					song.title,
					song.artist,
					0,
					song.getUrl()
				)
			).send();
			if (matchData.isPlaying())
				BlockPartyClientMessage.to(minigamer.getUniqueId()).play().send();
			if (matchData.getBlock() != null)
				BlockPartyClientMessage.to(minigamer.getUniqueId()).block(matchData.getBlock().name()).send();
		}
	}

	private void sendMusicLink(Minigamer minigamer) {
		if (BlockPartyWebSocketServer.isConnected(minigamer.getUniqueId()))
			return;

		minigamer.getMatch().getTasks().wait(30, () -> {
			minigamer.tell(new JsonBuilder("&e&lClick Here &3to listen to the music in your browser!")
				.url("https://projecteden.gg/blockparty?uuid=" + minigamer.getUniqueId())
				.hover("&3Open in your browser")
				.build());
		});
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		selectSong(event.getMatch());
		setSong(event.getMatch());
		startActionBarTask(event.getMatch());
		startEqAnimation(event.getMatch());

		event.getMatch().getAliveMinigamers().forEach(Minigamer::startTimeTracking);
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		super.onBegin(event);
		waitWithMusic(event.getMatch());
	}

	@Override
	public boolean shouldBeOver(@NotNull Match match) {
		if (match.getAliveMinigamers().isEmpty()) {
			checkWin(match);
			return true;
		}
		return false;
	}

	@Override
	public void announceWinners(@NotNull Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		if (matchData.getWinners() != null)
			Minigames.broadcast(
				StringUtils.asOxfordList(matchData.getWinners().stream().map(Minigamer::getColoredName).toList(), ", ") +
					(matchData.getWinners().size() > 1 ? " &3have tied" : " &3has won") +
					" in &eBlock Party"
			);
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		super.onEnd(event);

		List<UUID> uuids = event.getMatch().getMinigamersAndSpectators().stream().map(Minigamer::getUuid).toList();
		BlockPartyClientMessage.to(uuids).stop().send();

		pasteColorChanging(event.getMatch());
		updateEqWalls(event.getMatch(), 0);

		stopDiscoBallAnimation(event.getMatch());

		reportStats(event.getMatch());
	}

	@Override
	public @NotNull String getScoreboardTitle(@NotNull Match match) {
		return "&e&lBlock Party";
	}

	@Override
	public @NotNull GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public boolean canUseBlock(@NotNull Minigamer minigamer, @NotNull Block block) {
		return false;
	}

	@Override
	public boolean useScoreboardNumbers(Match match) {
		return false;
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Match match) {
		if (!match.isStarted())
			return super.getScoreboardLines(match);

		BlockPartyMatchData matchData = match.getMatchData();

		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
		lines.put("&e", Integer.MIN_VALUE);
		lines.put("&e&lGame Info", Integer.MIN_VALUE);
		lines.put("&3Round: &e" + (matchData.getRound() + 1) + "/" + MAX_ROUNDS, Integer.MIN_VALUE);
		lines.put("&3Speed: &e" + getRoundSpeed(matchData.getRound()) + "s", Integer.MIN_VALUE);
		lines.put("&e&e", Integer.MIN_VALUE);
		lines.put("&e&lDancers Left", Integer.MIN_VALUE);
		lines.put("&3" + match.getAliveMinigamers().size(), Integer.MIN_VALUE);
		lines.put("&e&e&e", Integer.MIN_VALUE);
		lines.put("&e&lBlock", Integer.MIN_VALUE);

		ColorType type = ColorType.of(matchData.getBlock());
		if (type == null)
			lines.put("", Integer.MIN_VALUE);
		else {
			ChatColor chatColor = type == ColorType.BLACK ? ChatColor.GRAY : type.getChatColor();
			lines.put(chatColor + StringUtils.camelCase(type.name()), Integer.MIN_VALUE);
		}

		return lines;
	}

	@EventHandler
	public void onDamage(MinigamerDamageEvent event) {
		if (!event.getMinigamer().isPlaying(this))
			return;
		event.setCancelled(true);
	}
	// endregion

	// region Floor pasting
	private void pasteLogoFloor(Match match) {
		pasteFloor(match, 0);
	}

	private void pasteNewFloor(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		matchData.incRound();
		if (matchData.getRound() % 2 == 1 && (matchData.getRound() + 1) < MAX_ROUNDS)
			match.broadcast("&3Round speed is now &e" + getRoundSpeed(matchData.getRound()) + "s");
		if ((matchData.getRound() + 1) > MAX_ROUNDS) {
			matchData.setWinners(match.getAliveMinigamers());
			end(match);
			return;
		}
		pasteFloor(match, RandomUtils.randomInt(1, matchData.countFloors()));
		playSong(match);
		waitWithMusic(match);

		startEqAnimation(match);
		setBlockInHand(match, null);
		spawnPowerUp(match);
	}

	private void pasteFloor(Match match, int index) {
		BlockPartyMatchData matchData = match.getMatchData();
		match.getArena().worldedit().paster("Block Party")
			.clipboard(matchData.getFloorInStack(index))
			.at(matchData.getPasteRegion().getMinimumPoint())
			.build();

		BlockPartyClientMessage.to(getListenerUUIDs(match)).block("").send();
		matchData.setBlock(null);

		setArenaColor(match);
		startDiscoBallAnimation(match);
	}


	private void waitWithMusic(Match match) {
		match.getTasks().wait(TimeUtils.TickTime.SECOND.x(5), () -> {
			selectColor(match);
			doCountdown(match);
		});
	}

	private void selectColor(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		matchData.setBlock(getRandomFloorColor(match));

		BlockPartyClientMessage.to(getListenerUUIDs(match)).block(matchData.getBlock().name()).send();
		setArenaColor(match);
		setBlockInHand(match, matchData.getBlock());
	}

	private Material getRandomFloorColor(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		List<Material> types = match.getArena().worldedit().getBlocks(matchData.getPasteRegion())
			.stream().map(Block::getType).distinct().toList();
		return RandomUtils.randomElement(types);
	}

	private void setBlockInHand(Match match, Material block) {
		if (block != null) {
			ItemBuilder item = new ItemBuilder(block).name("");
			match.getAliveMinigamers().forEach(minigamer -> minigamer.getPlayer().getInventory().setItem(0, item.build()));
		}
		else {
			match.getAliveMinigamers().forEach(minigamer -> minigamer.getPlayer().getInventory().setItem(0, null));
		}
	}

	private void doCountdown(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		double countdownTime = getRoundSpeed(matchData.getRound());
		Tasks.Countdown.builder()
				.duration(TimeUtils.TickTime.SECOND.x(countdownTime))
				.onStart(() -> sendCountdownActionBar(match, countdownTime))
				.onSecond(i -> sendCountdownActionBar(match, i))
				.onComplete(() -> {
					pauseSong(match);
					clearFloor(match);
				}).start();
	}

	private void sendCountdownActionBar(Match match, double time) {
		BlockPartyMatchData matchData = match.getMatchData();
		ColorType color = ColorType.of(matchData.getBlock());

		String chatColor = "&" + (color == ColorType.BLACK ? "#272727" : color.getBukkitColor().asHexString());
		JsonBuilder builder = new JsonBuilder();
		for (int i = 0; i < Math.ceil(time); i++)
			builder.next(chatColor + "■");
		builder.next(" &f&l" + color.name().replace("_", " ") + " ");
		for (int i = 0; i < Math.ceil(time); i++)
			builder.next(chatColor + "■");

		matchData.setActionBarMessage(builder);

		if (time == 3.0)
			pling(match, .7f);
		if (time == 2.0)
			pling(match, .6f);
		if (time == 1.0 || getRoundSpeed(matchData.getRound()) < 1)
			pling(match, .5f);
	}

	private void startActionBarTask(@NonNull Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		match.getTasks().repeat(0, 1, () -> {
			if (matchData.getActionBarMessage() != null)
				match.getMinigamersAndSpectators().forEach(minigamer -> minigamer.getPlayer().sendActionBar(matchData.getActionBarMessage()));
		});
	}

	private void pling(Match match, float pitch) {
		match.getMinigamersAndSpectators().forEach(minigamer -> minigamer.getPlayer().playSound(minigamer.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, pitch));
	}

	public static double getRoundSpeed(int round) {
		return Math.max(6 - 0.5 * ((round + 1) / 2), 0.5);
	}

	private void clearFloor(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();

		matchData.setAliveAtStartOfRound(match.getAliveMinigamers());

		matchData.getPasteRegion().forEach(vec3 -> {
			Block block = match.getArena().worldedit().toLocation(vec3).getBlock();
			if (block.getType() != matchData.getBlock())
				block.setType(Material.AIR);
		});
		stopDiscoBallAnimation(match);
		stopEqAnimation(match);
		removePowerUp(match);

		waitAfterClear(match);
	}

	private void waitAfterClear(Match match) {
		match.getTasks().wait(TimeUtils.TickTime.SECOND.x(3), () -> {
			if (checkWin(match)) {
				match.end();
				return;
			}
			pasteNewFloor(match);
		});
	}

	private boolean checkWin(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		match.getAliveMinigamers().forEach(minigamer -> {
			if (minigamer.getLocation().y() < matchData.getPasteRegion().getMinimumPoint().y())
				kill(minigamer);
		});

		if (match.getAliveMinigamers().isEmpty()) {
			if (match.getAllMinigamers().size() > 1)
				matchData.setWinners(matchData.getAliveAtStartOfRound());
			return true;
		}
		else if (match.getAliveMinigamers().size() == 1 && match.getAllMinigamers().size() != 1) {
			matchData.setWinners(match.getAliveMinigamers());
			return true;
		}
		return false;
	}
	// endregion

	// region Powerups
	public void setupPowerUps(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();

		ItemStack item = new ItemBuilder(CustomMaterial.GUI_ARROW_UP).dyeColor("#6BD0FF").build();
		Consumer<Minigamer> consumer = minigamer -> {
			BlockPartyPowerUp powerUp = RandomUtils.randomElement(BlockPartyPowerUp.values());
			ItemStack powerUpItem = powerUp.getItem();

			if (Nullables.isNullOrAir(minigamer.getPlayer().getInventory().getItem(0))) {
				minigamer.getPlayer().getInventory().setItem(0, new ItemBuilder(CustomMaterial.INVISIBLE).build());
				minigamer.getPlayer().getInventory().addItem(powerUpItem);
				minigamer.getPlayer().getInventory().setItem(0, null);
			}
			else
				minigamer.getPlayer().getInventory().addItem(powerUpItem);

			minigamer.getPlayer().playSound(minigamer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.5F);
			minigamer.tell("&3You picked up a &e" + StringUtils.camelCase(powerUp.name()) + " &3power up!");

			matchData.getPowerUpsCollected().put(minigamer.getUniqueId(), matchData.getPowerUpsCollected().getOrDefault(minigamer.getUniqueId(), 0) + 1);
		};

		PowerUpUtils.PowerUp powerUp = new PowerUpUtils.PowerUp(null, true, item, consumer);

		matchData.setPowerUpUtils(new PowerUpUtils(match, List.of(powerUp)));
	}

	public void spawnPowerUp(Match match) {
		if (RandomUtils.chanceOf(50))
			return;

		BlockPartyMatchData matchData = match.getMatchData();
		Location loc = match.getArena().worldguard().getRandomBlock(matchData.getPasteRegion()).getLocation().clone().toCenterLocation();
		matchData.setPowerUp(matchData.getPowerUpUtils().spawn(loc, false, null));
		match.broadcast("&e&lA power up has spawned!");
	}

	public void removePowerUp(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		if (matchData.getPowerUp() != null) {
			matchData.getPowerUp().remove();
			matchData.setPowerUp(null);
		}
	}

	@EventHandler
	public void onClickPowerup(PlayerInteractEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());

		if (!minigamer.isPlaying(this))
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (Nullables.isNullOrAir(event.getItem()))
			return;

		ItemStack item = event.getItem();
		for (BlockPartyPowerUp powerUp : BlockPartyPowerUp.values()) {
			if (!ItemUtils.isModelMatch(powerUp.getItem(), item))
				continue;

			if (!COOLDOWN_SERVICE.check(minigamer.getUuid(), "blockparty-powerup-" + powerUp.name().toLowerCase(), 10))
				return;

			event.setCancelled(true);

			item.subtract();
			powerUp.execute(minigamer, minigamer.getMatch());

			BlockPartyMatchData matchData = minigamer.getMatch().getMatchData();
			matchData.getPowerUpsUsed().put(minigamer.getUniqueId(), matchData.getPowerUpsUsed().getOrDefault(minigamer.getUniqueId(), 0) + 1);
			return;
		}
	}

	@EventHandler
	public void onSnowballLand(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Snowball))
			return;

		Location location;
		if (event.getHitBlock() != null)
			location = event.getHitBlock().getLocation();
		else if (event.getHitEntity() != null)
			location = event.getHitEntity().getLocation();
		else
			return;

		Arena arena = ArenaManager.getFromLocation(location);
		if (arena == null)
			return;

		if (arena.getMechanicType() != MechanicType.BLOCK_PARTY)
			return;

		Match match = MatchManager.find(arena);
		if (match == null)
			return;

		if (!match.isStarted() || match.isEnded())
			return;

		BlockPartyMatchData matchData = match.getMatchData();
		if (!matchData.getPasteRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
			location.setY(matchData.getPasteRegion().getMinimumY());
			if (!matchData.getPasteRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
				return;
		}

		List<Location> locations = new ArrayList<>() {{ add(location); }};
		for (BlockFace face : faces)
			if (RandomUtils.chanceOf(50))
				locations.add(location.getBlock().getRelative(face).getLocation());
		locations.removeIf(loc -> loc.getBlock().getType() == Material.AIR);

		Material material;
		if (matchData.getBlock() == null)
			material = getRandomFloorColor(match);
		else
			material = matchData.getBlock();

		locations.forEach(loc -> loc.getBlock().setType(material, false));
	}

	@EventHandler
	public void onPaintBucketLand(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof ThrownPotion potion))
			return;

		if (!ItemUtils.isModelMatch(potion.getItem(), BlockPartyPowerUp.COLOR_SPLASH.getItem()))
			return;

		Location location;
		if (event.getHitBlock() != null)
			location = event.getHitBlock().getLocation();
		else if (event.getHitEntity() != null)
			location = event.getHitEntity().getLocation();
		else
			return;

		Arena arena = ArenaManager.getFromLocation(location);
		if (arena == null)
			return;

		if (arena.getMechanicType() != MechanicType.BLOCK_PARTY)
			return;

		Match match = MatchManager.find(arena);
		if (match == null)
			return;

		if (!match.isStarted() || match.isEnded())
			return;

		BlockPartyMatchData matchData = match.getMatchData();
		if (!matchData.getPasteRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
			location.setY(matchData.getPasteRegion().getMinimumY());
			if (!matchData.getPasteRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
				return;
		}

		List<Location> locations = spawnColorSplash(matchData, location, location, new ArrayList<>(), 5);
		locations.removeIf(loc -> loc.distance(location.clone().toCenterLocation()) > 4 && RandomUtils.chanceOf(75));
		locations.removeIf(loc -> loc.distance(location.clone().toCenterLocation()) > 3 && RandomUtils.chanceOf(50));

		Material material;
		if (matchData.getBlock() == null)
			material = getRandomFloorColor(match);
		else
			material = matchData.getBlock();

		locations.forEach(loc -> loc.getBlock().setType(material, false));
	}

	BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	private List<Location> spawnColorSplash(BlockPartyMatchData matchData, Location origin, Location current, List<Location> checked, int maxDepth)  {
		if (!matchData.getPasteRegion().contains(current.getBlockX(), current.getBlockY(), current.getBlockZ()))
			return checked;

		if (origin.toCenterLocation().distanceSquared(current.toCenterLocation()) > maxDepth * maxDepth)
			return checked;

		if (checked.contains(current.toBlockLocation()))
			return checked;

		checked.add(current.clone().toBlockLocation());
		for (BlockFace blockFace : faces) {
			spawnColorSplash(matchData, origin, current.getBlock().getRelative(blockFace).getLocation(), checked, maxDepth);
		}
		return checked;
	}

	public enum BlockPartyPowerUp {
		LEAP {
			@Getter
			final ItemStack item = new ItemBuilder(Material.PAPER).modelId(7413).name("&eLeap").build();

			@Override
			void execute(Minigamer minigamer, Match match) {
				minigamer.getPlayer().setVelocity(minigamer.getLocation().getDirection().normalize().multiply(1.2f));
				minigamer.getPlayer().playSound(minigamer.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);
			}
		},
		COLOR_SPLASH {
			@Getter
			final ItemStack item = new ItemBuilder(Material.PAPER).modelId(7414).name("&eColor Splash").build();

			@Override
			void execute(Minigamer minigamer, Match match) {
				Vector vector = minigamer.getLocation().getDirection().normalize().multiply(0.5f);
				Location spawnLoc = minigamer.getPlayer().getEyeLocation().clone().add(vector);

				spawnLoc.getWorld().spawn(spawnLoc, ThrownPotion.class, potion -> {
					potion.setItem(item);
					potion.setVelocity(vector);
				});
			}
		},
		JUMP_POTION {
			@Getter
			final ItemStack item = new ItemBuilder(Material.PAPER).modelId(7415).name("&eJump Potion").build();
			final PotionEffect potion = new PotionEffect(PotionEffectType.JUMP_BOOST, (int) TimeUtils.TickTime.SECOND.x(15), 3, true, true);

			@Override
			void execute(Minigamer minigamer, Match match) {
				minigamer.getPlayer().addPotionEffect(potion);
				minigamer.getPlayer().playSound(minigamer.getLocation(), Sound.ITEM_BUCKET_FILL, 1, 1);
			}
		},
		SPEED_POTION {
			@Getter
			final ItemStack item = new ItemBuilder(Material.PAPER).modelId(7416).name("&eSpeed Potion").build();
			final PotionEffect potion = new PotionEffect(PotionEffectType.SPEED, (int) TimeUtils.TickTime.SECOND.x(8), 4, true, true);

			@Override
			void execute(Minigamer minigamer, Match match) {
				minigamer.getPlayer().addPotionEffect(potion);
				minigamer.getPlayer().playSound(minigamer.getLocation(), Sound.ITEM_BUCKET_FILL, 1, 1);
			}
		},
		COLOR_STORM {
			@Getter
			final ItemStack item = new ItemBuilder(Material.PAPER).modelId(7417).name("&eColor Storm").build();

			@Override
			void execute(Minigamer minigamer, Match match) {
				BlockPartyMatchData matchData = match.getMatchData();
				for (int i = 0; i < 100; i++)
					match.getTasks().wait(i / 5, () -> {
						Location location = match.getArena().worldguard().getRandomBlock(matchData.getPasteRegion()).getLocation().clone().add(0, 15, 0);
						location.getWorld().spawn(location, Snowball.class);
					});
			}
		},
		;

		abstract ItemStack getItem();

		abstract void execute(Minigamer minigamer, Match match);
	}
	// endregion

	// region Arena changes
	public CompletableFuture<Void> setupColorChangingAreas(Match match) {
		return pasteColorChanging(match).thenAccept($ -> {
			BlockPartyMatchData matchData = match.getMatchData();
			Arena arena = match.getArena();
			arena.getRegionsLike("color").forEach(region -> {
				arena.worldedit().getBlocks(region).forEach(block -> {
					if (block.getType() == Material.BLACK_GLAZED_TERRACOTTA)
						matchData.getColorChangingBlocks().add(block.getLocation());
				});
			});
			setArenaColor(match);
		});
	}

	public CompletableFuture<Void> pasteColorChanging(Match match) {
		return match.getArena().worldedit().paster()
			.at(match.getArena().getRegion().getMinimumPoint())
			.file(match.getArena().getSchematicName("color"))
			.air(false)
			.build();
	}

	public void setArenaColor(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		BlockData data = matchData.getBlock() == null ? Material.GRAY_CONCRETE_POWDER.createBlockData() : matchData.getBlock().createBlockData();
		matchData.getColorChangingBlocks().forEach(block -> {
			block.getBlock().setBlockData(data, false);
		});
	}

	public void startEqAnimation(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		if (matchData.getEqTaskId() > 0)
			return;
		matchData.setEqTaskId(match.getTasks().repeat(5, 5, () -> {
			int frame = matchData.getEqCurrentFrame() + 1;

			if (frame > getMaxFrames(match))
				frame = 0;

			updateEqWalls(match, frame);

			matchData.setEqCurrentFrame(frame);
		}));
	}

	private void updateEqWalls(Match match, int frame) {
		Region frameRegion = match.getArena().getRegion("eq_frames");
		BlockVector3 min, max;
		var diff = frameRegion.getMaximumPoint().subtract(frameRegion.getMinimumPoint());
		if (diff.x() > diff.z()) {
			min = frameRegion.getMinimumPoint().withX(frameRegion.getMinimumPoint().x() + (frame * 2));
			max = frameRegion.getMaximumPoint().withX(frameRegion.getMinimumPoint().x() + (frame * 2) + 1);
		}
		else {
			min = frameRegion.getMinimumPoint().withZ(frameRegion.getMinimumPoint().z() + (frame * 2));
			max = frameRegion.getMaximumPoint().withZ(frameRegion.getMinimumPoint().z() + (frame * 2) + 1);
		}

		CuboidRegion clipboard = new CuboidRegion(match.getArena().worldedit().worldEditWorld, min, max);
		AtomicInteger rotation = new AtomicInteger(90);

		match.getArena().getRegionsLike("eq_paste").stream()
			.sorted(Comparator.comparing(ProtectedRegion::getId))
			.forEachOrdered(region -> {
				match.getArena().worldedit().paster()
					.at(region.getMinimumPoint())
					.clipboard(clipboard)
					.transform(new AffineTransform().rotateX(0).rotateY(rotation.getAndAdd(90)).rotateZ(0))
					.build();
			});

	}

	private int getMaxFrames(Match match) {
		Arena arena = match.getArena();
		Region region = arena.getRegion("eq_frames");
		var diff = region.getMaximumPoint().subtract(region.getMinimumPoint());
		int length = Math.min(diff.x(), diff.z());
		return length / 2;
	}

	public void stopEqAnimation(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getEqTaskId());
		matchData.setEqTaskId(0);
	}

	public void resetDiscoBalls(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		matchData.getDiscoBalls().forEach(display -> {
			display.setInterpolationDuration(0);
			Transformation transformation = display.getTransformation();
			transformation.getLeftRotation().set(new AxisAngle4d(0, 0, 1, 0));
			display.setTransformation(transformation);
		});
	}

	int timeForOneRotation = (int) TimeUtils.TickTime.SECOND.x(10);

	public void startDiscoBallAnimation(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();

		if (matchData.getDiscoBallTaskId() > 0)
			return;

		matchData.setDiscoBallTaskId(match.getTasks().repeat(1,2, () -> {
			matchData.getDiscoBalls().forEach(display -> {
				Transformation transformation = display.getTransformation();
				Matrix4f matrix = display.getTransformation().getLeftRotation().get(new Matrix4f());
				matrix.scale(transformation.getScale().x());

				display.setTransformationMatrix(matrix.rotateY(((float) Math.toRadians(360 / (timeForOneRotation * 2d))) + 0.01F));
				display.setInterpolationDelay(0);
				display.setInterpolationDuration(2);
			});
		}));
	}

	public void stopDiscoBallAnimation(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getDiscoBallTaskId());
		matchData.setDiscoBallTaskId(0);
	}
	// endregion

	//region Song playing/selection
	private List<UUID> getListenerUUIDs(Match match) {
		return match.getMinigamersAndSpectators().stream().map(Minigamer::getUniqueId).collect(Collectors.toList());
	}

	public static BlockPartySong getSong(String id) {
		return songList.stream().filter(song -> song.fileName.equalsIgnoreCase(id)).findFirst().orElse(null);
	}

	private void selectSongsForVoting(Match match) {
		int songCount = Math.min(5, songList.size());

		List<BlockPartySong> selectedSongs = new ArrayList<>();

		List<BlockPartySong> random = new ArrayList<>(songList);
		for (int i = 0; i < songCount; i++) {
			Collections.shuffle(random);
			selectedSongs.add(random.remove(0));
		}

		((BlockPartyMatchData) match.getMatchData()).setPossibleSongs(selectedSongs);
	}

	private void selectSong(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		String id = matchData.getVotes().values().stream()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
			.entrySet().stream()
			.max(Map.Entry.comparingByValue())
			.map(Map.Entry::getKey)
			.orElse(RandomUtils.randomElement(matchData.getPossibleSongs()).fileName);

		BlockPartySong song = getSong(id);
		if (song == null)
			song = RandomUtils.randomElement(songList);
		matchData.setSong(song);
		match.broadcast("&3Now playing: &e&l" + song.title + " &3by &e&l" + song.artist);
	}

	private void setSong(Match match) {
		BlockPartySong song = ((BlockPartyMatchData) match.getMatchData()).getSong();

		BlockPartyClientMessage.to(getListenerUUIDs(match))
				.song(new Song(
					song.title,
					song.artist,
					0,
					song.getUrl()
				))
				.play()
				.send();

		BlockPartyMatchData matchData = match.getMatchData();
		matchData.setPlayTime(LocalDateTime.now());
	}

	private void playSong(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		matchData.setPlayTime(LocalDateTime.now());

		BlockPartyClientMessage.to(getListenerUUIDs(match)).play().send();
		matchData.setActionBarMessage(new JsonBuilder("&a♫ &f&lDANCE &a♫"));
	}

	private void pauseSong(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		double seconds = Duration.between(matchData.getPlayTime(), LocalDateTime.now()).toSeconds();
		matchData.setSongTimeInSeconds(matchData.getSongTimeInSeconds() + seconds);

		BlockPartyClientMessage.to(getListenerUUIDs(match)).pause().send();
		matchData.setActionBarMessage(new JsonBuilder("&c&l✖ &f&lSTOP &c&l✖"));
	}
	//endregion

	// region Voting
	@EventHandler
	public void openVoteMenu(PlayerInteractEvent event) {
		if (event.getItem() == null) return;
		if (event.getItem().getType() != Material.MUSIC_DISC_CAT) return;
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event)) return;

		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getWorld())) return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isInLobby(this)) return;

		Match match = minigamer.getMatch();
		if (match.isStarted()) return;

		if (((BlockPartyMatchData) match.getMatchData()).getPossibleSongs().isEmpty())
			return;

		new SongVoteMenu(match).open(player);
	}

	@Rows(6)
	@Title("Vote for a song")
	@AllArgsConstructor
	public static class SongVoteMenu extends InventoryProvider {

		private BlockPartyMatchData matchData;

		private SongVoteMenu(Match match) {
			this.matchData = match.getMatchData();
		}

		private static final Material[] colors = {
			Material.PURPLE_STAINED_GLASS_PANE,
			Material.LIME_STAINED_GLASS_PANE,
			Material.RED_STAINED_GLASS_PANE,
			Material.LIGHT_BLUE_STAINED_GLASS_PANE,
			Material.YELLOW_STAINED_GLASS_PANE,
		};

		private static final Material[] records = {
			Material.MUSIC_DISC_MALL,
			Material.MUSIC_DISC_CAT,
			Material.MUSIC_DISC_BLOCKS,
			Material.MUSIC_DISC_WAIT,
			Material.MUSIC_DISC_13,
		};

		@Override
		public void init() {
			addCloseItem();

			AtomicInteger col = new AtomicInteger(2);

			matchData.getPossibleSongs().forEach(song -> {
				contents.set(5, col.get(), ClickableItem.of(
					getItem(song, true, col.get(), getViewer().getPlayer()),
					e -> vote(e.getPlayer(), song))
				);
				contents.set(4, col.get(), ClickableItem.of(
					getItem(song, false, col.get(), getViewer().getPlayer()),
					e -> vote(e.getPlayer(), song))
				);
				if (!matchData.getVotes().isEmpty()) {
					int extra = (int) Math.ceil(((double) matchData.getVotes().values().stream()
						.filter(vote -> vote.equalsIgnoreCase(song.fileName)).count()
						/ matchData.getVotes().size()) * 3);
					for (int i = 0; i < extra; i++) {
						contents.set(3 - i, col.get(), ClickableItem.of(
							getItem(song, false, col.get(), getViewer().getPlayer()),
							e -> vote(e.getPlayer(), song))
						);
					}
				}
				col.getAndIncrement();
			});
		}

		public ItemStack getItem(BlockPartySong song, boolean main, int col, Player player) {
			ItemBuilder builder = new ItemBuilder(main ? records[col - 2] : colors[col - 2]);
			builder
				.name("&e&l" + song.title)
				.itemFlags(ItemBuilder.ItemFlags.HIDE_ALL)
				.lore(
					"&3Artist: &e" + song.artist,
					"",
					"&3Votes: &e" + matchData.getVotes().values().stream().filter(val -> val.equalsIgnoreCase(song.fileName)).count()
				);
			if (matchData.getVotes().containsKey(player.getUniqueId()) && matchData.getVotes().get(player.getUniqueId()).equalsIgnoreCase(song.fileName))
				builder.glow();
			return builder.build();
		}

		private void vote(Player player, BlockPartySong song) {
			matchData.getVotes().put(player.getUniqueId(), song.fileName);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.5F);
			refresh();
		}

	}
	// endregion

	// region Read song files
	@AllArgsConstructor
	public static class BlockPartySong {
		String title;
		String artist;
		String fileName;

		public String getUrl() {
			return "https://cdn.projecteden.gg/blockparty/music/" + fileName;
		}
	}

	@SneakyThrows
	public static void read(boolean removePadding) {
		Minigames.debug("Loading Block Party Music");
		Path path = Paths.get(FOLDER);
		File file = path.toFile();
		if (!file.exists()) file.createNewFile();
		songList.clear();
		try (Stream<Path> paths = Files.walk(path)) {
			paths.forEach(filePath -> {
				try {
					if (!Files.isRegularFile(filePath)) return;

					String name = filePath.getFileName().toString();
					if (name.startsWith(".")) return;
					if (!name.endsWith(".mp3")) return;

					read(name, removePadding);
				} catch (Exception ex) {
					Nexus.severe("An error occurred while trying to read block party music file: " + filePath.getFileName().toFile(), ex);
					if (Nexus.isDebug())
						ex.printStackTrace();
				}
			});
		} catch (Exception ex) {
			Nexus.severe("An error occurred while trying to read block party music files: " + ex.getMessage());
			if (Nexus.isDebug())
				ex.printStackTrace();
		}
	}

	@SneakyThrows
	public static void removeSilence() {
		Path path = Paths.get(FOLDER);
		File file = path.toFile();
		if (!file.exists()) file.createNewFile();
		songList.clear();
		try (Stream<Path> paths = Files.walk(path)) {
			paths.forEach(filePath -> {
				try {
					if (!Files.isRegularFile(filePath)) return;

					String name = filePath.getFileName().toString();
					if (name.startsWith(".")) return;
					if (!name.endsWith(".mp3")) return;

					Nexus.log("Processing " + file.getAbsolutePath() + "/" + name);

					ProcessBuilder pb = new ProcessBuilder(
						"ffmpeg", "-y", "-i", file.getAbsolutePath() + "/" + name,
						"-af", "silenceremove=start_periods=1:start_threshold=-30dB",
						file.getAbsolutePath() + "/silenceRemoved/" + name
					);
					pb.inheritIO();
					pb.start();
				} catch (Exception ex) {
					Nexus.severe("An error occurred while trying to remove the silence from bp music file: " + filePath.getFileName().toFile(), ex);
					if (Nexus.isDebug())
						ex.printStackTrace();
				}
			});
		} catch (Exception ex) {
			Nexus.severe("An error occurred while trying to read block party music files: " + ex.getMessage());
			if (Nexus.isDebug())
				ex.printStackTrace();
		}
	}

	@SneakyThrows
	private static void read(String name, boolean removePadding) {
		String path = FOLDER + name;
		Minigames.debug("Reading file: " + path);

		if (removePadding) {
			File mp3File = new File(path);
			TagOptionSingleton.getInstance().setId3v2PaddingWillShorten(true);
			AudioFile audioFile = AudioFileIO.read(mp3File);
			audioFile.commit();
		}

		File mp3File = new File(path);
		AudioFile audioFile = AudioFileIO.read(mp3File);

		Tag tag = audioFile.getTag();
		if (tag == null) {
			Minigames.debug("No metadata found.");
			return;
		}

		String title = tag.getFirst(FieldKey.TITLE);
		String artist = StringUtils.asOxfordList(Arrays.stream(tag.getFirst(FieldKey.ARTIST).split("/")).toList(), ", ");


		BlockPartySong song = new BlockPartySong(title, artist, name);
		songList.add(song);

		Minigames.debug("Loaded BP song: %s by %s (%s)".formatted(title, artist, name));
	}
	// endregion

	@EventHandler
	public void onWebClientJoin(BlockPartyClientConnectedEvent event) {
		MatchManager.getAll().stream()
			.filter(match -> match.getMechanic() instanceof BlockParty)
			.forEach(match -> {
				BlockPartyMatchData matchData = match.getMatchData();
				if (matchData.getSong() != null) {
					BlockPartySong song = matchData.getSong();
					event.addSong(
						getListenerUUIDs(match),
						song.title,
						song.artist,
						matchData.getSongTimeInSeconds(),
						song.getUrl(),
						matchData.isPlaying()
					);
				}
				if (matchData.getBlock() != null) {
					event.setBlock(getListenerUUIDs(match), matchData.getBlock().name());
				}
			});

		BlockPartySong song = RandomUtils.randomElement(songList);
		if (song != null) {
			event.addSong(
				List.of(UUIDUtils.UUID0),
				song.title,
				song.artist,
				30,
				song.getUrl(),
				true
			);
		}
	}

	static {
		read(false);
	}

	// region Temp stats tracking
	@Override
	public void onDeath(@NotNull Minigamer victim) {
		super.onDeath(victim);

		BlockPartyMatchData matchData = victim.getMatch().getMatchData();
		matchData.getRoundsSurvived().put(victim.getUniqueId(), matchData.getRound());
		victim.stopTimeTracking();
	}

	@Override
	public void onQuit(@NotNull MatchQuitEvent event) {
		super.onQuit(event);
		event.getMinigamer().stopTimeTracking();
	}

	private void reportStats(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		BlockPartyStatsService service = new BlockPartyStatsService();

		for (Minigamer minigamer : match.getAllMinigamers()) {
			BlockPartyStats stats = new BlockPartyStats();
			if (matchData.getWinners() != null && matchData.getWinners().contains(minigamer))
				stats.setWin(true);

			int rounds = matchData.getRoundsSurvived().getOrDefault(minigamer.getUniqueId(), 0);
			stats.setRoundsSurvived(rounds);

			int powerUpsCollected = matchData.getPowerUpsCollected().getOrDefault(minigamer.getUniqueId(), 0);
			stats.setPowerUpsCollected(powerUpsCollected);

			int powerUpsUsed = matchData.getPowerUpsUsed().getOrDefault(minigamer.getUniqueId(), 0);
			stats.setPowerUpsUsed(powerUpsUsed);

			minigamer.stopTimeTracking();
			int seconds = minigamer.getSecondsPlayed();
			stats.setPlayTimeInSeconds(seconds);

			service.edit(minigamer, user -> user.add(stats));
		}
	}
	// endregion
}
