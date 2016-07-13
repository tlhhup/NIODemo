package com.niodemo.action;

import java.io.Closeable;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 用于读取、写入、映射和操作文件的通道。
 */
public class FileChannelReadDemo {

	public static void main(String[] args) {
		String file = "resource/FileChannel.txt";

		FileInputStream fis = null;
		FileChannel fileChannel = null;
		try {
			fis = new FileInputStream(file);
			//获取文件通道
			fileChannel = fis.getChannel();
			
			//开辟一个字节缓冲区
			ByteBuffer dst=ByteBuffer.allocate(1024);
			
			while(fileChannel.read(dst)!=-1){
				//锁定数据为输出做好准备
				dst.flip();
				
				//创建CharSet对象
				Charset charset=Charset.forName("GBK");
				//创建解码器
				CharsetDecoder decoder = charset.newDecoder();
				//将ByteBuffer中的数据进行解码
				CharBuffer charBuffer = decoder.decode(dst);
				System.out.println(charBuffer);
				
				//buffer初始化，为输入做好准备
				dst.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(fileChannel);
			close(fis);
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
