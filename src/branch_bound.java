import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
 
public class branch_bound {
    private static double[][] costs; //Holds cost of each verticy.
    private boolean visited[][]; //Marks if the vertex was visited.
    //Prints out the best tour.
    private ArrayList<Integer>bestTour = new ArrayList<Integer>();
    private int xPos[]; //x coordinates.
    private int yPos[]; //y coordinates.
    private int currCity = 0;
    private double routeCost = 0; //Current route cost.
    private double optimalCost = Double.MAX_VALUE; //Best cost.
     
    public static void main(String[] args)
    {
        //Make new tour.
        ArrayList<Integer> tour = new ArrayList<Integer>(); 
        branch_bound bb = new branch_bound(); 
        bb.readfile("bb.txt"); //Read file.
        bb.generateMatrix(); //Generate costs.
        bb.init_visited(); //Generate visited matrix.
        tour.add((int)costs[0][0]); //Add start city.
        long startTime = System.currentTimeMillis();
        bb.branch_and_bound(bb.currCity, tour); 
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total Time: " + totalTime);
        System.out.println("Optimal Cost: " + bb.optimalCost);
        System.out.println("Best Tour:");
        for(int i = 0; i < bb.bestTour.size(); i++)
        {
            System.out.print(bb.bestTour.get(i) + " ");
        }
    }
 
    /**
     * Read tsp file and add coordinates 
     * to cost matrix.
     * @param fileName the name of the 
     * tsp file.
     */
    private void readfile(String fileName)
    {
        File file = new File(fileName);
        ArrayList<String> x = new ArrayList<String>();
        ArrayList<String> y = new ArrayList<String>();
        boolean found = false;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
              
            while ((line = br.readLine()) != null)
            {
                System.out.println(line);
                if(found)
                {
                    if(line.contains("EOF"))
                    {
                        break;
                    } 
                    else
                    {
                        String[] parts = line.split("\\s+");
                        x.add(parts[1]);
                        y.add(parts[2]);
                    }
                }
                if(line.contains("NODE_COORD_SECTION"))
                {
                    found = true;
                }
            }
            br.close();
        } 
        catch (IOException xx)
        {
            System.out.println("IO EXCEPTION");
        }
  
        xPos = new int[x.size()];
        yPos = new int[y.size()];
        costs = new double[x.size()][y.size()];
  
        for(int i = 0; i < xPos.length; i++)
        {   
            int valx = (int) Double.parseDouble(x.get(i));
            xPos[i] = valx;
            int valy = (int) Double.parseDouble(y.get(i));
            yPos[i] = valy;
        }
    }
     
    /**
     * Uses the branch and bound method
     * to find the best possible tour.
     * @param currCity the current city 
     * in the tour.
     * @param tour an array of the tour so far.
     */
    private void branch_and_bound(int currCity, ArrayList<Integer> tour)
    {
        int startCity = 0; //The first city of the tour.
        //If at the end of the tour go back to start city.
        if (tour.size() == costs.length) 
        {
            //Add the cost of the current edge.
            routeCost += getCost(currCity, startCity);
            //Check if better than optimal cost.
            if(routeCost < optimalCost) 
            {
                optimalCost = routeCost; //New optimal cost.
                bestTour = tour; //New best tour.
            }
            //In case of recursion reset routeCost.
            routeCost -= getCost(currCity, startCity);
        }
        else
        {
            for(int i = 1; i < costs.length; i++)
            {
                if(!tour.contains(i)) //Check if city is in tour.
                {
                    routeCost += getCost(currCity, i);
                    visited[currCity][i] = visited[i][currCity];
                    double lowerBound = calculateBound(tour);
                    if(routeCost < optimalCost && lowerBound < optimalCost)
                    {
                        //Next is the tour so far.
                        ArrayList<Integer>next = tour; 
                        next.add(i);
                        branch_and_bound(i, next);
                    }
                    //In case of recursion reset routeCost.
                    routeCost -= getCost(currCity, i);
                    visited[i][currCity] = visited[currCity][i] = false;
                }
            }
        }
    }
     
    /**
     * Calculates the lower bound for the tour.
     * @param constraints the edges that have 
     * to be taken.
     */
    private double calculateBound(ArrayList<Integer> constraints)
    {
        //row array holds the edges in the current row.
        ArrayList<Double> rowItems = new ArrayList<Double>();
        double total = 0; //The actual bound total.
        if (constraints.size() == 1)
        { //Only one node in the tour.
            double rowTotal = 0; //Adds the two edges in the current row.
            total = 0; //The actual bound total.
            for (int i = 0; i < costs.length; i++)
            {
                for (int j = 0; j < costs.length; j++) 
                {
                    rowItems.add(costs[i][j]); //Add all edges in current row.
                }
                Collections.sort(rowItems); //Sort from least to greatest.
                rowTotal = rowItems.get(0) + rowItems.get(1); //Get first two lowest edges.
                total += rowTotal; //Add to total bound.
                rowItems.clear(); 
            }
        } 
         
        else
        {
            double rowConstraint = 0; //Row constraint value.
            double rowTotal = 0; //Adds the two edges in the current row.
            total = 0;
            //ArrayList<Double> rowItems = new ArrayList<Double>();
            for (int i = 0; i < costs.length; i++) 
            {
                for (int j = 0; j < costs.length; j++) 
                {
                    rowItems.add(costs[i][j]);
                }
                Collections.sort(rowItems);
                if (i < constraints.size() - 1) 
                {
                    /*See if there are any constraints
                     * for this row.
                     */
                    if (rowContainsConst(i, constraints)) 
                    {
                        int ind = constraints.indexOf(i);
                        //If root node accounts for single constraint. 
                        if (i == 0)
                        {
                            rowConstraint = costs[(int) constraints.get(ind)]
                                    [(int) constraints.get(ind + 1)]; 
                            if (rowConstraint == rowItems.get(0)) 
                            { 
                                rowTotal = rowConstraint + rowItems.get(1);
                            } 
                            else
                            {
                                rowTotal = rowConstraint + rowItems.get(0);
                            }
                        } 
                        //Otherwise inner edges have 2 constraints.
                        else
                        {
                            double rowCost1 = 0;
                            double rowCost2 = 0;
                            rowCost1 = costs[(int) constraints.get(ind)][(int) constraints
                                    .get(ind + 1)];
                            rowCost2 = costs[(int) constraints.get(ind)][(int) constraints
                                    .get(ind - 1)];
                            rowTotal = rowCost1 + rowCost2;
                        }
                    }
                }
                /*The last edge containing a constraint
                 * and edges containing no constraints.
                 * @local var rc is row constraint.
                 */
                else if (i == constraints.size() - 1 || i > constraints.size()-1) 
                {
                    if (rowContainsConst(i, constraints))
                    {
                        double rowCost = costs[(int) constraints.get(constraints.size() - 1)]
                                [(int) constraints.get(constraints.size() - 2)];
                        if (rowCost == rowItems.get(0))
                        {
                            rowTotal = rowCost + rowItems.get(1);
                        } 
                        else
                        {
                            rowTotal = rowCost + rowItems.get(0);
                        }
                    } 
                    else
                    {
                        rowTotal = rowItems.get(0) + rowItems.get(1);
                    }
                } 
                total += rowTotal; //Compute bound.
                rowItems.clear();
            }
        }
        return total/2;
    }
     
    /**
     * Tells you if that row has a constraint
     * or not.
     * @param row the current row in the matrix.
     * @param constraints the array holding the 
     * constrained edges.
     * @return returns true if there are constraints 
     * or false if none.
     */
    public boolean rowContainsConst(int row, ArrayList<Integer> constraints)
    {
        if (constraints.contains(row))
        {
            return true;
        } 
        else
        {
            return false;
        }
    }
 
    /**
     * Gets the cost of the edge 
     * based on the xy positions.
     * @param x coordinate.
     * @param y coordinate.
     * @return cost of xy.
     */
    public double getCost(int x, int y)
    {
        return costs[x][y];
    }
     
    /**
     * Creates the boolean matrix 
     * to check if a city has been 
     * visited or not.
     */
    private void init_visited()
    {
        visited = new boolean[costs.length][costs.length];
        for(int i = 0; i < costs.length; i++)
        {
            for(int j = 0; j < costs.length; j++)
            {
                visited[i][j] = false;
            }
        }
    }
     
    /**
     * Geneartes the cost matrix of 
     * the edge weights.
     * @return
     */
    private double[][] generateMatrix()
    {
        int[] index = new int[xPos.length];
        index = populate();
        for(int i=0; i<xPos.length; i++)
        {
            for(int j=0; j<xPos.length ; j++)
            {
                costs[i][j] = (double) Math.sqrt(Math.pow((xPos[index[i]] - 
                        xPos[index[j]]),2)+Math.pow((yPos[index[i]] - yPos[index[j]]),2));
            }
        }
        return costs;
    }
     
    /**
     * Populates the index array with city 
     * numbers.
     * 
     * @return returns the populated array.
     */
    private int[] populate()
    {
        int[] index = new int[xPos.length];
        for(int i = 0; i < index.length; i++)
        {
            index[i] = i;
        }
        return index;
    }
}