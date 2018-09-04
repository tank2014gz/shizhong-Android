package com.shizhong.view.ui.bean;


/**
 * Created by yuliyan on 15/12/29.
 */
public class RegisterDataPackage  {
	public int code;
	public RegisterData data;


    public static class RegisterData  {
    	public String token;
    	public String memberId;


        @Override
        public String toString() {
            return "RegisterData{" +
                    "token='" + token + '\'' +
                    ", memberId='" + memberId + '\'' +
                    '}';
        }

    }


    @Override
    public String toString() {
        return "RegisterDataPackage{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
