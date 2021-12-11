package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicReference;

import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

@Permission("group.staff")
public class SlapCommand extends CustomCommand {

	public SlapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(Player player) {
		slap(Nerd.of(player));
	}

	public static void slap(Nerd nerd) {
		final Player player = nerd.getOnlinePlayer();
		player.setVelocity(player.getLocation().getDirection().multiply(-2).setY(player.getEyeLocation().getPitch() > 0 ? 1.5 : -1.5));
		PlayerUtils.send(nerd, "&6You have been slapped!");
	}

	static {
		new AtomicReference<Runnable>() {{
			set(() -> Tasks.wait(TickTime.MINUTE.x(randomInt(5, 30)), () -> {
				final Nerd marshy = Nerd.of("Marshy");
				if (marshy.isOnline())
					slap(marshy);
				get().run();
			}));
		}}.get().run();
	}

}
