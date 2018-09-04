package com.shizhong.view.ui.base.db;

import java.io.File;
import java.util.List;

import org.xutils.DbManager;
import org.xutils.DbManager.DbOpenListener;
import org.xutils.DbManager.DbUpgradeListener;
import org.xutils.x;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import com.hyphenate.easeui.ContantsActivity;
import com.shizhong.view.ui.bean.MessageInfoExtraBean;

import android.content.Context;

public class MessageDBManager {

	private static MessageDBManager mInstance;
	// 数据库名称
	private static final String MESSAGE_DB_NAME = "message.db";
	public final int version = 1;
	DbManager db;

	private MessageDBManager() {
		super();
	}

	private MessageDBManager(Context context, String member_id) {
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

	public static synchronized MessageDBManager getInstance(Context context, String member_id) {

		if (mInstance == null) {
			mInstance = new MessageDBManager(context, member_id);
		}
		return mInstance;
	}

	public boolean insertMessage(MessageInfoExtraBean bean) {
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

	public synchronized void insertMessageList(final List<MessageInfoExtraBean> beans, final OptionCallback callback) {
		x.task().run(new Runnable() {

			@Override
			public void run() {
				for (MessageInfoExtraBean bean : beans) {
					try {
						db.save(bean);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		x.task().post(new Runnable() {

			@Override
			public void run() {
				callback.callback();
			}
		});
	}

	public interface OptionCallback {
		public void callback();
	}

	public boolean isHasNoReadMessage() {
		if (db != null) {
			try {
				List<MessageInfoExtraBean> list = db.selector(MessageInfoExtraBean.class).where("isRead", "=", 0)
						.findAll();
				return list != null && list.size() > 0;
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 判断某一类消息是否一经读过
	 * 
	 * @param type
	 * @param targetTypes
	 * @return
	 */
	public boolean isReaded(String type) {
		if (db != null) {
			try {
				List<MessageInfoExtraBean> list = db.selector(MessageInfoExtraBean.class).where("type", "=", type)
						.and("isRead", "=", 0).findAll();

				return list != null && list.size() > 0;
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 *  将某范围值类型的消息改成一经阅读的状态
	 * 
	 * @param type
	 * @param targetTypes
	 */
	public void changeReaded(String type) {

		try {
			db.update(MessageInfoExtraBean.class, WhereBuilder.b("type", "=", type), new KeyValue("isRead", 1));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<MessageInfoExtraBean> findTypesMessage(String targetTypes) {
		List<MessageInfoExtraBean> messages = null;
		try {
			messages = db.selector(MessageInfoExtraBean.class).where("type", "=", getMessageType(targetTypes))
					.findAll();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messages;
	}

	/**
	 * 通过类型获取消息列表 1000：
	 * 
	 * @param type
	 * @return
	 */
	public List<MessageInfoExtraBean> findTypeMessage(String type) {
		List<MessageInfoExtraBean> messages = null;
		try {
			messages = db.selector(MessageInfoExtraBean.class).where("type", "=", type).findAll();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messages;
	}

	public boolean delect(int id) {
		if (db != null) {
			try {
				db.deleteById(MessageInfoExtraBean.class, id);
				return true;
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 通过具体类型获取消息列表
	 * 
	 * @param targetTypes
	 * @return
	 */
	public static String getMessageType(String targetTypes) {
		if (targetTypes.equals(ContantsActivity.Message.MESSAGE_LIKE_VIDEO)
				|| targetTypes.equals(ContantsActivity.Message.MESSAGE_LIKE_REPLY)) {
			return ContantsActivity.Message.MESSAGE_LIKE; // 喜欢
		}
		if (targetTypes.equals(ContantsActivity.Message.MESSAGE_COMMENT_VIDEO)) {
			return ContantsActivity.Message.MESSAGE_COMMENT; // 评论
		}

		if (targetTypes.equals(ContantsActivity.Message.MESSAGE_ATTENT_PERSION)) {
			return ContantsActivity.Message.MESSAGE_NEW_FRIEND;// 好友关注
		}
		if (targetTypes.equals(ContantsActivity.Message.MESSAGE_RECOMMEND_PERSON)
				|| targetTypes.equals(ContantsActivity.Message.MESSAGE_RECOMMEND_VIDEO)
				|| targetTypes.equals(ContantsActivity.Message.MESSAGE_RECOMMEND_TOPIC)
				|| targetTypes.equals(ContantsActivity.Message.MESSAGE_RECOMMEND_NEWS)
				|| targetTypes.equals(ContantsActivity.Message.MESSAGE_RECOMMEND_CLUB)) { // 通知
			return ContantsActivity.Message.MESSAGE_NOTIC;
		}
		return ContantsActivity.Message.MESSAGE_NOTIC;
	}

}
