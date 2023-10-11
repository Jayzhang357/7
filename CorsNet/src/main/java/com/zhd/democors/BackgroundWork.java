package com.zhd.democors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import android.os.Handler;
import android.os.Message;

public class BackgroundWork {
	
	private final static int state_start = 0; 
	private final static int state_end = 1; 
	private final static int state_cancel = -1; 
	private final static int state_work = 2; 
	
	private ProgressReportListener report;
	private IDoWork dowork;
	private RunWorkListener workListener;
	private Handler handler;
	private ExecutorService exec;
	private boolean isEx=false;
	private Future future;
	
	private Object arg;
	private String str_msg;
	private Object result;
	private Exception exception;
	
	public ExecutorService getExec() {
		return exec;
	}

	public void setExec(ExecutorService exec) {
		isEx=true;
		this.exec = exec;
	}
	
	public IDoWork getDowork() {
		return dowork;
	}

	public void setDowork(IDoWork iDoWork) {
		this.dowork = iDoWork;
	}

	public Object getArg() {
		return arg;
	}

	public void setArg(Object arg) {
		this.arg = arg;
	}

	public ProgressReportListener getReport() {
		return report;
	}

	public RunWorkListener getWorkListener() {
		return workListener;
	}

	public void setWorkListener(RunWorkListener workListener) {
		this.workListener = workListener;
	}

	public void setReport(ProgressReportListener report) {
		this.report = report;
	}

	public void run(){
		if(dowork==null){
			return;
		}
		initHandler();
		if(exec==null){
			exec = Executors.newSingleThreadExecutor();
			
			execRun();
//			exec.shutdown();
		}
		execRun();
	}
	
	private void initHandler(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				switch (msg.what) {
				case state_start:
					if(workListener!=null){
						workListener.started(arg);
					}
					break;
				case state_work:
					if(report!=null){
						report.onProgress(msg.arg1, str_msg, result);
					}
					break;
				case state_cancel:
					if(workListener!=null){
						workListener.complete(true, exception, result);
					}
					release();
					break;
				case state_end:
					if(workListener!=null){
						workListener.complete(false, exception, result);
					}
					release();
					break;
				default:
					break;
				}
				
				
			}
		};
	}
	
	private void release(){
		this.handler=null;
		this.arg=null;
		this.exception=null;
		this.result=null;
		this.str_msg=null;
		if(!isEx){
			this.exec.shutdown();
			this.exec=null;
		}
	}
	
	private void execRun(){
		workListener.started(arg);
		this.future = exec.submit(getTask());
//		exec.execute();
	}
	
	private Runnable getTask(){
		
		return new Runnable() {
			private boolean isEnd = false;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if(handler==null){
						return;
					}
					
					dowork.doWork(new WorkHandler() {
						
						@Override
						public void onProgress(int count, String msg, Object obj) {
							// TODO Auto-generated method stub
							if(handler==null){
								return;
							}
							Message mesg = new Message();
							mesg.arg1=count;
							mesg.what=state_work;
							str_msg = msg;
							result = obj;
							handler.sendMessage(mesg);
						}

						@Override
						public void complete(boolean isCancel, Exception ex,
								Object result)  {
							// TODO Auto-generated method stub
							isEnd=true;
							exception = ex;
							BackgroundWork.this. result=result;
							if(isCancel){
								handler.sendEmptyMessage(state_cancel);
								
							}else{
								handler.sendEmptyMessage(state_end);
							}
							
							future.cancel(true);
//							exec.
//							Thread.interrupted();
						}

						
					}, arg);
					if(!isEnd){
						handler.sendEmptyMessage(state_end);
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					exception=e;
					
					if(handler!=null){
						handler.sendEmptyMessage(state_cancel);
					}
					e.printStackTrace();
					
					return;
				}
			}
		};
	}

	public interface ProgressReportListener {
		public void onProgress(int count, String msg, Object obj);
	}

	public interface WorkHandler {
		public void onProgress(int count, String msg, Object obj);

		public void complete(boolean isCancel,Exception ex,Object result);
		
	}
	
	public interface RunWorkListener{
		public void started(Object arg);
		
		public void complete(boolean isCancel,Exception ex,Object result);
	}

	public interface IDoWork {
		public void doWork(WorkHandler handler, Object arg);
	}

}