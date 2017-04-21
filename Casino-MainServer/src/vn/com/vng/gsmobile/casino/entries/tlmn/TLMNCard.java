package vn.com.vng.gsmobile.casino.entries.tlmn;

import vn.com.vng.gsmobile.casino.entries.Card;

public class TLMNCard extends Card implements Comparable<TLMNCard> {
	public TLMNCard(int c){
		super(c);
	}
	public TLMNCard(int c, int idx){
		super(c, idx);
	}
	@Override
	public int compareTo(TLMNCard o) {
		// TODO Auto-generated method stub
		if(this.Id > o.Id)
			return 1;
		else if(this.Id < o.Id)
			return -1;
		else
			return -0;
	}
}