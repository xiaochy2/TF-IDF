import java.io.BufferedReader;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStreamReader;

public class Kp_re {
	public static void main(String args[]) {
		TestR R = new TestR();
		R.readfile();
		/*
		 * for(int i = 0;i<R.v.length;i++){
		 * System.out.println(R.v[i]+" "+R.W[i]); }
		 * System.out.println(R.weight);
		 */
		
		

	}
}

class TestR {
	
	String path = "KP_input_2.txt";

	public void readfile() {

		FileInputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			in = new FileInputStream(this.path);
			isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
            int num;
			int i = 0;
            num = Integer.parseInt(br.readLine());
				
			
			
			in = new FileInputStream(this.path);
			isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
            String line = new String;
            int cou = 1;
			while ((line = br.readLine()) != null) {
                Hashset<Charactor> set1 = new Hashset<Charactor>();
                set1.add('1');
                set1.add('2');
                set1.add('3');
                
                Hashset<Charactor> set2 = new Hashset<Charactor>();
                set2.add('E');
                set2.add('S');
                set2.add('F');
               
                Hashset<Charactor> set3 = new Hashset<Charactor>();
                set3.add('R');
                set3.add('P');
                set3.add('G');
                
                Hashset<Charactor> set4 = new Hashset<Charactor>();
                set4.add('O');
                set4.add('S');
                set4.add('D');
                
                
                
                
                String[] sp = line.split(" ");
                char c1[] = sp[0].toCharArray();
                char c2[] = sp[1].toCharArray();
                char c3[] = new char[4];
                set1.remove(c1[0]);
                set1.remove(c2[0]);
                
                set2.remove(c1[1]);
                set2.remove(c2[1]);
                
                set3.remove(c1[2]);
                set3.remove(c2[2]);
                
                set4.remove(c1[3]);
                set4.remove(c2[3]);
                
                if(set1.size()==2){
                    c3[0] = c1[0];
                } else{
                    for(Iterator it = set1.iterator();it.hasNext();){
                        c3[0] =it.next();
                    }
                }
                
                if(set1.size()==2){
                    c3[1] = c1[1];
                } else{
                    for(Iterator it = set2.iterator();it.hasNext();){
                        c3[1] =it.next();
                    }
                }
                if(set1.size()==2){
                    c3[2] = c1[2];
                } else{
                    for(Iterator it = set3.iterator();it.hasNext();){
                        c3[2] =it.next();
                    }
                }
                if(set1.size()==2){
                    c3[3] = c1[3];
                } else{
                    for(Iterator it = set4.iterator();it.hasNext();){
                        c3[3] =it.next();
                    }
                }
                System.out.println("Group "+cou+ ": ");
                for(int i = 0 ; i < c3.length;i++){
                    System.out.println(c3[i]);
                }
				

				cou++;
			}
			

			br.close();
			isr.close();
			in.close();

		} catch (IOException e1) {
			System.exit(-1);
		}
	}
}
