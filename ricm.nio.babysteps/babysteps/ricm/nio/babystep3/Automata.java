package ricm.nio.babystep3;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

public class Automata {

	WriterAutomata Wa;
	ReaderAutomata Ra;
	
	public Automata (WriterAutomata Wa, ReaderAutomata Ra) {
		this.Wa = Wa;
		this.Ra= Ra;
	}
	
	void writeHandle() throws IOException {
		Wa.writeHandle();
	}
	
	void readHanldle() throws IOException {
		Ra.readHandle();
	}
	
	void send(byte[] bytes) throws ClosedChannelException {
		Wa.send(bytes);
	}
	
	void setWA (WriterAutomata Wa ) {
		this.Wa=Wa;
	}
	
	void setRA (ReaderAutomata Ra ) {
		this.Ra=Ra;
	}
}
