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
	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length<6){
			System.out.println("Insufficient parameter(s)!");
			System.exit(0);
		}
		int packet_size = 150;
		int lag_level = Integer.parseInt(args[3]);
		int packet_lost_level = Integer.parseInt(args[4]);	//The higher level, the more packets lost
		int shuffle_level = Integer.parseInt(args[5]);		//This will disorder packets
		
		if(lag_level<0 || lag_level>10) {
			System.out.println("p1 out of range[0,10]!");		//Please put 0 for best quality
			System.exit(0);
		}
		else if(packet_lost_level<0 || packet_lost_level>10) {    	//Please put 0 for best quality
			System.out.println("p2 out of range[0,10]");
			System.exit(0);
			
		}
		else if(shuffle_level<0 || shuffle_level>10) {
			System.out.println("p3 out of range[0,10]");  	//Please put 0 for best quality
			System.exit(0);
			
		}
		
		try {	DatagramSocket ds = new DatagramSocket(54321);   	//Will use port 54321			
				InputStream audio=new FileInputStream(new File(args[2])); 
			while (audio.available()>0) {
				if(lag_level!=0) {									//Cause Delay
					Thread.sleep((long) (Math.pow(1.542,lag_level)*(0.2+((int)Math.random()*0.8))));
				}
				if(packet_size < audio.available()) {
					byte[] data=new byte[packet_size];
					audio.read(data);
					Random r = new Random();
					for(int i=0; i <(data.length);i++ ) {			//Shuffle packets
						if(shuffle_level>((int)(Math.random()*5000))){
							if (shuffle_level == 0) {
								break;
							}
							int j = r.nextInt(data.length-1);
							byte temp = data[i];					
							data[i] = data[j];
							data[j]=temp;
						}	
					}
					DatagramPacket dp=new DatagramPacket(data,0,data.length);
					dp.setPort(Integer.parseInt(args[1]));
					dp.setAddress(InetAddress.getByName(args[0]));
					if(packet_lost_level == 0||packet_lost_level<(int)(Math.random()*15+1)) {	//Drop packets
						ds.send(dp);
					}
					
				}
					else {
						byte[] data = new byte[audio.available()];
						audio.read(data);
						DatagramPacket dp=new DatagramPacket(data,0,data.length);
						dp.setPort(Integer.parseInt(args[1]));
						dp.setAddress(InetAddress.getByName(args[0]));
						if(packet_lost_level == 0||packet_lost_level<(int)(Math.random()*15+1)) {
						ds.send(dp);
						}
						  
					}
			}
				
			System.out.println("Done");
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
