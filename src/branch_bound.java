import java.util.ArrayList;
import java.util.Collections;
 
 
 
public class branch_bound {
    static double[][] costs;
 
     int opt_cost = Integer.MAX_VALUE;
     int verts;
     int cost;
      
        public static void main(String[] args)
        {   
        	ArrayList<Integer> tour = new ArrayList<Integer>();
            branch_bound bb = new branch_bound();
            bb.init_matrix();
            tour.add(0);
            tour.add(1);
            tour.add(2);
            
           
            System.out.println("tour size: " + tour.size());
            bb.calculateBound(tour);
        }
        
    private void branch_and_bound(int currCity, ArrayList tour)
    {
             
        if(tour.size() == 0)
        {
        //    tour.add(0);
             
        }
         
    }
     
    private void calculateBound(ArrayList constraints)
    {
        double rowTotal = 0;
        double total = 0;
        ArrayList<Double>row = new ArrayList<Double>();
       
        if(constraints.size() == 1)
        {
        	 System.out.println("took if");
            for(int i = 0; i < costs.length; i++)
            {
                for(int j = 0; j < costs.length; j++)
                {
                    row.add(costs[i][j]);
                }
                Collections.sort(row);
                rowTotal = row.get(0) + row.get(1);
                total += rowTotal;
                row.clear();
            }
            System.out.println(total/2);
        }
        else
        {
        	 System.out.println("took else");
            double rowConstraint = 0;
            double rowT = 0;
            double totl = 0;
            ArrayList<Double>rowItems = new ArrayList<Double>();
            for(int i = 0; i < costs.length; i++)
            {
                for(int j = 0; j < costs.length; j++)
                {    
                	rowItems.add(costs[i][j]);
                
                }
                		Collections.sort(rowItems);
               
		               if(i < constraints.size()-1){
		                  rowConstraint = costs[(int)constraints.get(i)][(int)constraints.get(i+1)];
		                  rowT = rowConstraint + rowItems.get(0); 	
		                  System.out.println(rowConstraint + " + " + rowItems.get(0));
		               } else if(i == constraints.size() -1) {
		            	   rowConstraint = costs[(int)constraints.get(constraints.size()-1)][(int)constraints.get(constraints.size()-2)];
			               rowT = rowConstraint + rowItems.get(0);
			               System.out.println(rowConstraint + " + " + rowItems.get(0));
		               } else {
		               rowT = rowItems.get(0) + rowItems.get(1);
			           System.out.println(rowItems.get(0) + " + " + rowItems.get(1));
		               }
		               
               totl += rowT;
               rowItems.clear();
            }
            System.out.println(totl/2);
        }
    }
     
    public void init_matrix()
    {
         costs = new double[][]
            {
             {Integer.MAX_VALUE, 3.0, 1.0, 5.0, 8.0},
             {3.0, Integer.MAX_VALUE, 6.0, 7.0, 9.0},
             {1.0, 6.0, Integer.MAX_VALUE, 4.0, 2.0},
             {5.0, 7.0, 4.0, Integer.MAX_VALUE, 3.0},
             {8.0, 9.0, 2.0, 3.0, Integer.MAX_VALUE}
             };
         verts = costs.length;
    }
}