package ricm.nio.babystep3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class WriterAutomata {

	enum State {WRITING_LENGTH, WRITING_MSG, WRITING_IDLE} ;
	State state;
	SocketChannel soc;
	int size;
	ByteBuffer len;
	ByteBuffer data;
	Selector selector;
	ArrayList <byte[]> pendingMsgs;
	SelectionKey key;
	
	WriterAutomata(SocketChannel soc, Selector selector, SelectionKey key){
		state= State.WRITING_IDLE;
		this.selector= selector;
		this.soc = soc;
		len = ByteBuffer.allocate(4);
		pendingMsgs = new ArrayList <byte[]>();
		this.key = key;
	}
	
	// lire len avant, puis data
	void writeHandle() throws IOException {
		if(state == State.WRITING_LENGTH) {
			soc.write(len);
			if(len.remaining() ==0) {
				data = ByteBuffer.wrap(pendingMsgs.get(0),0,pendingMsgs.get(0).length);
				//pendingMsgs.remove(0);
				state = State.WRITING_MSG;
			}
		}else if (state == State.WRITING_MSG) {
			soc.write(data);
			if (data.remaining() == 0) {
				if(!pendingMsgs.isEmpty()) {
					//len.rewind();
					state = State.WRITING_LENGTH;
					len.rewind();
					len.putInt(pendingMsgs.get(0).length);
					//len.rewind();
				}else {
					state = State.WRITING_IDLE;
					soc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				}
			}
			 
		}else if (state == State.WRITING_IDLE) {
			if(!pendingMsgs.isEmpty()){  //il y a un message 
				state = State.WRITING_LENGTH;
			}else {
				soc.register(selector, SelectionKey.OP_READ);
			}
		}
	}
	
	public void send(byte[] bytes) throws ClosedChannelException {
		pendingMsgs.add(bytes);
		if (state == State.WRITING_IDLE) {
			state = State.WRITING_LENGTH;
			len.rewind();
			len.putInt(bytes.length);
			len.rewind();
			soc.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		}
	}
}
