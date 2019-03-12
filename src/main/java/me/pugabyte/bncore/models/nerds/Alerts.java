package me.pugabyte.bncore.models.nerds;

import lombok.Data;
import me.pugabyte.bncore.features.chat.alerts.models.Highlight;

import java.util.List;

@Data
public class Alerts {
	private List<Highlight> highlights;
	private boolean muted;
	private boolean dirty;
}
