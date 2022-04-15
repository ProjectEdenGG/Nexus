package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.NMSUtils.SoundType;
import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Date;

@Data
public class BrokenBlock {

	private int time;
	private int oldAnimation;
	private double damage = -1;
	private Block block;
	private Date lastDamage;

	public BrokenBlock(Block block, int time) {
		this.block = block;
		this.time = time;
		lastDamage = new Date();
	}

	public void incrementDamage(Player from, double multiplier) {
		if (isBroken()) return;

		damage += multiplier;
		int animation = getAnimation();

		if (animation != oldAnimation) {
			if (animation < 10) {
				sendBreakPacket(animation);
				lastDamage = new Date();
			} else {
				breakBlock(from);
				return;
			}
		}

		oldAnimation = animation;
	}

	public void decrementDamage(double multiplier) {
		if (isBroken()) return;

		damage -= multiplier;
		int animation = getAnimation();

		if (animation != oldAnimation) {
			if (animation > 0) {
				sendBreakPacket(animation);
			} else {
				CustomBlockBreaking.getManager().removeBrokenBlock(this);
				destroyBlock();
				return;
			}
		}

		oldAnimation = animation;
	}

	public boolean isBroken() {
		return getAnimation() >= 10;
	}

	public void breakBlock(Player breaker) {
		destroyBlock();
		BlockUtils.playSound(SoundType.BREAK, block);

		if (breaker == null)
			return;
		BlockBreakingUtils.sendBreakBlock(breaker, block);
	}

	public void destroyBlock() {
		sendBreakPacket(-1);
	}

	public int getAnimation() {
		return (int) (damage / time * 11) - 1;
	}

	public void sendBreakPacket(int animation) {
		BlockBreakingUtils.sendBreakPacket(animation, block);
	}
}
