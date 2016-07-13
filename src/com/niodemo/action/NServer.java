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
		//1������Selector�����ʵ��
		selector=Selector.open();
		//2����ȡServerSocketChannel����
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		//�󶨵���Ӧ�Ķ˿ں�
		SocketAddress address=new InetSocketAddress(30000);
		serverSocketChannel.socket().bind(address);
		//3������Ϊ������ģʽ
		serverSocketChannel.configureBlocking(false);
		//4����ServerSocketChannelע�ᵽselector��
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		//5����ʼѡ��
		while(selector.select()>0){//���뵽ѡ���SelectionKey������
			//
			for(SelectionKey sk:selector.selectedKeys()){
				//ɾ�����ڴ����key--->��Ŀ�������´β��ô������Channel�ȴ�������֮������ӽ���
				selector.selectedKeys().remove(sk);
				//����ǿͻ��˵���������-->����뵽selector��
				if(sk.isAcceptable()){
					//��ȡ�ͻ��˵�SocketChannel-->��������
					SocketChannel socketChannel = serverSocketChannel.accept();
					socketChannel.configureBlocking(false);
					//ע�ᵽselector��--->����Ϊ׼����--->����Ϊ��ȡ�ͻ��˵�����
					socketChannel.register(selector, SelectionKey.OP_READ);
					//��sk�Ķ�Ӧ��Channel����Ϊ׼������
					sk.interestOps(SelectionKey.OP_ACCEPT);
				}
				//����Ƕ�ȡ����--->
				if(sk.isReadable()){
					//��ȡkey����Ӧ��Channel��׼����ȡ����
					SocketChannel socketChannel = (SocketChannel) sk.channel();
					
					ByteBuffer buffer=ByteBuffer.allocate(1024);
					String content="";
					try {
						while(socketChannel.read(buffer)>0){
							//������׼�����
							buffer.flip();
							
							content+=charset.decode(buffer);
							
							buffer.clear();
						}
						System.out.println("��ȡ������Ϊ��"+content);
						//Ϊ�´ζ�ȡ��׼��
						sk.interestOps(SelectionKey.OP_READ);
					} catch (Exception e) {//�����쳣��˵���ͻ��˳������⣬�Ƴ�key
						sk.cancel();
						if(sk.channel()!=null){
							sk.channel().close();
						}
					}
					//��ȡ���ͻ��˵�����
					if(content.length()>0){
						//��������ע���˵�Channel
						for(SelectionKey key:selector.keys()){
							Channel targetChannel = key.channel();
							//�����SocketChannel �ͻ���
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
