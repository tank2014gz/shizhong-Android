package com.wind.ffmpeghelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shizhong.view.ui.base.utils.LogUtils;


public class FFmpegNativeHelper {
	private VideoHandlerCallBack mVideoHandlerCallBack;
	private static volatile FFmpegNativeHelper mFFmpegHepler;

	private FFmpegNativeHelper() {

	}

	public synchronized static FFmpegNativeHelper getHelper() {
		if (mFFmpegHepler == null) {
			mFFmpegHepler = new FFmpegNativeHelper();
		}
		return mFFmpegHepler;
	}

	static {
		System.loadLibrary("avutil-54");
		System.loadLibrary("postproc-53");
		System.loadLibrary("swresample-1");
		System.loadLibrary("avcodec-56");
		System.loadLibrary("avformat-56");
		System.loadLibrary("swscale-3");
		System.loadLibrary("avfilter-5");
		System.loadLibrary("avdevice-56");
		System.loadLibrary("sfftranscoder");
	}
	/**
	 * 每秒帧率
	 */
	static double fps;
	static Boolean getFps = false;

	static double duration;
	static Boolean getDuration = false;

	static int width;
	static int height;

	static Boolean getWidthAndHeight = false;

	static int rotate = 0;
	static Boolean getRotate = false;

	/**
	 * 解码以后返回的总共的帧数
	 */
	int FrameTotal = 0;

	/**
	 * 现在操作到第几帧
	 */
	public int DealingFrameNo = 0;

	/**
	 * API return the count of frame
	 * 
	 * @return
	 */

	public long getFrameTotal() {
		return FrameTotal;
	}

	/**
	 * Function CallBack For JNI to get the FPS( num/den)
	 * 
	 * @param num
	 * @param den
	 */
	public void signFps(int num, int den) {
		fps = ((float) num / den);
		LogUtils.e("error", "num(" + num + ");den(" + den + ")");
	}

	/**
	 * Function CallBack For JNI to get the frameNo(
	 * 
	 * @param i
	 * @param q
	 */
	public void signTest(int i, int q) {
		DealingFrameNo = i;
		LogUtils.e("error", "abc " + i + "q=" + q);
		if (mVideoHandlerCallBack != null) {
			mVideoHandlerCallBack.getCurrentProgress(i, fps, q);
		}

	}

	/**
	 * Function CallBack for JNI to get the Total FrameNumber(afterDecoding)
	 * 
	 * @param i
	 */
	public void signFrameTotal(int i) {

		FrameTotal = i;
	}

	// success 0, error 1
	public synchronized int ffmpegRunCommand(String command, int commandKind) {
		if (command.isEmpty()) {
			return 1;
		}
		String[] args = command.split("\\s+");
		for (int i = 0; i < args.length; i++) {
			LogUtils.e("ffmpeg-jni", args[i]);

		}
		return ffmpeg(args.length, args, commandKind);
	}

	// argc maybe dont be needed
	public synchronized native int ffmpeg(int argc, String[] args, int commandKind);

	/**
	 * 发送JNI 停止命令 command 1 is Stop Command
	 * 
	 * @param command
	 * @return
	 */
	public synchronized native int stopSignal(int command);

	public int InitTheVideoMessage(String path) {
		Getwidth(path);
		ffmpegRunCommand("ffmpeg -i " + path, 3);
		readTxtFile("/storage/emulated/0/av_log2.txt");
		LogUtils.e("error", "egfffggff   " + fps + "   " + width + "   " + height + "  " + rotate);
		return 0;
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	public static int getRotate() {
		return rotate;
	}

	public static double getDuration() {
		return duration;
	}

	/**
	 * Init the message (fps width height during rotate)
	 * 
	 * @param filePath
	 */
	public static void readTxtFile(String filePath) {
		rotate = 0;
		getFps = false;
		getDuration = false;
		getWidthAndHeight = false;
		getRotate = false;
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// System.out.println( "AAAA"+lineTxt);
					LogUtils.e("AAAA", "-----"+lineTxt);
					if (lineTxt.contains("Duration") & (getDuration == false)) {
//						System.out.println("AAAA" + lineTxt);
						LogUtils.e("AAAA", "-----"+lineTxt);
						// String patternStr="\\d+";
						String patternStr = "\\d+:\\d+:\\d+.\\d+";
						Pattern p = Pattern.compile(patternStr);
						Matcher m = p.matcher(lineTxt);

						while (m.find()) {
							String[] ss = m.group(0).split(":");
							double a0 = Double.parseDouble(ss[0]);
							double a1 = Double.parseDouble(ss[1]);
							double a2 = Double.parseDouble(ss[2]);
							duration = a0 * 3600 + a1 * 60 + a2;
							getDuration = true;
							System.out.println(a0 * 3600 + a1 * 60 + a2);
						}

					}
					if (lineTxt.contains("Stream #0:0") & (getFps == false)) {
//						System.out.println("AAAA" + lineTxt);
						LogUtils.e("AAAA", "-----"+lineTxt);
						// String patternStr="\\d+x\\d+";
						String patternStr2 = "(\\d+ fps)|(\\d+.\\d+ fps)";
						Pattern p = Pattern.compile(patternStr2);
						Matcher m = p.matcher(lineTxt);
						while (m.find()) {
							String[] ss = m.group(0).split(" ");
							double a0 = Double.parseDouble(ss[0]);
							fps = a0;
							getFps = true;
							LogUtils.e("AAAA", "-----"+a0);
						}

						String patternStr = "\\d+x\\d+";
						p = Pattern.compile(patternStr);
						m = p.matcher(lineTxt);

						while (m.find()) {
							String[] ss = m.group(0).split("x");
							width = Integer.parseInt(ss[0]);
							height = Integer.parseInt(ss[1]);
							// System.out.println(a0 +" "+ a1);
							getWidthAndHeight = true;
						}
					}

					if (lineTxt.contains("rotate") & (getRotate == false)) {
//						System.out.println("AAAA" + lineTxt);
						LogUtils.e("AAA","----"+lineTxt);
						// String patternStr="\\d+x\\d+";
						String patternStr2 = "\\d+";
						Pattern p = Pattern.compile(patternStr2);
						Matcher m = p.matcher(lineTxt);
						while (m.find()) {
							int a0 = Integer.parseInt(m.group(0));
							rotate = a0;
							getRotate = true;
						}
					}
				}
				read.close();
			} else {
				LogUtils.e("AAA","----找不到指定的文件");
				System.out.println("");
			}
		} catch (Exception e) {
			LogUtils.e("AAA","----读取文件内容出错");
			e.printStackTrace();
		}

	}

	/**
	 * 获取当前JNI 运行状态 1 在运行 0 运行结束
	 * 
	 * @return
	 */
	public synchronized native int getSignal();
	
	public native int SetDebug(int DebugMode);
	public void setVideoHandler(VideoHandlerCallBack callabek) {
		this.mVideoHandlerCallBack = callabek;
	}

	public native int GetIKeyInterval(String path);

	public native int Getheight(String path);

	public native int Getwidth(String path);

	public native int Getrotate(String path);
}
