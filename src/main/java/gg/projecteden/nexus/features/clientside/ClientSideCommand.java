package gg.projecteden.nexus.features.clientside;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.listeners.events.PlayerChangingWorldsEvent;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
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
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionType;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@Permission(Group.ADMIN)
public class ClientSideCommand extends CustomCommand {
	private final ClientSideConfigService configService = new ClientSideConfigService();
	private final ClientSideConfig config = configService.get0();
	private final ClientSideUserService userService = new ClientSideUserService();
	private final ClientSideUser user;

	public ClientSideCommand(@NonNull CommandEvent event) {
		super(event);
		user = userService.get(player());
	}

	@Path("edit [state]")
	void edit(Boolean state) {
		if (state == null)
			state = !user.isEditing();

		user.setEditing(state);
		userService.save(user);
		send(PREFIX + (state ? "&aEnabled" : "&cDisabled") + " edit mode");
	}

	@Path("entities create")
	void entities_create() {
		final var target = getTargetEntityRequired();
		final var clientSideEntity = ClientSideEntityType.of(target);
		ClientSideConfig.getEntities().add(clientSideEntity);
		saveConfig();
		target.remove();
		send(PREFIX + "Created client side " + camelCase(clientSideEntity.getType()));
	}

	private void saveConfig() {
		configService.save(config);
	}

	@Path("entities send all")
	void entities_send_all() {
		final var entities = ClientSideConfig.getEntities(world());
		user.send(entities);
		send(PREFIX + "Sent " + entities.size() + " client side entities");
	}

	@EventHandler
	public void on(PlayerChangingWorldsEvent event) {
		new ClientSideUserService().edit(event.getPlayer(), ClientSideUser::destroyAll);
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		new ClientSideUserService().edit(event.getPlayer(), ClientSideUser::destroyAll);
	}

	@EventHandler
	public void on(PlayerUseUnknownEntityEvent event) {
		final ClientSideUser user = new ClientSideUserService().get(event.getPlayer());
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
				.onConfirm(e2 -> entity.setHidden(!entity.isHidden()))
				.onFinally(e2 -> refresh())
				.open(player)));

			contents.set(0, 5, ClickableItem.of(Material.PANDA_SPAWN_EGG, "&aSpawn", e -> ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					entity.spawn();
					player.closeInventory();
				})
				.onCancel(e2 -> refresh())
				.open(player)));

			contents.set(0, 8, ClickableItem.of(Material.TNT, "&cDelete", e -> ConfirmationMenu.builder()
				.onConfirm(e2 -> ClientSideConfig.deleteEntity(entity.id()))
				.onFinally(e2 -> refresh())
				.open(player)));
		}

	}

}
