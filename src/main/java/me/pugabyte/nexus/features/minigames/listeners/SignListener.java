package me.pugabyte.nexus.features.minigames.listeners;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.ArenaManager;
import me.pugabyte.nexus.features.minigames.managers.MatchManager;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

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
				case "join":
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

						TextComponent.Builder builder = Component.text().color(NamedTextColor.GOLD);
						builder.append(Component.text("\n" + arena.getDisplayName(), NamedTextColor.DARK_AQUA, TextDecoration.BOLD));
						builder.append(Component.text("\nGamemode: " ).append(Component.text(arena.getMechanic().getName(), NamedTextColor.YELLOW)));

						String descriptionText = arena.getMechanic().getDescription();
						if (descriptionText != null && !descriptionText.isEmpty() && !descriptionText.equalsIgnoreCase("todo"))
							builder.append(Component.text("\nDescription: ").append(Component.text(descriptionText, NamedTextColor.YELLOW)));

						builder.append(Component.text("\n"+players, NamedTextColor.YELLOW)).append(Component.text('/').append(Component.text(arena.getMaxPlayers(), NamedTextColor.YELLOW))
								.append(Component.text(" players")));

						TextComponent.Builder availability = Component.text().content("\nThis game is ");
						availability.append(canJoin ? Component.text("available", NamedTextColor.GREEN) : Component.text("unavailable", NamedTextColor.RED));
						availability.append(Component.text(" to join"));
						if (canJoin)
							availability.clickEvent(ClickEvent.runCommand("/mgm join " + arena.getName())).hoverEvent(Component.text("Click to join the game!", NamedTextColor.DARK_AQUA));

						builder.append(availability);

						event.getPlayer().sendMessage(Identity.nil(), builder, MessageType.SYSTEM);
					} else
						PlayerManager.get(event.getPlayer()).join(arena);
					break;
				case "quit":
					PlayerManager.get(event.getPlayer()).quit();
					break;
				case "lobby":
					PlayerUtils.runCommand(event.getPlayer(), "warp minigames");
					break;
				case "force start":
					PlayerUtils.runCommandAsOp(event.getPlayer(), "newmgm start");
					break;
			}
		}
	}

}
