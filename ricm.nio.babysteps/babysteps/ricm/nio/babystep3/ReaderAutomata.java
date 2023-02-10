package ricm.nio.babystep3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ReaderAutomata {
	enum State {
		READING_LENGTH, READING_MSG
	};

	State state;
	SocketChannel soc;
	int size;
	ByteBuffer len;
	ByteBuffer data;
	WriterAutomata writer;
	SelectionKey key;

	ReaderAutomata(SocketChannel soc, WriterAutomata writer, SelectionKey key) {
		state = State.READING_LENGTH;
		this.soc = soc;
		this.writer = writer;
		len = ByteBuffer.allocate(4);
		this.key = key;
	}

	// lire len avant, puis data
	void readHandle() throws IOException {
		if (state == State.READING_LENGTH) {
			soc.read(len);
			if (len.remaining() == 0) {
				len.rewind();
				size = len.getInt();
				data = ByteBuffer.allocate(size);
				state = State.READING_MSG;
			}
		} else if (state == State.READING_MSG) {
			soc.read(data);
			if (data.remaining() == 0) {
				byte[] bytes = new byte[size];
				data.rewind();
				data.get(bytes, 0, size);
				String msg = new String(bytes);
				System.out.println(msg);
				writer.send(msg.getBytes());
				state = State.READING_LENGTH;
			}

		}
	}

}
