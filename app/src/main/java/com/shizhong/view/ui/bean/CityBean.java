package com.shizhong.view.ui.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by yuliyan on 16/1/2.
 */
@Table(name = ConstansDBTable.Address.TABLE_CITY)
public class CityBean {
	@Column(name = "CityName")
	public String CityName;
	@Column(name = "ProID")
	public int ProID;
	@Column(name = "CitySort")
	public int CitySort;

	@Override
	public String toString() {
		return CityName.replace(" ", "").trim();
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof CityBean) {
			return CityName.equals(((CityBean) o).CityName);
		}
		return false;
	}
}
