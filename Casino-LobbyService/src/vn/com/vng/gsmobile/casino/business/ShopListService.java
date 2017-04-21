package vn.com.vng.gsmobile.casino.business;

import java.util.Arrays;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.Shop;
import vn.com.vng.gsmobile.casino.flatbuffers.CMDShopList;
import vn.com.vng.gsmobile.casino.ulti.ErrorCode;

public class ShopListService implements IService {

	@Override
	public List<?> execute(String sTrid, List<?> params) throws Exception {
		// TODO Auto-generated method stub
		//params=[0: from_channel, 1: cmd, 2: subcmd, 3: version, 4:data FlatBuffers]
		byte bKq = ErrorCode.UNKNOWN;
		CMDShopList rq = (CMDShopList) params.get(4);
		CMDShopList rs = null;//ResponseData
		rs = Shop.getShopList(String.format("%d", rq.uid()));
		if(rs!=null)
			bKq = ErrorCode.OK;
		else
			bKq = ErrorCode.NOTEXISTS;
		List<?> channels = Arrays.asList(params.get(0));
		List<?> outparams = Arrays.asList(params.get(1),params.get(2),params.get(3),bKq,rs); //noi dung phan hoi
		return Arrays.asList(bKq, channels, outparams);
	}

}

