import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import com.objectplanet.chart.*;
import java.awt.*;

public class game_theoretic_smart_grid {

    public static double Ti=0.5;
    public static double W = 0.3;
    public static double sigam = 0.02;

    public static void main(String[] args) {

        double Buyer_reservation[],Seller_reservation[],Buyer_demand[],Seller_supply[],Seller_expectedPrice[];
        int Number = ThreadLocalRandom.current().nextInt(5, 10 + 1);
        Buyer_reservation=new double[Number];
        Buyer_demand = new double[Number];
        Seller_reservation=new double[Number];
        Seller_supply = new double[Number];
        Seller_expectedPrice = new double[Number];

        Collection sellers,buyers;

        for (int i = 0; i < Number; i++) {
            Buyer_reservation[i]  = ThreadLocalRandom.current().nextInt(15, 60 + 1);
            Buyer_demand[i]  = ThreadLocalRandom.current().nextInt(20, 60 + 1);
            Seller_supply[i]  = ThreadLocalRandom.current().nextInt(75, 220 + 1);
            Seller_reservation[i]  = ThreadLocalRandom.current().nextInt(10, 50 + 1);
            Seller_expectedPrice[i]=ThreadLocalRandom.current().nextInt(10, 50 + 1);
        }
        double randomNum =0;
        double [][] array1 = new double [2][Seller_reservation.length];
        double [][] array2 = new double [2][Seller_reservation.length];
        double [] best_responces= new double[Seller_reservation.length];
        int [] selected_persons = new int[2];
        double [] previous_seller_supply = new double [Number];

        //---------------------------------------------------
        int number=1;
        do {
            sellers = Insertion_sort_indexing(Seller_reservation, Seller_supply,"Asc");
            Seller_reservation = (double [])sellers.get(0);
            Seller_supply = (double [])sellers.get(1);
            buyers = Insertion_sort_indexing(Buyer_reservation, Buyer_demand,"Des");

            Buyer_demand = (double []) buyers.get(0);
            Buyer_reservation = (double []) buyers.get(1);
            boolean found=false;

            if (number>1){
                for (int i = 0; i < Number; i++) {

                    Seller_reservation[i]  = ThreadLocalRandom.current().nextInt(10, 50 + 1);
                    Seller_supply[i]  = ThreadLocalRandom.current().nextInt(75, 220 + 1);
                }
                sellers = Insertion_sort_indexing(Seller_reservation, Seller_supply,"Asc");
                Seller_reservation = (double [])sellers.get(0);
                Seller_supply = (double [])sellers.get(1);

            }
            while(!found)
            {
                selected_persons = find_Intersection(Number,Seller_reservation, Seller_supply, Buyer_reservation, Buyer_demand);
                System.out.print("selected persons are " +selected_persons[0]+" - "+selected_persons[1]+"\n");
                if (selected_persons[0]<3 && selected_persons[1]<3) {
                    for (int i = 0; i < Number; i++) {

                        Seller_reservation[i]  = ThreadLocalRandom.current().nextInt(10, 50 + 1);
                        Seller_supply[i]  = ThreadLocalRandom.current().nextInt(75, 220 + 1);
                    }
                    sellers = Insertion_sort_indexing(Seller_reservation, Seller_supply,"Asc");
                    Seller_reservation = (double [])sellers.get(0);
                    Seller_supply = (double [])sellers.get(1);
                }
                else
                    found=true;
            }
            double Unite_price = Seller_reservation[selected_persons[0]]+Buyer_reservation[selected_persons[1]]/2;
            for (int i = 0; i <= selected_persons[0]; i++)
                previous_seller_supply[i] =  Seller_supply[i];

            for (int i = 0; i <= selected_persons[0]; i++)

                best_responces[i] =  best_response_strategy(Unite_price,selected_persons,i,Seller_reservation,Buyer_reservation,Seller_supply,Buyer_demand);

            for (int i = 0; i <= selected_persons[0]; i++) {
                randomNum = 0 + (double)(Math.random() * 1);
                if (randomNum>0.3) {
                    Seller_supply[i] =  best_responces[i];
                }
                else
                    Seller_supply[i] =  W*Seller_supply[i];
            }

            System.out.print("the program is running "+number+"\n");
            number++;

        } while(!NE(selected_persons[0],Seller_supply,previous_seller_supply));

        //--------------------------------------------------------------------------------------

        System.out.print("the program over");
        draw_curve(Seller_reservation,Seller_supply,Buyer_demand,Buyer_reservation);

        // Find_NE(selected_persons[0],selected_persons[1],Seller_supply,Buyer_demand)
    }
    public static void draw_curve(double [] Seller_reservation,double [] Seller_supply,double [] Buyer_demand,double [] Buyer_reservation){
        double a=0,b=0;
        int c=0,d=0;
        double[] sample_seller1 =new double[Seller_supply.length*2];
        double[] sample_seller2 =new double[Seller_reservation.length*2];
        double[] sample_buyer1 =new double[Buyer_demand.length*2];
        double[] sample_buyer2 =new double[Buyer_reservation.length*2];
        for (int i = 0; i < Seller_reservation.length; i++) {
            sample_seller1[c]=Seller_reservation[i];
            a+=Seller_supply[i];
            sample_seller2[c]=a;
            c++;
            sample_seller1[c]=Seller_reservation[i];
            sample_seller2[c]=a;
            c++;
        }
        for (int i = 0; i < Buyer_reservation.length; i++) {
            sample_buyer1[d]=Buyer_reservation[i];
            b+=Buyer_demand[i];
            sample_buyer2[d]=b;
            d++;
            sample_buyer1[d]=Buyer_reservation[i];
            sample_buyer2[d]=b;
            d++;
        }
        String[] sampleLabels2 = new String[sample_buyer2.length*2];
        String[] sampleLabels3 = new String[sample_buyer1.length*2];
        for (int i = 0; i < sample_buyer2.length; i++) {
            sampleLabels2[i]=(String.valueOf(sample_buyer2[i]));
        }
        for (int i = 0; i < sample_seller2.length; i++) {
            sampleLabels3[i]=(String.valueOf(sample_seller2[i]));
        }
        Color[] sampleColors = new Color[] {new Color(0xFFCC00),new Color(0xFF6600)};
        LineChart chart = new LineChart();
        chart.set3DModeOn(true);
        chart.set3DDepth(30);
        chart.setSeriesCount(2);
        chart.setSampleCount(sample_seller1.length);
        chart.setSampleValues(0, sample_seller1);
        chart.setSampleLabels(sampleLabels3);
        chart.setSampleValues(1, sample_buyer1);
        chart.setSampleColors(sampleColors);
        chart.setSampleLabelsOn(true);
        chart.setSampleLabelStyle(Chart.BELOW);
        chart.setFont("sampleLabelFont", new Font("Arial", Font.BOLD, 11));
        chart.setValueLabelsOn(true);
        chart.setFont("valueLabelFont", new Font("Arial", Font.BOLD, 11));
        chart.setLegendOn(true);
        chart.setLegendLabels(new String[] {"Seller", "Buyer"});
        chart.setFont("legendFont", new Font("Arial", Font.BOLD, 11));
        chart.setFont("rangeLabelFont", new Font("Arial", Font.BOLD, 11));
        chart.setBackground(Color.white);
        com.objectplanet.chart.NonFlickerPanel p = new com.objectplanet.chart.NonFlickerPanel(new BorderLayout());
        p.add("Center", chart);
        Frame f = new Frame();

        f.add("Center", p);
        f.setSize(1400,700);
        f.show();
    }

    public static Collection Insertion_sort_indexing(double [] A ,double [] B ,String type_sort)
    {
        // A as key and B as a value like hashmap <double,double> structure
        // at the end A would be sorted in (descending or ascending) order with corresponding value in array of B
        int j;
        double key,value;

        for (int i = 1; i < A.length; i++) {

            j=i-1;
            key = A[i]; // is an item to be sorted
            value= B[i];
            if ( type_sort == "Asc") {
                while (j >= 0 && A[j] > key) {
                    A[j + 1] = A[j];
                    B[j + 1] = B[j];
                    j--;
                }
            }
            else
            {
                while (j >= 0 && A[j] < key)
                {
                    A[j + 1] = A[j];
                    B[j + 1] = B[j];
                    j--;
                }
            }
            A[j+1]=key;
            B[j+1]= value;
            }
       return new Collection(A, B);
    }

    public static boolean NE(int sellers,double [] seller_supply,double [] previous_seller_reservation )
    {
        boolean found = false;
        int count=0;
        for (int i = 0; i <= sellers; i++)
            if (Math.abs(seller_supply[i]-previous_seller_reservation[i]) < sigam)
                count++;
        if (count==sellers)
            return true;
        return false;
    }
    public static boolean Find_NE(int sellers,int buyers,double [] seller_supply,double [] buyer_demand )
    {
        double volume_seller=0,volume_buyer=0;
        for (int i = 0; i < sellers; i++) {
            volume_seller+= seller_supply[i];
            for (int j = 0; j < buyers; j++)
                volume_buyer+=buyer_demand[i];
            if (volume_seller<=volume_buyer)
                return true;
            return false;
        }
        return false;
    }

    public static double best_response_strategy(double Unite_price,int [] selected_persons ,int seller_i, double [] seller_reservation,double [] buyer_reservation, double [] seller_supply,double [] buyer_demand )
    {
        int supply=0,demand=0;

        for (int i = 0; i <= selected_persons[0]; i++)
            supply+=seller_supply[i];
        for (int i = 0; i <=selected_persons[1]; i++)
            demand+=buyer_demand[i];
        if (supply<=demand)
            return ( (Unite_price - seller_reservation[seller_i] )/(2*Ti) );
        else
            return (double) ((Unite_price-seller_reservation[seller_i])*selected_persons[0]+2*Ti*( (supply-seller_supply[seller_i])-demand ))/((2*Ti)*(selected_persons[0]-2));
    }

    public static int [] find_Intersection(int n,double [] seller_reservation,double [] seller_supply,double[] Buyer_reservation,double [] Buyer_demand)
    {
        double volume_seller=0,volume_Buyer=0;
        boolean found=false;
        int i,j ;
        int [] array= new int[2];
        i=j=0;
        volume_seller = seller_reservation[0];
        volume_Buyer = Buyer_reservation[0];


        if (seller_reservation[seller_reservation.length-1]>Buyer_reservation[Buyer_reservation.length-1]) {
            while(!found && i<n-1 && j<n-1)
            {
                if (seller_reservation[i]<=Buyer_demand[j] && i<n-1 && j<n-1) {


                    if (volume_seller<volume_Buyer) {

                        volume_seller +=seller_supply[++i];
                    }
                    else
                    {
                        volume_Buyer+=Buyer_demand[++j];
                    }
                }
                else
                    found=true;

            }//end while

            array[0]=i-1;
            array[1]=j-1;
        }// end if
        else
        {
            System.out.print("------ seller reservation is lower than buyer reservation ------ "+"\n");
            array[0]=-1;
            array[1]=-1;
        }
        return array;
    }


}
