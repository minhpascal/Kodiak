package com.clearpool.kodiak.feedlibrary.mbeans;

import com.clearpool.kodiak.feedlibrary.mbeans.format.MBeanMethodDescription;

public interface NbboQuoteCacheMgmtMBean extends IMdServiceCacheMgmt
{
	@MBeanMethodDescription("Republish all data for all symbols in cache")
	public String publishAllData();
}
