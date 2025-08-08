package gg.projecteden.nexus.features;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.listeners.common.TemporaryListener;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.models.files.FontFile.CustomCharacter;
import gg.projecteden.nexus.features.virtualinventories.VirtualInventoryUtils.VirtualInventoryHolder;
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
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.offline.OfflineMessage;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Reloader.ReloadCondition;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tasks.QueuedTask;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gg.projecteden.nexus.utils.Reloader.reloader;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class NexusCommand extends CustomCommand implements Listener {

	public NexusCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public void _shutdown() {
		reloader.reloading = true;
		for (QueuedTask task : QueuedTask.QUEUE.keySet())
			if (task.isCompleteBeforeShutdown())
				task.getTask().run();
	}

	@Path("reload cancel")
	@Description("Cancel a queued reload")
	void reload_cancel() {
		reloader.cancel();
	}

	@Path("reload [--excludedConditions]")
	@Description("Reload Nexus, or queue a reload if applicable")
	void reload(@Switch @Arg(type = ReloadCondition.class) List<ReloadCondition> excludedConditions) {
		reloader.executor(sender()).excludedConditions(excludedConditions).reload();
	}

	@Path("reload notifySound [enable]")
	@Description("Toggle reload notifications")
	void reload_notifySound(Boolean enable) {
		if (enable == null)
			enable = !nerd().isReloadNotify();

		final Boolean finalEnable = enable;
		new NerdService().edit(player(), nerd -> nerd.setReloadNotify(finalEnable));

		send(PREFIX + "Reload sound notifications " + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Tasks.wait(5, reloader::tryReload);
	}

	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		Tasks.wait(5, reloader::tryReload);
	}

	@Path("uptime")
	@Description("View Nexus uptime")
	void uptime() {
		send(PREFIX + "Up for &e" + Timespan.of(Nexus.EPOCH).format());
	}

	@Path("gc")
	@Description("Force garbage collection")
	void gc() {
		send("Collecting garbage...");
		System.gc();
		send("Garbage collected");
	}

	@SneakyThrows
	@Path("initializeClass <class>")
	@Description("Force initialize a class")
	void initializeClass(String clazz) {
		send(PREFIX + "Class " + Class.forName(clazz).getSimpleName() + " initialized");
	}

	@Path("invs")
	@Description("View open custom inventories")
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
	void closeInventory(@Arg("self") Player player) {
		player.closeInventory();
	}

	@Path("temporaryListeners")
	@Description("View registered temporary listeners")
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
	void getEnv() {
		send(Nexus.getEnv().name());
	}

	@Path("updateCommands")
	@Description("Call Player#updateCommands")
	void updateCommands() {
		player().updateCommands();
	}

	@Path("jingles <jingle>")
	@Description("Play a jingle")
	void jingles(Jingle jingle) {
		jingle.play(player());
	}

	@Path("offlineMessage <player>")
	@Description("Test sending an offline message")
	void offlineMessage(OfflinePlayer player) {
		OfflineMessage.send(player, new JsonBuilder("&3Testing").hover("&eTesting"));
	}

	@Path("feature reload <feature>")
	@Description("Reload a specific feature")
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
