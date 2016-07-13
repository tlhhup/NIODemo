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
		//创建selector对象
		selector=Selector.open();
		//创建客户端对象
		SocketAddress remote=new InetSocketAddress(30000);
		socketChannel=SocketChannel.open();
		socketChannel.connect(remote);
		//设置为非阻塞方式
		socketChannel.configureBlocking(false);
		//注册
		socketChannel.register(selector, SelectionKey.OP_READ);
		//启动读取的线程
		new ClientThread().start();
		//向服务器发送数据
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
						//删除正在处理的key--->其目的是在下次不用处理这个Channel等处理完了之后在添加进来
						selector.selectedKeys().remove(key);
						//去读数据
						if(key.isReadable()){
							SocketChannel channel = (SocketChannel)key.channel();
							ByteBuffer buffer=ByteBuffer.allocate(1024);
							String content="";
							while(channel.read(buffer)>0){
								buffer.flip();
								content+=charset.decode(buffer);
								
								buffer.clear();
							}
							System.out.println("读取到的数据为："+content);
							
							//为下一次读作准备
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
