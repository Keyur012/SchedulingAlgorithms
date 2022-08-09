package rrabt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

import rr.RRSimulator;


public class RRABTDataCenterBroker extends DatacenterBroker {

	public RRABTDataCenterBroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	protected void submitCloudlets() {
//		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
//		int vmIndex = 0;
//				
//		// Logic of RoundRobin
//		List<Cloudlet> finallist = new ArrayList<Cloudlet>();
//		ArrayList<Cloudlet> rq = new ArrayList<Cloudlet>();
//				
//		for (Cloudlet cloudlet : getCloudletList()) {
//			rq.add(cloudlet);
//		}
//				
//		int count = 1;
//		for(Cloudlet printCloudlet : rq) {
//			Log.printLine(count + ". Cloudlet ID: " + printCloudlet.getCloudletId() + ", Cloudlet Length: " + printCloudlet.getCloudletLength());
//			count++;
//		}
//		
//		int nt = rq.size();
//		int i = 0;
//		
//		while(true) {
//			int tq = 0;
//			
//			for(Cloudlet cl : rq) {
//				
//			}
//		}
		
//int vmIndex = 0;
//		
//		// Logic of RoundRobin
//		List<Cloudlet> finalList = new ArrayList<Cloudlet>();
//		ArrayList<Cloudlet> tempList = new ArrayList<Cloudlet>();
//		
//		for (Cloudlet cloudlet : getCloudletList()) {
//			tempList.add(cloudlet);
//		}
//		
//		int count = 1;
//		for(Cloudlet printCloudlet : tempList) {
//			Log.printLine(count + ". Cloudlet ID: " + printCloudlet.getCloudletId() + ", Cloudlet Length: " + printCloudlet.getCloudletLength());
//			count++;
//		}
//		int timequantum = 1000;
//		int total=tempList.size();
//		Boolean[] boolArray = new Boolean[total];
//		Arrays.fill(boolArray, Boolean.FALSE);
//		count=0;
//		while(count<total)
//		{
//			ArrayList<Cloudlet> t2 = new ArrayList<>();
//			for(int i=0;i<total;i++)
//			{
//				Cloudlet checkCloudlet = tempList.get(i);
//				long temp=checkCloudlet.getCloudletLength();
//				if(temp>timequantum)
//				{
//				
//					checkCloudlet.setCloudletLength(timequantum);
//					finalList.add(checkCloudlet);
//					t2.add(checkCloudlet);
//					checkCloudlet.setCloudletLength(temp-timequantum);
//					//Log.printLine("Reduced . Cloudlet ID: " + checkCloudlet.getCloudletId() + ", Cloudlet Length: " + checkCloudlet.getCloudletLength());
//					
//				}
//				else if (!boolArray[i])
//				{
//					count++;
//					finalList.add(checkCloudlet);
//					//Log.printLine("Finished . Cloudlet ID: " + checkCloudlet.getCloudletId() + ", Cloudlet Length: " + checkCloudlet.getCloudletLength());
//					t2.add(checkCloudlet);
//					boolArray[i]=!boolArray[i];
//				}
//				
//			}
//			
//			for (Cloudlet cloudlet : t2) {
//				Vm vm;
//				// if user didn't bind this cloudlet and it has not been executed yet
//				if (cloudlet.getVmId() == -1) {
//					vm = getVmsCreatedList().get(vmIndex);
//				} else { // submit to the specific vm
//					vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
//					if (vm == null) { // vm was not created
//						Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet "
//								+ cloudlet.getCloudletId() + ": bount VM not available");
//						continue;
//					}
//				}
//
//				Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet "
//						+ cloudlet.getCloudletId() + " to VM #" + vm.getId() + " here ");
//				cloudlet.setVmId(vm.getId());
//				sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
//				cloudletsSubmitted++;
//				vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
//				getCloudletSubmittedList().add(cloudlet);
//			}
//
//			// remove submitted cloudlets from waiting list
//			for (Cloudlet cloudlet : getCloudletSubmittedList()) {
//				System.out.println("cpu time " +  cloudlet.getActualCPUTime());
//				t2.remove(cloudlet);
//			}
//		}
//		
//		
//		// Logic of Round Robin ends here
//		
//		
//	}
	
	@Override
	protected void submitCloudlets() {
		
		int vmIndex = 0;
		for (Cloudlet cloudlet : getCloudletList()) {
			Vm vm;
			// if user didn't bind this cloudlet and it has not been executed yet
			if (cloudlet.getVmId() == -1) {
				vm = getVmsCreatedList().get(vmIndex);
			} else { // submit to the specific vm
				vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				if (vm == null) { // vm was not created
					Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet "
							+ cloudlet.getCloudletId() + ": bount VM not available");
					continue;
				}
			}

			Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet "
					+ (cloudlet.getCloudletId()%RRABTSimulator.NUMBER_OF_CLOUDLET) + " to VM #" + vm.getId());
			cloudlet.setVmId(vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
			cloudletsSubmitted++;
			vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
			getCloudletSubmittedList().add(cloudlet);
		}

		// remove submitted cloudlets from waiting list
		for (Cloudlet cloudlet : getCloudletSubmittedList()) {
			getCloudletList().remove(cloudlet);
		}
	}
	
	@Override
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		getCloudletReceivedList().add(cloudlet);
		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + (cloudlet.getCloudletId()%RRABTSimulator.NUMBER_OF_CLOUDLET)
				+ " received" + " len " + cloudlet.getCloudletLength() + " " + cloudlet.getExecStartTime());
		
		
		cloudletsSubmitted--;
		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
			Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			finishExecution();
		} else { // some cloudlets haven't finished yet
			if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
				// all the cloudlets sent finished. It means that some bount
				// cloudlet is waiting its VM be created
				clearDatacenters();
				createVmsInDatacenter(0);
			}

		}
	}
	
	

}
