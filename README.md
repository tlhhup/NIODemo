###NIODemo

在JDK1.4之后java引入的新的IO API，用于操作各种类型的流。采用不同的方式来处理输入/输出，新IO采用内存映射文件的方式来处理输入/输出，新IO将文件或文件的一段区域映射到内存中，这样就可以像反问内存一样来反问文件了。

----
###Buffer-->缓冲区
定义类各种数据类型的的缓冲区。发送到Channel中的数据都必须首先放到Buffer中，而从Channel中读取的数据也必须先读取到Buffer中。常用的有ByteBuffer和CharBuffer

1. 容量：该buffer中最大的数据容量 capacity
2. 界限：第一个不应该读取或写入的元素的索引，其后面的数据及不可读也不可写 limit
3. 位置：下一个要读取或写入的元素的索引 position。从Channel读取了多少数据那儿当前的position就为多少。
4. mark:将position的位置指定到相应的位置
5. 关系：0 <= 标记 <= 位置 <= 限制 <= 容量 
6. flip：当buffer装入数据结束之后，调用该方法将limit设置为position所在位置，将position设为0，为输出数据做好了准备。
7. clear：当buffer输出数据结束后，调用clear方法，将position置为0，将limit置为capacity，为再次读入数据做好准备。
8. rewind() 使缓冲区为重新读取已包含的数据做好准备：它使限制保持不变，将位置设置为 0。

分类：

- ByteBuffer 
- MappedByteBuffer 
- CharBuffer 
- DoubleBuffer 
- FloatBuffer 
- IntBuffer 
- LongBuffer 
- ShortBuffer


###channels--->通道
定义了各种通道，这些通道表示到能够执行 I/O 操作的实体（如文件和套接字）的连接；定义了用于多路复用的、非阻塞 I/O 操作的选择器。

分类：

1. FileChannel：从文件中读写数据。 
2. DatagramChannel：能通过UDP读写网络中的数据。 
3. SocketChannel：能通过TCP读写网络中的数据。 
4. ServerSocketChannel：可以监听新进来的TCP连接，像Web服务器那样。对每一个新进来的连接都会创建一个SocketChannel。


方法:

1. map：直接将数据映射到内存中

![读取数据](http://i.imgur.com/Pb3LVNM.png)
	

###编码集和CharSet
java中的编码采用Unicode字符集，但很多操作系统并不使用unicode字符集，当从系统中读取数据到java程序中进行处理的时候就可能出项乱码等问题。<br>

JDK1.4提供CharSet来处理字节序列和字符序列之间的转换关系，该类包含了用于创建解码器和编码器的方法。

1. forName：获取指定编码集的CharSet
2. newDecoder:获取该CharSet的解码器
3. newEncoder：获取该CharSet的编码器

###Selector
Selector允许单线程处理多个 Channel。如果你的应用打开了多个连接（通道），但每个连接的流量都很低，使用Selector就会很方便。

![](http://i.imgur.com/NPz8p5a.png)

要使用Selector，得向Selector注册Channel，然后调用它的select()方法。这个方法会一直阻塞到某个注册的通道有事件就绪。一旦这个方法返回，线程就可以处理这些事件，事件的例子有如新连接进来，数据接收等。 

1. Selector：它是SelectableChannel对象的多路复用器，所有采用非阻塞方式进行通信的Channel都可以注册到Selector对象中。可以通过此类的静态open方法来创建Selector对象。可以同时监控多个SelectableChannel对象，是非阻塞IO的核心。
	1. 一个Selector对象有三个SelectionKey的集合
		1. 所有的SelectionKey集合：代表注册到该Selector上的Channel，可以通过keys方法获取
		2. 被选择的SelectionKey集合：代表所有可通过select方法检测到、需要进行IO处理的Channel，可以通过selectedKeys方法获取
		3. 被取消的SelectionKey集合：代表所有被取消注册关系的Channel，在下一次执行select方法时，这些Channel对应的SelectionKey会被彻底删除
	4. 方法
		1. select()：监控所有注册的Channel，如果有需要处理IO操作，该方法放回，并将对应的SelectionKey加入到被选择的SelectionKey集合中，该方法返回Channel的数量
		2. select(long timeout)：可以设置操作时长
		3. selectNow():执行一个立即方法的select()操作，相对select()方法，该方法不会阻塞线程
		4. wakeup()：使一个还未返回的select()方法立刻返回
2. SelectableChannel:它代表可以支持非阻塞IO操作的Channel对象，调用register()方法将Channel注册到指定的Selector上。该对象支持阻塞和非阻塞两种模式(默认为阻塞模式)
	1. 子类：DatagramChannel, Pipe.SinkChannel, Pipe.SourceChannel, ServerSocketChannel, SocketChannel 
	2. 相关方法：
		1. configureBlocking：设置是否采用阻塞模式
		2. isBlocking()：是否阻塞
		3. register():方法的第二个参数是一个“interest集合”，意思是在通过Selector监听Channel时对什么事件感兴趣:Connect、 Accept、Read、Write
 
	3. 创建ServerSocketChannel
	
			//先获取该通过的实例
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			//在调用socket获取到ServerSocket最后在绑定到相应的地址上
			SocketAddress endpoint=new InetSocketAddress("127.0.0.1", 30000);
			serverSocketChannel.socket().bind(endpoint);

