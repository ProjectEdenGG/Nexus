package gg.projecteden.nexus.features.minigames.listeners;

import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicSubGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.NoArgsConstructor;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
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

						// TODO - 1.19.2 Chat Validation Kick
						// event.getPlayer().sendMessage(builder, MessageType.SYSTEM);
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
	public void on(PlayerInteractAtEntityEvent event) {
		if (!Minigames.worldguard().isInRegion(event.getPlayer().getLocation(), "lobby"))
			return;

		if (!(event.getRightClicked() instanceof ArmorStand armorStand))
			return;

		final ItemStack item = armorStand.getItem(EquipmentSlot.HEAD);
		if (item.getType() != Material.PAPER)
			return;

		for (MechanicType mechanic : MechanicType.values()) {
			final ItemBuilder displayImage = mechanic.get().getDisplayImage();
			if (displayImage == null)
				continue;

			if (displayImage.modelId() == ModelId.of(item)) {
				if (MechanicSubGroup.isParent(mechanic)) {
					PlayerUtils.runCommand(event.getPlayer(), "mgm newgl menus subgroup " + mechanic.name());
				} else {
					if (ArenaManager.getAllEnabled(mechanic).size() == 0) {
						PlayerUtils.send(event.getPlayer(), "No arenas found for " + camelCase(mechanic));
					} else if (ArenaManager.getAllEnabled(mechanic).size() == 1) {
						PlayerUtils.send(event.getPlayer(), "Join " + camelCase(mechanic));
					} else {
						PlayerUtils.runCommand(event.getPlayer(), "mgm newgl menus arenas " + mechanic.name());
					}
				}
			}
		}
	}

}

