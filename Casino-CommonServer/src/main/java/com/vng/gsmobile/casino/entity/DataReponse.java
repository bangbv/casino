package com.vng.gsmobile.casino.entity;

public class DataReponse {
	String uid;
	boolean new_version;
	String new_version_msg;
	String new_version_url;
	boolean review;
	String location;
	String session;
	Object pack;
	int[] game;
	int[] lobby;
	Object bet_config;
	Object shop_config;
	
	
	public Object getShop_config() {
		return shop_config;
	}
	public void setShop_config(Object shop_config) {
		this.shop_config = shop_config;
	}
	public int[] getGame() {
		return game;
	}
	public void setGame(int[] game) {
		this.game = game;
	}
	public int[] getLobby() {
		return lobby;
	}
	public void setLobby(int[] lobby) {
		this.lobby = lobby;
	}
	public Object getBet_config() {
		return bet_config;
	}
	public void setBet_config(Object bet_config) {
		this.bet_config = bet_config;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public boolean isNew_version() {
		return new_version;
	}
	public void setNew_version(boolean new_version) {
		this.new_version = new_version;
	}
	public String getNew_version_msg() {
		return new_version_msg;
	}
	public void setNew_version_msg(String new_version_msg) {
		this.new_version_msg = new_version_msg;
	}
	public String getNew_version_url() {
		return new_version_url;
	}
	public void setNew_version_url(String new_version_url) {
		this.new_version_url = new_version_url;
	}
	public boolean isReview() {
		return review;
	}
	public void setReview(boolean review) {
		this.review = review;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public Object getPack() {
		return pack;
	}
	public void setPack(Object pack) {
		this.pack = pack;
	}
	
}
