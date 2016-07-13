package com.niodemo.action;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 用于读取、写入、映射和操作文件的通道。
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
			src.put("今天天气好热哈".getBytes());
			
			//锁定limite 为输出做好准备
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
