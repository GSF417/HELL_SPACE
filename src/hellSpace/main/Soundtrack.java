package hellSpace.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Soundtrack {
	
	/* Death March from Megami Tensei II OST */
	public static ClipCollection bgm = load("/Universe_Big_Takedown.wav", 1);
	/* SFX made with BFRX */
	public static ClipCollection laserShoot = load("/laser_shoot.wav", 1);
	public static ClipCollection hurtSound = load("/hurt.wav", 1);

	public static class ClipCollection {
		public Clip[] clipCollection;
		private int p;
		private int count;
		
		public ClipCollection (byte[] buffer,
											int count) throws LineUnavailableException,
															IOException,
															UnsupportedAudioFileException {
			if (buffer == null) {
				return;
			}
			
			clipCollection = new Clip[count];
			this.count = count;
			
			for (int i = 0; i < count; i++) {
				clipCollection[i] = AudioSystem.getClip();
				clipCollection[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
			}
		}
		
		public void play() {
			if (clipCollection == null) {
				return;
			}
			clipCollection[p].stop();
			clipCollection[p].setFramePosition(0);
			FloatControl gainControl = (FloatControl) clipCollection[p].getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-12.0f);
			clipCollection[p].start();
			p++;
			if (p >= count) {
				p = 0;
			}
		}
		
		public void loop() {
			if (clipCollection == null) {
				return;
			}
			clipCollection[p].loop(Clip.LOOP_CONTINUOUSLY);
		}
		
		public void stop() {
			if (clipCollection == null) {
				return;
			}
			clipCollection[p].stop();
			clipCollection[p].setFramePosition(0);
		}
	}
	
	public static ClipCollection load(String name, int count) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			DataInputStream dis = new DataInputStream(new FileInputStream(FileSystems.getDefault().getPath("res", name).toFile()));
			byte[] buffer = new byte[1024];
			int read = 0;
			while ((read = dis.read(buffer)) >= 0) {
				baos.write(buffer, 0, read);
			}
			dis.close();
			byte[] data = baos.toByteArray();
			return new ClipCollection(data, count);
		}
		catch (Exception e) {
			try {
				e.printStackTrace();
				return new ClipCollection(null, 0);
			}
			catch (Exception er) {
				e.printStackTrace();
				return null;
			}
		}
	}
}