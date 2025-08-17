package gg.projecteden.nexus.utils;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.crates.api.models.CrateAnimation;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.crates.CrateHandler;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.Pugmas21TrainBackground;
import gg.projecteden.nexus.features.events.y2024.vulan24.lantern.VuLan24LanternAnimation;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonEditor;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.Pugmas25WhacAMole;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachine;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Geyser;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25TrainBackground;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStoreLayouts;
import gg.projecteden.nexus.features.virtualinventories.VirtualInventoryUtils.VirtualInventoryHolder;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CommandCooldownException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;


@Data
@Accessors(fluent = true)
public class Reloader {
	private static final String PREFIX = StringUtils.getPrefix("Nexus");

	public static final Reloader reloader = new Reloader();

	public boolean queued;
	public boolean reloading;
	private CommandSender executor;
	private List<ReloadCondition> excludedConditions;

	public Reloader() {
		Tasks.repeat(TickTime.SECOND.x(5), TickTime.SECOND.x(5), this::tryReload);
	}

	public static boolean isReloadQueued() {
		return reloader.queued || reloader.reloading;
	}

	public void cancel() {
		if (!isReloadQueued()) {
			PlayerUtils.send(executor, PREFIX + "No reload queued");
			return;
		}

		queued = false;
		reloading = false;
		PlayerUtils.send(executor, PREFIX + "Reload cancelled");
		executor = null;
	}

	public void reload() {
		try {
			ReloadCondition.tryReload(excludedConditions);
		} catch (Exception ex) {
			queued = true;
			JsonBuilder json = new JsonBuilder(ex.getMessage(), NamedTextColor.RED);
			if (ex instanceof NexusException nex)
				json = new JsonBuilder(nex.getJson());

			PlayerUtils.send(executor, json.next("&3, reload queued"));
			return;
		}

		CooldownService cooldownService = new CooldownService();
		if (CooldownService.isOnCooldown(UUID0, "reload", TickTime.SECOND.x(15)))
			throw new CommandCooldownException(UUID0, "reload");

		reloading = true;

		broadcastReload();

		new NexusReloadEvent().callEvent();

		PlayerUtils.runCommand(executor, "plugman reload Nexus");
	}

	public void broadcastReload() {
		if (Nexus.getLuckPerms() == null)
			return;

		for (Player player : OnlinePlayers.staff().get()) {
			Nerd nerd = Nerd.of(player);
			if (!nerd.isReloadNotify())
				continue;

			player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, SoundCategory.RECORDS, 1, 1);

			GeoIP geoip = new GeoIPService().get(player);
			String message = " &c&l ! &c&l! &eReloading Nexus &c&l! &c&l!";
			if (GeoIP.exists(geoip))
				PacketUtils.sendPacket(player, new ClientboundSystemChatPacket(new JsonBuilder("&7 " + geoip.getCurrentTimeShort() + message).asComponent(), false));
			else
				PacketUtils.sendPacket(player, new ClientboundSystemChatPacket(new JsonBuilder(message).asComponent(), false));
		}
	}

	public void tryReload() {
		if (!isReloadQueued())
			return;

		if (!ReloadCondition.canReload(excludedConditions))
			return;

		PlayerUtils.runCommand(executor, "nexus reload");
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
				throw new InvalidInputException("There are " + Nexus.getSignMenuFactory().getInputReceivers().size() + " sign menus open (" + Nexus.getSignMenuFactory().getInputReceivers().keySet().stream().map(Nickname::of).collect(Collectors.joining(", ")) + ")");
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

		private final Runnable runnable;

		public void run() {
			runnable.run();
		}

		public static boolean canReload(List<ReloadCondition> excludedConditions) {
			try {
				tryReload(excludedConditions);
			} catch (Exception ex) {
				return false;
			}

			return true;
		}

		public static void tryReload(List<ReloadCondition> excludedConditions) {
			for (ReloadCondition condition : ReloadCondition.values())
				if (Nullables.isNullOrEmpty(excludedConditions) || !excludedConditions.contains(condition))
					condition.run();
		}
	}

	public static class NexusReloadEvent extends Event {

		private static final HandlerList HANDLERS = new HandlerList();

		public static HandlerList getHandlerList() {
			return HANDLERS;
		}

		@Override
		public HandlerList getHandlers() {
			return HANDLERS;
		}

	}
}
