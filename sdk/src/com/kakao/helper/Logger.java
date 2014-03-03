/**
 * Copyright 2014 Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.helper;

import android.util.Log;


/**
 * Logging을 위한 Logger 클래스
 */
public class Logger {
    public static enum LogLevel {
        Verbose, Debug, Info, Warn, Error, Release
    }


    private static final String TAG = "kakao-android-sdk";
	private volatile static Logger instance;

	private LogLevel logLevel;

	private Logger() {
		super();
		this.logLevel = LogLevel.Debug;
	}

	public static Logger getInstance() {
		if (instance == null) {
			synchronized (Logger.class) {
				if (instance == null) instance = new Logger();
			}
		}

		return instance;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

    public boolean isLoggable(LogLevel logLevel){
        return this.logLevel.compareTo(logLevel) <= 0;
    }

	public void v(String msg) {
		v(TAG, msg);
	}

	public void d(String msg) {
		d(TAG, msg);
	}

	public void i(String msg) {
		i(TAG, msg);
	}

	public void w(String msg) {
		w(TAG, msg);
	}

	public void e(String msg) {
		e(TAG, msg);
	}
	
	void v(String tag, String msg) {
		switch (logLevel) {
		case Verbose:
			Log.v(tag, msg);
			break;
		}
	}

	public void d(String tag, String msg) {
		switch (logLevel) {
		case Verbose:
		case Debug:
			Log.d(tag, msg);
			break;
		}
	}

	void i(String tag, String msg) {
		switch (logLevel) {
		case Verbose:
		case Debug:
		case Info:
			Log.i(tag, msg);
			break;
		}
	}

	public void w(String tag, String msg) {
		switch (logLevel) {
		case Verbose:
		case Debug:
		case Info:
		case Warn:
			Log.w(tag, msg);
			break;
		}
	}

	void e(String tag, String msg) {
		switch (logLevel) {
		case Verbose:
		case Debug:
		case Info:
		case Warn:
		case Error:
			Log.e(tag, msg);
			break;
		}
	}

	public void v(Throwable tr) {
		v(TAG, tr);
	}
	
	public void d(Throwable tr) {
		d(TAG, tr);
	}
	
	public void i(Throwable tr) {
		i(TAG, tr);
	}
	
	public void w(Throwable tr) {
		w(TAG, tr);
	}
	
	public void e(Throwable tr) {
		e(TAG, tr);
	}
	
	void v(String tag, Throwable tr) {
		switch (logLevel) {
		case Verbose:
			Log.v(tag, tr.getLocalizedMessage(), tr);
			break;
		}
	}
	
	void d(String tag, Throwable tr) {
		switch (logLevel) {
		case Verbose:
		case Debug:
			Log.d(tag, tr.getLocalizedMessage(), tr);
			break;
		}
	}
	
	public void i(String tag, Throwable tr) {
		switch (logLevel) {
		case Verbose:
		case Debug:
		case Info:
			Log.i(tag, tr.getLocalizedMessage(), tr);
			break;
		}
	}
	
	public void w(String tag, Throwable tr) {
		switch (logLevel) {
		case Verbose:
		case Debug:
		case Info:
		case Warn:
			Log.w(tag, tr.getLocalizedMessage(), tr);
			break;
		}
	}
	
	void e(String tag, Throwable tr) {
		switch (logLevel) {
		case Verbose:
		case Debug:
		case Info:
		case Warn:
		case Error:
			Log.e(tag, tr.getLocalizedMessage(), tr);
			break;
		}
	}
}
