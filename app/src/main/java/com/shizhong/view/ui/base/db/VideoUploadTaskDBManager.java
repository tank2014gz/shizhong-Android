package com.shizhong.view.ui.base.db;

import java.io.File;

import org.xutils.DbManager;
import org.xutils.x;
import org.xutils.DbManager.DbOpenListener;
import org.xutils.DbManager.DbUpgradeListener;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import com.shizhong.view.ui.bean.UploadVideoTaskBean;

import android.content.Context;

public class VideoUploadTaskDBManager {
	private static VideoUploadTaskDBManager mInstance;
	// 数据库名称
	private static final String MESSAGE_DB_NAME = "video_upload.db";
	public final int version = 1;
	DbManager db;

	private VideoUploadTaskDBManager() {
		super();
	}

	private VideoUploadTaskDBManager(Context context, String member_id) {
		DbManager.DaoConfig daoConfig = new DbManager.DaoConfig().setDbName(member_id + "_" + MESSAGE_DB_NAME)
				.setDbDir(new File(context.getCacheDir().getAbsolutePath())).setDbVersion(version)
				.setDbUpgradeListener(new DbUpgradeListener() {

					@Override
					public void onUpgrade(DbManager arg0, int arg1, int arg2) {

					}
				}).setDbOpenListener(new DbOpenListener() {

					@Override
					public void onDbOpened(DbManager db) {
						db.getDatabase().enableWriteAheadLogging();
					}
				});
		db = x.getDb(daoConfig);
	}

	public static synchronized VideoUploadTaskDBManager getInstance(Context context, String member_id) {

		if (mInstance == null) {
			mInstance = new VideoUploadTaskDBManager(context, member_id);
		}
		return mInstance;
	}

	public UploadVideoTaskBean findTaskUnSuccessUpload() {
		if (db != null) {
			try {
				UploadVideoTaskBean bean = db.selector(UploadVideoTaskBean.class).where("code_task", "!=", 1)
						.findFirst();
				if (bean != null) {
					return bean;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public UploadVideoTaskBean findTask(String taskId) {
		if (db != null) {
			try {
				UploadVideoTaskBean bean = db.selector(UploadVideoTaskBean.class).where("taskId", "=", taskId)
						.findFirst();
				if (bean != null) {
					return bean;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean isTaskCanUpload(String taskId) {
		if (db != null) {
			try {
				UploadVideoTaskBean bean = db.selector(UploadVideoTaskBean.class).where("taskId", "=", taskId)
						.and("code_task", "!=", 1).findFirst();
				if (bean != null) {
					return true;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;
	}

	public boolean insertTask(UploadVideoTaskBean bean) {
		if (db != null) {
			try {
				if (bean != null)
					db.save(bean);
				return true;
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean isHasVideoUpload() {
		if (db != null) {
			try {
				UploadVideoTaskBean bean = db.selector(UploadVideoTaskBean.class).where("code_task", "=", 2)
						.findFirst();
				if (bean != null) {
					return true;
				} else {
					return false;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 更改上传封面的状态
	 * 
	 * @param id
	 * @param code_cover
	 */
	public void updateCoverUploadState(String taskId, int code_cover) {
		if (db != null) {
			try {
				UploadVideoTaskBean bean = db.selector(UploadVideoTaskBean.class).where("taskId", "=", taskId)
						.findFirst();
				if (bean != null) {
					bean.code_cover = code_cover;
					db.update(bean);
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateCoverUploadState(String taskId, String coverUrl, int code_cover) {
		if (db != null) {
			try {
				UploadVideoTaskBean bean = db.selector(UploadVideoTaskBean.class).where("taskId", "=", taskId)
						.findFirst();
				if (bean != null) {
					bean.code_cover = code_cover;
					bean.coverFileName = coverUrl;
					db.update(bean);
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更改上传视频的状态
	 * 
	 * @param id
	 * @param code_cover
	 */
	public void updateVideoUploadState(String taskId, int code_video) {
		if (db != null) {
			try {
				UploadVideoTaskBean bean = db.selector(UploadVideoTaskBean.class).where("taskId", "=", taskId)
						.findFirst();
				if (bean != null) {
					bean.code_video = code_video;
					db.update(bean);
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateVideoUploadState(String taskId, String videoUrl, int code_video) {
		if (db != null) {
			try {
				UploadVideoTaskBean bean = db.selector(UploadVideoTaskBean.class).where("taskId", "=", taskId)
						.findFirst();
				if (bean != null) {
					bean.code_video = code_video;
					bean.videoFileName = videoUrl;
					db.update(bean);
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更改上传任务的状态
	 * 
	 * @param id
	 * @param code_cover
	 */
	public void updateVideoUploadTaskState(String taskId, int code_task) {
		if (db != null) {
			try {
				UploadVideoTaskBean bean = db.selector(UploadVideoTaskBean.class).where("taskId", "=", taskId)
						.findFirst();
				if (bean != null) {
					bean.code_task = code_task;
					db.update(bean);
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateVideoUploadTaskState(String taskId, int code_task, String video_id) {
		if (db != null) {
			try {
				UploadVideoTaskBean bean = db.selector(UploadVideoTaskBean.class).where("taskId", "=", taskId)
						.findFirst();
				if (bean != null) {
					bean.code_task = code_task;
					bean.video_id = video_id;
					db.update(bean);
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除任务
	 * 
	 * @param taskId
	 */
	public void delectedTask(String taskId) {
		if (db != null) {
			try {
				db.delete(UploadVideoTaskBean.class, WhereBuilder.b("taskId", "=", taskId));
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
