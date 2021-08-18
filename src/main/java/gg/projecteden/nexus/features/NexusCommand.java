package gg.projecteden.nexus.features;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTFile;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.SmartInvsPlugin;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.customenchants.OldCEConverter;
import gg.projecteden.nexus.features.listeners.TemporaryListener;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.framework.commands.CommandMapUtils;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CommandCooldownException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.setting.Setting;
import gg.projecteden.nexus.models.setting.SettingService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.SerializationUtils.JSON;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.QueuedTask;
import gg.projecteden.utils.TimeUtils.Time;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import static gg.projecteden.utils.TimeUtils.shortDateFormat;

@NoArgsConstructor
@Permission("group.admin")
public class NexusCommand extends CustomCommand implements Listener {

	public NexusCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public void _shutdown() {
		for (QueuedTask task : Utils.TASK_QUEUE.keySet())
			if (task.isCompleteBeforeShutdown())
				task.getTask().run();
	}

	@Path("uptime")
	void uptime() {
		send(PREFIX + "Up for &e" + Timespan.of(Nexus.EPOCH).format());
	}

	@Path("reload cancel")
	void cancelReload() {
		reloader = null;
		send(PREFIX + "Reload cancelled");
	}

	@Path("reload")
	void reload() {
		try {
			ReloadCondition.tryReload();
		} catch (Exception ex) {
			reloader = uuid();
			JsonBuilder json = new JsonBuilder(ex.getMessage(), NamedTextColor.RED);
			if (ex instanceof NexusException nex)
				json = new JsonBuilder(nex.getJson());

			error(json.next(", reload queued ").group().next("&e⟳").hover("&eClick to retry manually").command("/nexus reload"));
		}

		for (Player player : PlayerUtils.getOnlinePlayers())
			if (Dev.WAKKA.is(player) || Dev.BLAST.is(player))
				new SoundBuilder(Sound.ENTITY_EVOKER_PREPARE_WOLOLO).receiver(player).play();

		CooldownService cooldownService = new CooldownService();
		if (!cooldownService.check(StringUtils.getUUID0(), "reload", Time.SECOND.x(15)))
			throw new CommandCooldownException(StringUtils.getUUID0(), "reload");

		runCommand("plugman reload Nexus");
	}

	private static UUID reloader;

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
		SMARTINVS(() -> {
			long invCount = PlayerUtils.getOnlinePlayers().stream().filter(player -> SmartInvsPlugin.manager().getInventory(player).isPresent()).count();
			if (invCount > 0)
				throw new InvalidInputException(new JsonBuilder("There are " + invCount + " SmartInvs menus open").command("/nexus smartInvs").hover("&eClick to view"));
		}),
		TEMP_LISTENERS(() -> {
			if (!Nexus.getTemporaryListeners().isEmpty())
				throw new InvalidInputException(new JsonBuilder("There are " + Nexus.getTemporaryListeners().size() + " temporary listeners registered").command("/nexus temporaryListeners").hover("&eClick to view"));
		}),
		SIGN_MENUS(() -> {
			if (!Nexus.getSignMenuFactory().getInputReceivers().isEmpty())
				throw new InvalidInputException("There are " + Nexus.getSignMenuFactory().getInputReceivers().size() + " sign menus open");
		}),
		CRATES(() -> {
			for (CrateType crateType : Arrays.stream(CrateType.values()).filter(crateType -> crateType != CrateType.ALL).collect(Collectors.toList()))
				if (crateType.getCrateClass().isInUse())
					throw new InvalidInputException("Someone is opening a crate");
		}),
		WITHER(() -> {
			if (WitherChallenge.currentFight != null)
				throw new InvalidInputException("The wither is currently being fought");
		});

		public static boolean canReload() {
			try {
				tryReload();
			} catch (Exception ex) {
				return false;
			}

			return true;
		}

		public static void tryReload() {
			for (ReloadCondition condition : ReloadCondition.values())
				condition.run();
		}

		public void run() {
			runnable.run();
		}

		private final Runnable runnable;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Tasks.wait(5, NexusCommand::tryReload);
	}

	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		Tasks.wait(5, NexusCommand::tryReload);
	}

	static {
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(5), NexusCommand::tryReload);
	}

	private static void tryReload() {
		if (reloader == null)
			return;

		if (!ReloadCondition.canReload())
			return;

		OfflinePlayer player = Bukkit.getOfflinePlayer(reloader);
		if (!player.isOnline())
			return;

		PlayerUtils.runCommand(player.getPlayer(), "nexus reload");
	}

	@Path("debug")
	void debug() {
		Nexus.setDebug(!Nexus.isDebug());
		send(PREFIX + "Debugging " + (Nexus.isDebug() ? "&aenabled" : "&cdisabled"));
	}

	@Path("gc")
	void gc() {
		send("Collecting garbage...");
		System.gc();
		send("Garbage collected");
	}

	@Path("smartInvs")
	void smartInvs() {
		Map<String, String> playerInventoryMap = new HashMap<>();
		PlayerUtils.getOnlinePlayers().stream()
				.filter(player -> SmartInvsPlugin.manager().getInventory(player).isPresent())
				.forEach(player -> playerInventoryMap.put(player.getName(),
						SmartInvsPlugin.manager().getInventory(player).map(SmartInventory::getTitle).orElse(null)));

		if (playerInventoryMap.isEmpty())
			error("No SmartInvs open");

		send(PREFIX + "Open SmartInvs:");
		for (Map.Entry<String, String> entry : playerInventoryMap.entrySet())
			send(" &7- " + entry.getKey() + " - " + entry.getValue());
	}

	@Path("closeInventory [player]")
	void closeInventory(@Arg("self") Player player) {
		player.closeInventory();
	}

	@Path("temporaryListeners")
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
	void stats() {
		send("Features: " + Features.getFeatures().size());
		send("Commands: " + new HashSet<>(Commands.getCommands().values()).size());
		send("Listeners: " + Nexus.getListeners().size());
		send("Temporary Listeners: " + Nexus.getTemporaryListeners().size());
		send("Event Handlers: " + Nexus.getEventHandlers().size());
		send("Services: " + MongoService.getServices().size());
		send("Arenas: " + ArenaManager.getAll().size());
		send("Mechanics: " + MechanicType.values().length);
		send("Recipes: " + CustomRecipes.getRecipes().size());
		send("Custom Enchants: " + CustomEnchants.getEnchants().size());
	}

	@Path("stats commands [page]")
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
		paginate(Utils.sortByValueReverse(commands).keySet(), (plugin, index) ->
				json("&3" + index + " &e" + plugin.getName() + " &7- " + commands.get(plugin)), "/nexus stats commands", page);
	}

	@Path("stats eventHandlers [page]")
	void statsEventHandlers(@Arg("1") int page) {
		Map<Class<? extends Event>, Integer> counts = new HashMap<>();
		for (Class<? extends Event> eventHandler : Nexus.getEventHandlers())
			counts.put(eventHandler, counts.getOrDefault(eventHandler, 0) + 1);

		if (counts.isEmpty())
			error("No event handlers found");

		Map<Class<? extends Event>, Integer> sorted = Utils.sortByValueReverse(counts);

		send(PREFIX + "Event Handlers");
		BiFunction<Class<? extends Event>, String, JsonBuilder> formatter = (clazz, index) ->
				json("&3" + index + " &e" + clazz.getSimpleName() + " &7- " + counts.get(clazz));
		paginate(sorted.keySet(), formatter, "/nexus stats eventHandlers", page);
	}

	@Path("getEnv")
	void getEnv() {
		send(Nexus.getEnv().name());
	}

	@Path("updateCommands")
	void updateCommands() {
		player().updateCommands();
	}

	@Path("jingles <jingle>")
	void jingles(Jingle jingle) {
		jingle.play(player());
	}

	@Path("setting <type> [value]")
	void setting(String type, String value) {
		if (!isNullOrEmpty(value))
			new SettingService().save(new Setting(player(), type, value));
		send("Setting: " + new SettingService().get(player(), type));
	}

	@SneakyThrows
	@Path("getOfflineVehicle <player>")
	void getOfflineVehicle(Nerd nerd) {
		Block air = getTargetBlock().getRelative(BlockFace.UP);
		if (!MaterialTag.ALL_AIR.isTagged(air.getType()))
			error("You must be looking at the ground");

		NBTFile dataFile = nerd.getDataFile();
		NBTCompound rootVehicle = dataFile.getCompound("RootVehicle");
		if (rootVehicle == null)
			error("RootVehicle compound is null");

		NBTCompound entityCompound = rootVehicle.getCompound("Entity");
		if (entityCompound == null)
			error("Entity compound is null");

		String id = entityCompound.getString("id");
		EntityType type = EntityType.valueOf(id.replace("minecraft:", "").toUpperCase());

		Entity horse = world().spawnEntity(air.getLocation(), type);
		NBTEntity nbt = new NBTEntity(horse);
		nbt.mergeCompound(entityCompound);

		dataFile.setObject("RootVehicle", null);
		dataFile.save();
		send(PREFIX + "Respawned " + camelCase(type) + " and deleted original");
	}

	@Path("advancements [player] [page]")
	void advancements(@Arg("self") Player player, @Arg("1") int page) {
		BiFunction<Advancement, String, JsonBuilder> formatter = (advancement, index) -> {
			JsonBuilder json = json(" ");
			AdvancementProgress progress = player.getAdvancementProgress(advancement);
			json.next((progress.isDone() ? "&e" : "&c") + advancement.getKey().getKey());

			json.hover("&eAwarded Criteria:");
			for (String criteria : progress.getAwardedCriteria()) {
				String text = "&7- &e" + criteria;
				Date dateAwarded = progress.getDateAwarded(criteria);
				if (dateAwarded != null)
					text += " &7- " + shortDateFormat(dateAwarded.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				json.hover(text);
			}

			json.hover(" ");
			json.hover("&cRemaining Criteria:");
			for (String criteria : progress.getRemainingCriteria())
				json.hover("&7- &c" + criteria);

			return json;
		};

		paginate(PlayerUtils.getAdvancements().values(), formatter, "/nexus advancements " + player.getName(), page);
	}

	@Path("convertEnchants")
	void convertEnchants() {
		OldCEConverter.convertItem(getToolRequired());
	}

	@ConverterFor(Timespan.class)
	Timespan convertToTimespan(String input) {
		return Timespan.of(input);
	}

	@ConverterFor(Location.class)
	Location convertToLocation(String value) {
		if ("current".equalsIgnoreCase(value))
			return location();
		if ("target".equalsIgnoreCase(value))
			return getTargetBlockRequired().getLocation();

		return JSON.deserializeLocation(value);
	}

}
