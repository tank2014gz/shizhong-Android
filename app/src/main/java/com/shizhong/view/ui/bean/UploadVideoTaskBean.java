package com.shizhong.view.ui.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 文件状态码：code:0表示初始化，－1表示失败 1表示成功 2:上传中
 * 
 * @author yuliyan
 *
 */

@Table(name = "task_video_upload")
public class UploadVideoTaskBean {
	@Column(name = "id", isId = true)
	public int id;
	@Column(name = "taskId")
	public String taskId;
	@Column(name = "coverPath")
	public String coverPath;// 视频封面路径
	@Column(name = "coverFileName")
	public String coverFileName;// 视频封面URL
	@Column(name = "code_cover")
	public int code_cover;
	@Column(name = "videoPath")
	public String videoPath;// 视频路径
	@Column(name = "videoFileName")
	public String videoFileName;// 视频url
	@Column(name = "code_video")
	public int code_video;
	@Column(name = "vodeo_id")
	public String video_id;
	@Column(name = "description")
	public String description;// 视频描述
	@Column(name = "topicId")
	public String topicId;// 视频话题
	@Column(name = "code_task")
	public int code_task;


	@Column(name = "categroyId")
	public String categroyId;
	@Column(name = "videoLength")
	public int videoLength;
	@Column(name = "estras")
	public String estras;

	@Override
	public String toString() {
		return "UploadVideoTaskBean [id=" + id + ", taskId=" + taskId + ", coverPath=" + coverPath + ", coverFileName="
				+ coverFileName + ", code_cover=" + code_cover + ", videoPath=" + videoPath + ", videoFileName="
				+ videoFileName + ", code_video=" + code_video + ", description=" + description + ", topicId=" + topicId
				+ ", code_task=" + code_task + ", categroyId=" + categroyId + ", videoLength=" + videoLength
				+ ", estras=" + estras + "]";
	}

}
