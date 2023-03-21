package gg.projecteden.nexus.features.minigames.listeners;

import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityInteractEvent;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityTargetEndEvent;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityTargetTickEvent;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.menus.lobby.ArenasMenu;
import gg.projecteden.nexus.features.minigames.menus.lobby.MechanicSubGroupMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicSubGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.NoArgsConstructor;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
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

import java.util.Collections;
import java.util.List;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.NMSUtils.toNMS;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

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

		if (HEADER.equals(stripColor(sign.getLine(0)))) {
			switch (stripColor(sign.getLine(1).toLowerCase())) {
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

			final MechanicType mechanic = MechanicType.from(event.getEntity());

			if (MechanicSubGroup.isParent(mechanic)) {
				new MechanicSubGroupMenu(MechanicSubGroup.from(mechanic)).open(player);
			} else {
				final List<Arena> arenas = ArenaManager.getAllEnabled(mechanic);
				if (arenas.size() == 0) {
					PlayerUtils.send(player, "No arenas found for " + camelCase(mechanic));
				} else if (arenas.size() == 1) {
					PlayerUtils.send(player, "Join " + arenas.get(0).getDisplayName());
				} else
					new ArenasMenu(mechanic).open(player);
			}
		} catch (Exception ex) {
			MenuUtils.handleException(player, Minigames.PREFIX, ex);
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
		final MechanicType mechanic = getMechanic(event.getEntity());
		if (mechanic == null)
			return;

		final Entity outline = event.getEntity().getAssociatedEntity("outline");
		if (outline == null)
			return;

		CustomMaterial outlineMaterial = CustomMaterial.IMAGES_OUTLINE_3x2;
		if (mechanic.getGroup() == MechanicGroup.ARCADE)
			outlineMaterial = CustomMaterial.IMAGES_OUTLINE_1x2;

		final int ITEM_INDEX = 22;
		final var item = new ItemBuilder(outlineMaterial).dyeColor("#FD6A02").build();
		final var dataValue = new DataValue<>(ITEM_INDEX, EntityDataSerializers.ITEM_STACK, toNMS(item));
		final var packet = new ClientboundSetEntityDataPacket(outline.getEntityId(), Collections.singletonList(dataValue));

		PacketUtils.sendPacket(event.getPlayer(), packet);
	}

	@EventHandler
	public void on(CustomBoundingBoxEntityTargetEndEvent event) {
		final MechanicType mechanic = getMechanic(event.getEntity());
		if (mechanic == null)
			return;

		final Entity outline = event.getEntity().getAssociatedEntity("outline");
		if (outline == null)
			return;

		final int ITEM_INDEX = 22;
		final var item = new ItemStack(Material.AIR);
		final var dataValue = new DataValue<>(ITEM_INDEX, EntityDataSerializers.ITEM_STACK, toNMS(item));
		final var packet = new ClientboundSetEntityDataPacket(outline.getEntityId(), Collections.singletonList(dataValue));

		PacketUtils.sendPacket(event.getPlayer(), packet);
	}

}

