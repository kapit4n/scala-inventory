$ ->
  for pRow in $("tr")
    $(pRow).css("color", "red") if (parseInt($($(pRow).children("td")[6]).text(), 10 ) >parseInt($($(pRow).children("td")[7]).text(), 10 ))
