package com.sxb.commons.cache;

import com.sxb.commons.json.JsonResult;

public interface ICacheManagerService {

	public void evictAllRedisCaches();

	public JsonResult getStatus(String type);

	public void evictAllGuavaCaches();

}
