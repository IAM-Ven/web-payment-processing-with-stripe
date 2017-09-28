/**
 * 
 */

$(document).ready(function(){
	$('.item-qty').on('input', function(){
		var id=this.id.substring(4);
		
		$('#update-'+id).css("display", "inline-block");
	});
});