package com.shizhong.view.ui.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by yuliyan on 16/1/2.
 */
@Table(name = ConstansDBTable.Address.TABLE_PROVINCE)
public class ProvinceBean {
	@Column(name = "ProName")
	public String ProName;
	@Column(name = "ProSort")
	public int ProSort;
	@Column(name = "ProRemark")
	public String ProRemark;

	@Override
	public String toString() {
		return ProName.replace(" ", "").trim();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ProvinceBean) {
			return ProName.equals(((ProvinceBean) o).ProName);
		}
		return false;
	}
}
