$(document).ready(function () {

    $('.menu').hide();

    $(document).on('click', '.filter-cat-button', function () {
        $('.menu').hide(300);
        $('.category-menu').show(300);
    })

    $(document).on('click', '.filter-sup-button', function () {
        $('.menu').hide(300);
        $('.supplier-menu').show(300);
    })

    $('.menu-item').on('click', function (e) {
        var title = $(this).data('title');
        $('.shop-card').fadeOut(300);

        setTimeout(function () {
            if (title === "All") {
                $('.shop-card').fadeIn(300);
            } else {
                $('*[data-sup=' + title + ']').fadeIn(300);
                $('*[data-cat=' + title + ']').fadeIn(300);
            }
        }, 300);
    });

    $(document).on('click', '#my_account', function () {
       window.location.replace("/login");
    });

    $(document).on('click', '.button', function () {
        var btn = $(this)
        btn.addClass('clicked').delay(500)
        event.preventDefault();
        var productId = $(this).data('product_id');
        $.ajax({
            url: '/addToCart?productId=' + productId,
            type: "get",
            success: function (response) {
                if (response == "user is not logged in") {
                    window.location.replace("/login");
                } else {
                    $('.cart-content').html(checkCartSize());
                }
            }
        });
        setTimeout(function () {
            btn.removeClass('clicked');
        }, 500);
    });

    $(document).on('focusout', '.quantity-input', function () {
        var name = $(this).data('name');
        var quantity = $(this).val();
        if (quantity == "0") {
            $(this).parent().parent().hide(300);
            getTotalPrice();
        }
        $.ajax({
            url: '/updateShoppingCart?productName=' + name + '&quantity=' + quantity,
            type: "get",
            success: function (response) {
                checkCartSize();
                getTotalPrice();
            }
        });
    });

    $(document).on('click', '.fa-trash', function () {
        var name = $(this).data('name');
        $.ajax({
            url: '/updateShoppingCart?productName=' + name + '&quantity=0',
            type: "get",
            success: function (response) {
                checkCartSize();
            }
        });
        $(this).parent().fadeOut(300);
    });

    $('.close-modal').on('click', function (e) {
        $('body').css('overflow', 'auto');
        $('.cart-modal').removeClass('active');
        checkCartSize();

    });

    $('.checkout-button').on('click', function (e) {
        window.location.replace("/pay");
    });

    $('#sign-out').on('click', function (e) {
        window.location.replace("/logout");
    });

    $('.or-register').on('click', function (e) {
        window.location.replace("/register");
    });

    $('.or-login').on('click', function (e) {
        window.location.replace("/login");
    });

    $(document).on('click', '.cart-container', function () {
        if ($('.cart-content').text() == "0 items") {
            alert('Your cart is empty!');
        }
        else {
            $('.cart-item-container').empty();
            $('body').css('overflow', 'hidden');
            $('')
            $.ajax({
                url: '/getCartContentFromDB',
                type: "get",
                success: function (data) {
                    var products = JSON.parse(data);
                    for (var i = 0; i < products.length; i++) {
                        var name = products[i].name;
                        var quantity = products[i].quantity;
                        var price = products[i].defaultPrice;
                        var cartItem = populate(name, quantity, price);
                        renderCartItems(cartItem);
                    }
                }
            });
            getTotalPrice();
            $('.cart-modal').toggleClass('active');
        }
    })

    $(document).on('keydown', function (e) {
        if (e.keyCode === 27) {
            $('.cart-modal').removeClass('active');
        };
    });

    $(document).on('click', '#show-crcard-option', function () {
        var menuBox = $('#crcard-dropdown');
        menuBox.show(300);
    });

    $(document).on('click', '#hide-crcard-option', function () {
        var menuBox = $('#crcard-dropdown');
        menuBox.hide(300);
    });

    var renderCartItems = function (cartItem) {
        $('.cart-item-container').append(cartItem);
    }

    var checkCartSize = function () {
        $.ajax({
            url: '/getCartSize',
            type: "get",
            success: function (response) {
                if (response == null) {
                    $('.cart-content').html(0 + " items");
                }
                else {
                    $('.cart-content').html(response + " items");
                }
            }
        });
    }

    var checkUserLoggedIn = function () {
        $.ajax({
            url: '/checkUser',
            type: "get",
            success: function (response) {
                if (response == "null") {
                    $('#sign-out').css("display", "none");
                }
                else {
                    $('#sign-out').css("display", "flex");
                }
            }
        });
    }

    var getTotalPrice = function () {
        $.ajax({
            url: '/getTotalPrice',
            type: "get",
            success: function (data) {
                $('.cart-total').html(data);
            }
        });
    }
    checkCartSize();
    checkUserLoggedIn();

    var populate = function (name, quantity, price) {
        var cartItem = `<div class="cart-item">
                        <div class="item-name">` + name + `</div>
                        <div class="item-quantity">quantity: <input type="text" class="quantity-input" data-name="` + name + `" value="` + quantity + `"></input> </div>
                        <div class="item-price">price: ` + price + `</div>
                        <i class="fa fa-trash" aria-hidden="true" data-name="` + name + `"></i>
                    </div>`
        return cartItem;
    }
})