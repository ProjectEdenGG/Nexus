package gg.projecteden.nexus.features.minigames;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.minigames.lobby.ActionBar;
import gg.projecteden.nexus.features.minigames.lobby.MinigameInviter;
import gg.projecteden.nexus.features.minigames.lobby.Parkour;
import gg.projecteden.nexus.features.minigames.lobby.TickPerks;
import gg.projecteden.nexus.features.minigames.lobby.exchange.MGMExchange;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import gg.projecteden.nexus.features.minigames.utils.MinigameNight;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.minigamessetting.MinigamesConfigService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import gg.projecteden.parchment.OptionalLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.ReflectionUtils.subTypesOf;

@NoArgsConstructor
public class Minigames extends Feature implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("Minigames");
	public static final Component COMPONENT_PREFIX = AdventureUtils.getPrefix("Minigames");
	@Getter
	@Accessors(fluent = true)
	private static final MinigameInviter inviter = new MinigameInviter();

	@Getter
	@Setter
	public static boolean debug;

	public static void debug(String message) {
		if (debug)
			Nexus.log("[DEBUG] [Minigames] " + message);
	}

	@Override
	public void onStart() {
		Utils.registerSerializables(getPath());
		registerMatchDatas();
		Tasks.async(() -> {
			ArenaManager.read();
			Utils.registerListeners(getPath());

			new ActionBar();
			new Parkour();
			new TickPerks();
			new MGMExchange();
		});

		Nexus.getCron().schedule("0 */2 * * *", Minigames::updateTopic);
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent event) {
		for (Arena arena : ArenaManager.getAll())
			arena.findMenuImage();
		for (MechanicType mechanic : MechanicType.values())
			mechanic.get().findImages();
	}

	private static String channelTopic;

	public static void updateTopic() {
		if (Discord.getGuild() == null)
			return;

		final MinigameNight mgn = new MinigameNight();
		String topic = (mgn.isNow() ? "Minigame night has started!" : "Next minigame night: <t:%s>".formatted(mgn.getNext().toEpochSecond()))
			+ "\n\nUse /subscribe minigames to get @mentioned for minigame updates";

		if (topic.equals(channelTopic))
			return;

		channelTopic = topic;

		var channel = TextChannel.MINIGAMES.get(Bot.KODA.jda());
		if (channel != null)
			channel.getManager().setTopic(topic).queue();
	}

	public static World getWorld() {
		return Bukkit.getWorld("gameworld");
	}

	@Deprecated // Use Match#worldguard or Arena#worldguard
	public static WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	@Deprecated // Use Match#worldedit or Arena#worldedit
	public static WorldEditUtils worldedit() {
		return new WorldEditUtils(getWorld());
	}

	public static Location getLobby() {
		return new Location(Minigames.getWorld(), -587, 150, -3330, 90, 0);
	}

	public static ProtectedRegion getLobbyRegion() {
		return worldguard().getProtectedRegion("lobby");
	}

	@Override
	public void onStop() {
		new ArrayList<>(MatchManager.getAll()).forEach(Match::end);
		ArenaManager.write();
		ArenaManager.getAll().clear();
		MatchManager.getAll().clear();
	}

	public static boolean isInMinigameLobby(Player player) {
		return isInMinigameLobbyWorld(player) && isInMinigameLobbyRegion(player);
	}

	public static boolean isInMinigameLobbyWorld(Player player) {
		return player.getWorld().equals(getWorld());
	}

	public static boolean isInMinigameLobbyRegion(Player player) {
		return worldguard().isInRegion(player.getLocation(), Minigames.getLobbyRegion());
	}

	public static OnlinePlayers getPlayersInLobby() {
		return OnlinePlayers.where().filter(Minigames::isInMinigameLobby);
	}

	public static boolean isMinigameWorld(World world) {
		return isMinigameWorld(WorldGroup.of(world));
	}

	public static boolean isMinigameWorld(OptionalLocation location) {
		return isMinigameWorld(WorldGroup.of(location));
	}

	public static boolean isMinigameWorld(WorldGroup worldGroup) {
		return worldGroup == WorldGroup.MINIGAMES;
	}

	public static List<Player> getPlayers() {
		return OnlinePlayers.getAll().stream()
			.filter(player -> isMinigameWorld(player.getWorld()))
			.collect(Collectors.toList());
	}

	public static List<Minigamer> getMinigamers() {
		return getPlayers().stream().map(Minigamer::of).collect(Collectors.toList());
	}

	public static List<Minigamer> getActiveMinigamers() {
		return getPlayers().stream().map(Minigamer::of).filter(minigamer -> minigamer.getMatch() != null).collect(Collectors.toList());
	}

	public static void broadcast(String announcement) {
		getPlayers().forEach(player -> PlayerUtils.send(player, Minigames.PREFIX + announcement));
	}

	public static void broadcast(ComponentLike component) {
		if (component == null) return;
		final Component message = Minigames.COMPONENT_PREFIX.append(component);
		getPlayers().forEach(player -> player.sendMessage(message));
		Nexus.log(AdventureUtils.asPlainText(message));
	}

	// Registration

	private Package getPath() {
		return this.getClass().getPackage();
	}

	@Getter
	private static final Map<Mechanic, Constructor<?>> matchDataMap = new HashMap<>();

	public static void registerMatchDatas() {
		try {
			String path = Minigames.class.getPackage().getName();
			Set<Class<? extends MatchData>> matchDataTypes = subTypesOf(MatchData.class, path + ".models.matchdata");

			for (Class<?> matchDataType : matchDataTypes) {
				if (matchDataType.getAnnotation(MatchDataFor.class) == null)
					continue;

				for (MechanicType mechanicType : MechanicType.values())
					for (Class<? extends Mechanic> superclass : mechanicType.get().getSuperclasses())
						for (Class<? extends Mechanic> applicableMechanic : matchDataType.getAnnotation(MatchDataFor.class).value())
							if (applicableMechanic.equals(superclass))
								try {
									Constructor<?> constructor = matchDataType.getConstructor(Match.class);
									constructor.setAccessible(true);
									matchDataMap.put(mechanicType.get(), constructor);
								} catch (NoSuchMethodException ex) {
									Nexus.warn("MatchData " + matchDataType.getSimpleName() + " has no Match constructor");
								}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static @NotNull MinigameModifier getModifier() {
		return new MinigamesConfigService().get0().getModifier().get();
	}

}
