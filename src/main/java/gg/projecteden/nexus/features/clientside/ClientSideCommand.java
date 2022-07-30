package gg.projecteden.nexus.features.clientside;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.listeners.events.PlayerChangingWorldEvent;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideConfigService;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.clientside.ClientSideUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionType;

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

	private void saveUser() {
		userService.save(user);
	}

	@Path("edit [state]")
	@Permission(Group.ADMIN)
	void edit(Boolean state) {
		if (state == null)
			state = !user.isEditing();

		user.setEditing(state);
		saveUser();
		send(PREFIX + (state ? "&aEnabled" : "&cDisabled") + " edit mode");

		for (var entity : ClientSideConfig.getEntities(world()))
			user.updateVisibility(entity);
	}

	@Path("entities create")
	@Permission(Group.ADMIN)
	void entities_create() {
		final var target = getTargetEntityRequired();
		final var clientSideEntity = ClientSideEntityType.of(target);
		ClientSideConfig.create(clientSideEntity);
		saveConfig();
		target.remove();
		user.send(clientSideEntity);
		send(PREFIX + "Created client side " + camelCase(clientSideEntity.getType()));
	}

	@Path("entities hide all")
	@Permission(Group.ADMIN)
	void entities_hide_all() {
		send(PREFIX + "Hid " + user.destroyAll() + " client side entities");
	}

	@Path("entities show all")
	@Permission(Group.ADMIN)
	void entities_show_all() {
		final var entities = ClientSideConfig.getEntities(world());
		user.forceSend(entities);
		send(PREFIX + "Sent " + entities.size() + " client side entities");
	}

	@Path("entities (hide|show) (reset|normal)")
	@Permission(Group.ADMIN)
	void entities_show_normal() {
		final var entities = ClientSideConfig.getEntities(world());
		user.updateVisibility(entities);
		send(PREFIX + "Updated visibility of " + entities.size() + " client side entities");
	}

	@Path("radius <radius> [user]")
	void toggle(@Arg(min = 15, max = 50) int radius, ClientSideUser user) {
		user.setRadius(radius);
		userService.save(user);

		send(PREFIX + "Set entity render radius to &e" + radius + " blocks");
	}

	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		new ClientSideUserService().edit(event.getPlayer(), ClientSideUser::sendAll);
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		new ClientSideUserService().edit(event.getPlayer(), ClientSideUser::sendAll);
	}

	@EventHandler
	public void on(PlayerChangingWorldEvent event) {
		new ClientSideUserService().edit(event.getPlayer(), ClientSideUser::destroyAll);
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		new ClientSideUserService().edit(event.getPlayer(), ClientSideUser::destroyAll);
	}

	@EventHandler
	public void on(PlayerUseUnknownEntityEvent event) {
		final var user = new ClientSideUserService().get(event.getPlayer());
		if (!user.isEditing())
			return;

		final var entity = ClientSideConfig.getEntity(event.getEntityId());
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
				.open(player)));

			contents.set(0, 5, ClickableItem.of(Material.PANDA_SPAWN_EGG, "&aSpawn", e -> ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					ClientSideConfig.delete(entity);
					ClientSideConfig.save();
					player.closeInventory();
					entity.spawn();
				})
				.onCancel(e2 -> refresh())
				.open(player)));

			contents.set(0, 8, ClickableItem.of(Material.TNT, "&cDelete", e -> ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					ClientSideConfig.delete(entity.id());
					ClientSideConfig.save();
					player.closeInventory();
				})
				.onCancel(e2 -> refresh())
				.open(player)));
		}

	}

}
