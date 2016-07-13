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

###channels--->通道
定义了各种通道，这些通道表示到能够执行 I/O 操作的实体（如文件和套接字）的连接；定义了用于多路复用的、非阻塞 I/O 操作的选择器。

1. map：直接将数据映射到内存中

###编码集和CharSet
java中的编码采用Unicode字符集，但很多操作系统并不使用unicode字符集，当从系统中读取数据到java程序中进行处理的时候就可能出项乱码等问题。<br>

JDK1.4提供CharSet来处理字节序列和字符序列之间的转换关系，该类包含了用于创建解码器和编码器的方法。

1. forName：获取指定编码集的CharSet
2. newDecoder:获取该CharSet的解码器
3. newEncoder：获取该CharSet的编码器