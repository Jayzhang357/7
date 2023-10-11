package com.zhd.TCPSocketClientBase;

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
		try {
			mLock.lock();
			mAskToWait.signalAll();

		} catch (Exception ex) {

		}
		finally{
			mLock.unlock();//释放锁
		}
	}

	public boolean wait(long time, TimeUnit unit) {
		boolean b = false;
		mLock.lock();
		try {
			if (mAskToWait.await(time, unit))
				b = true;

		} catch (InterruptedException e) {

		} catch (Exception ex) {
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
		}
		finally{
			mLock.unlock();//释放锁
		}
	}
}

