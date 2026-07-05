package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.creative.CreativeUser;
import gg.projecteden.nexus.models.creative.CreativeUserService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static gg.projecteden.nexus.features.commands.creative.ReachCommand.PERMISSION;

@Permission(PERMISSION)
@WikiConfig(rank = "Guest", feature = "Creative")
@Description("Set your creative mod block interaction range")
public class ReachCommand extends CustomCommand {
	public static final String PERMISSION = "essentials.gamemode.creative";
	public static final NamespacedKey KEY = new NamespacedKey(Nexus.getInstance(), "creative-reach");
	private static final CreativeUserService service = new CreativeUserService();
	private CreativeUser user;

	public ReachCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path(value = "<reach>")
	void run(@Arg(min = 0, max = 64) double reach) {
		user.setReach(reach);
		service.save(user);
		user.updateReach();
		send(PREFIX + "Creative mode reach set to " + reach);
	}

	static {
		Tasks.repeat(0, 10, () -> service.getOnline().forEach(CreativeUser::updateReach));
	}

	@EventHandler
	public void on(PlayerTeleportEvent event) {
		service.get(event.getPlayer()).updateReach();
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		service.get(event.getPlayer()).updateReach();
	}

	@EventHandler
	public void on(PlayerGameModeChangeEvent event) {
		service.get(event.getPlayer()).updateReach();
	}

}
