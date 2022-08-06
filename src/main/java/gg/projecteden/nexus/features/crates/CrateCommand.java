package gg.projecteden.nexus.features.crates;

import gg.projecteden.nexus.features.crates.menus.CrateEditMenu;
import gg.projecteden.nexus.features.crates.menus.CrateEditMenu.CrateEditProvider;
import gg.projecteden.nexus.features.crates.menus.CratePreviewProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.crate.CrateConfig;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateConfigService;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
		CrateConfig config = CrateConfigService.get();
		config.setEnabled(!config.isEnabled());
		config.save();
		send(PREFIX + "Crates " + (config.isEnabled() ? "&aenabled" : "&cdisabled"));
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

	@Path("reset <type> <uuid>")
	void reset(CrateType type, @Arg(context = 1) CrateEntity uuid) {
		Entity entity = Bukkit.getEntity(uuid.getUuid());
		if (entity == null)
			error("Invalid entity");
		CrateHandler.reset(entity);
		send(PREFIX + "Reset " + uuid.getUuid());
	}

	@Path("edit [filter]")
	@Permission(Group.ADMIN)
	void edit(CrateType filter) {
		new CrateEditProvider(filter, null).open(player());
	}

	@Path("edit announcement reset <id>")
	@Permission(Group.ADMIN)
	void resetAnnouncement(int id) {
		CrateLoot loot = CrateLoot.byId(id);
		if (loot == null)
			error("Unknown loot id");
		loot.setAnnouncement(null);
		loot.setShouldAnnounce(false);
		CrateConfigService.get().save();
		new CrateEditMenu.LootSettingsProvider(loot.getType(), loot).open(player());
	}

	@Path("edit announcement set <id> <message...>")
	@Permission(Group.ADMIN)
	void setAnnouncement(int id, String message) {
		CrateLoot loot = CrateLoot.byId(id);
		if (loot == null)
			error("Unknown loot id");
		loot.setAnnouncement(message);
		loot.setShouldAnnounce(true);
		CrateConfigService.get().save();
		new CrateEditMenu.LootSettingsProvider(loot.getType(), loot).open(player());
	}
	
	@Path("preview <type>")
	@Permission(Group.ADMIN)
	void preview(CrateType type) {
		new CratePreviewProvider(type, null).open(player());
	}

	@Path("entities add <type> <uuid>")
	@Permission(Group.ADMIN)
	void entitiesAdd(CrateType type, UUID uuid) {
		if (uuid == null)
			error("Invalid UUID");
		CrateConfig config = CrateConfigService.get();
		if (!config.getCrateEntities().containsKey(type))
			config.getCrateEntities().put(type, new ArrayList<>());
		if (config.getCrateEntities().get(type).contains(uuid))
			error("That entity is already registered to that type");
		config.getCrateEntities().get(type).add(uuid);
		config.save();
		send(PREFIX + "Added " + uuid + " to " + camelCase(type));
	}

	@Path("entities remove <type> <uuid>")
	@Permission(Group.ADMIN)
	void entitiesRemove(CrateType type, @Arg(context = 1) CrateEntity uuid) {
		CrateConfig config = CrateConfigService.get();
		if (!config.getCrateEntities().containsKey(type) || config.getCrateEntities().get(type).isEmpty()) {
			config.getCrateEntities().put(type, new ArrayList<>());
			config.save();
			error("That type has no registered entities");
		}
		if (!config.getCrateEntities().get(type).contains(uuid.getUuid()))
			error("That entity is not registered to that type");
		config.getCrateEntities().get(type).remove(uuid.getUuid());
		config.save();
		send(PREFIX + "Removed " + uuid.getUuid() + " from " + camelCase(type));
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

	@Path("open <type> <uuid> [amount]")
	@Permission(Group.ADMIN)
	void open(CrateType type, @Arg(context = 1) CrateEntity uuid, @Arg("1") int amount) {
		CrateHandler.openCrate(type, (ArmorStand) Bukkit.getEntity(uuid.getUuid()), player(), amount);
	}

	public void list(CrateType type) {
		send("&3" + camelCase(type) + ":");
		if (!CrateConfigService.get().getCrateEntities().containsKey(type) || CrateConfigService.get().getCrateEntities().get(type).isEmpty())
			send("&cNo entities for this type");
		else {
			for (UUID uuid : CrateConfigService.get().getCrateEntities().get(type)) {
				Entity entity = Bukkit.getEntity(uuid);
				if (entity == null)
					continue;
				Location loc = entity.getLocation();

				String tpCommand = String.format("/tppos %s %s %s %s", loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName());
				send(json("&e" + camelCase(entity.getType())).hover("&eClick to teleport").command(tpCommand).group()
					     .next(" &7[Copy UUID]").hover("&eShift-Click to copy").copy(entity.getUniqueId().toString()));
			}
		}
	}

	@Data
	public static class CrateEntity {
		private final UUID uuid;
	}

	@ConverterFor(CrateEntity.class)
	CrateEntity convertToCrateEntity(String value) {
		return new CrateEntity(UUID.fromString(value));
	}

	@TabCompleterFor(CrateEntity.class)
	List<String> tabCompleteCrateEntity(String filter, CrateType context) {
		return CrateConfigService.get().getCrateEntities().getOrDefault(context, new ArrayList<>()).stream()
			       .map(UUID::toString)
			       .filter(str -> argsString().startsWith(filter))
			       .collect(Collectors.toList());
	}

}
