package gg.projecteden.nexus.features.minigames.listeners;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityInteractEvent;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityTargetEndEvent;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityTargetTickEvent;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.lobby.MinigameInviter;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.menus.annotations.Scroll;
import gg.projecteden.nexus.features.minigames.menus.lobby.ArenasMenu;
import gg.projecteden.nexus.features.minigames.menus.lobby.MechanicSubGroupMenu;
import gg.projecteden.nexus.features.minigames.menus.lobby.ScrollableMechanicSubGroupMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicSubGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import lombok.NoArgsConstructor;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@NoArgsConstructor
public class SignListener implements Listener {
	public static final String HEADER = "< Minigames >";

	@EventHandler
	public void onClickOnSign(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (event.getClickedBlock() == null || !MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
		if (!Minigames.isMinigameWorld(event.getPlayer().getWorld())) return;

		boolean leftClick = ActionGroup.LEFT_CLICK.applies(event);
		if (leftClick && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

		Sign sign = (Sign) event.getClickedBlock().getState();

		if (HEADER.equals(StringUtils.stripColor(sign.getLine(0)))) {
			switch (StringUtils.stripColor(sign.getLine(1).toLowerCase())) {
				case "join" -> {
					Arena arena;
					try {
						arena = ArenaManager.find(sign.getLine(2));
					} catch (Exception ex) {
						PlayerUtils.send(event.getPlayer(), Minigames.PREFIX + ex.getMessage());
						break;
					}
					if (leftClick) {
						Match match = MatchManager.find(arena);
						int players = match == null ? 0 : match.getMinigamers().size();
						boolean canJoin = match == null || !match.isStarted() || arena.canJoinLate();

						JsonBuilder builder = new JsonBuilder(NamedTextColor.GOLD);
						builder.newline(false).next(arena.getDisplayName(), NamedTextColor.DARK_AQUA, TextDecoration.BOLD);
						builder.newline(false).next("Gamemode: ").next(arena.getMechanic().getName(), NamedTextColor.YELLOW);

						String descriptionText = arena.getMechanic().getDescription();
						if (!descriptionText.isEmpty() && !descriptionText.equalsIgnoreCase("todo"))
							builder.newline(false).next("Description: ").next(new JsonBuilder(descriptionText).color(NamedTextColor.YELLOW));

						builder.newline(false).next(String.valueOf(players), NamedTextColor.YELLOW).next("/").next(Component.text(arena.getMaxPlayers(), NamedTextColor.YELLOW))
								.next(Component.text(" players"));

						builder.newline().color(NamedTextColor.GOLD).next("This game is ");
						if (canJoin)
							builder.next("available", NamedTextColor.GREEN);
						else
							builder.next("unavailable", NamedTextColor.RED);
						builder.next(" to join");
						if (canJoin)
							builder.command("/mgm join " + arena.getName()).hover(new JsonBuilder("Click to join the game!", NamedTextColor.DARK_AQUA));

						event.getPlayer().sendMessage(builder, MessageType.SYSTEM);
					} else
						Minigamer.of(event.getPlayer()).join(arena);
				}
				case "quit" -> Minigamer.of(event.getPlayer()).quit();
				case "lobby" -> PlayerUtils.runCommand(event.getPlayer(), "warp minigames");
				case "force start" -> PlayerUtils.runCommandAsOp(event.getPlayer(), "mgm start");
				case "join random" -> PlayerUtils.runCommandAsOp(event.getPlayer(), "mgm join random " + sign.getLine(2));
			}
		}
	}

	@EventHandler
	public void on(CustomBoundingBoxEntityInteractEvent event) {
		final Player player = event.getPlayer();

		try {
			if (PlayerUtils.isWGEdit(player))
				return;

			if (!Minigames.isInMinigameLobby(player))
				return;

			final String id = event.getEntity().getId();
			if (gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(id))
				return;

			if (!id.startsWith(MechanicType.BOUNDING_BOX_ID_PREFIX) && !id.startsWith(MechanicSubGroup.BOUNDING_BOX_ID_PREFIX))
				return;

			final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
			final boolean holdingInvite = CustomMaterial.ENVELOPE_1.is(itemInMainHand);

			if (!Nullables.isNullOrAir(itemInMainHand) && !holdingInvite) {
				if (new CooldownService().check(player, "minigames-sign-interact-holding-item", TickTime.SECOND.x(3)))
					PlayerUtils.send(player, Minigames.PREFIX + "Your hand must be empty to join a game");
				return;
			}

			if (!new CooldownService().check(player, "minigames-sign-interact", TickTime.SECOND))
				return;

			final String mechanicName = id.replace(MechanicType.BOUNDING_BOX_ID_PREFIX, "").replace(MechanicSubGroup.BOUNDING_BOX_ID_PREFIX, "");

			switch (mechanicName) {
				case "mob_arena" -> PlayerUtils.send(player, Minigames.PREFIX + "&cComing soon!");
				case "tictactoe" -> PlayerUtils.runCommand(player, "warp " + mechanicName);
				default -> {
					MechanicType mechanic = null;
					MechanicSubGroup subGroup = null;
					try { mechanic = MechanicType.from(event.getEntity()); }
					catch (Exception ignore) {
						try { subGroup = MechanicSubGroup.from(event.getEntity()); }
						catch (Exception ignore2) { return; }
					}

					if (mechanic == null && subGroup == null)
						return;

					if (mechanic != null && mechanic.get().isTestMode()) {
						PlayerUtils.send(player, Minigames.PREFIX + "&cComing soon!");
						return;
					}

					if (subGroup != null || MechanicSubGroup.isParent(mechanic)) {
						if (subGroup == null)
							subGroup = MechanicSubGroup.from(mechanic);

						boolean scroll = false;
						try {
							scroll = MechanicSubGroup.class.getField(subGroup.name()).isAnnotationPresent(Scroll.class);
						} catch (NoSuchFieldException | SecurityException ignored) {
						}

						if (!scroll)
							new MechanicSubGroupMenu(subGroup).open(player);
						else
							new ScrollableMechanicSubGroupMenu(subGroup).open(player);

						if (holdingInvite) {
							if (MinigameInviter.canSendInvite(player))
								Tasks.wait(1, () -> player.setItemOnCursor(ArenasMenu.getInviteItem(player).build()));
						}
					} else {
						final List<Arena> arenas = ArenaManager.getAllEnabled(mechanic);
						if (arenas.size() == 0)
							PlayerUtils.send(player, Minigames.PREFIX + "&cNo arenas found for " + gg.projecteden.api.common.utils.StringUtils.camelCase(mechanic));
						else if (arenas.size() == 1) {
							if (holdingInvite) {
								invite(player, arenas.get(0));
								return;
							}

							Minigamer.of(player).join(arenas.get(0));
						} else {
							new ArenasMenu(mechanic).open(player);
							if (holdingInvite) {
								if (MinigameInviter.canSendInvite(player))
									Tasks.wait(1, () -> player.setItemOnCursor(ArenasMenu.getInviteItem(player).build()));
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			MenuUtils.handleException(player, Minigames.PREFIX, ex);
		}
	}

	private static void invite(Player player, Arena arena) {
		if (MinigameInviter.canSendInvite(player)) {
			final MinigameInviter invite = Minigames.inviter().create(player, arena);
			if (Rank.of(player).isStaff() && player.isSneaking())
				invite.inviteAll();
			else
				invite.inviteLobby();
		} else {
			PlayerUtils.send(player, Minigames.PREFIX + "&cYou cannot send invites right now");
			player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		}
	}

	@Nullable
	private static MechanicType getMechanic(CustomBoundingBoxEntity entity) {
		try {
			return MechanicType.from(entity);
		} catch (InvalidInputException ex) {
			return null;
		}
	}

	@EventHandler
	public void on(CustomBoundingBoxEntityTargetTickEvent event) {
		final String id = event.getEntity().getId();
		if (gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(id))
			return;

		final Entity outline = event.getEntity().getAssociatedEntity("outline");
		if (outline == null)
			return;

		final String mechanicName = id.replace(MechanicType.BOUNDING_BOX_ID_PREFIX, "").replace(MechanicSubGroup.BOUNDING_BOX_ID_PREFIX, "");

		switch (mechanicName) {
			case "mob_arena" -> PacketUtils.sendFakeDisplayItem(event.getPlayer(), outline, new ItemBuilder(CustomMaterial.IMAGES_OUTLINE_3x2_COMING_SOON).dyeColor("#FD6A02").build());
			case "tictactoe" -> PacketUtils.sendFakeDisplayItem(event.getPlayer(), outline, new ItemBuilder(CustomMaterial.IMAGES_OUTLINE_1x2).dyeColor("#FD6A02").build());
			default -> {
				MechanicType mechanic = null;
				MechanicSubGroup subGroup = null;
				try { mechanic = MechanicType.from(event.getEntity()); }
				catch (Exception ignore) {
					try { subGroup = MechanicSubGroup.from(event.getEntity()); }
					catch (Exception ignore2) { return; }
				}

				if (mechanic == null && subGroup == null)
					return;

				if (mechanic == null)
					mechanic = subGroup.getMechanics().get(0);

				CustomMaterial outlineMaterial = CustomMaterial.IMAGES_OUTLINE_3x2;
				if (mechanic != null) {
					if (mechanic.getGroup() == MechanicGroup.ARCADE)
						outlineMaterial = CustomMaterial.IMAGES_OUTLINE_1x2;

					if (mechanic.get().isTestMode()) {
						outlineMaterial = CustomMaterial.IMAGES_OUTLINE_3x2_COMING_SOON;
						if (mechanic.getGroup() == MechanicGroup.ARCADE)
							outlineMaterial = CustomMaterial.IMAGES_OUTLINE_1x2_COMING_SOON;
					}
				}


				PacketUtils.sendFakeDisplayItem(event.getPlayer(), outline, new ItemBuilder(outlineMaterial).dyeColor("#FD6A02").build());
			}
		}
	}

	@EventHandler
	public void on(CustomBoundingBoxEntityTargetEndEvent event) {
		final String id = event.getEntity().getId();
		if (gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(id))
			return;

		if (!id.startsWith(MechanicType.BOUNDING_BOX_ID_PREFIX) && !id.startsWith(MechanicSubGroup.BOUNDING_BOX_ID_PREFIX))
			return;

		final Entity outline = event.getEntity().getAssociatedEntity("outline");
		if (outline == null)
			return;

		PacketUtils.sendFakeDisplayItem(event.getPlayer(), outline, new ItemStack(Material.AIR));
	}

}

