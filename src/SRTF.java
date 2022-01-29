import java.io.*;
import java.util.*;

public class SRTF {
    static class Process{
        int id;
        int arrivalTime;
        int burstTime;
        public Process(int a, int b, int c){
            id = a;
            arrivalTime = b;
            burstTime = c;
        }
    }
    static class MyComp implements Comparator<Process>{
        public int compare(Process a, Process b){
            if(a.burstTime == b.burstTime){
                if(a.arrivalTime == b.arrivalTime)
                    return a.id - b.id;
                return a.arrivalTime-b.arrivalTime;
            }
            return a.burstTime-b.burstTime;
        }
    }
    public static void main(String[] args) {
        int arr[]={2,1,3,0,3,0,3,7,99,87};
        int bur[]={3,4,2,4,1,1,2,2,3,2};
        int turn_around_time[]=new int[10];
        int waiting_time[]=new int[10];
        int burst_Time[]=new int[10];
        ArrayList<Process> gantt=new ArrayList<>();
        double average_waiting_time=0;
        int clock=0, totalBurst = 0,max_arrival_time=0;
        ArrayList<LinkedList<Process>> requests=new ArrayList<LinkedList<Process>>();
        PriorityQueue<Process> pq= new PriorityQueue<Process>(arr.length, new MyComp());
        PriorityQueue<Process> pq2= new PriorityQueue<Process>(arr.length, new MyComp());

        int max_time = 0;
        for(int i=0;i<arr.length;i++){
            if(max_arrival_time<arr[i])
                max_arrival_time=arr[i];
        }
        for(int i=0;i<=max_arrival_time;i++){
            requests.add(new LinkedList<Process>());
        }
        System.out.println("Process-Id 	Arrival-Time	Burst-Time");
        System.out.println("-----------------------------------------");
        for(int i=0;i<arr.length;i++){
            Process newProcess = new Process(i, arr[i], (int)(Math.random()*10)%8+1);
            //Process newProcess = new Process(i, arr[i], bur[i]);
            System.out.println("|\t"+newProcess.id+"\t\t|\t"+newProcess.arrivalTime+"\t\t|\t\t"+newProcess.burstTime+"\t\t|");
            burst_Time[i]=newProcess.burstTime;
            requests.get(arr[i]).add(newProcess);
            pq2.add(newProcess);
            totalBurst += newProcess.burstTime;
        }
        max_time=Math.max(max_arrival_time,totalBurst);
        System.out.println("------------------------------------------");

        System.out.println("Total Burst Time: "+totalBurst);

        System.out.println("------------------------------------------");
        // start scheduling
        System.out.println("\n--------------GANTT CHART---------------------------");
        System.out.println("TimeStamp\t|\tProcess-Id\t\t|\tBurst-Time-left\t|");
        System.out.println("----------------------------------------------------");
        Process cur = null;
        int count=10;
        int i=0;
        for(int c=0;c<count;){
            // check if there is any new request
            if(i<=max_time && requests.get(i).size() != 0){
                for(int j=0;j<requests.get(i).size();j++)
                    pq.add(requests.get(i).get(j));
            }
            if(i<=max_time && requests.get(i).size() == 0 && pq.size()==0){
                pq.add(new Process(-1,i,Integer.MAX_VALUE));
            }
            if(pq.size()!=0 && (cur == null || cur.burstTime<=0)){
                cur = pq.poll();
            }
            if(pq.size()!=0 && cur.burstTime >= pq.peek().burstTime){
                pq.add(cur);
                cur = pq.poll();
            }
            if(cur.id==-1){
                System.out.println("\t"+i+"\t\t|\t\t"+"FREE" + "\t\t|\t\t" + "-"+"\t\t\t|");
            }
            else
                System.out.println("\t"+i+"\t\t|\t\t"+"P" + cur.id + "\t\t\t|\t\t" + cur.burstTime+"\t\t\t|");
            gantt.add(new Process(cur.id,i,cur.burstTime));
            cur.burstTime--;
            if(cur.burstTime<=0 && cur.id!=-1){
                turn_around_time[cur.id]=i+1-cur.arrivalTime;
                waiting_time[cur.id]=turn_around_time[cur.id]-burst_Time[cur.id];
                c++;
            }
            i++;
        }

        System.out.println("----------------------------------------------------");
        System.out.println("Turn Around Time of Processes:");
        System.out.println("----------------------------------------------------");
        for(int i_=0;i_<10;i_++){
            System.out.println("P"+i_+"\t|\t"+turn_around_time[i_]);
        }
        System.out.println("----------------------------------------------------");
        System.out.println("Waiting Time of Processes:");
        System.out.println("----------------------------------------------------");
        for(int i_=0;i_<10;i_++){
            System.out.println("P"+i_+"\t|\t"+waiting_time[i_]);
            average_waiting_time+=waiting_time[i_];
        }
        System.out.println("----------------------------------------------------");
        System.out.println("Average Waiting Time of Processes:");
        System.out.println("----------------------------------------------------");
        System.out.println(average_waiting_time);
        int start_time=0,end_time=0,flag=0;
        start_time=0;
        System.out.println("----------------------------------------------------");
        System.out.println("Compressed GANTT Chart:");
        System.out.println("----------------------------------------------------");
        System.out.println("\tBlock\t\t\t|\tProcess Running\t|");
        System.out.println("----------------------------------------------------");
        for(int i_=1;i_<gantt.size();i_++){
            if(gantt.get(i_).id==gantt.get(i_-1).id){
                end_time=gantt.get(i_).arrivalTime;
            }
            else{
                if(gantt.get(i_-1).id==-1){
                    System.out.println("\t"+start_time+" - "+(end_time+1)+"\t\t\t|\tFREE\t\t\t|");
                }
                else
                    System.out.println("\t"+start_time+" - "+(end_time+1)+"\t\t\t|\tP"+gantt.get(i_-1).id+"\t\t\t\t|");
                start_time=gantt.get(i_).arrivalTime;
                end_time=start_time;
            }
        }
        System.out.println("\t"+start_time+" - "+i+"\t\t|\tP"+gantt.get(gantt.size()-1).id+"\t\t\t\t|");
        System.out.println("----------------------------------------------------");
    }
}
