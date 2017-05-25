
$(document).ready(function () {

	var getTotalPrice = function () {
        $.ajax({
            url: '/getTotalPrice',
            type: "get",
            success: function (data) {
                $('#total-amount').html(data);
            }
        });
    }

    $(document).on('click', '.footer', function () {
        $.ajax({
            url: '/addCartToOrder',
            type: "get",
            success: function (response) {
                console.log(response)
            }
        });
    });

    getTotalPrice();

	$('.card').on('click', function() {
        console.log("asd");
        $('.card').removeClass('active');
        $(this).addClass('active');
        $('.form').slideUp();
        $('.form').slideDown();
	});
})