package gg.projecteden.nexus.hooks.glowapi;

import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.GlowAPI;

import java.util.Collection;

public class GlowAPIHookImpl extends GlowAPIHook {

	@Override
	public void setGlowing(Collection<? extends Entity> entities, GlowColor color, Collection<? extends Player> receivers) {
		GlowAPI.setGlowing(entities, color == null ? null : GlowAPI.Color.valueOf(color.name()), receivers);
	}

}
