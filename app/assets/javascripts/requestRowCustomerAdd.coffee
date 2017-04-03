$ ->
  $("#quantity").keyup (e) ->
    $("#totalPrice").val($("#price").val() * $("#quantity").val())
    $("#credit").val($("#totalPrice").val() - $("#paid").val())
    $("#paid").val($("#totalPrice").val() - $("#credit").val())
  $("#price").keyup (e) ->
    $("#totalPrice").val($("#price").val() * $("#quantity").val())
    $("#credit").val($("#totalPrice").val() - $("#paid").val())
    $("#paid").val($("#totalPrice").val() - $("#credit").val())
  $("#paid").keyup (e) ->
    $("#credit").val($("#totalPrice").val() - $("#paid").val())
  $("#credit").keyup (e) ->
    $("#paid").val($("#totalPrice").val() - $("#credit").val())
