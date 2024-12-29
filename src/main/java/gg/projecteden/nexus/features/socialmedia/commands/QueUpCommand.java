package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.queup.QueUp;
import gg.projecteden.nexus.models.queup.QueUpService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;

@Aliases("dubtrack")
@SuppressWarnings("SameParameterValue")
public class QueUpCommand extends CustomCommand {
	private static final String URL = EdenSocialMediaSite.QUEUP.getUrl();

	private static boolean enabled = true;
	private static final QueUpService service = new QueUpService();
	private static final QueUp queup = service.get0();

	public QueUpCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Receive a link to the server's QueUp")
	void run() {
		send(json("&e" + URL));

		if (!Nullables.isNullOrEmpty(queup.getLastSong()))
			send("&3Currently playing: " + queup.getLastSong());
	}

	@Path("updates [enable]")
	@Permission(Group.STAFF)
	@Description("Toggle listening for song updates")
	void updates(Boolean enable) {
		if (enable == null)
			enable = !enabled;

		enabled = enable;
		send(PREFIX + "Song updates " + (enable ? "&aenabled" : "&cdisabled"));
	}

	static {
		if (Nexus.getEnv() == Env.PROD)
			Tasks.repeatAsync(TickTime.SECOND, TickTime.SECOND.x(15), () -> {
				if (!enabled)
					return;

				try {
					String currentSong = queup.getCurrentSong();

					if (currentSong != null && currentSong.equals(queup.getLastSong()))
						return;

					queup.setLastSong(currentSong);
					service.save(queup);

					if (currentSong == null)
						return;

					String hover = "&eClick me to join &dQueUp&e!";
					Broadcast.ingame().message(new JsonBuilder("&3Now playing on &d" + URL + "&3:").hover(hover).url(URL)).muteMenuItem(MuteMenuItem.QUEUP).send();
					Broadcast.ingame().message(new JsonBuilder(" " + currentSong).hover(hover).url(URL)).muteMenuItem(MuteMenuItem.QUEUP).send();
				} catch (Exception ignored) {
				}
			});
	}

}
