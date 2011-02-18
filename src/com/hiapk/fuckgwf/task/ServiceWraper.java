package com.hiapk.fuckgwf.task;

import com.cooler.schema.service.IService;
import com.cooler.schema.task.AServiceWraper;
import com.cooler.schema.task.AsyncOperation;
import com.cooler.schema.task.IResultReceiver;
import com.cooler.schema.task.mark.ATaskMark;
import com.cooler.schema.task.tracker.AInvokeTracker;
import com.hiapk.fuckgfw.tracker.UpdateHostTracker;

public class ServiceWraper extends AServiceWraper {
	private IService service;

	public ServiceWraper(IService service) {
		super(service);
		// TODO Auto-generated constructor stub
		this.service = service;
	}

	public AsyncOperation updateHost(IResultReceiver resultReceiver,
			ATaskMark taskMark, Object attach) {

		AsyncOperation operation = null;
		AInvokeTracker webServerTracker = new UpdateHostTracker(resultReceiver);
		operation = wraperOperationLocal(resultReceiver, taskMark, attach,
				"updateHost", webServerTracker);

		return operation;
	}

	private AsyncOperation wraperOperationLocal(IResultReceiver resultReceiver,
			ATaskMark taskMark, Object attach, String wraperMethodName,
			AInvokeTracker tracker, Object... args) {
		try {
			AsyncOperation operation = null;
			if (AsyncOperation.isTaskExist(taskMark)) {
				operation = takeoverExistTask(resultReceiver, taskMark);
			} else {
				operation = wraperOperation(tracker, taskMark,
						wraperMethodName, attach);
				operation.excuteOperate(service, taskMark);
			}
			return operation;
		} catch (Exception e) {
			return null;
		}
	}
}
