package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.punishments.SkinBanConfig;
import gg.projecteden.nexus.models.punishments.SkinBanConfigService;
import gg.projecteden.nexus.models.skincache.SkinCache;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.util.UUID;
import java.util.function.BiFunction;

@NoArgsConstructor
@Permission(Group.MODERATOR)
public class SkinBanCommand extends _JusticeCommand implements Listener {
	private static final SkinBanConfigService service = new SkinBanConfigService();
	private static final SkinBanConfig config = service.get0();

	public SkinBanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	@Description("Ban a player until they change their skin")
	void ban(SkinCache player) {
		if (!player.isCached())
			player.update();

		config.ban(uuid(), player.getUniqueId());
		service.save(config);
	}

	@Path("unban <player>")
	@Description("Remove a skin ban")
	void unban(SkinCache player) {
		config.unban(uuid(), player.getUuid());
		service.save(config);
	}

	@Path("list [page]")
	@Description("List skin banned players")
	void list(@Arg("1") int page) {
		final BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) -> {
			final String textureUrl = SkinCache.of(uuid).getTextureUrl();
			return json("&3" + index + " &e" + Nickname.of(uuid) + " &7- [Click to view]").url(textureUrl);
		};

		new Paginator<UUID>()
			.values(config.getBanned())
			.formatter(formatter)
			.command("/skinban list")
			.page(page)
			.send();
	}

	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent event) {
		if (config.isBanned(event.getUniqueId())) {
			final SkinCache skin = SkinCache.of(event.getUniqueId());
			final boolean changed = skin.update();

			if (changed) {
				Punishments.broadcast("&e" + skin.getNickname() + " &chas changed their skin to &e" + skin.getTextureUrl());
				config.unban(skin.getUuid());
				service.save(config);
			} else {
				Punishments.broadcast("&e" + skin.getNickname() + " &ctried to join, but is skin banned");
				event.disallow(Result.KICK_BANNED, SkinBanConfig.BAN_MESSAGE);
			}
		}

	}

}
