package me.pugabyte.bncore.models.cooldown;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cooldown {
	@Id
	@NonNull
	private String id;
	@NonNull
	private String type;
	@NonNull
	private double ticks;
	@NonNull
	private LocalDateTime time;

	public Cooldown(String id, String type, double ticks) {
		this.id = id;
		this.type = type;
		this.ticks = ticks;
		update();
	}

	public long getSeconds() {
		return (long) ticks / 20;
	}

	public LocalDateTime getExpiration() {
		return time.plusSeconds(getSeconds());
	}

	public void update() {
		this.time = LocalDateTime.now();
	}

}

