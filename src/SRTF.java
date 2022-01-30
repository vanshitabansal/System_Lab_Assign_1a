import java.io.*;
import java.util.*;

public class SRTF {

    //Creating class for process, each object will contain process id , arrival time and burst time
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

    //Comparator used for picking processes
    static class MyComp implements Comparator<Process>{
        public int compare(Process a, Process b){

            //if burst time of processes is equal check for arrival time else return process with less burst time
            if(a.burstTime == b.burstTime){

                //if burst time of processes is equal check for process id, return minimum id
                if(a.arrivalTime == b.arrivalTime)
                    return a.id - b.id;
                return a.arrivalTime-b.arrivalTime;
            }
            return a.burstTime-b.burstTime;
        }
    }
    public static void main(String[] args) throws FileNotFoundException {

        //Reading contents from file "arrival.txt"
        File file = new File("arrival.txt");
        Scanner sc = new Scanner(file);

        //t denotes the test case number
        int t=1;
        while (sc.hasNextLine()){
            System.out.println("\n\n----------------------------This is Test Case: "+t+" ------------------------------------");
            t++;

            //Storing input in arrays
            String str=sc.nextLine();
            String[] splited = str.split(" ");
            int n=splited.length;
            int arr[]=new int[n];
            int turn_around_time[]=new int[n];
            int waiting_time[]=new int[n];
            int burst_Time[]=new int[n];
            ArrayList<Process> gantt=new ArrayList<>();
            double average_waiting_time=0;
            int clock=0, totalBurst = 0,max_arrival_time=0,max_time = 0,start_time=0,end_time=0,flag=0;
            ArrayList<LinkedList<Process>> requests=new ArrayList<LinkedList<Process>>();
            PriorityQueue<Process> pq= new PriorityQueue<Process>(arr.length, new MyComp());

            for(int i=0;i<n;i++){
                arr[i]=Integer.parseInt(splited[i]);
            }

            //Finding maximum arrival time out of all processes
            for(int i=0;i<n;i++){
                if(max_arrival_time<arr[i])
                    max_arrival_time=arr[i];
            }

            //Adding new LinkedList to buckets in request array
            for(int i=0;i<=max_arrival_time;i++){
                requests.add(new LinkedList<Process>());
            }

            System.out.println("Process-Id 	Arrival-Time	Burst-Time");
            System.out.println("-----------------------------------------");
            for(int i=0;i<n;i++){
                Process newProcess = new Process(i, arr[i], (int)(Math.random()*10)%8+1);
                System.out.println("|\t"+newProcess.id+"\t\t|\t"+newProcess.arrivalTime+"\t\t|\t\t"+newProcess.burstTime+"\t\t|");

                //storing burst time to use later for calculating waiting time
                burst_Time[i]=newProcess.burstTime;

                //Adding processes to the LinkedLists of respective bucket(timestamp) in request array
                requests.get(arr[i]).add(newProcess);
                totalBurst += newProcess.burstTime;
            }
            max_time=Math.max(max_arrival_time,totalBurst);
            System.out.println("------------------------------------------");
            System.out.println("Total Burst Time: "+totalBurst);

            //Start scheduling
            Process cur = null;
            int count=n;
            int i=0;
            for(int c=0;c<count;){
                //check if there is any new request in the current timestamp, if yes add it to pq
                if(i<=max_time && i<requests.size() && requests.get(i).size() != 0){
                    for(int j=0;j<requests.get(i).size();j++){
                        pq.add(requests.get(i).get(j));
                    }
                }

                //if there is process available in current timestamp we will add process with -1 to denote "FREE" block
                if(i<=max_time && i<requests.size() && requests.get(i).size() == 0 && pq.size()==0){
                    pq.add(new Process(-1,i,Integer.MAX_VALUE));
                }

                //if there no process currently or current process has finished its execution and
                // pq is not empty then we will pop new process
                if(pq.size()!=0 && (cur == null || cur.burstTime<=0)){
                    cur = pq.poll();
                }

                //if burst time of current process greater than a process in pq then we will execute the process in pq and push this to pq
                if(pq.size()!=0 && cur.burstTime >= pq.peek().burstTime){
                    pq.add(cur);
                    cur = pq.poll();
                }

                //Store current execution info in arraylist "gantt"
                gantt.add(new Process(cur.id,i,cur.burstTime));
                cur.burstTime--;

                //if the execution of current process is finished we will find its TAT and WT and increment count of processes executed
                if(cur.burstTime<=0 && cur.id!=-1){
                    turn_around_time[cur.id]=i+1-cur.arrivalTime;
                    waiting_time[cur.id]=turn_around_time[cur.id]-burst_Time[cur.id];
                    c++;
                }
                i++;
            }

            //Printing data
            System.out.println("----------------------------------------------------");
            System.out.println("Process-ID\tTurn-Around-Time\tWaiting-Time");
            System.out.println("----------------------------------------------------");
            for(int i_=0;i_<n;i_++){
                System.out.println("\tP"+i_+"\t\t|\t\t"+turn_around_time[i_]+"\t\t|\t\t"+waiting_time[i_]);
                average_waiting_time+=waiting_time[i_];
            }

            //calculating average waiting time and printing it
            System.out.println("----------------------------------------------------");
            System.out.println("Average Waiting Time of Processes:");
            System.out.println("----------------------------------------------------");
            System.out.println(average_waiting_time*1.0/n);
            start_time=0;

            //Compressing and Printing Gantt chart
            System.out.println("-----------------GANTT Chart------------------------");
            System.out.println("\tBlock\t\t\t|\tProcess Running\t|");
            System.out.println("----------------------------------------------------");
            for(int i_=1;i_<gantt.size();i_++){
                if(gantt.get(i_).id==gantt.get(i_-1).id){
                    end_time=gantt.get(i_).arrivalTime;
                }
                else{
                    if(gantt.get(i_-1).id==-1){
                        System.out.println("\t"+start_time+" - "+(end_time+1)+"\t\t\t|\t\tFREE\t\t|");
                    }
                    else
                        System.out.println("\t"+start_time+" - "+(end_time+1)+"\t\t\t|\t\tP"+gantt.get(i_-1).id+"\t\t\t|");
                    start_time=gantt.get(i_).arrivalTime;
                    end_time=start_time;
                }
            }
            System.out.println("\t"+start_time+" - "+i+"\t\t\t|\t\tP"+gantt.get(gantt.size()-1).id+"\t\t\t|");
            System.out.println("----------------------------------------------------");
        }
    }
}
