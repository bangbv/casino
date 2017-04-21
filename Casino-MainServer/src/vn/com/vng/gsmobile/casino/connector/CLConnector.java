package vn.com.vng.gsmobile.casino.connector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.channel.Channel;

public class CLConnector {
	private String sId = null;
	private ConcurrentHashMap<String, Long> users = new ConcurrentHashMap<String, Long>();
	private ConcurrentHashMap<Long, Channel> channels = new ConcurrentHashMap<Long, Channel>();
    public CLConnector(String sId){
    	this.sId = sId;
    }
    public String getId(){
    	return this.sId;
    }
    public ConcurrentHashMap<String, Long> getUsers(){
    	return this.users;
    }
    public ConcurrentHashMap<Long, Channel> getChannels(){
    	return this.channels;
    }
    public Long getUser(Channel c){
    	if(c!=null)
    		return this.users.get(c.id().toString());
    	else
    		return null;
    }
    public Channel getChannel(Long uid){
    	if(uid!=null && uid > 0)
    		return this.channels.get(uid);
    	else
    		return null;
    }
    public void add(Long uid, Channel c){
    	remove(uid);
    	this.users.put(c.id().toString(), uid);
    	this.channels.put(uid, c);
    }
    public Long remove(Channel c){
    	Long uid = null;
        if(c!=null){
	    	uid = this.users.remove(c.id().toString());
	    	if(uid != null && uid > 0)
	    		this.channels.remove(uid);
			try {
				c.close().sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return uid;
    }
    public void remove(Long uid){
    	Channel c = this.channels.remove(uid);
    	if(c != null){
    		this.users.remove(c.id().toString());
			try {
				c.close().sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    @Override
    public String toString(){
    	Map<String, Object> data = new HashMap<String, Object>();
		data.put("Id", sId);
		data.put("users", users);
		data.put("channels", channels);
		return data.toString();
    }
}
