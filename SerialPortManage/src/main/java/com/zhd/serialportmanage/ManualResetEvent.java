package com.zhd.serialportmanage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ManualResetEvent {
	Lock mLock;
	Condition mAskToWait;

	public ManualResetEvent() {
		mLock = new ReentrantLock();
		mAskToWait = mLock.newCondition();
	}

	public void set() {
		mLock.lock();
		try {
			mAskToWait.signalAll();

		} catch (Exception e) {
		} finally {
			mLock.unlock();// 释放锁
		}
	}

	public boolean wait(long time, TimeUnit unit) {
		boolean b = false;
		mLock.lock();
		try {
			if (mAskToWait.await(time, unit))
				b = true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			mLock.unlock();//释放锁
		}
		return b;
	}

	public void waitUntilSignal() {
		mLock.lock();
		try {
			mAskToWait.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			mLock.unlock();//释放锁
		}
	}
}
