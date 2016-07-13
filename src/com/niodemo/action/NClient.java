package com.niodemo.action;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class NClient {

	private Selector selector;
	private Charset charset=Charset.forName("UTF-8");
	private SocketChannel socketChannel;
	
	public void init() throws IOException{
		//����selector����
		selector=Selector.open();
		//�����ͻ��˶���
		SocketAddress remote=new InetSocketAddress(30000);
		socketChannel=SocketChannel.open();
		socketChannel.connect(remote);
		//����Ϊ��������ʽ
		socketChannel.configureBlocking(false);
		//ע��
		socketChannel.register(selector, SelectionKey.OP_READ);
		//������ȡ���߳�
		new ClientThread().start();
		//���������������
		Scanner scanner=new Scanner(System.in);
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			socketChannel.write(charset.encode(line));
		}
	}
	
	private class ClientThread extends Thread{
		
		@Override
		public void run() {
			try {
				while(selector.select()>0){
					for(SelectionKey key:selector.selectedKeys()){
						//ɾ�����ڴ����key--->��Ŀ�������´β��ô������Channel�ȴ�������֮������ӽ���
						selector.selectedKeys().remove(key);
						//ȥ������
						if(key.isReadable()){
							SocketChannel channel = (SocketChannel)key.channel();
							ByteBuffer buffer=ByteBuffer.allocate(1024);
							String content="";
							while(channel.read(buffer)>0){
								buffer.flip();
								content+=charset.decode(buffer);
								
								buffer.clear();
							}
							System.out.println("��ȡ��������Ϊ��"+content);
							
							//Ϊ��һ�ζ���׼��
							key.interestOps(SelectionKey.OP_READ);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		try {
			new NClient().init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
