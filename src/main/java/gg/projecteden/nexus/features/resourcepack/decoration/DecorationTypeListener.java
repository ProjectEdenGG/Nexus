package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.MultiState;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Toggleable;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.isSameColor;

@SuppressWarnings("deprecation")
public class DecorationTypeListener implements Listener {

	public DecorationTypeListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getDismounted() instanceof ArmorStand armorStand)) return;
		if (Seat.isSeat(armorStand)) {
			Seat.dismount(player, armorStand);
		}
	}

	@EventHandler
	public void onDropMultiState(ItemSpawnEvent event) {
		final ItemStack item = event.getEntity().getItemStack();
		DecorationConfig config = DecorationConfig.of(item);
		if (config == null)
			return;

		if (!(config instanceof MultiState multiState))
			return;

		ItemStack converted = new ItemBuilder(item)
				.modelId(multiState.getDroppedMaterial().getModelId())
				.build();

		event.getEntity().setItemStack(converted);
	}

	@EventHandler
	public void onToggle(DecorationInteractEvent event) {
		if (event.isCancelled())
			return;

		Decoration decoration = event.getDecoration();
		DecorationConfig config = decoration.getConfig();
		if (!(config instanceof Toggleable toggleable))
			return;

		toggleable.tryToggle(event.getPlayer(), event.getClickedBlock(), decoration.getItemFrame());
	}

	@EventHandler
	public void onColorSign(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		Block clicked = event.getClickedBlock();
		if (clicked == null || !MaterialTag.ALL_SIGNS.isTagged(clicked))
			return;

		if (!(clicked.getState() instanceof Sign sign))
			return;

		Player player = event.getPlayer();
		ItemStack tool = ItemUtils.getTool(player);
		if (Nullables.isNullOrAir(tool))
			return;

		if (DecorationListener.isCancelled(event)) {
			DecorationLang.debug(player, "PlayerInteractEvent was cancelled (onColorSign)");
			return;
		}

		if (DyeStation.isMagicPaintbrush(tool)) {
			DecorationLang.debug(player, " attempting to paint sign");
			tryPaintSign(player, tool, sign, event);
			return;
		}

		Material type = tool.getType();
		SignSide side = sign.getSide(sign.getInteractableSideFor(player));

		if (MaterialTag.DYES.isTagged(type)) {
			DecorationLang.debug(player, " attempting to dye sign");
			tryDyeSign(player, tool, sign, side, event);
			return;
		}

		if (type == Material.GLOW_INK_SAC) {
			DecorationLang.debug(player, " attempting to glow sign");
			tryGlowSign(player, tool, sign, side, event);
			return;
		}

		if (type == Material.INK_SAC) {
			DecorationLang.debug(player, " attempting to unglow sign");
			tryUnglowSign(player, tool, sign, side, event);
		}
	}

	private void tryPaintSign(Player player, ItemStack tool, Sign sign, PlayerInteractEvent event) {
		// TODO DECORATIONS - Remove on release
		if (!DecorationUtils.canUseFeature(event.getPlayer())) {
			return;
		}
		//

		if (!DecorationUtils.canUsePaintbrush(player, tool))
			return;

		SignSide side = sign.getSide(sign.getInteractableSideFor(player));
		if (isSameColor(tool, side))
			return;

		DecorationLang.debug(player, " painting sign...");

		event.setCancelled(true);

		Color paintbrushDye = new ItemBuilder(tool).dyeColor();
		String lineColor = "&" + StringUtils.toHex(paintbrushDye);

		int index = 0;
		for (String line : side.getLines()) {
			if (Nullables.isNotNullOrEmpty(line))
				side.setLine(index, StringUtils.colorize(lineColor + StringUtils.stripColor(line)));
			++index;
		}

		side.setColor(ColorType.ofClosest(paintbrushDye).getDyeColor());
		sign.update();

		DecorationUtils.usePaintbrush(player, tool);
		player.swingMainHand();
	}

	private void tryDyeSign(Player player, ItemStack tool, Sign sign, SignSide side, PlayerInteractEvent event) {
		ColorType colorType = ColorType.of(tool.getType());
		if (colorType == null)
			return;

		DyeColor dyeColor = colorType.getDyeColor();
		if (dyeColor == null)
			return;

		DecorationLang.debug(player, " dyeing sign...");

		new SoundBuilder(Sound.ITEM_DYE_USE).category(SoundCategory.PLAYERS).location(player.getLocation()).play();
		ItemUtils.subtract(player, tool);
		event.setCancelled(true);

		int index = 0;
		for (String line : side.getLines()) {
			if (Nullables.isNotNullOrEmpty(line))
				side.setLine(index, StringUtils.colorize(colorType.getVanillaChatColor() + StringUtils.stripColor(line)));
			++index;
		}

		side.setColor(colorType.getDyeColor());
		sign.update();
	}

	private void tryGlowSign(Player player, ItemStack tool, Sign sign, SignSide side, PlayerInteractEvent event) {
		if (side.isGlowingText())
			return;

		DecorationLang.debug(player, " glowing sign...");

		event.setCancelled(true);
		ItemUtils.subtract(player, tool);
		new SoundBuilder(Sound.ITEM_GLOW_INK_SAC_USE).category(SoundCategory.PLAYERS).location(player.getLocation()).play();

		side.setGlowingText(true);
		sign.update();
	}

	private void tryUnglowSign(Player player, ItemStack tool, Sign sign, SignSide side, PlayerInteractEvent event) {
		if (!side.isGlowingText())
			return;

		DecorationLang.debug(player, " unglowing sign...");

		event.setCancelled(true);
		ItemUtils.subtract(player, tool);
		new SoundBuilder(Sound.ITEM_INK_SAC_USE).category(SoundCategory.PLAYERS).location(player.getLocation()).play();

		side.setGlowingText(false);
		sign.update();
	}
}
