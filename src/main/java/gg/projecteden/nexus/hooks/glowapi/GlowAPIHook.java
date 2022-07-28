package gg.projecteden.nexus.hooks.glowapi;

import gg.projecteden.nexus.hooks.IHook;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public class GlowAPIHook extends IHook<GlowAPIHook> {

	public void setGlowing(Collection<? extends Entity> entities, GlowColor color, Collection<? extends Player> receivers) {}

}
