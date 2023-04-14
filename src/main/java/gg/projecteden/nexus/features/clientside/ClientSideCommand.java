package gg.projecteden.nexus.features.clientside;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.ErasureType;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.RangeArgumentValidator.Range;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideConfigService;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.clientside.ClientSideUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.features.clientside.ClientSideEntitiesManager.debug;
import static gg.projecteden.nexus.utils.StringUtils.getShortLocationString;

@NoArgsConstructor
public class ClientSideCommand extends CustomCommand implements Listener {
	private final ClientSideConfigService configService = new ClientSideConfigService();
	private final ClientSideConfig config = configService.get0();
	private final ClientSideUserService userService = new ClientSideUserService();
	private ClientSideUser user;

	public ClientSideCommand(@NonNull CommandEvent event) {
		super(event);
		user = userService.get(player());
	}

	private void saveConfig() {
		configService.save(config);
	}

	@Permission(Group.ADMIN)
	@Description("Toggle debug mode")
	void debug(@Optional Boolean state) {
		if (state == null)
			state = !debug;

		debug = state;
		send(PREFIX + "Debug " + (debug ? "&aenabled" : "&cdisabled"));
	}

	@Permission(Group.ADMIN)
	@Description("Toggle seeing all entities regardless of whether they are hidden")
	void edit(@Optional Boolean state) {
		if (state == null)
			state = !user.isEditing();

		user.setEditing(state);
		send(PREFIX + (state ? "&aEnabled" : "&cDisabled") + " edit mode");

		for (var entity : ClientSideConfig.getEntities(world()))
			user.updateVisibility(entity);
	}

	@Async
	@Permission(Group.ADMIN)
	@Description("List client side entities")
	void entities_list(
		@Optional("1") int page,
		@Optional @Switch World world,
		@Optional @Switch World excludeWorld,
		@Optional @Switch ClientSideEntityType type,
		@Optional @Switch boolean onlyHidden,
		@Optional @Switch boolean onlyShown
	) {
		var entities = ClientSideConfig.getAllEntities().stream()
			.filter(entity -> world  == null || world.equals(entity.location().getWorld()))
			.filter(entity -> excludeWorld  == null || !excludeWorld.equals(entity.location().getWorld()))
			.filter(entity -> type == null || entity.getType() == type)
			.filter(entity -> !onlyHidden || entity.isHidden())
			.filter(entity -> !onlyShown || !entity.isHidden())
			.toList();

		if (isNullOrEmpty(entities))
			error("No matching entities found");

		send(PREFIX + "Matching entities  |  Total: &e" + entities.size());

		String command = "/clientside entities list";
		if (world != null) command += " --world=" + world.getName();
		if (excludeWorld != null) command += " --excludeWorld=" + excludeWorld.getName();
		if (type != null) command += " --type=" + type;
		command += " --onlyHidden=" + onlyHidden;
		command += " --onlyShown=" + onlyShown;

		final BiFunction<IClientSideEntity<?, ?, ?>, String, JsonBuilder> formatter = (entity, index) -> json("&3" + index + " &e" + camelCase(entity.getType()) + " &7- " + getShortLocationString(entity.location()))
			.command(StringUtils.getTeleportCommand(entity.location()));

		paginate(entities, formatter, command, page);
	}

	@Permission(Group.ADMIN)
	@Description("Convert the target entity to a client side entity")
	void entities_create() {
		final var target = getTargetEntityRequired();
		if (ClientSideConfig.isIgnoredEntity(target))
			error("You cannot convert that entity, it is marked as ignored");

		ClientSideConfig.createEntity(ClientSideEntityType.createFrom(target));
		saveConfig();
		target.remove();
		send(PREFIX + "Created client side " + camelCase(target.getType()));
	}

	// TODO Load chunks & their entities asynchronously for processing
	// https://discord.com/channels/289587909051416579/555462289851940864/1003043232231477308
	// https://github.com/PaperMC/Paper/pull/7628
	// https://paste.projecteden.gg/iniqe.java

	@Permission(Group.ADMIN)
	@Description("Convert all entities within your selection to client side entities")
	void entities_create_fromSelection(
		@Switch @Optional @ErasureType(ClientSideEntityType.class) List<ClientSideEntityType> types,
		@Switch @Optional boolean ignoreGlowing
	) {
		final Map<EntityType, Integer> counts = new HashMap<>();
		final WorldEditUtils worldedit = new WorldEditUtils(player());
		final Region selection = worldedit.getPlayerSelection(player());

		final List<Chunk> allChunks = selection.getChunks().stream()
			.map(chunk -> world().getChunkAt(chunk.getX(), chunk.getZ()))
			.toList();

		final List<Chunk> loadedChunks = allChunks.stream()
			.filter(Chunk::isEntitiesLoaded)
			.toList();

		final int unloadedChunks = allChunks.size() - loadedChunks.size();

		if (unloadedChunks != 0)
			send(PREFIX + "&cWarning: &3You selected " + unloadedChunks + " unloaded chunks, they will not be processed");

		final List<Entity> entities = loadedChunks.stream()
			.map(chunk -> Arrays.asList(chunk.getEntities()))
			.flatMap(Collection::stream)
			.filter(entity -> selection.contains(worldedit.toBlockVector3(entity.getLocation())))
			.toList();

		if (entities.isEmpty())
			error("No entities found in selection");

		for (Entity entity : entities) {
			if (!ClientSideEntityType.isSupportedType(entity.getType()))
				continue;

			if (ClientSideConfig.isIgnoredEntity(entity))
				continue;

			if (!isNullOrEmpty(types))
				if (!types.contains(ClientSideEntityType.of(entity.getType())))
					continue;

			if (ignoreGlowing)
				if (entity.isGlowing())
					continue;

			ClientSideConfig.createEntity(ClientSideEntityType.createFrom(entity));
			entity.remove();
			counts.put(entity.getType(), counts.getOrDefault(entity.getType(), 0) + 1);
		}

		if (counts.isEmpty())
			error("No matching entities found in selection");

		saveConfig();

		send(PREFIX + "Created &e" + counts.values().stream().mapToInt(Integer::valueOf).sum() + " &3client side entities");
		counts.forEach((type, count) -> send(" &e" + camelCase(type) + " &7- " + count));
	}

	@Permission(Group.ADMIN)
	@Description("Delete all client side entities within your selection")
	void entities_delete_fromSelection(@Switch @Optional @ErasureType(ClientSideEntityType.class) List<ClientSideEntityType> types) {
		final Map<ClientSideEntityType, Integer> counts = new HashMap<>();
		final WorldEditUtils worldedit = new WorldEditUtils(player());
		final Region selection = worldedit.getPlayerSelection(player());

		for (var entity : ClientSideConfig.getEntities(world())) {
			if (!selection.contains(worldedit.toBlockVector3(entity.location())))
				continue;

			if (!isNullOrEmpty(types))
				if (!types.contains(entity.getType()))
					continue;

			ClientSideConfig.delete(entity);
			counts.put(entity.getType(), counts.getOrDefault(entity.getType(), 0) + 1);
		}

		if (counts.isEmpty())
			error("No matching entities found in selection");

		saveConfig();

		send(PREFIX + "Deleted &e" + counts.values().stream().mapToInt(Integer::valueOf).sum() + " &3client side entities");
		counts.forEach((type, count) -> send(" &e" + camelCase(type) + " &7- " + count));
	}

	@Permission(Group.ADMIN)
	@Description("Prevent your target entity from being converted to a client side entity")
	void entities_ignore(@Optional Boolean state) {
		final Entity target = getTargetEntityRequired();
		if (state == null)
			state = !ClientSideConfig.isIgnoredEntity(target);
		if (state) {
			ClientSideConfig.ignoreEntity(target);
			send(PREFIX + "Ignored " + camelCase(target.getType()));
		} else {
			ClientSideConfig.unignoreEntity(target);
			send(PREFIX + "Unignored " + camelCase(target.getType()));
		}
	}

	@Description("Set your client side entity render radius")
	void radius(
		@Range(min = 15, max = 50, bypass = Group.STAFF) int radius,
		@Permission(Group.STAFF) @Optional("self") ClientSideUser user
	) {
		user.setRadius(radius);
		send(PREFIX + "Set entity render radius to &e" + radius + " blocks");
	}

	@EventHandler
	public void on(PlayerUseUnknownEntityEvent event) {
		final var user = new ClientSideUserService().get(event.getPlayer());
		if (!user.isEditing())
			return;

		final var entity = ClientSideConfig.getEntity(event.getPlayer().getWorld(), event.getEntityId());
		if (entity == null)
			return;

		new ClientSideEntityEditorMenu(entity).open(event.getPlayer());
	}

	@Data
	@Rows(1)
	@Title("Client Side Entity Editor")
	private static class ClientSideEntityEditorMenu extends InventoryProvider {
		private final IClientSideEntity<?, ?, ?> entity;

		public void init() {
			addCloseItem();

			final ItemBuilder toggleHidden;
			if (entity.isHidden())
				toggleHidden = new ItemBuilder(Material.MILK_BUCKET).name("&aShow");
			else
				toggleHidden = new ItemBuilder(Material.POTION).potionType(PotionType.INVISIBILITY).name("&cHide");

			contents.set(0, 3, ClickableItem.of(toggleHidden, e -> ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					entity.setHidden(!entity.isHidden());
					ClientSideConfig.onUpdateVisibility(entity);
					ClientSideConfig.save();
				})
				.onFinally(e2 -> refresh())
				.open(viewer)));

			contents.set(0, 5, ClickableItem.of(Material.PANDA_SPAWN_EGG, "&aSpawn", e -> ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					ClientSideConfig.delete(entity);
					ClientSideConfig.save();
					viewer.closeInventory();
					entity.spawn();
				})
				.onCancel(e2 -> refresh())
				.open(viewer)));

			contents.set(0, 8, ClickableItem.of(Material.TNT, "&cDelete", e -> ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					ClientSideConfig.delete(viewer.getWorld(), entity.id());
					ClientSideConfig.save();
					viewer.closeInventory();
				})
				.onCancel(e2 -> refresh())
				.open(viewer)));
		}

	}

}
