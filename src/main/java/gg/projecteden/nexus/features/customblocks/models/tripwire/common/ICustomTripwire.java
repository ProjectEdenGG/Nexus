package gg.projecteden.nexus.features.customblocks.models.tripwire.common;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Tripwire;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Environments(Env.TEST)
public interface ICustomTripwire extends ICustomBlock {

	static boolean isNotEnabled() {
		return Nexus.getEnv() == Env.PROD;
	}

	@Override
	default Material getVanillaBlockMaterial() {
		return Material.TRIPWIRE;
	}

	@Override
	default Material getVanillaItemMaterial() {
		return Material.STRING;
	}

	@Override
	default PistonPushAction getPistonPushedAction() {
		return PistonPushAction.BREAK;
	}

	private CustomTripwireConfig getConfig() {
		return getClass().getAnnotation(CustomTripwireConfig.class);
	}

	default boolean isNorth_NS() {
		return getConfig().north_NS();
	}

	default boolean isSouth_NS() {
		return getConfig().south_NS();
	}

	default boolean isEast_NS() {
		return getConfig().east_NS();
	}

	default boolean isWest_NS() {
		return getConfig().west_NS();
	}

	default boolean isAttached_NS() {
		return getConfig().attached_NS();
	}

	default boolean isDisarmed_NS() {
		return getConfig().disarmed_NS();
	}

	default boolean isPowered_NS() {
		return getConfig().powered_NS();
	}

	default boolean isIgnorePowered() {
		return getConfig().ignorePowered();
	}

	// Directional / Waterlogged

	default boolean isNorth(@Nullable BlockFace facing, Material underneathType) {
		if (facing == BlockFace.UP)
			return true;

		boolean ns = this.isNorth_NS();

		if (this instanceof IDirectionalTripwire directional)
			return getFacingBool(facing, ns, directional.isNorth_EW());

		if (this instanceof IWaterLogged waterLogged && underneathType == Material.WATER)
			return waterLogged.isWaterLoggedNorth_NS();

		return ns;
	}

	default boolean isSouth(@Nullable BlockFace facing, Material underneathType) {
		if (facing == BlockFace.UP)
			return true;

		boolean ns = this.isSouth_NS();

		if (this instanceof IDirectionalTripwire directional)
			return getFacingBool(facing, ns, directional.isSouth_EW());

		if (this instanceof IWaterLogged waterLogged && underneathType == Material.WATER)
			return waterLogged.isWaterLoggedSouth_NS();

		return ns;
	}

	default boolean isEast(@Nullable BlockFace facing, Material underneathType) {
		if (facing == BlockFace.UP)
			return true;

		boolean ew = this.isEast_NS();

		if (this instanceof IDirectionalTripwire directional)
			return getFacingBool(facing, ew, directional.isEast_EW());

		if (this instanceof IWaterLogged waterLogged && underneathType == Material.WATER)
			return waterLogged.isWatterLoggedEast_NS();

		return ew;
	}

	default boolean isWest(@Nullable BlockFace facing, Material underneathType) {
		if (facing == BlockFace.UP)
			return true;

		boolean ew = this.isWest_NS();

		if (this instanceof IDirectionalTripwire directional)
			return getFacingBool(facing, ew, directional.isWest_EW());

		if (this instanceof IWaterLogged waterLogged && underneathType == Material.WATER)
			return waterLogged.isWaterLoggedWest_NS();

		return ew;
	}

	default boolean isAttached(@Nullable BlockFace facing, Material underneathType) {
		boolean attached = this.isAttached_NS();

		if (this instanceof IDirectionalTripwire directional)
			return getFacingBool(facing, attached, directional.isAttached_EW());

		if (this instanceof IWaterLogged waterLogged && underneathType == Material.WATER)
			return waterLogged.isWaterLoggedAttached_NS();

		return attached;
	}

	default boolean isDisarmed(@Nullable BlockFace facing, Material underneathType) {
		boolean disarmed = this.isDisarmed_NS();

		if (this instanceof IDirectionalTripwire directional)
			return getFacingBool(facing, disarmed, directional.isDisarmed_EW());

		if (this instanceof IWaterLogged waterLogged && underneathType == Material.WATER)
			return waterLogged.isWaterLoggedDisarmed_NS();

		return disarmed;
	}

	default boolean isPowered(@Nullable BlockFace facing, Material underneathType) {
		boolean powered = this.isPowered_NS();

		if (this instanceof IDirectionalTripwire directional)
			return getFacingBool(facing, powered, directional.isPowered_EW());

		if (this instanceof IWaterLogged waterLogged && underneathType == Material.WATER)
			return waterLogged.isWaterLoggedPowered_NS();

		return powered;
	}

	Set<BlockFace> directions = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

	private boolean getFacingBool(@Nullable BlockFace facing, boolean ns, boolean ew) {
		if (facing == null || !directions.contains(facing))
			return ns;

		return switch (facing) {
			case EAST, WEST -> ew;
			default -> ns;
		};
	}

	// Sounds

	@Override
	default @NonNull String getBreakSound() {
		Sound sound = getConfig().breakSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.UI_BUTTON_CLICK)) {
			customSound = getConfig().customBreakSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getPlaceSound() {
		Sound sound = getConfig().placeSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.UI_BUTTON_CLICK)) {
			customSound = getConfig().customPlaceSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getStepSound() {
		Sound sound = getConfig().stepSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.UI_BUTTON_CLICK)) {
			customSound = getConfig().customStepSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getHitSound() {
		Sound sound = getConfig().hitSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.UI_BUTTON_CLICK)) {
			customSound = getConfig().customHitSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getFallSound() {
		Sound sound = getConfig().fallSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.UI_BUTTON_CLICK)) {
			customSound = getConfig().customFallSound();
		}

		return customSound;
	}

	@Override
	default BlockData getBlockData(@NotNull BlockFace facing, @Nullable Block underneath) {
		Material underneathType = Material.AIR;
		if (underneath != null)
			underneathType = underneath.getType();

		Tripwire tripwire = (Tripwire) getVanillaBlockMaterial().createBlockData();
		tripwire.setFace(BlockFace.NORTH, isNorth(facing, underneathType));
		tripwire.setFace(BlockFace.EAST, isEast(facing, underneathType));
		tripwire.setFace(BlockFace.SOUTH, isSouth(facing, underneathType));
		tripwire.setFace(BlockFace.WEST, isWest(facing, underneathType));
		tripwire.setAttached(isAttached(facing, underneathType));
		tripwire.setDisarmed(isDisarmed(facing, underneathType));
		tripwire.setPowered(isPowered(facing, underneathType));
		if (this.isIgnorePowered())
			tripwire.setPowered(false);

		return tripwire;
	}

	@Override
	default String getStringBlockData(BlockData blockData) {
		return CustomBlockUtils.getBlockDataString((Tripwire) blockData);
	}

	@Override
	default boolean equals(@NotNull BlockData blockData, BlockFace facing, @Nullable Block underneath) {
		if (!(blockData instanceof Tripwire tripwire))
			return false;

		Tripwire _tripwire = (Tripwire) this.getBlockData(facing, underneath);
		if (this.isIgnorePowered())
			tripwire.setPowered(false);

		return tripwire.matches(_tripwire);
	}
}
