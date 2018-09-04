package com.shizhong.view.ui.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by yuliyan on 16/1/2.
 */
@Table(name = ConstansDBTable.Address.TABLE_ZONE)
public class ZoneBean {
	@Column(name = "ZoneName")
	public String ZoneName;
	@Column(name = "ZoneID")
	public int ZoneID;
	@Column(name = "CityID")
	public int CityID;

	public String getZoneName() {
		return ZoneName.replace(" ", "").trim();
	}

	public void setZoneName(String zoneName) {
		ZoneName = zoneName.replace(" ", "").trim();
	}

	@Override
	public String toString() {
		return ZoneName.replace(" ", "").trim();
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof ZoneBean) {
			return ZoneName.equals(((ZoneBean) o).ZoneName);
		}
		return false;
	}
}
