package rr;

import java.util.*;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

import example.Pair;

public class RRDataCenterBroker extends DatacenterBroker {

	public RRDataCenterBroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void submitCloudlets() {
		// TODO Auto-generated method stub
//		super.submitCloudlets();
		
//		int count = 1;
//		for(Cloudlet printCloudlet : getCloudletList()) {
//			Log.printLine(count + ". Cloudlet ID: " + printCloudlet.getCloudletId() + ", Cloudlet Length: " + printCloudlet.getCloudletLength());
//			count++;
//		}
		
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
					+ (cloudlet.getCloudletId()%RRSimulator.NUMBER_OF_CLOUDLET) + " to VM #" + vm.getId());
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

//	@Override
//	protected void submitCloudlets() {
//		// TODO Auto-generated method stub
//		int vmIndex = 0;
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
//		
//		int[] cloudletLength = new int[total];
////		for(int )
//		
//		Boolean[] boolArray = new Boolean[total];
//		Arrays.fill(boolArray, Boolean.FALSE);
//		count=0;
//		UtilizationModel utilizationModel = new UtilizationModelFull();
//		while(count<total)
//		{
//			for(int i=0;i<total;i++)
//			{
//				Cloudlet checkCloudlet = tempList.get(i);
//				long temp=checkCloudlet.getCloudletLength();
//				if(temp>timequantum)
//				{
//				
//					checkCloudlet.setCloudletLength(timequantum);
//					finalList.add(new Cloudlet(checkCloudlet.getCloudletId() + (int)(Math.random()%50000), timequantum, checkCloudlet.getNumberOfPes(), checkCloudlet.getCloudletFileSize(), checkCloudlet.getCloudletOutputSize(), utilizationModel, utilizationModel, utilizationModel));
////					finalList.add(checkCloudlet);
//					checkCloudlet.setCloudletLength(temp-timequantum);
//					//Log.printLine("Reduced . Cloudlet ID: " + checkCloudlet.getCloudletId() + ", Cloudlet Length: " + checkCloudlet.getCloudletLength());
//					
//				}
//				else if (!boolArray[i])
//				{
//					count++;
//					finalList.add(checkCloudlet);
//					//Log.printLine("Finished . Cloudlet ID: " + checkCloudlet.getCloudletId() + ", Cloudlet Length: " + checkCloudlet.getCloudletLength());
//					boolArray[i]=!boolArray[i];
//				}
//				
//			}
//			
//			for (Cloudlet cloudlet : finalList) {
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
//						+ cloudlet.getCloudletId() + " to VM #" + vm.getId());
//				cloudlet.setVmId(vm.getId());
//				sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
//				cloudletsSubmitted++;
//				vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
//				getCloudletSubmittedList().add(cloudlet);
//			}
//
//			// remove submitted cloudlets from waiting list
//			for (Cloudlet cloudlet : getCloudletSubmittedList()) {
//				getCloudletList().remove(cloudlet);
//			}
//			finalList.clear();
//			
//			
//		}
//		
//		
//		// Logic of Round Robin ends here
//		
//	}
//	
	@Override
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		getCloudletReceivedList().add(cloudlet);
		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + (cloudlet.getCloudletId()%RRSimulator.NUMBER_OF_CLOUDLET)
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
