package com.wind.ffmpeghelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.shizhong.view.ui.base.utils.VideoUtils;

public class FFmpegUtils {

	/**
	 * pad and clip(pad和时间切割)
	 * 
	 * @param inputfilepath
	 *            (视频文件)
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param width
	 *            视频宽度
	 * @param height
	 *            视频高度
	 * @param degree
	 *            旋转角度
	 * @param hasAudio
	 *            是否有音频
	 * @param outpid
	 *            video file .yuv
	 * @param output
	 *            audio file .pcm
	 * @return
	 */

	public static String getPadAndClipVideoCommond(String inputfilepath, int startTime, int endTime, int width,
			int height, int degree, boolean hasAudio, String outpid, String output) {

		int q1 = startTime / 1000;
		int q2 = endTime / 1000;

		StringBuffer buffer = new StringBuffer("ffmpeg");
		buffer.append(" -ss");
		buffer.append(" 00:" + secToTime(q1));

		buffer.append(" -i " + inputfilepath);

		buffer.append(" -t");
		buffer.append(" 00:" + secToTime(q2 - q1));

		if (height > width) {
			buffer.append(" -filter_complex");
			buffer.append(" pad=" + height + ":" + height + ":" + (int) ((height - width) / 2) + ":0,scale=480*480");
		} else if (width > height) {
			buffer.append(" -filter_complex pad=" + width + ":" + width + ":0:" + (int) ((width - height) / 2)
					+ ",scale=480*480");
		} else {
			buffer.append(" -filter_complex scale=480*480");
		}

		switch (degree) {
		case 90:
			buffer.append(",transpose=1");
			break;
		case 270:
			buffer.append(",transpose=2");
			break;
		case 180:
			buffer.append(",vflip,hflip");
			break;
		default:
			break;
		}
		buffer.append(" -an -vcodec rawvideo -f rawvideo -s 480x480 -pix_fmt yuv420p -r 15 " + outpid);

		if (hasAudio) {
			// 裁剪时间 -ss是秒 -t也是秒
			buffer.append(" -ss");
			buffer.append(" 00:" + secToTime(q1));
			buffer.append(" -t");
			buffer.append(" 00:" + secToTime(q2 - q1));

			buffer.append(" -vn -acodec pcm_s16le -f s16le -ar 44100 -ac 1 " + output);

		}
		return buffer.toString();
	}

	/**
	 * pad+clip+water (视频处理：pad+clip+water)
	 * 
	 * @param inputfilepath
	 *            (处理视频)
	 * @param inputPNGFilePath（水印文件）
	 * @param startTime（裁剪开始时间）
	 * @param endTime（
	 *            要裁剪视频的长度）
	 * @param width（视频的宽度）
	 * @param height（视频的高度）
	 * @param degree（视频旋转角度）
	 * @param bitrate（视频码率）
	 * @param outputVideoPath（处理输出视频文件）
	 * @return
	 */
	public static String getPadAndClipWithWater(String inputfilepath, String inputPNGFilePath, int startTime,
			int endTime, int width, int height, int degree, long bitrate, String outputVideoPath) {
		int q1 = startTime / 1000;
		int q2 = endTime / 1000;

		StringBuffer buffer = new StringBuffer("ffmpeg");
		buffer.append(" -ss");
		buffer.append(" 00:" + secToTime(q1));

		buffer.append(" -i " + inputfilepath);
		buffer.append(" -i " + inputPNGFilePath);
		buffer.append(" -t ");
		buffer.append(" 00:" + secToTime(q2 - q1));

		if (height > width) {
			buffer.append(" -filter_complex");
			buffer.append(" pad=" + height + ":" + height + ":" + (int) ((height - width) / 2) + ":0,scale=480*480");
		} else if (width > height) {
			buffer.append(" -filter_complex pad=" + width + ":" + width + ":0:" + (int) ((width - height) / 2)
					+ ",scale=480*480");
		} else {
			buffer.append(" -filter_complex scale=480*480");
		}
		
		switch (degree) {
		case 90:
			buffer.append(",transpose=1");
			break;
		case 270:
			buffer.append(",transpose=2");
			break;
		case 180:
			buffer.append(",vflip,hflip");
			break;
		default:
			break;
		}
		buffer.append(",overlay");
		buffer.append(" -vcodec mpeg4 -b " + bitrate);
		buffer.append(" -r 15  " + outputVideoPath);

		return buffer.toString();
	}

	/**
	 * crop and clip(crop和时间裁剪方法)
	 * 
	 * @param inputfilepath
	 *            (视频文件)
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param width
	 *            视频宽度
	 * @param height
	 *            视频高度
	 * @param cropx
	 *            crop x
	 * @param cropy
	 *            crop y
	 * @param degree
	 *            旋转角度
	 * @param hasAudio
	 *            是否有音频
	 * @param outpid
	 *            video file .yuv
	 * @param output
	 *            audio file .pcm
	 * @return
	 */

	public static String getCropAndClipVideo(String inputfilepath, int startTime, int endTime, int width, int height,
			int cropx, int cropy, int degree, boolean hasAudio, String outpid, String output) {
		int q1 = startTime / 1000;
		int q2 = endTime / 1000;

		StringBuffer buffer = new StringBuffer("ffmpeg");
		buffer.append(" -ss");
		buffer.append(" 00:" + secToTime(q1));

		buffer.append(" -i " + inputfilepath);
		buffer.append(" -t ");
		buffer.append(" 00:" + secToTime(q2 - q1));

		buffer.append(" -filter_complex");
		buffer.append(" crop=" + width + ":" + height + ":" + cropx + ":" + cropy + ",scale=480*480");

		switch (degree) {
		case 90:
			buffer.append(",transpose=1");
			break;
		case 270:
			buffer.append(",transpose=2");
			break;
		case 180:
			buffer.append(",vflip,hflip");
			break;
		default:
			break;
		}
		buffer.append(" -an -vcodec rawvideo -f rawvideo -s 480x480 -pix_fmt yuv420p -r 15 " + outpid);

		if (hasAudio) {
			// 裁剪时间 -ss是秒 -t也是秒
			buffer.append(" -ss");
			buffer.append(" 00:" + secToTime(q1));
			buffer.append(" -t ");
			buffer.append(" 00:" + secToTime(q2 - q1));

			buffer.append(" -vn -acodec pcm_s16le -f s16le -ar 44100 -ac 1 " + output);

		}
		return buffer.toString();
	}

	/**
	 * crop+clip+water
	 * 
	 * @param inputfilepath
	 *            (处理视频)
	 * @param inputPNGFilePath（水印文件）
	 * @param startTime（处理的开始时间）
	 * @param endTime（处理视频时长）
	 * @param width（裁剪宽度）
	 * @param height（裁剪高度）
	 * @param cropx（开始的X坐标）
	 * @param cropy（开始Y坐标）
	 * @param degree（旋转角度）
	 * @param bitrate（码率）
	 * @param outputVideoPath（视频输出文件）
	 * @return
	 */

	public static String getCropAndClipWithWater(String inputfilepath, String inputPNGFilePath, int startTime,
			int endTime, int width, int height, int cropx, int cropy, int degree, long bitrate,
			String outputVideoPath) {

		int q1 = startTime / 1000;
		int q2 = endTime / 1000;

		StringBuffer buffer = new StringBuffer("ffmpeg");
		buffer.append(" -ss");
		buffer.append(" 00:" + secToTime(q1));

		buffer.append(" -i " + inputfilepath);
		buffer.append(" -i " + inputPNGFilePath);
		buffer.append(" -t ");
		buffer.append(" 00:" + secToTime(q2 - q1));

		buffer.append(" -filter_complex");
		buffer.append(" crop=" + width + ":" + height + ":" + cropx + ":" + cropy + ",scale=480*480");
		
		switch (degree) {
		case 90:
			buffer.append(",transpose=1");
			break;
		case 270:
			buffer.append(",transpose=2");
			break;
		case 180:
			buffer.append(",vflip,hflip");
			break;
		default:
			break;
		}
		buffer.append(",overlay");
		buffer.append(" -vcodec mpeg4 -b " + bitrate);
		buffer.append(" -r 15  " + outputVideoPath);
		return buffer.toString();
	}

	/**
	 * 
	 * @param inpid
	 *            video file .yuv
	 * @param hasAudio
	 *            是否有音频
	 * @param input
	 *            audio file .pcm
	 * @param inputPNGFilePath
	 *            (水印图片地址)
	 * @param bitrate
	 *            视频码率
	 * @param outputVideoPath
	 *            输出视频文件 （.mp4）
	 * @return
	 */
	public static String getMuxAndLogoVideo(String inpid, Boolean hasAudio, String input, String inputPNGFilePath,
			long bitrate, String outputVideoPath) {

		StringBuffer buffer = new StringBuffer("ffmpeg ");
		buffer.append("-s 480*480 -pix_fmt yuv420p -r 15 -i " + inpid);
		if (hasAudio) {
			buffer.append(" -f s16le -ar 44100 -ac 1 -i " + input);
		}

		buffer.append(" -i " + inputPNGFilePath);

		buffer.append(" -filter_complex overlay");
		buffer.append(" -b:v " + bitrate);
		buffer.append(" -vcodec mpeg4");
		// buffer.append(" -vcodec libx264");

		if (hasAudio) {
			buffer.append(" -acodec libfaac");
		}
		buffer.append(" " + outputVideoPath);
		return buffer.toString();
	}

	/**
	 * 视频拼接
	 * 
	 * @param inputVideoFilepath1
	 * @param inputVideoFilepath2
	 * @param outputVideoFilePath
	 * @return
	 */
	public static String getMuxTwoVideo(String inputVideoFilepath1, String inputVideoFilepath2,
			String outputVideoFilePath) {
		StringBuffer bufferCommand = new StringBuffer("ffmpeg");
		StringBuffer bufferFile = new StringBuffer();
		File fileout2 = new File("/sdcard/a.txt");
		FileOutputStream out2 = null;

		if (!fileout2.exists()) {
			try {
				fileout2.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		try {
			out2 = new FileOutputStream(fileout2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		bufferFile.append("file '" + inputVideoFilepath1 + "'\n");
		bufferFile.append("file '" + inputVideoFilepath2 + "'\n");

		try {
			out2.write(bufferFile.toString().getBytes());
			out2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		bufferCommand.append(" -i /sdcard/a.txt");

		bufferCommand.append(" -codec copy " + outputVideoFilePath);
		return bufferCommand.toString();
	}

	/**
	 * pad+水印
	 * 
	 * 
	 * inputPNGFilePath bitrate: int bitrate
	 */

	public String UsefulPadVideo(String inputfilepath, String inputPNGFilePath, int bitrate, String finalVideoPath) {
		int height = VideoUtils.getVideoHeight(inputfilepath);// 视频高度
		int width = VideoUtils.getVideoWidth(inputfilepath); // 视频宽度
		int degree = VideoUtils.getVideoRotate(inputfilepath);// 视频旋转角度
		String command = null;

		switch (degree % 360) {
		case 0:
		case 180:
			if (height > width) {
				command = "ffmpeg -i " + inputfilepath + " -i " + inputPNGFilePath + " -filter_complex pad=" + height
						+ ":" + height + ":" + (int) ((height - width) / 2) + ":0,scale=480*480,overlay -b " + bitrate
						+ " " + finalVideoPath;

			} else if (width < height) {
				command = "ffmpeg -i " + inputfilepath + " -i " + inputPNGFilePath + " -filter_complex pad=" + width
						+ ":" + width + ":0:" + (int) ((width - height) / 2) + ",scale=480*480,overlay -b " + bitrate
						+ " " + finalVideoPath;
			} else {
				command = "ffmpeg -i " + inputfilepath + " -i " + inputPNGFilePath
						+ " -filter_complex scale=480*480,overlay -b " + bitrate + " " + finalVideoPath;
			}
			break;
		case 90:
		case 270:
			if (height > width) {
				command = "ffmpeg -i " + inputfilepath + " -i " + inputPNGFilePath + " -filter_complex pad=" + height
						+ ":" + height + ":" + (int) ((height - width) / 2) + ":0,scale=480*480,transpose=1,overlay -b "
						+ bitrate + " " + finalVideoPath;

			} else if (width < height) {
				command = "ffmpeg -i " + inputfilepath + " -i " + inputPNGFilePath + " -filter_complex pad=" + width
						+ ":" + width + ":0:" + (int) ((width - height) / 2) + ",scale=480*480,transpose=1,overlay -b "
						+ bitrate + " " + finalVideoPath;
			} else {
				command = "ffmpeg -i " + inputfilepath + " -i " + inputPNGFilePath
						+ " -filter_complex scale=480*480,transpose=1,overlay -b " + bitrate + " " + finalVideoPath;
			}
			break;
		}
		return command;
	}

	/**
	 * crop+水印
	 * 
	 * @param inputfilepath
	 * @param inputPNGFilePath
	 * @param bitrate
	 * @param degree
	 * @param cropx
	 * @param cropy
	 * @param width
	 * @param height
	 * @param outputVideoPath
	 * @return
	 */
	public static String getCropWithWater(String inputfilepath, String inputPNGFilePath, int bitrate, int degree,
			int cropx, int cropy, int width, int height, String outputVideoPath) {

		String command = null;

		switch (degree % 360) {
		case 0:
			command = "ffmpeg -i " + inputfilepath + " -i " + inputPNGFilePath + " -filter_complex crop=" + width + ":"
					+ height + ":" + cropx + ":" + cropy + ",scale=480*480,overlay -b " + bitrate + " -vcodec mpeg4 "
					+ outputVideoPath;
			// command = "ffmpeg -i " + inputfilepath + " -i " +
			// inputPNGFilePath + " -filter_complex crop=" + width + ":"
			// + height + ":" + cropx + ":" + cropy + ",scale=480*480,overlay -b
			// " + bitrate + " -vcodec libx264 -preset ultrafast "
			// + outputVideoPath;
			break;

		case 90:
			// command = "ffmpeg -i " + inputfilepath + " -i " +
			// inputPNGFilePath + " -filter_complex crop=" + width + ":"
			// + height + ":" + cropx + ":" + cropy +
			// ",scale=480*480,transpose=1,overlay -b " + bitrate
			// + " -vcodec libx264 -preset ultrafast " + outputVideoPath;
			command = "ffmpeg -i " + inputfilepath + " -i " + inputPNGFilePath + " -filter_complex crop=" + width + ":"
					+ height + ":" + cropx + ":" + cropy + ",scale=480*480,transpose=1,overlay -b " + bitrate
					+ " -vcodec mpeg4 " + outputVideoPath;
			break;
		case 180:
			// command = "ffmpeg -i " + inputfilepath + " -i " +
			// inputPNGFilePath + " -filter_complex crop=" + width + ":"
			// + height + ":" + cropx + ":" + cropy +
			// ",scale=480*480,transpose=2,overlay -b " + bitrate
			// + " -vcodec libx264 -preset ultrafast " + outputVideoPath;
			command = "ffmpeg -i " + inputfilepath + " -i " + inputPNGFilePath + " -filter_complex crop=" + width + ":"
					+ height + ":" + cropx + ":" + cropy + ",scale=480*480,transpose=2,overlay -b " + bitrate
					+ " -vcodec mpeg4 " + outputVideoPath;
			break;
		case 270:
			// command = "ffmpeg -i " + inputfilepath + " -i " +
			// inputPNGFilePath + " -filter_complex crop=" + width + ":"
			// + height + ":" + cropx + ":" + cropy +
			// ",scale=480*480,vflip,hflip,overlay -b " + bitrate
			// + " -vcodec libx264 -preset ultrafast " + outputVideoPath;
			command = "ffmpeg -i " + inputfilepath + " -i " + inputPNGFilePath + " -filter_complex crop=" + width + ":"
					+ height + ":" + cropx + ":" + cropy + ",scale=480*480,vflip,hflip,overlay -b " + bitrate
					+ " -vcodec mpeg4 " + outputVideoPath;
			break;

		}

		return command;

	}

	/**
	 * get The quick photo input: inputfilepath input: size sample: 80*80 fps 1s
	 * 几张图片
	 */
	public static String getQuickPng(String inputfilepath, String size, int degree, String outputdir, int during,
			int total) {

		StringBuilder commandBuilder = new StringBuilder("ffmpeg -i " + inputfilepath + " -f image2 -vf");
		// if(VideoDuration > 180000)
		// {
		double count = ((double) during) / total / 1000.0;
		commandBuilder.append(" fps=fps=1/" + String.format("%.2f", count));
		// command = "ffmpeg -i "+inputfilepath + " -f image2 -vf fps=fps=1/10
		// -s "+size +" "+outputdir+"%d.png";
		// }else
		// {
		// Double q2 = VideoDuration/1000.0000;
		// String result = String .format("%.2f",q2);
		// commandBuilder.append(" fps=fps=1/"+result);
		//
		// }
		switch (degree) {
		case 0:
			break;
		case 90:
			commandBuilder.append(",transpose=1");
			break;
		case 180:
			commandBuilder.append(",vflip");
			break;
		case 270:
			commandBuilder.append("transpose=3");
			break;
		}
		commandBuilder.append(" -s " + size + " " + outputdir.concat(File.separator) + "%d.png");
		return commandBuilder.toString();
	}

	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return timeStr;
	}

	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

}
