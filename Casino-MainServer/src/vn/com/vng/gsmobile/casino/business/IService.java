package vn.com.vng.gsmobile.casino.business;

import java.util.List;

public interface IService {
	public List<?> execute(String sTrid, List<?> params) throws Exception;
}
