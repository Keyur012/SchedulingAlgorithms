package rrabt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import example.Pair;
import rr.RRDataCenterBroker;

public class RRABTSimulator {
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;



	private static List<Cloudlet> createCloudlet(int userId, int cloudlets){
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

		//cloudlet parameters
		long length = 1000;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for(int i=0;i<cloudlets;i++){
			Random rand = new Random();
			cloudlet[i] = new Cloudlet(i, (length)+rand.nextInt(2000), pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		}

		return list;
	}
	
	private static Cloudlet getCloudlet(int userId, int id, int length) {
		//cloudlet parameters
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		
		Cloudlet cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
		cloudlet.setUserId(userId);
		return cloudlet;
	}


	////////////////////////// STATIC METHODS ///////////////////////
	
	protected static int NUMBER_OF_CLOUDLET;
	protected static ArrayList<Integer> lengths = new ArrayList<>();
	protected static ArrayList<Double> avgWait = new ArrayList<>();

	/**
	 * Creates main() to run this example
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		FileInputStream in = new FileInputStream("E:/Asem8/cloud/input.xlsx");
		
		XSSFWorkbook workbook = new XSSFWorkbook(in);
		XSSFSheet spreadsheet = workbook.getSheet("Input");
		
		
		for(int i = 1;i<=10;i++) {
			int p = (new Double(spreadsheet.getRow(i).getCell(0).getNumericCellValue())).intValue();
			lengths.add(p);
		}
		
		
		System.out.println(lengths);
		
		for(int i = 0;i<10;i++) {
			go(i+1);
		}
		
		XSSFSheet spreadsheet2 = workbook.getSheet("Output");
		
		spreadsheet2.getRow(0).createCell(4).setCellValue(("RRABT"));
		for(int i = 0;i<10;i++) {
			spreadsheet2.getRow(i+1).createCell(4).setCellValue(avgWait.get(i));
		}
		
		FileOutputStream out = new FileOutputStream("E:/Asem8/cloud/input.xlsx");
		workbook.write(out);
		
		in.close();
		out.close();
		workbook.close();
	}
	
	private static void go(int num) {

		NUMBER_OF_CLOUDLET = num;
		
		Log.printLine("Starting Round Robin Simulation...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 1;   // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
			
			//Third step: Create Broker
			RRABTDataCenterBroker broker = createBroker();
			int brokerId = broker.getId();

			// Fourth step: Create one virtual machine
			vmlist = new ArrayList<Vm>();

			// VM description
			int vmid = 0;
			int mips = 1000;
			long size = 10000; // image size (MB)
			int ram = 1024; // vm memory (MB)
			long bw = 1000;
			int pesNumber = 1; // number of cpus
			String vmm = "Xen"; // VMM name

			// create VM
			Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());

						// add the VM to the vmList
			vmlist.add(vm);
						// submit vm list to the broker
			broker.submitVmList(vmlist);

//			cloudletList = createCloudlet(brokerId,10); // creating 10 cloudlets
			cloudletList = new ArrayList<>();
			
			ArrayList<Pair<Integer>> list = new ArrayList<>();
			
			for(int i = 0;i<NUMBER_OF_CLOUDLET;i++) {
				list.add(new Pair<>(i, lengths.get(i)));
			}
			
			int timeQuantam = 0;
			
			while(!list.isEmpty()) {
				
				ArrayList<Pair<Integer>> t2 = new ArrayList<>();
				
				double tempsum = 0.0;
				
				for(Pair clp : list) {
					tempsum += (int)clp.getEn();
				}
				
				tempsum = tempsum*1.0/list.size();
				
				timeQuantam = (int) Math.ceil(tempsum);
				
				Collections.sort(list, new Comparator<Pair<Integer>>() {

					@Override
					public int compare(Pair<Integer> o1, Pair<Integer> o2) {
						// TODO Auto-generated method stub
						return Integer.compare((int)o1.getEn(), (int)o2.getEn());
					}
					
				});
				
				for(Pair clp : list) {
					int id = (int) clp.getSt();
					int time = (int) clp.getEn();
					
					if(time > timeQuantam) {
						cloudletList.add(getCloudlet(brokerId, id, timeQuantam));
						t2.add(new Pair<>(id + NUMBER_OF_CLOUDLET, time - timeQuantam));
					} else {
						cloudletList.add(getCloudlet(brokerId, id, time));
					}
				}
				list = t2;
			}
		
			

			broker.submitVmList(vmlist);
			broker.submitCloudletList(cloudletList);

			// Fifth step: Starts the simulation
			CloudSim.startSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			avgWait.add(printCloudletList(newList));

			Log.printLine("Round Robin Simulation finished!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	private static Datacenter createDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store one or more
		//    Machines
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
		//    create a list to store these PEs before creating
		//    a Machine.
		List<Pe> peList1 = new ArrayList<Pe>();

		int mips = 4000;

		// 3. Create PEs and add these into the list.
		//for a quad-core machine, a list of 4 PEs is required:
		peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList1.add(new Pe(1, new PeProvisionerSimple(mips)));

		//Another list, for a dual-core machine
		List<Pe> peList2 = new ArrayList<Pe>();

		//4. Create Hosts with its id and list of PEs and add them to the list of machines
		int hostId=0;
		int ram = 1024*4; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 10000;

		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList1,
    				new VmSchedulerSpaceShared(peList1)
    			)
    		); // This is our first machine

//		hostId++;

//		hostList.add(
//    			new Host(
//    				hostId,
//    				new RamProvisionerSimple(ram),
//    				new BwProvisionerSimple(bw),
//    				storage,
//    				peList2,
//    				new VmSchedulerTimeShared(peList2)
//    			)
//    		); // Second machine


		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.1;	// the cost of using storage in this resource
		double costPerBw = 0.1;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static RRABTDataCenterBroker createBroker(){

		RRABTDataCenterBroker broker = null;
		try {
			broker = new RRABTDataCenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static double printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + indent + "Time" + indent + "Start Time" + indent + "Finish Time");
		
		HashMap<Integer, ArrayList<Pair<Double>>> map = new HashMap<>();
		
		for(int i= 0;i<NUMBER_OF_CLOUDLET;i++) {
			map.put(i, new ArrayList<>());
			map.get(i).add(new Pair<>(0.0, 0.0));
		}

		DecimalFormat dft = new DecimalFormat("#####.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId()%NUMBER_OF_CLOUDLET + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + indent + dft.format(cloudlet.getActualCPUTime()) +
						indent + indent + dft.format(cloudlet.getExecStartTime())+ indent + indent + indent + dft.format(cloudlet.getFinishTime()));
			}
			
			int p = cloudlet.getCloudletId()%NUMBER_OF_CLOUDLET;
			
			map.get(p).add(new Pair<>(cloudlet.getExecStartTime(), cloudlet.getFinishTime()));
		}
		
		double sum = 0;
		
		for(int i = 0;i<NUMBER_OF_CLOUDLET;i++) {
			ArrayList<Pair<Double>> listt = map.get(i);
			
			for(int j = 1;j<listt.size();j++) {
				sum += (listt.get(j).getSt() - listt.get(j-1).getEn());
			}
		}
		
		sum = sum*1.0/NUMBER_OF_CLOUDLET;
		
		
		return sum;
	}
}
