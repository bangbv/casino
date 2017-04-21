package vn.com.vng.gsmobile.casino.entries;

public enum CONNECT_STATUS {
	INIT(0), CONNECTING(1), CONNECTED(2), RECONNECT(3), CLOSE(4), CLOSING(5);
	public int value;
	private CONNECT_STATUS(int v){
		this.value = v;
	}
	@Override
	public String toString(){
		return String.valueOf(value);
	}
}
