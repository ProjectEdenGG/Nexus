package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.BossBarBuilder;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Permission(Group.ADMIN)
@HideFromWiki
@NoArgsConstructor
public class DeployCommand extends CustomCommand implements Listener {

	static int duplicateTaskId;
	static final BossBar deployingBar = new BossBarBuilder().color(ColorType.PINK).title("&3Incoming Deploy").build();
	static final NerdService nerdService = new NerdService();

	public DeployCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public void _shutdown() {
		getUsersToShowTo().forEach(deployingBar::removeViewer);
	}

	@Path("create <id> <name> <player>")
	@HideFromHelp
	@TabCompleteIgnore
	void create(UUID uuid, String plugin, Nerd dev) {
		console();

		Deployment deployment = Deployment.of(uuid);
		deployment.setPlugin(plugin);
		deployment.setDev(dev);

		if (Deployment.currentDeployments.size() > 1)
			deployingBar.name(new JsonBuilder("&3Incoming Deploys (&e%d&3)".formatted(Deployment.currentDeployments.size())));
		getUsersToShowTo().forEach(_dev -> deployingBar.addViewer(_dev.getPlayer()));

		generateBossBar(deployment);
		getUsersToShowTo().forEach(_dev -> {
			deployment.getBossBar().addViewer(_dev.getPlayer());
			new SoundBuilder(CustomSound.NOTE_MARIMBA).location(dev.getLocation()).pitch(.6).volume(.3).receiver(dev).play();
		});

		if (Deployment.getByPlugin(plugin).size() > 1) {
			deployingBar.name(new JsonBuilder("&3Incoming Deploys (&e%d&3) &e| &cDUPLICATES".formatted(Deployment.currentDeployments.size())));
			Deployment.getByPlugin(plugin).forEach(deploy -> {
				deploy.getDev().sendMessage(new JsonBuilder(PREFIX + "&cDUPLICATE DEPLOYS DETECTED"));
			});
			startDuplicateError();
		}
	}

	@Path("status <id> <status>")
	@HideFromHelp
	@TabCompleteIgnore
	void status(Deployment deployment, Deployment.Status status) {
		console();

		deployment.setStatus(status);
		deployment.getBossBar().name(deployment.getTitle());
	}

	@Path("remove <id>")
	@HideFromHelp
	@TabCompleteIgnore
	void remove(Deployment deployment) {
		console();
		removeNoConsole(deployment);
	}

	@Path("cancel [id]")
	void cancel(Deployment deployment) {
		if (deployment == null) {
			send(PREFIX + "Current Deploys:");
			Deployment.currentDeployments.forEach(deploy -> {
				json().next("&e - " + deploy.getTitle())
					.command("deploy cancel " + deploy.getUuid())
					.hover("&eClick to remove &c(This does not stop the deploy if still active!)")
					.send(player());
			});
		}
		else {
			removeNoConsole(deployment);
		}
	}

	private List<Player> getUsersToShowTo() {
		return OnlinePlayers.staff().filter(staff -> nerdService.get(staff).isDeployNotify()).get();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player dev = event.getPlayer();
		if (!Rank.of(dev).isStaff())
			return;
		if (Deployment.currentDeployments.isEmpty())
			return;
		if (!nerdService.get(dev).isDeployNotify())
			return;

		deployingBar.addViewer(dev.getPlayer());
		Deployment.currentDeployments.forEach(deploy -> {
			deploy.getBossBar().addViewer(dev.getPlayer());
		});
	}

	public void removeNoConsole(Deployment deployment) {
		Deployment.currentDeployments.removeIf(deploy -> deploy.getUuid().equals(deployment.getUuid()));

		getUsersToShowTo().forEach(_dev -> deployment.getBossBar().removeViewer(_dev));

		if (Deployment.getByPlugin(deployment.plugin).size() <= 1)
			stopDuplicateError();

		if (Deployment.currentDeployments.isEmpty())
			getUsersToShowTo().forEach(deployingBar::removeViewer);
		else if (Deployment.getByPlugin(deployment.plugin).size() > 1)
			deployingBar.name(new JsonBuilder("&3Incoming Deploys (&e%d&3) &e| &cDUPLICATES".formatted(Deployment.currentDeployments.size())));
		else if (Deployment.currentDeployments.size() > 1)
			deployingBar.name(new JsonBuilder("&3Incoming Deploys (&e%d&3)".formatted(Deployment.currentDeployments.size())));
		else
			deployingBar.name(new JsonBuilder("&3Incoming Deploy"));
	}

	void startDuplicateError() {
		if (duplicateTaskId > 0)
			Tasks.cancel(duplicateTaskId);
		duplicateTaskId = Tasks.repeat(0, 4, () ->
			getUsersToShowTo().forEach(dev ->
				new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_HARP).location(dev.getLocation()).pitch(1.8).volume(2).receiver(dev).play()));
	}

	void stopDuplicateError() {
		if (duplicateTaskId > 0)
			Tasks.cancel(duplicateTaskId);
	}

	public void generateBossBar(Deployment deployment) {
		BossBar bar = new BossBarBuilder()
			.color(ColorType.PINK)
			.title(deployment.getTitle())
			.build();

		deployment.setBossBar(bar);
	}

	@TabCompleterFor(Deployment.class)
	List<String> tabCompleteDeployment(String filter) {
		return Deployment.currentDeployments.stream().map(deployment -> deployment.getUuid().toString()).filter(uuid -> uuid.toLowerCase().startsWith(filter)).collect(Collectors.toList());
	}

	@ConverterFor(Deployment.class)
	Deployment convertToDeployment(String string) {
		return Deployment.currentDeployments.stream().filter(deploy -> deploy.getUuid().toString().equals(string)).findFirst().orElse(null);
	}

	@Data
	@RequiredArgsConstructor
	public static class Deployment {
		private static final List<Deployment> currentDeployments = new ArrayList<>();

		@NonNull
		private UUID uuid;
		private String plugin;
		private Nerd dev;
		private Status status = Status.COMPILING;

		private BossBar bossBar;

		public JsonBuilder getTitle() {
			return new JsonBuilder("&e" + plugin)
				.next(" &3| ")
				.next("&e" + StringUtils.camelCase(status))
				.next(" &3| ")
				.next(dev.getColoredName());
		}

		public static Deployment of(UUID uuid) {
			return currentDeployments.stream().filter(deployment -> deployment.getUuid().equals(uuid)).findFirst().orElseGet(() -> {
				Deployment deployment = new Deployment(uuid);
				currentDeployments.add(deployment);
				return deployment;
			});
		}

		public static List<Deployment> getByPlugin(String plugin) {
			return currentDeployments.stream().filter(deploy -> deploy.getPlugin().equalsIgnoreCase(plugin)).collect(Collectors.toList());
		}

		public enum Status {
			COMPILING,
			UPLOADING
		}
	}



}
