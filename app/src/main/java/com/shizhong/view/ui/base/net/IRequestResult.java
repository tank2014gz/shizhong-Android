package com.shizhong.view.ui.base.net;

public interface IRequestResult {

	
	public void requestSuccess(String req);
	
	public void requestFail();
	
	public void requestNetExeption();
}
