package com.zhd.shj;

import java.io.DataOutputStream;
import java.io.IOException;

public class Ioport {
	String path = "/sys/class/gpio";
	public void beep(String cmd) {
		DataOutputStream dos ;
		try {
			Process process = Runtime.getRuntime().exec("su");
			process.getOutputStream().write(cmd.getBytes());
			dos = new DataOutputStream(process.getOutputStream());
			dos.writeBytes(cmd);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void gpio(int pins,String direction,int value){

		String cmd= "echo "+direction +" >  " + path + "/gpio" + pins + "/direction" + "\n"
				+"echo " + value + "  > " + path + "/gpio" + pins + "/value" + "\n";
		beep(cmd);
	}
	public void export(int pins){
		String  ex="echo " + pins + " > " + path + "/export" + "\n";
		beep(ex);
	}
	public void unexport(int pins){
		String  unex="echo " + pins + " > " + path + "/unexport" + "\n";
		beep(unex);
	}
	
}
