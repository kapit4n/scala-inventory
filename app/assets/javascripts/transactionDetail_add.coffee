$ ->
  $("#debitInput").keyup (e) ->
    $("#creditInput").val("0")
  $("#creditInput").keyup (e) ->
    $("#debitInput").val("0")
