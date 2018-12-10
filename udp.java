import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

public class udp {
	public static void main(String[] args) throws IOException {
		if(args.length<6){
			System.out.println("Error00: Insufficient parameter(s)!");
			System.exit(0);
		}
		int packet_size = Integer.parseInt(args[3]);
		int packet_lost_level = Integer.parseInt(args[4]);
		int shuffle_level = Integer.parseInt(args[5]);
		
		if(packet_size<1 || packet_size > 65500) {
			System.out.println("Error01: Invalid packet_size(p1)!");
			System.exit(0);
		}
		else if(packet_lost_level<0 || packet_lost_level>10) {
			System.out.println("Error02: p2 out of range[0,10]");
			System.exit(0);
			
		}
		else if(shuffle_level<0 || shuffle_level>10) {
			System.out.println("Error03: p3 out of range[0,10]");
			System.exit(0);
			
		}
		
		try { DatagramSocket ds = new DatagramSocket(Integer.parseInt(args[1])+1);
			InputStream audio=new FileInputStream(new File(args[2]));
			  System.out.println("Listening on"+args[1]);
		while (audio.available()>0) {
			if(packet_size < audio.available()) {
				byte[] data=new byte[packet_size];
				audio.read(data);
				Random r = new Random();
				for(int i=0; i <(data.length);i++ ) {
					if(shuffle_level>((int)(Math.random()*1000))){
						int j = r.nextInt(data.length-1);
						byte temp = data[i];					
						data[i] = data[j];
						data[j]=temp;
					}	
				}
				DatagramPacket dp=new DatagramPacket(data,0,data.length);
				dp.setPort(Integer.parseInt(args[1]));
				dp.setAddress(InetAddress.getByName(args[0]));
				if(packet_lost_level<(int)(Math.random()*15+1)) {
				ds.send(dp);
				}
				
			}
			else {
				byte[] data = new byte[audio.available()];
				audio.read(data);
				DatagramPacket dp=new DatagramPacket(data,0,data.length);
				dp.setPort(Integer.parseInt(args[1]));
				dp.setAddress(InetAddress.getByName(args[0]));
				if(packet_lost_level<(int)(Math.random()*15+1)) {
				ds.send(dp);
				}
				  
			}
		}
			
			
		audio.close();
					ds.close();
		}catch (SocketException e){
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		
  

	}
}
