package com.shizhong.view.ui.base;

import android.content.Context;

import com.shizhong.view.ui.base.utils.LogUtils;
import com.shizhong.view.ui.bean.CityBean;
import com.shizhong.view.ui.bean.ConstansDBTable;
import com.shizhong.view.ui.bean.ProvinceBean;
import com.shizhong.view.ui.bean.ZoneBean;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by yuliyan on 16/1/2.
 */
public class PlaceDBManager {
	private final String TAG = "DBManager";
	private  PlaceDBManager mInstance;

	private DbManager db = null;

	private DbManager.DaoConfig city_zone_daoConfig = null;


	public PlaceDBManager(Context context) {
		city_zone_daoConfig = new DbManager.DaoConfig().setDbName(ConstansDBTable.Address.DB_NAME)
				.setDbDir(new File(context.getCacheDir().getAbsolutePath())).setDbVersion(1)// 数据库版本
				.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
					@Override
					public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
						// TODO: ...
						// db.addColumn(...);
						// db.dropTable(...);
						// ...
					}
				});
		db = x.getDb(city_zone_daoConfig);

	}



	public List<ProvinceBean> getAllProvince() {
		List<ProvinceBean> list = null;
		if (db != null) {
			LogUtils.i(TAG, "db 不等于null");
			try {
				list = db.selector(ProvinceBean.class).findAll();
			} catch (DbException e) {
				e.printStackTrace();
				LogUtils.i(TAG, "查找省份表格失败");
			}

		}
		return list;
	}

	public String getProvinceId(String province) {
		if (db != null) {
			try {
				// SELECT * FROM "T_Province" WHERE "ProName" like %s '北京' LIMIT
				// 1 OFFSET 0
				ProvinceBean bean = db.selector(ProvinceBean.class).where("ProName", "like", "%" + province + "%")
						.or("ProName", "=", province).findFirst();
				if (bean != null) {
					return bean.ProSort + "";
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getProvice(String proviceId) {
		if (db != null) {
			try {
				ProvinceBean bean = db.selector(ProvinceBean.class).where("ProSort", "=", proviceId).findFirst();
				if (bean != null) {
					return bean.ProName;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public List<CityBean> getCity(int proID) {
		List<CityBean> list = null;
		if (db != null) {
			try {
				list = db.selector(CityBean.class).where("ProID", "=", proID).findAll();
			} catch (DbException e) {
				LogUtils.i(TAG, "查找城市表格失败");
				e.printStackTrace();
			}
		}
		return list;
	}

	public String getCityId(String city) {
		if (db != null) {
			try {
				CityBean bean = db.selector(CityBean.class).where("CityName", "like", "%" + city + "%")
						.or("CityName", "=", city).findFirst();
				if (bean != null) {
					return bean.CitySort + "";
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getCity(String cityId) {
		if (db != null) {
			CityBean bean;
			try {
				bean = db.selector(CityBean.class).where("CitySort", "=", cityId).findFirst();
				if (bean != null) {
					return bean.CityName ;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	public List<ZoneBean> getZone(int cityId) {
		List<ZoneBean> list = null;
		if (db != null) {
			try {
				list = db.selector(ZoneBean.class).where("CityID", "=", cityId).findAll();
			} catch (DbException e) {
				e.printStackTrace();
				LogUtils.i(TAG, "查找城区表格失败");
			}
		}
		return list;
	}

	public String getZoneId(String zone) {
		if (db != null) {
			try {
				ZoneBean bean = db.selector(ZoneBean.class).where("ZoneName", "like", "%" + zone + "%")
						.or("ZoneName", "=", zone).findFirst();
				if (bean != null) {
					return bean.ZoneID + "";
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getZone(String zone) {
		if (db != null) {
			try {
				ZoneBean bean = db.selector(ZoneBean.class).where("ZoneID", "=", zone).findFirst();
				if (bean != null) {
					return bean.ZoneName;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
