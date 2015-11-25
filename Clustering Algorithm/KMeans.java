package part1;

import java.io.*;
import java.util.*;

public class KMeans {

	public static void main(String[] args) throws FileNotFoundException {
		
		String clusters=args[0];
		int numberOfClusters=Integer.parseInt(clusters);
		String inputFileName=args[1];
		String outputFileName=args[2];
		int iterations=1;
		
		File input=new File(inputFileName);
		Scanner sc=new Scanner(input);
		List <String> line = new ArrayList<String>();
		
		while(sc.hasNextLine()){
				String lines=sc.nextLine();
				line.addAll(Arrays.asList(lines.split("\t")));
		}
		sc.close();
		
		String items[]=new String[line.size()];
		items=line.toArray(items);
		String id[]=new String[100];
		double x[]=new double[100];
		double y[]=new double[100];
		int count=3;
		
		for(int i=0;i<100;i++){
			
			id[i]=items[count];
			x[i]=Double.valueOf(items[count+1]);
			y[i]=Double.valueOf(items[count+2]);
			count=count+3;
		}
		
		
		KMeans object=new KMeans();
		Points centroid[]=new Points[numberOfClusters];			
		System.out.println("Initializing...");
		System.out.println("Selecting random points as centroids...");
		System.out.println();
		centroid=object.init(id,x,y,numberOfClusters);
		System.out.println("Point" + "\t" + "x" + "\t" + "y");
		for(Points p2:centroid){
			
			System.out.println(p2.pointId + "\t" + p2.x + "\t" +p2.y + "\t" + "Cluster Id : " + "\t" + p2.clusterId);
		}
		
		Points assign=new Points();
		List<Points> cluster=new ArrayList<Points>();
		System.out.println();
		System.out.println("Assigning points to the clusters...");
		System.out.println();
		cluster=object.minimumDistance(centroid,x,y,assign);
		
		for(Points p3:cluster){
			System.out.println("Cluster Id : " + "\t" + p3.clusterId + "\t" + "Point ID : " + "\t " + p3.pointId);
		}
		
		
		Points newCenters[];
		do{
			
			System.out.println();
			System.out.println("Recalculating cluster centers...");
			List<Points> centers=object.reCalculateCenters(cluster,centroid.length);
			System.out.println();
			for(Points p4:centers){
				System.out.println("Cluster ID: " + p4.clusterId + "\t" + "\t" + "x: " +  p4.x + "\t"+ "\t" + "y: " + p4.y);
			}	
			System.out.println();
			System.out.println("Recalculating distances of data points from the new centroids...");
			System.out.println("Assigning new data points to cluster centers...");
			System.out.println();
			Points assignNew=new Points();
			List<Points> clusterNew=new ArrayList<Points>();
			newCenters=centers.toArray(new Points[centers.size()]);
			System.out.println();
			System.out.println("Assigning points to the clusters...");			
			clusterNew=object.minimumDistance(newCenters,x,y,assignNew);
			System.out.println();
			for(Points p4:clusterNew){
				System.out.println("Cluster Id : " + "\t" + p4.clusterId + "\t" + "Point ID : " + "\t " + p4.pointId);
			}
			
			if(clusterNew.equals(cluster)){
				System.out.println("Convergence after " + iterations + " iterations.");
				break;
			}			
			cluster=clusterNew;
			iterations++;
			System.out.println();
		}while(iterations<=25);
		
		System.out.println("Validating the goodness of the clustering...");
		double sse=0;
		sse=object.validate(numberOfClusters,cluster,0,newCenters);
		System.out.println("SSE = "+ sse);
		
		PrintWriter writer = new PrintWriter(new File(outputFileName));
		writer.println("Cluster ID" + "\t" + "\t" + "Point IDs");
		
		for(int i=0;i<centroid.length;i++){
			
			List<Integer> points = new ArrayList<Integer>();
			for(int j=0;j<cluster.size();j++){
				
				if(cluster.get(j).clusterId==i+1)	{
					
					points.add(cluster.get(j).pointId);
				}	
			}
			
			writer.print(i+1 + "\t"  + "\t");
			
			for(int p6:points){
				writer.print(p6 + " , ");
				
			}
			
			writer.println();							
		}
		
		writer.close();		
	}
	
	public Points[] init(String id[],double x[],double y[],int clusters){
		
		Points centroid[]=new Points[clusters];
		Points p=new Points();
		String pointID;
		int rnd=0;
		
		for(int i=0;i<clusters;){
			 rnd = new Random().nextInt((id.length-1)+1);
			 if(rnd>=100||rnd<0){
				 i--;
				 continue;
			 }
			 else{
				 pointID=id[rnd];
				 centroid[i]=p.getInitialPoints(pointID, x, y);
				 centroid[i].clusterId=i+1;
				 i++;
			 }			
		}
		
		return centroid;
		
	}
	
	public List<Points> minimumDistance(Points centroids[],double x[],double y[],Points assign){
		Points center[]=centroids;
		Points cluster=new Points();
		double min[][]= new double[x.length][y.length];
		double distance=0;
		List <Points> list = new ArrayList <Points>();
		int index=0;
		
		
		for(int i=0;i<center.length;i++){
			
			for(int j=0;j<x.length;j++){
				
				min[i][j]=Math.sqrt(Math.pow(Math.abs(center[i].x-x[j]), 2) + Math.pow(Math.abs(center[i].y-y[j]), 2));	
				}	
				
		}	
		
		for(int i=0;i<x.length;i++){	
			
			List <Double> temp = new ArrayList <Double>();
			
			for(int j=0;j<center.length;j++){
				
				temp.add(min[j][i]);
			}
			
			distance = Collections.min(temp);
			
			index=temp.indexOf(distance);
			assign=cluster.setPoints(x[i], y[i], index+1, i+1);
			list.add(assign);
		}	
		
		return list;		
	}
	
	public List<Points> reCalculateCenters(List<Points> cluster,int clustLength){
		double sumX[]=new double[clustLength];
		double sumY[]=new double[clustLength];		
		Points coordinates=new Points();
		List<Points> check=new ArrayList<Points>();
		Points centers[]=new Points[clustLength];
		double meanX[]=new double[clustLength];
		double count[]=new double[clustLength];
		double meanY[]=new double[clustLength];
		int len=1;
		while(clustLength>0){
			for(Points c:cluster){
				if(c.clusterId==len){
					sumX[len-1]+=coordinates.getX(c);
					sumY[len-1]+=coordinates.getY(c);
					count[len-1]++;
				}
			}
			clustLength--;
			len++;
		}
		System.out.println();
		int s=0;
		for(Points c2:centers){
			meanX[s]=sumX[s]/count[s];
			meanY[s]=sumY[s]/count[s];
			c2=coordinates.getNewPoints(meanX[s],meanY[s],s+1);
			check.add(c2);
			s++;
		}
		
		return check;
	}
	
	public double validate(int k,List<Points> cluster,double sse,Points centers[]){
		
		int noOfClusters=k;
		double diffX;
		double diffY;
		double dist;
		double sum[]=new double[centers.length];
		Points obj=new Points();
		for(int i=0;i<noOfClusters;i++){
			for(Points p:cluster){
				if(p.clusterId==(i+1)){
					obj.x=centers[i].x;
					obj.y=centers[i].y;
					
					diffX=Math.pow((p.x-obj.x),2);
					diffY=Math.pow((p.y-obj.y),2);
					
					dist=Math.sqrt((diffX+diffY));
					
					sum[i]=sum[i]+Math.pow(dist, 2);
				}
			}
		}
		
		for(double s:sum){
			sse=sse+s;
		}
		return sse;
		
	}
}


class Points{
	
	double x=0;
	double y=0;
	int clusterId=0;
	int pointId=0;
	
	public Points getInitialPoints(String pointID,double a[],double b[]){
		Points p=new Points();
		p.pointId=Integer.parseInt(pointID);
		p.x=a[p.pointId];
		p.y=b[p.pointId];
		return p;
		
	}
	
	public Points setPoints(double a,double b,int c,int d){
		Points p=new Points();
		p.x=a;
		p.y=b;
		p.clusterId=c;
		p.pointId=d;
		return p;
	}
	
	public double getX(Points c){
		
		return c.x;
	}
	
	public double getY(Points c){
		return c.y;
	}
	
	public Points getNewPoints(double x,double y,int cId){
		Points p=new Points();
		p.x=x;
		p.y=y;
		p.clusterId=cId;
		return p;
	}
}
