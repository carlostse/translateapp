package com.iems5722.translateapp.task;

public interface TranslateAPICallback {

	public void showLoading(boolean show);

	public void translated(String[] result);

}
