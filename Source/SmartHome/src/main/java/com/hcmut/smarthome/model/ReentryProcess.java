package com.hcmut.smarthome.model;

public class ReentryProcess extends Process {

	public static final String REENTRY_PAGE = "REENTRY_PAGE";
	public static final String EMPLOYMENT_PAGE = "EMPLOYMENT_PAGE";
	public static final String PAYSLIP_PAGE = "PAYSLIP_PAGE";
	
	public ReentryProcess() {
		super();
		addPage(new ReentryPage(REENTRY_PAGE));
		addPage(new EmploymentPage(EMPLOYMENT_PAGE));
		addPage(new PayslipPage(PAYSLIP_PAGE));
		addPage(new ConfirmationPage());
	}

}
