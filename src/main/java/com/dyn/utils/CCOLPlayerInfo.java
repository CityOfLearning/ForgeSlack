package com.dyn.utils;

import java.util.UUID;

public class CCOLPlayerInfo {

	private UUID user_id;
	private String minecraft_name = "";
	private UUID ccol_id;
	private int program_id = -1;
	private String ccol_name = "";
	private String username = "";
	private String display_name = "";
	private String password = "";

	public CCOLPlayerInfo(String mc_username) {

	}

	public CCOLPlayerInfo(UUID ccol_id, int program_id, String name, String username, boolean grabAllData) {

	}

	public UUID getCCOLid() {
		return ccol_id;
	}

	public String getCCOLName() {
		return ccol_name;
	}

	public String getDisplayName() {
		return display_name;
	}

	public String getMinecraftUsername() {
		return minecraft_name;
	}

	public String getPassword() {
		return password;
	}

	public int getProgramId() {
		return program_id;
	}

	public UUID getUserId() {
		return user_id;
	}

	public String getUsername() {
		return username;
	}

	public void grabMissingData() {

	}

	public void setCCOLid(UUID ccol_id) {
		this.ccol_id = ccol_id;
	}

	public void setCCOLName(String name) {
		ccol_name = name;
	}

	public void setDisplayName(String display_name) {
		this.display_name = display_name;
	}

	public void setMinecraftUsername(String minecraft_name) {
		this.minecraft_name = minecraft_name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setProgramId(int program_id) {
		this.program_id = program_id;
	}

	public void setUserId(UUID user_id) {
		this.user_id = user_id;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
