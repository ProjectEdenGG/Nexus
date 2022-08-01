package gg.projecteden.nexus.features.crates;

import gg.projecteden.nexus.features.crates.menus.CrateEditMenu.CrateEditProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.crate.CrateConfigService;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Aliases("crates")
public class CrateCommand extends CustomCommand {

	public CrateCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void warp() {
		runCommand("warp crates");
	}

	@Path("info")
	void info() {
		line();
		send("&3Hi there, I'm &eBlast.");
		line();
		send("&3These here are our server's &eCrates&3. They can give you amazing rewards to help boost your survival experience.");
		send("&3To open a Crate, you must have a &eCrate Key&3. You can get these from &evoting&3, &eevents&3, &eand more&3!");
		line();
		send("&3To &epreview rewards&3, you can &eright-click with an empty hand &3to open a preview menu.");
		send("&3To &eopen multiple at a time&3, simply &eshift-click &3with multiple keys in your hand.");
		line();
		send("&3I hope you enjoy, and have a good day!");
	}

	@Path("toggle")
	@Permission(Group.ADMIN)
	void toggle() {
		CrateConfigService.get().setEnabled(!CrateConfigService.get().isEnabled());
		send(PREFIX + "Crates " + (CrateConfigService.get().isEnabled() ? "&aenabled" : "&cdisabled"));
	}

	@Path("give <type> [player] [amount]")
	@Permission(Group.ADMIN)
	void key(CrateType type, @Arg("self") OfflinePlayer player, @Arg("1") Integer amount) {
		type.give(player, amount);
		if (player.isOnline())
			send(player.getPlayer(), Crates.PREFIX + "You have been given &e" + amount + " " + StringUtils.camelCase(type.name()) +
					" Crate Key" + (amount == 1 ? "" : "s"));
		if (!isSelf(player))
			send(Crates.PREFIX + "You gave &e" + amount + " " + StringUtils.camelCase(type.name()) + " Crate Key" +
					(amount == 1 ? "" : "s") + "  &3to &e" + Nickname.of(player));
	}

	@Path("edit [filter]")
	@Permission(Group.ADMIN)
	void edit(CrateType filter) {
		new CrateEditProvider(filter, null).open(player());
	}

	@Path("entities add <type> [uuid]")
	@Permission(Group.ADMIN)
	void entitiesAdd(CrateType type, UUID uuid) {
		Entity entity;
		if (uuid == null)
			entity = getTargetEntity();
		else {
			entity = Bukkit.getEntity(uuid);
			if (entity == null)
				error("Invalid entity UUID");
		}
		if (entity == null)
			error("You must be looking at an entity or specify a UUID");
		if (!CrateConfigService.get().getCrateEntities().containsKey(type))
			CrateConfigService.get().getCrateEntities().put(type, new ArrayList<>());
		if (CrateConfigService.get().getCrateEntities().get(type).contains(uuid))
			error("That entity is already registered to that type");
		CrateConfigService.get().getCrateEntities().get(type).add(uuid);
		CrateConfigService.get().save();
		send(PREFIX + "Added " + uuid + " to " + camelCase(type));
	}

	@Path("entities remove <type> <uuid>")
	@Permission(Group.ADMIN)
	void entitiesRemove(CrateType type, UUID uuid) {
		if (!CrateConfigService.get().getCrateEntities().containsKey(type) || CrateConfigService.get().getCrateEntities().get(type).isEmpty()) {
			CrateConfigService.get().getCrateEntities().put(type, new ArrayList<>());
			CrateConfigService.get().save();
			error("That type has no registered entities");
		}
		if (!CrateConfigService.get().getCrateEntities().get(type).contains(uuid))
			error("That entity is not registered to that type");
		CrateConfigService.get().getCrateEntities().get(type).remove(uuid);
		CrateConfigService.get().save();
		send(PREFIX + "Removed " + uuid + " from " + camelCase(type));
	}

	@Path("entities list [type]")
	@Permission(Group.ADMIN)
	void entitiesList(CrateType type) {
		send(Crates.PREFIX + "Crate entities:");
		if (type == null)
			Arrays.stream(CrateType.values()).forEach(type2 -> {
				list(type2);
				line();
			});
		else
			list(type);
	}

	public void list(CrateType type) {
		send("&3" + camelCase(type) + ":");
		if (!CrateConfigService.get().getCrateEntities().containsKey(type) || CrateConfigService.get().getCrateEntities().get(type).isEmpty())
			send(PREFIX + "&cNo entities for this type");
		else {
			for (UUID uuid : CrateConfigService.get().getCrateEntities().get(type)) {
				Entity entity = Bukkit.getEntity(uuid);
				if (entity == null)
					continue;
				Location loc = entity.getLocation();

				String tpCommand = String.format("/tppos %s %s %s %s", loc.getX(), loc.getY(), loc.getZ(), loc.getWorld());
				send(json("&e" + camelCase(entity.getType())).hover("&eClick to teleport").command(tpCommand)
					     .next(" &7[Copy UUID]").copy(entity.getUniqueId().toString()));
			}
		}
	}

}
