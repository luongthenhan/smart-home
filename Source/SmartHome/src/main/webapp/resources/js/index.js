function toggleExpandIconOfLightBlub() {
	$(".btn_expand_light_bulb span").toggleClass('fa-chevron-right fa-chevron-down');
}

function toggleExpandIconOfCamera(){
	$(".btn_expand_camera span").toggleClass('fa-chevron-right fa-chevron-down');
}

$(document).ready(function() {
	$(".btn_toggle_light_bulb").click(function() {
		toggleLightBulb();
	});
});