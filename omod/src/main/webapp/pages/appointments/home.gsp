<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: patient ])
%>

<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaemr", "patient/patientSearchForm", [ defaultWhich: "all" ]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "patient/patientSearchResults", [ pageProvider: "knh", page: "appointments/appointments" ]) }
</div>

<script type="text/javascript">
	jQuery(function() {
		jQuery('input[name="query"]').focus();
	});
</script>