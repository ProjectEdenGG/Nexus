package gg.projecteden.nexus.features;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.crates.api.models.CrateAnimation;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.crates.CrateHandler;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.Pugmas21TrainBackground;
import gg.projecteden.nexus.features.events.y2024.vulan24.lantern.VuLan24LanternAnimation;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonEditor;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.Pugmas25WhacAMole;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachine;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Geyser;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25TrainBackground;
import gg.projecteden.nexus.features.listeners.common.TemporaryListener;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.models.files.FontFile.CustomCharacter;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStoreLayouts;
import gg.projecteden.nexus.features.virtualinventories.VirtualInventoryUtils.VirtualInventoryHolder;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.framework.commands.CommandMapUtils;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CommandCooldownException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.offline.OfflineMessage;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tasks.QueuedTask;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipFile;

@NoArgsConstructor
@Permission(Group.STAFF)
public class NexusCommand extends CustomCommand implements Listener {

	private static boolean queued;
	private static boolean reloading;

	public NexusCommand(CommandEvent event) {
		super(event);
	}

	public static boolean isReloadQueued() {
		return queued || reloading;
	}

	@Override
	public void _shutdown() {
		reloading = true;
		for (QueuedTask task : QueuedTask.QUEUE.keySet())
			if (task.isCompleteBeforeShutdown())
				task.getTask().run();
	}

	@Path("uptime")
	@Description("View Nexus uptime")
	void uptime() {
		send(PREFIX + "Up for &e" + Timespan.of(Nexus.EPOCH).format());
	}

	@Path("reload notifySound [enable]")
	@Description("Toggle reload notifications")
	void notifyReload(Boolean enable) {
		if (enable == null)
			enable = !nerd().isReloadNotify();

		final Boolean finalEnable = enable;
		new NerdService().edit(player(), nerd -> nerd.setReloadNotify(finalEnable));

		send(PREFIX + "Reload sound notifications " + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@Path("reload cancel")
	@Description("Cancel a queued reload")
	@Permission(Group.ADMIN)
	void cancelReload() {
		queued = false;
		reloading = false;
		reloader = null;
		excludedConditions = null;
		send(PREFIX + "Reload cancelled");
	}

	@Path("reload [--excludedConditions]")
	@Description("Reload Nexus, or queue a reload if applicable")
	@Permission(Group.ADMIN)
	void reload(@Switch @Arg(type = ReloadCondition.class) List<ReloadCondition> excludedConditions) {
		NexusCommand.excludedConditions = excludedConditions;

		try {
			ReloadCondition.tryReload(excludedConditions);
		} catch (Exception ex) {
			queued = true;
			reloader = uuid();
			JsonBuilder json = new JsonBuilder(ex.getMessage(), NamedTextColor.RED);
			if (ex instanceof NexusException nex)
				json = new JsonBuilder(nex.getJson());

			error(json.next(", reload queued ").group().next("&e⟳").hover("&eClick to retry manually").command("/nexus reload"));
		}

		reloading = true;

		for (Player player : OnlinePlayers.onlyStaff().get()) {
			Nerd nerd = Nerd.of(player);
			if (!nerd.isReloadNotify())
				continue;

			player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, SoundCategory.RECORDS, 1, 1);
		}

		CooldownService cooldownService = new CooldownService();
		if (!cooldownService.check(UUIDUtils.UUID0, "reload", TickTime.SECOND.x(15)))
			throw new CommandCooldownException(UUIDUtils.UUID0, "reload");

		runCommand("plugman reload Nexus");
	}

	private static UUID reloader;
	private static List<ReloadCondition> excludedConditions;

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Tasks.wait(5, NexusCommand::tryReload);
	}

	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		Tasks.wait(5, NexusCommand::tryReload);
	}

	static {
		Tasks.repeat(TickTime.SECOND.x(5), TickTime.SECOND.x(5), NexusCommand::tryReload);
	}

	private static void tryReload() {
		if (reloader == null)
			return;

		if (!ReloadCondition.canReload(excludedConditions))
			return;

		OfflinePlayer player = Bukkit.getPlayer(reloader);
		if (player == null)
			return;

		PlayerUtils.runCommand(player.getPlayer(), "nexus reload");
	}

	@Getter
	@AllArgsConstructor
	public enum ReloadCondition {
		FILE_NOT_FOUND(() -> {
			File file = Paths.get("plugins/Nexus.jar").toFile();
			if (!file.exists())
				throw new InvalidInputException("Nexus.jar doesn't exist");
		}),
		FILE_NOT_COMPLETE(() -> {
			File file = Paths.get("plugins/Nexus.jar").toFile();
			try {
				new ZipFile(file).entries();
			} catch (IOException ex) {
				throw new InvalidInputException("Nexus.jar is not complete");
			}
		}),
		MINIGAMES(() -> {
			long matchCount = MatchManager.getAll().stream().filter(match -> match.isStarted() && !match.isEnded()).count();
			if (matchCount > 0)
				throw new InvalidInputException("There are " + matchCount + " active matches");
		}),
		TEMP_LISTENERS(() -> {
			if (!Nexus.getTemporaryListeners().isEmpty())
				throw new InvalidInputException(new JsonBuilder("There are " + Nexus.getTemporaryListeners().size() + " temporary listeners registered").command("/nexus temporaryListeners").hover("&eClick to view"));
		}),
		SMARTINVS(() -> {
			long count = OnlinePlayers.getAll().stream().filter(player -> {
				boolean open = SmartInvsPlugin.manager().getInventory(player).isPresent();

				if (open && AFK.get(player).hasBeenAfkFor(TickTime.MINUTE.x(15))) {
					player.closeInventory();
					open = false;
				}

				return open;
			}).count();

			if (count > 0)
				throw new InvalidInputException(new JsonBuilder("There are " + count + " smart menus open").command("/nexus invs").hover("&eClick to view"));
		}),
		SIGN_MENUS(() -> {
			if (!Nexus.getSignMenuFactory().getInputReceivers().isEmpty())
				throw new InvalidInputException("There are " + Nexus.getSignMenuFactory().getInputReceivers().size() + " sign menus open");
		}),
		VIRTUAL_INVENTORIES(() -> {
			var count = OnlinePlayers.getAll().stream().filter(player -> player.getOpenInventory().getTopInventory().getHolder() instanceof VirtualInventoryHolder).count();

			if (count > 0)
				throw new InvalidInputException(new JsonBuilder("There are " + count + " virtual inventories open").command("/nexus invs").hover("&eClick to view"));
		}),
		CRATES(() -> {
			for (CrateAnimation animation : CrateHandler.ANIMATIONS.values())
				if (animation.isActive())
					throw new InvalidInputException("Someone is opening a crate");
		}),
		WITHER(() -> {
			if (WitherChallenge.currentFight != null)
				throw new InvalidInputException("The wither is currently being fought");
		}),
		PUGMAS21_TRAIN_BACKGROUND(() -> {
			if (Nexus.isProdOrUpdate()) {
				if (Pugmas21TrainBackground.isActive())
					throw new InvalidInputException("Someone is traveling to Pugmas");
			}
		}),
		QUEST_DIALOG(() -> {
			for (Quester quester : new QuesterService().getOnline())
				if (quester.getDialog() != null)
					if (quester.getDialog().getTaskId().get() > 0)
						throw new InvalidInputException("Someone is in a quest dialog");
		}),
		RESOURCE_PACK(() -> {
			if (ResourcePack.isReloading())
				throw new InvalidInputException("Resource pack is reloading");
		}),
		ANIMATIONS(() -> {
			if (DecorationStoreLayouts.isPreventReload())
				throw new InvalidInputException("Decoration store is animating");

			if (Nexus.isProdOrUpdate()) {
				if (Pugmas25Geyser.isAnimating())
					throw new InvalidInputException("Pugmas25 geyser is animating");

				if (gg.projecteden.nexus.features.events.models.Train.anyActiveInstances())
					throw new InvalidInputException("There is an active train");
			} else {
				for (gg.projecteden.nexus.features.events.models.Train train : new ArrayList<>(gg.projecteden.nexus.features.events.models.Train.getInstances()))
					train.stop();
			}
		}),
		PUGMAS25_BALLOON_EDITOR(() -> {
			if (Nexus.isProdOrUpdate()) {
				if (Pugmas25BalloonEditor.reload())
					throw new InvalidInputException("A balloon is being editing at pugmas, saving session...");
			}
		}),
		PUGMAS25_SLOT_MACHINE(() -> {
			if (Nexus.isProdOrUpdate()) {
				if (Pugmas25SlotMachine.get().isPlaying())
					throw new InvalidInputException("The slot machine is being rolled at pugmas");
			}
		}),
		PUGMAS25_WAC_A_MOLE(() -> {
			if (Nexus.isProdOrUpdate()) {
				if (Pugmas25WhacAMole.get().isPlaying())
					throw new InvalidInputException("Whac A Mole is being played at pugmas");
			}
		}),
		PUGMAS25_TRAIN_BACKGROUND(() -> {
			if (Nexus.isProdOrUpdate()) {
				if (Pugmas25TrainBackground.isAnimating())
					throw new InvalidInputException("Pugmas25 Train Background is animating!");
			}
		}),
		CHAT_GAMES(() -> {
			if (ChatGamesConfig.getCurrentGame() != null)
				if (ChatGamesConfig.getCurrentGame().isStarted())
					throw new InvalidInputException("There is an active chat game");
		}),
		VU_LAN_LANTERN_ANIMATION(() -> {
			if (Nexus.isProdOrUpdate()) {
				if (VuLan24LanternAnimation.getInstance() != null)
					throw new InvalidInputException("There is an active Vu Lan lantern animation");
			}
		}),
		;

		public static boolean canReload() {
			return canReload(null);
		}

		public static boolean canReload(List<ReloadCondition> excludedConditions) {
			try {
				tryReload(excludedConditions);
			} catch (Exception ex) {
				return false;
			}

			return true;
		}

		public static void tryReload() {
			tryReload(null);
		}

		public static void tryReload(List<ReloadCondition> excludedConditions) {
			for (ReloadCondition condition : ReloadCondition.values())
				if (Nullables.isNullOrEmpty(excludedConditions) || !excludedConditions.contains(condition))
					condition.run();
		}

		public void run() {
			runnable.run();
		}

		private final Runnable runnable;
	}

	@Path("debug [state]")
	@Description("Toggle debug mode")
	@Permission(Group.ADMIN)
	void debug(Boolean state) {
		if (state == null)
			state = !Nexus.isDebug();

		Nexus.setDebug(state);
		send(PREFIX + "Debugging " + (Nexus.isDebug() ? "&aenabled" : "&cdisabled"));
	}

	@Path("gc")
	@Description("Force garbage collection")
	@Permission(Group.ADMIN)
	void gc() {
		send("Collecting garbage...");
		System.gc();
		send("Garbage collected");
	}

	@SneakyThrows
	@Path("initializeClass <class>")
	@Description("Force initialize a class")
	@Permission(Group.ADMIN)
	void initializeClass(String clazz) {
		send(PREFIX + "Class " + Class.forName(clazz).getSimpleName() + " initialized");
	}

	@Path("invs")
	@Description("View open custom inventories")
	@Permission(Group.SENIOR_STAFF)
	void smartInvs() {
		var anySmartInventories = OnlinePlayers.getAll()
			.stream()
			.anyMatch(player ->  SmartInvsPlugin.manager().getInventory(player).isPresent());

		var anyVirtualInventories = OnlinePlayers.getAll()
			.stream()
			.anyMatch(player -> player.getOpenInventory().getTopInventory().getHolder() instanceof VirtualInventoryHolder);

		if (!anySmartInventories && !anyVirtualInventories)
			error("No custom inventories open");

		if (anySmartInventories) {
			send(PREFIX + "Open Smart Menus:");
			for (Player player : OnlinePlayers.getAll()) {
				var inv = SmartInvsPlugin.manager().getInventory(player);
				if (inv.isEmpty())
					continue;

				String title = StringUtils.stripColor(inv.map(SmartInventory::getTitle).map(AdventureUtils::asLegacyText).orElse(null));
				for (CustomCharacter character : ResourcePack.getFontFile().getProviders()) {
					if (character.getChars() == null)
						continue;

					for (String _char : character.getChars())
						try { title = title.replaceAll(_char, ""); } catch (Exception ignore) {}
				}

				send(" &7- " + Nickname.of(player) + " - " + title);
			}
		}

		if (anyVirtualInventories) {
			send(PREFIX + "Open Virtual Inventories:");
			for (Player player : OnlinePlayers.getAll()) {
				if (player.getOpenInventory().getTopInventory().getHolder() instanceof VirtualInventoryHolder holder)
					send(" &7- " + Nickname.of(player) + " - " + camelCase(holder.getVirtualInventory().getType()));
			}
		}
	}

	@Path("closeInventory [player]")
	@Description("Force close a player's inventory")
	@Permission(Group.SENIOR_STAFF)
	void closeInventory(@Arg("self") Player player) {
		player.closeInventory();
	}

	@Path("temporaryListeners")
	@Description("View registered temporary listeners")
	@Permission(Group.SENIOR_STAFF)
	void temporaryListeners() {
		if (Nexus.getTemporaryListeners().isEmpty())
			error("No temporary listeners registered");

		send(PREFIX + "Temporary listeners");

		for (TemporaryListener temporaryListener : Nexus.getTemporaryListeners()) {
			final Player player = temporaryListener.getPlayer();

			send((player.isOnline() ? "&e" : "&c") + Nickname.of(player) + " &7- " + temporaryListener.getClass().getSimpleName());
		}
	}

	@Path("temporaryListeners unregisterOffline")
	@Description("Unregister temporary listeners for offline players")
	@Permission(Group.SENIOR_STAFF)
	void temporaryListeners_unregisterOffline() {
		List<TemporaryListener> unregistered = new ArrayList<>() {{
			for (TemporaryListener temporaryListener : Nexus.getTemporaryListeners())
				if (!temporaryListener.getPlayer().isOnline())
					add(temporaryListener);
		}};

		if (unregistered.isEmpty())
			error("Could not unregister any temporary listeners");

		send(PREFIX + "Unregistered:");
		for (TemporaryListener temporaryListener : unregistered) {
			Nexus.unregisterTemporaryListener(temporaryListener);
			send("&c" + Nickname.of(temporaryListener.getPlayer()) + " &7- " + temporaryListener.getClass().getSimpleName());
		}
	}

	@Path("stats")
	@Description("View miscellaneous stats")
	void stats() {
		send("Features: " + Features.getRegistered().size());
		send("Commands: " + new HashSet<>(Commands.getCommands().values()).size());
		send("Listeners: " + Nexus.getListeners().size());
		send("Temporary Listeners: " + Nexus.getTemporaryListeners().size());
		send("Event Handlers: " + Nexus.getEventHandlers().size());
		send("Services: " + MongoPlayerService.getServices().size());
		send("Arenas: " + ArenaManager.getAll().size());
		send("Mechanics: " + MechanicType.values().length);
		send("Recipes: " + CustomRecipes.getRecipes().size());
		send("Custom Enchants: " + CustomEnchants.getEnchants().size());
		send("Custom Blocks: " + CustomBlock.values().length);
		send("Custom Decoration: " + DecorationConfig.getALL_DECOR_CONFIGS().size());
	}

	@Path("stats commands [page]")
	@Description("View commands stats by plugin")
	void statsCommands(@Arg("1") int page) {
		CommandMapUtils mapUtils = Nexus.getInstance().getCommands().getMapUtils();

		Map<Plugin, Integer> commands = new HashMap<>();
		Set<String> keys = new HashSet<>();

		for (Command value : mapUtils.getKnownCommandMap().values()) {
			if (!(value instanceof PluginCommand command))
				continue;

			Plugin plugin = command.getPlugin();

			String commandName = command.getName();
			if (commandName.contains(":"))
				commandName = commandName.split(":")[1];

			String key = plugin.getName() + "-" + commandName;
			if (keys.contains(key))
				continue;

			keys.add(key);
			commands.put(plugin, commands.getOrDefault(plugin, 0) + 1);
		}

		send(PREFIX + "Commands by plugin");
		new Paginator<Plugin>()
			.values(Utils.sortByValueReverse(commands).keySet())
			.formatter((plugin, index) -> json(index + " &e" + plugin.getName() + " &7- " + commands.get(plugin)))
			.command("/nexus stats commands")
			.page(page)
			.send();
	}

	@Path("stats eventHandlers [page]")
	@Description("View event handler stats")
	void statsEventHandlers(@Arg("1") int page) {
		Map<Class<? extends Event>, Integer> counts = new HashMap<>();
		for (Class<? extends Event> eventHandler : Nexus.getEventHandlers())
			counts.put(eventHandler, counts.getOrDefault(eventHandler, 0) + 1);

		if (counts.isEmpty())
			error("No event handlers found");

		Map<Class<? extends Event>, Integer> sorted = Utils.sortByValueReverse(counts);

		send(PREFIX + "Event Handlers");
		new Paginator<Class<? extends Event>>()
			.values(sorted.keySet())
			.formatter((clazz, index) -> json(index + " &e" + clazz.getSimpleName() + " &7- " + counts.get(clazz)))
			.command("/nexus stats eventHandlers")
			.page(page)
			.send();
	}

	@Path("getEnv")
	@Description("Print current environment")
	@Permission(Group.SENIOR_STAFF)
	void getEnv() {
		send(Nexus.getEnv().name());
	}

	@Path("updateCommands")
	@Description("Call Player#updateCommands")
	@Permission(Group.SENIOR_STAFF)
	void updateCommands() {
		player().updateCommands();
	}

	@Path("jingles <jingle>")
	@Description("Play a jingle")
	void jingles(Jingle jingle) {
		jingle.play(player());
	}

	@Path("offlineMessage <player>")
	@Permission(Group.ADMIN)
	void offlineMessage(OfflinePlayer player) {
		OfflineMessage.send(player, new JsonBuilder("&3Testing").hover("&eTesting"));
	}

	@Permission(Group.ADMIN)
	@Path("feature reload <feature>")
	void feature_reload(Feature feature) {
		feature.reload();
		send(PREFIX + "Reloaded " + feature.getName());
	}

	@TabCompleterFor(Feature.class)
	List<String> tabCompleteFeature(String filter) {
		return Features.getRegistered().values().stream()
			.map(Feature::getName)
			.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	@ConverterFor(Feature.class)
	Feature convertToFeature(String value) {
		return Features.getRegistered().values().stream()
			.filter(feature -> feature.getName().equalsIgnoreCase(value))
			.findFirst().orElseThrow(() -> new InvalidInputException("Feature &e" + value + " &c not found"));
	}


}
