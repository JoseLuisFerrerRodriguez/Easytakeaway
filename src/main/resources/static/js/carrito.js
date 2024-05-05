$(document).ready(function () {
    $(".minusButton").on("click", function (evt) {
        evt.preventDefault();
        decrementarCantidad($(this));
    });

    $(".plusButton").on("click", function (evt) {
        evt.preventDefault();
        incrementarCantidad($(this));
    });

    actualizarTotal();
});

function incrementarCantidad(link) {
    productId = link.attr("pid");
    qtyInput = $("#quantity" + productId);

    newQty = parseInt(qtyInput.val()) + 1;
    if (newQty <= 100) {
        qtyInput.val(newQty);
        actualizar(productId, newQty);
    }
}

function decrementarCantidad(link) {
    productId = link.attr("pid");
    qtyInput = $("#quantity" + productId);

    newQty = parseInt(qtyInput.val()) - 1;
    if (newQty > 0) {
        qtyInput.val(newQty);
        actualizar(productId, newQty);
    }
}

function actualizar(productId, quantity) {
    url = contextPath + "carrito/actualizar/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (newSubtotal) {
        $("#subtotal" + productId).text(formatearNumero(newSubtotal));
        actualizarTotal();
    }).fail(function () {
        $("#modalTitle").text("Carrito");
        $("#modalBody").text("Error al actualizar el producto");
        $("#myModal").modal();
    });
}

function actualizarTotal() {
    total = 0.0;
    $(".productSubtotal").each(function (index, element) {
        subtotal = parseFloat(element.innerHTML.replace(',','.'));
        total = total + subtotal;
    });
    $("#totalAmount").text(formatearNumero(total) + "\u20AC");
}

function formatearNumero(numero) {
    let numFormat = parseFloat(numero).toFixed(2);
    return numFormat.toString().replace('.',',');
}