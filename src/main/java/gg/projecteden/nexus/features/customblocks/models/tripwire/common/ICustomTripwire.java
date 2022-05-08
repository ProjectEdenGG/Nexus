package gg.projecteden.nexus.features.customblocks.models.tripwire.common;

import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Tripwire;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ICustomTripwire extends ICustomBlock {
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


	// Directional

	default boolean isNorth(@Nullable BlockFace facing) {
		boolean ns = this.isNorth_NS();
		if (!(this instanceof IDirectionalTripwire directional))
			return ns;

		return getFacingBool(facing, ns, directional.isNorth_EW());
	}

	default boolean isSouth(@Nullable BlockFace facing) {
		boolean ns = this.isSouth_NS();
		if (!(this instanceof IDirectionalTripwire directional))
			return ns;

		return getFacingBool(facing, ns, directional.isSouth_EW());
	}

	default boolean isEast(@Nullable BlockFace facing) {
		boolean ns = this.isEast_NS();
		if (!(this instanceof IDirectionalTripwire directional))
			return ns;

		return getFacingBool(facing, ns, directional.isEast_EW());
	}

	default boolean isWest(@Nullable BlockFace facing) {
		boolean ns = this.isWest_NS();
		if (!(this instanceof IDirectionalTripwire directional))
			return ns;

		return getFacingBool(facing, ns, directional.isWest_EW());
	}

	default boolean isAttached(@Nullable BlockFace facing) {
		boolean ns = this.isAttached_NS();
		if (!(this instanceof IDirectionalTripwire directional))
			return ns;

		return getFacingBool(facing, ns, directional.isAttached_EW());
	}

	default boolean isDisarmed(@Nullable BlockFace facing) {
		boolean ns = this.isDisarmed_NS();
		if (!(this instanceof IDirectionalTripwire directional))
			return ns;

		return getFacingBool(facing, ns, directional.isDisarmed_EW());
	}

	default boolean isPowered(@Nullable BlockFace facing) {
		boolean ns = this.isPowered_NS();
		if (!(this instanceof IDirectionalTripwire directional))
			return ns;

		return getFacingBool(facing, ns, directional.isPowered_EW());
	}

	Set<BlockFace> directions = Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

	private boolean getFacingBool(@Nullable BlockFace facing, boolean ns, boolean ew) {
		if (facing == null || !directions.contains(facing))
			return ns;

		switch (facing) {
			case NORTH, SOUTH -> {
				return ns;
			}
			case EAST, WEST -> {
				return ew;
			}
		}

		return ns;
	}

	// Sounds

	@Override
	default @NonNull String getBreakSound() {
		Sound sound = getConfig().breakSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customBreakSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getPlaceSound() {
		Sound sound = getConfig().placeSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customPlaceSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getStepSound() {
		Sound sound = getConfig().stepSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customStepSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getHitSound() {
		Sound sound = getConfig().hitSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customHitSound();
		}

		return customSound;
	}

	@Override
	default @NonNull String getFallSound() {
		Sound sound = getConfig().fallSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customFallSound();
		}

		return customSound;
	}

	@Override
	default BlockData getBlockData(@NotNull BlockFace facing) {
		Tripwire tripwire = (Tripwire) getVanillaBlockMaterial().createBlockData();
		tripwire.setFace(BlockFace.NORTH, isNorth(facing));
		tripwire.setFace(BlockFace.SOUTH, isSouth(facing));
		tripwire.setFace(BlockFace.EAST, isEast(facing));
		tripwire.setFace(BlockFace.WEST, isWest(facing));
		tripwire.setAttached(isAttached(facing));
		tripwire.setDisarmed(isDisarmed(facing));
		tripwire.setPowered(isPowered(facing));
		if (this.isIgnorePowered())
			tripwire.setPowered(false);

		return tripwire;
	}

	@Override
	default boolean equals(@NotNull BlockData blockData, BlockFace facing) {
		if (!(blockData instanceof Tripwire tripwire))
			return false;

		Tripwire _tripwire = (Tripwire) this.getBlockData(facing);
		if (this.isIgnorePowered())
			tripwire.setPowered(false);

		return tripwire.matches(_tripwire);
	}
}
