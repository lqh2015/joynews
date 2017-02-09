package com.android.book.server;


public abstract class BaseRequestServer implements RequestServerInterface {

	protected abstract String getRequestUrl();

	protected abstract String getRequestData();

}
