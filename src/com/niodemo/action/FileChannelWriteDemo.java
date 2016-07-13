package com.niodemo.action;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ���ڶ�ȡ��д�롢ӳ��Ͳ����ļ���ͨ����
 */
public class FileChannelWriteDemo {

	public static void main(String[] args) {
		String path = "resource/FileChannelWrite.txt";
		File file=new File(path);

		FileOutputStream fos=null;
		FileChannel fileChannel=null;
		try {
			fos = new FileOutputStream(file);
			fileChannel = fos.getChannel();
			
			ByteBuffer src=ByteBuffer.allocate(1024);
			src.put("�����������ȹ�".getBytes());
			
			//����limite Ϊ�������׼��
			src.flip();
			
			fileChannel.write(src);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			close(fileChannel);
			close(fos);
		}
	}

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
