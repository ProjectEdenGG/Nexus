package me.pugabyte.bncore.features.staff.antibots.models;

import java.time.LocalDateTime;

public class DeniedEntry {
	private String ip;
	private String uuid;
	private String name;
	private LocalDateTime timestamp;

	public DeniedEntry(String ip, String uuid, String name) {
		this.ip = ip;
		this.uuid = uuid;
		this.name = name;
		this.timestamp = LocalDateTime.now();
	}

	public DeniedEntry(String ip, String uuid, String name, LocalDateTime timestamp) {
		this.ip = ip;
		this.uuid = uuid;
		this.name = name;
		this.timestamp = timestamp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
