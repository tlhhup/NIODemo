package com.niodemo.action;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class NServer {
	
	private Selector selector;
	
	private Charset charset=Charset.forName("UTF-8");
	
	public void init() throws Exception{
		//1、创建Selector对象的实例
		selector=Selector.open();
		//2、获取ServerSocketChannel对象
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		//绑定到对应的端口号
		SocketAddress address=new InetSocketAddress(30000);
		serverSocketChannel.socket().bind(address);
		//3、设置为非阻塞模式
		serverSocketChannel.configureBlocking(false);
		//4、将ServerSocketChannel注册到selector中
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		//5、开始选择
		while(selector.select()>0){//加入到选择的SelectionKey集合中
			//
			for(SelectionKey sk:selector.selectedKeys()){
				//删除正在处理的key--->其目的是在下次不用处理这个Channel等处理完了之后在添加进来
				selector.selectedKeys().remove(sk);
				//如果是客户端的连接请求-->则加入到selector中
				if(sk.isAcceptable()){
					//获取客户端的SocketChannel-->接受连接
					SocketChannel socketChannel = serverSocketChannel.accept();
					socketChannel.configureBlocking(false);
					//注册到selector中--->设置为准备读--->设置为读取客户端的请求
					socketChannel.register(selector, SelectionKey.OP_READ);
					//将sk的对应的Channel设置为准备连接
					sk.interestOps(SelectionKey.OP_ACCEPT);
				}
				//如果是读取操作--->
				if(sk.isReadable()){
					//获取key所对应的Channel，准备读取数据
					SocketChannel socketChannel = (SocketChannel) sk.channel();
					
					ByteBuffer buffer=ByteBuffer.allocate(1024);
					String content="";
					try {
						while(socketChannel.read(buffer)>0){
							//锁定，准备输出
							buffer.flip();
							
							content+=charset.decode(buffer);
							
							buffer.clear();
						}
						System.out.println("读取的数据为："+content);
						//为下次读取做准备
						sk.interestOps(SelectionKey.OP_READ);
					} catch (Exception e) {//出现异常，说明客户端出现问题，移除key
						sk.cancel();
						if(sk.channel()!=null){
							sk.channel().close();
						}
					}
					//获取到客户端的请求
					if(content.length()>0){
						//遍历所有注册了的Channel
						for(SelectionKey key:selector.keys()){
							Channel targetChannel = key.channel();
							//如果是SocketChannel 客户端
							if(targetChannel instanceof SocketChannel){
								((SocketChannel) targetChannel).write(charset.encode(content));
							}
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			new NServer().init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
