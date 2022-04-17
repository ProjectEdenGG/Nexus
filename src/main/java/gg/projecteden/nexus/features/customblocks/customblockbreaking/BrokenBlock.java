package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.NMSUtils.SoundType;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.utils.MathUtils;
import lombok.Data;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Data
public class BrokenBlock {
	@Getter
	private static final DecimalFormat df = new DecimalFormat("#.###");

	private Block block;
	private double hardness;
	private double damage = 0;
	private int damageFrame = 0;

	private LocalDateTime lastDamage;

	public BrokenBlock(Block block) {
		this.block = block;
		this.hardness = NMSUtils.getBlockHardness(block);
		this.lastDamage = LocalDateTime.now();
	}

	public void incrementDamage(Player from, double multiplier) {
		if (isBroken())
			return;

		this.lastDamage = LocalDateTime.now();
		Dev.WAKKA.send("Inc Damage: " + getDf().format(this.damage) + " --> " + getDf().format(this.damage + multiplier));
		this.damage += multiplier;
		updateFrame(true);

		if (this.damageFrame == 10)
			breakBlock(from);
	}

	public void decrementDamage() {
		decrementDamage(hardness / 10);
	}

	public void decrementDamage(double multiplier) {
		if (isBroken() || !isDamaged())
			return;

		Dev.WAKKA.send("Dec Damage: " + getDf().format(this.damage) + " --> " + getDf().format(this.damage - multiplier));
		this.damage -= multiplier;
		updateFrame(false);
	}

	private void updateFrame(boolean increment) {
		int count = 1;
		if (!increment)
			count = -1;

		int newFrame = getFrame() + count;
		if (newFrame == this.damageFrame)
			return;

		this.damageFrame = MathUtils.clamp(newFrame, -1, 10);
		if (this.damageFrame == -1) {
			resetBreakPacket();
		} else
			sendBreakPacket(this.damageFrame);
	}

	public boolean isDamaged() {
		return this.damage > 0.0;
	}

	public boolean isBroken() {
		return getDamageFrame() == 10;
	}

	public void breakBlock(Player breaker) {
		resetBreakPacket();
		BlockUtils.playSound(SoundType.BREAK, this.block);
		CustomBlockBreaking.getManager().removeBrokenBlock(this);

		if (breaker == null)
			return;
		BlockBreakingUtils.sendBreakBlock(breaker, this.block);
	}

	public void resetBreakPacket() {
		sendBreakPacket(-1);
	}

	public int getFrame() {
		return (int) ((this.damage / (this.hardness / 10.0)) - 1);
	}

	public void sendBreakPacket(int animation) {
		BlockBreakingUtils.sendBreakPacket(animation, this.block);
	}
}
