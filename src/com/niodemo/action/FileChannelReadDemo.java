package com.niodemo.action;

import java.io.Closeable;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * ���ڶ�ȡ��д�롢ӳ��Ͳ����ļ���ͨ����
 */
public class FileChannelReadDemo {

	public static void main(String[] args) {
		String file = "resource/FileChannel.txt";

		FileInputStream fis = null;
		FileChannel fileChannel = null;
		try {
			fis = new FileInputStream(file);
			//��ȡ�ļ�ͨ��
			fileChannel = fis.getChannel();
			
			//����һ���ֽڻ�����
			ByteBuffer dst=ByteBuffer.allocate(1024);
			
			while(fileChannel.read(dst)!=-1){
				//��������Ϊ�������׼��
				dst.flip();
				
				//����CharSet����
				Charset charset=Charset.forName("GBK");
				//����������
				CharsetDecoder decoder = charset.newDecoder();
				//��ByteBuffer�е����ݽ��н���
				CharBuffer charBuffer = decoder.decode(dst);
				System.out.println(charBuffer);
				
				//buffer��ʼ����Ϊ��������׼��
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
