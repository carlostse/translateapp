package com.iems5722.translateapp;

public interface TranslateAPICallback {

	public void showLoading(boolean show);

	public void translated(String result);

}
