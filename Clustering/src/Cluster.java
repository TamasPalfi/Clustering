/* This class will be used to implement an agglomerative clustering of the given data
 * of Congressional voting records from some number of congress men/women.  The measurement
 * that the clustering will be based on for calculating similarity is Jaccard index ( 1 - | A ^ B |/| A U B|).
 * The way the class works is that it is inputed  the data records and the desired number of clusters.
 * The algorithm then outputs to stdout the clusters (separated by line) with their members.
 * 
 * Code Property of Tamas Palfi
 * 12/7/2018
 */

//import statements
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Cluster {

	/*
	 * main method of the class where most of the code progress is implemented.
	 */
	public static void main(String[] args) throws FileNotFoundException {
		//get the file where the congressional data is held and the # of clusters to be formed
		//args[0] should be the file and args[1] should be the # of clusters to be formed.
		File file = new File(args[0]);  //should be args[0]
		int numClustersToForm = Integer.valueOf(args[1]);  //should be args[q]
		
		//conditions to check for file integrity
		if(!file.exists()){
			//File doesn't exist
			System.out.println("ERROR: File does not exist.  Please input a proper text file"); //ERROR MSG #1
			System.exit(0);    
		} 
		if(!file.isFile()){
			//File is not a "regular" file.  May be a directory.
			System.out.println("ERROR: File is not a proper file format.  Please input a proper text file"); //ERROR MSG #2
			System.exit(0);
		}
		if(!file.canRead()){
			//File can't be read
			System.out.println("ERROR: File cannot be read. Please input a proper text file"); //ERROR MSG #3
			System.exit(0);
		}
		
		//search through the file to get the data of voting records and store it in some data structure
		//set up data structure to hold data
		ArrayList<String[]> data = new ArrayList<String[]>();
		//set up variable to track number of representatives
		int numClustersCurrent = 0;
		//set up scanner of training file
		Scanner scanner = new Scanner(file);
		//go through each line in file to fill data struct with voting records
		while(scanner.hasNextLine()){
			//get the data from the line or one specific CongressPerson's vote
			String congressPerson = scanner.nextLine();
			//split the string to separate each vote and store into String array
			String[] line = congressPerson.split(",");
			//get rid of the last element of the array - or party alliance - not needed
			String[] personVotes = new String[42]; //we know that the training file has 42 votes by looking at it - wouldn't include this if didn't have file
			for(int i = 0; i < 42; i++){
				personVotes[i] = line[i];
			}
			//now that we have only the person's votes we can add to data struct
			data.add(personVotes);
			//increment the number of people
			numClustersCurrent++;
		}
		//close scanner
		scanner.close();
		//variable to track number of representatives
		int numReps = numClustersCurrent;
			
		//create a data struct. to hold the clusters to be output - will have numClustersCurrent size always
		ArrayList<ArrayList<Integer>> clusterOutput = new ArrayList<ArrayList<Integer>>();
		//fill data struct. with base clusters (so 350 representatives)
		for(int j = 0; j < numClustersCurrent; j++){
			//create inside ArrayList<Integer> -> need to be stored as an ArrayList since need to combine clusters and they are dynamic
			ArrayList<Integer> cluster = new ArrayList<Integer>();
			//add the index or cluster number/representative number
			cluster.add(j);
			//add this to the total clusterOutput data struct to be outputted
			clusterOutput.add(cluster);
		}	
		
		//we have extracted the correct data -> now we can form clusters based on Lowest Jaccard index -> start with 350 (size of training file) clusters
		//check that the input value was valid
		if(0 < numClustersToForm && numClustersToForm <= numReps){
			//loop through to create numClustersToForm clusters using the agglomerative clustering algorithm.
			for(int a = 0; a < (numReps-numClustersToForm); a++){
				//compare each of the clusters data to each others data and merge two clusters with lowest Jaccard index/highest similarity
				//set up double variable to track the lowest Jaccard Index for this instance, and two int var to track which clusters to link
				double lowestJaccardIndex = 1;
				int clusterToAdd1 = -1;
				int clusterToAdd2 = -1;
				//double for loop ( O(n^2)) to compare each combination of representatives.
				for(int b = 0; b < numClustersCurrent; b++){  //TODO : speed up fix here to cut it to O(n^2/2) - doing lots of repeating here i.e a = 0 and b = 1 same as a = 1 and b = 0;
					for(int c = 0; c < numClustersCurrent; c++){
						//calculate Jaccard index if b =/ c  (otherwise same cluster) and can skip
						if(b != c){
							//get each cluster and the respective representatives in each
							ArrayList<Integer> c1 = clusterOutput.get(b);
							ArrayList<Integer> c2 = clusterOutput.get(c);
							//set up variable to hold the results of the sum of jaccardIndexes of the clusters
							double totalJaccardIndex = 0;
							//compute the average link distance of the values you are comparing
							for(int d = 0; d < c1.size();  d++){
								for(int e = 0; e < c2.size(); e++){
									totalJaccardIndex += getJaccardIndex(data.get(c1.get(d)), data.get(c2.get(e)));
								}
							}
							//finish computation of average link
							totalJaccardIndex = totalJaccardIndex/(double)(c1.size() * c2.size());
							
							//compare to current lowestJaccardIndex for this cluster formation
							if(lowestJaccardIndex > totalJaccardIndex){
								//new Jaccard value is smaller than previous so replace it
								lowestJaccardIndex = totalJaccardIndex;
								//replace clusters to add values
								clusterToAdd1 = b;
								clusterToAdd2 = c;
							}
						}
					}
				}
				
				//when forming new cluster. get rid of two original cluster values from data struct. and replace with one combined
				if(clusterToAdd1 != -1 && clusterToAdd2 != -1){
					//System.out.println("Here");
					ArrayList<Integer> cluster1 = clusterOutput.get(clusterToAdd1);
					ArrayList<Integer> cluster2 = clusterOutput.get(clusterToAdd2);
					
					//remove old ones
					clusterOutput.remove(clusterToAdd1);
					//after removing one need to decrement the 2nd index (for the size of ArrayList decreased by 1) if it isn't the first element
					if(clusterToAdd2 > clusterToAdd1){  
						clusterToAdd2 -= 1;
					}
					clusterOutput.remove(clusterToAdd2);
					//combine other two clusters to one cluster
					ArrayList<Integer> clusterNew = new ArrayList<Integer>();
					Iterator<Integer> x = cluster1.iterator();
					Iterator<Integer> y = cluster2.iterator();
					while(x.hasNext()){
						clusterNew.add(x.next()); 
					}
					while(y.hasNext()){
						clusterNew.add(y.next());
					}
					//sort the ArrayList in ascending order
					Collections.sort(clusterNew);
					clusterOutput.add(clusterNew);
					//new cluster has been created so decrement numClustersCurrent
					numClustersCurrent--;
				}
			}
		}
		
		//sort the output into ascending order based on 1st value of each cluster
		//create new ArrayList to store results
		ArrayList<ArrayList<Integer>> res = new ArrayList<ArrayList<Integer>>();
		//get rid of all vals in clusterOutput and put them in new struct.
		while(clusterOutput.size() != 0){
			//get variables to track cluster with lowest initial value
			int value = Integer.MAX_VALUE;
			int indexVal = -1;
			for(int k = 0; k < clusterOutput.size(); k++){
				if(clusterOutput.get(k).get(0) < value){
					//cluster has lower value so replace lowest for value and store index
					value = clusterOutput.get(k).get(0);
					indexVal = k;
				}
			}
			//with cluster with lowest value get rid of it from old struct and put it in new
			if(indexVal != -1){
				res.add(clusterOutput.get(indexVal));
				clusterOutput.remove(indexVal);
			}
		}
		
		//print out clusters
		for(int z = 0; z < res.size(); z++){
			//create a new iterator for each cluster so that the output is in a correct format
			Iterator<Integer> it = res.get(z).iterator();
			//set up loop to progress through iterator
			while(it.hasNext()){
				int val = it.next();
				if(it.hasNext()){
					System.out.print(val + ", ");
				}
				else{
					System.out.print(val);
				}
			}
			System.out.println();
		}	
	}
	
	/* 
	 * Method to compute the Jaccard index between two representatives
	 */
	private static double getJaccardIndex(String[] a, String[] b){
		//set up variable to store final results, and variable to track same votes
		double jaccardDist = 0;
		int countSimilarVotes = 0;
		//go through each of the 42 votes and count number of similar results
		for(int i=0; i < 42; i++){
			//check if votes are equal
			if(a[i].equals(b[i])){
				//votes are = so increment the count
				countSimilarVotes++;
			}
		}
		//System.out.println(countSimilarVotes);
		//compute the final distance by using the Jaccard index: ( 1 - | A ^ B |/| A U B|)
		jaccardDist = 1 - ((double)countSimilarVotes/42);
		//return 
		return jaccardDist;
	}

}
