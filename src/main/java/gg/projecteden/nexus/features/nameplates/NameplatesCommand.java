package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nameplates.NameplateUser;
import gg.projecteden.nexus.models.nameplates.NameplateUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class NameplatesCommand extends CustomCommand {
	private final NameplateUserService service = new NameplateUserService();
	private NameplateUser user;

	public NameplatesCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("viewSelf [enable]")
	void see(Boolean enable) {
		if (enable == null)
			enable = !user.isViewOwnNameplate();

		user.setViewOwnNameplate(enable);
		service.save(user);

		Nameplates.get().getNameplateManager().updateForSelf(player());

		send(PREFIX + "Own nameplate visibility " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("update [player]")
	@Permission("group.admin")
	void update(@Arg("self") Player player) {
		Nameplates.get().getNameplateManager().update(player);
		send(PREFIX + "Updated " + (isSelf(player) ? "your" : Nickname.of(player) + "'s") + " nameplate entity");
	}

}
