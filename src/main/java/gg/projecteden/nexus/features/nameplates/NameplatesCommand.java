package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nameplates.NameplateUser;
import gg.projecteden.nexus.models.nameplates.NameplateUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.StringUtils;
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
	@Description("Toggle whether you can see your own nameplate")
	void see(Boolean enable) {
		if (enable == null)
			enable = !user.isViewOwnNameplate();

		user.setViewOwnNameplate(enable);
		service.save(user);

		Nameplates.get().getNameplateManager().updateForSelf(player());

		send(PREFIX + "Own nameplate visibility " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("update [player]")
	@Permission(Group.ADMIN)
	@Description("Update a player's nameplate")
	void update(@Arg("self") Player player) {
		Nameplates.get().getNameplateManager().update(player);
		send(PREFIX + "Updated " + (isSelf(player) ? "your" : Nickname.of(player) + "'s") + " nameplate entity");
	}

	@Path("spawn [player]")
	@Permission(Group.ADMIN)
	@Description("Spawn a player's nameplate")
	void spawn(@Arg("self") Player player) {
		Nameplates.get().getNameplateManager().spawn(player);
		send(PREFIX + "Spawned " + (isSelf(player) ? "your" : Nickname.of(player) + "'s") + " nameplate entity");
	}

	@Path("respawn [player]")
	@Permission(Group.ADMIN)
	@Description("Respawn a player's nameplate")
	void respawn(@Arg("self") Player player) {
		Nameplates.get().getNameplateManager().respawn(player);
		send(PREFIX + "Respawned " + (isSelf(player) ? "your" : Nickname.of(player) + "'s") + " nameplate entity");
	}

	@Path("destroy [player]")
	@Permission(Group.ADMIN)
	@Description("Despawn a player's nameplate")
	void destroy(@Arg("self") Player player) {
		Nameplates.get().getNameplateManager().destroy(player);
		send(PREFIX + "Destroyed " + (isSelf(player) ? "your" : Nickname.of(player) + "'s") + " nameplate entity");
	}

	@Path("debug")
	@Permission(Group.ADMIN)
	@Description("Toggle debug mode")
	void debug() {
		Nameplates.toggleDebug();
		send(PREFIX + "Debug " + (Nameplates.isDebug() ? "&aenabled" : "&cdisabled"));
	}

	@Path("debug <player>")
	@Permission(Group.ADMIN)
	@Description("Debug a player's nameplate")
	void debug(Player player) {
		send(StringUtils.toPrettyString(NameplateManager.get(player)));
	}

	@Path("npcs fix")
	@Permission(Group.ADMIN)
	@Description("Attempt to fix NPC nameplates")
	void npcs_fix() {
		Nameplates.fixNPCNameplates();
	}

}
